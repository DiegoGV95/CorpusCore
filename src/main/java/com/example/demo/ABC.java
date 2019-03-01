package com.example.demo;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.QueryBuilder;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ABC extends Scraper {
	
	public ABC(String url, String database, String collection, String fuente, String categoria, String tipo, String direccion) {
		super(url, database, collection, fuente, categoria, tipo, direccion);
	}
	
	@Override
	public void scrap() throws IOException, LangDetectException {
		
		Document doc = Jsoup.connect(this.getURL()).get();
		
		Elements titles = doc.select("article.h3");
		
		for (Element title : titles) {
			System.out.println(title.text());
		}
		
		Elements articulos = doc.getElementsByTag("article");
		
		List<String> arrTitulos = new ArrayList<>();
		List<String> arrLinks = new ArrayList<>();
		List<String> arrAutores = new ArrayList<>();
		List<String> arrAutores2 = new ArrayList<>();
		List<String> arrFechas = new ArrayList<>();
		List<String> arrDescripciones = new ArrayList<>();
		
		for (Element articulo : articulos) {
			
			Elements titulos = articulo.getElementsByTag("h3");
			Elements footers = articulo.getElementsByTag("footer");
			
			if (titulos.isEmpty())
				continue;
			if (footers.isEmpty())
				arrAutores.add("");
			
			for (int i = 0; i < titulos.size(); i++) {
				
				if (titulos.get(i).text().isEmpty())
					continue;
				else
					arrTitulos.add(titulos.get(i).text());
				
				if (titulos.get(i).child(0).attr("href").isEmpty())
					arrLinks.add("");
				else {
					if (!titulos.get(i).child(0).attr("href").contains("http"))
						arrLinks.add("https://www.abc.es" + titulos.get(i).child(0).attr("href"));
					else
						arrLinks.add(titulos.get(i).child(0).attr("href"));
				}
				
				if (!footers.isEmpty()) {
					
					Elements autores = footers.get(i).getElementsByClass("autor");
					String cadena = "";
					
					if (autores.isEmpty())
						arrAutores.add("");
					else {
						for (int j = 0; j < autores.size(); j++) {
							if (j < autores.size() - 1)
								cadena += autores.get(j).text() + ", ";
							else
								cadena += autores.get(j).text();
						}
						if (cadena.isEmpty())
							arrAutores.add("");
						else
							arrAutores.add(cadena);
					}
					
				}
			}
			
		}
		
		for (String link : arrLinks) {
			
			Document doc2 = Jsoup.connect(link).get();
			
			Elements fechas = doc2.getElementsByTag("time");
			Elements encabezados = doc2.getElementsByClass("encabezado-articulo");
			
			if (fechas.isEmpty())
				arrFechas.add("");
			if (encabezados.isEmpty())
				arrDescripciones.add("");
			
			for (Element fecha : fechas) {
				
				if (fecha.text().isEmpty())
					arrFechas.add("");
				else
					arrFechas.add(fecha.text());
				
			}	
			
			for (Element encabezado : encabezados) {
				
				Elements descripciones = encabezado.getElementsByTag("h2");
				
				if (descripciones.isEmpty())
					arrDescripciones.add("");
				
				String cadena = "";
				
				for (int i = 0; i < descripciones.size(); i++) {

					if (i < descripciones.size() - 1)
						cadena += descripciones.get(i).text() + ". ";
					else
						cadena += descripciones.get(i).text();
					
				}
				
				if (cadena.isEmpty())
					arrDescripciones.add("");
				else
					arrDescripciones.add(cadena);
				
			}
			
		}

		Detector detector;	
				
		if (getQuiero_fichero().isSelected()) {

			try {
				
				//Creamos el fichero
				String rutaArchivo = getDireccion() + "." + getTipo();
				
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File(rutaArchivo)));
				bw.write("Título\tDescripción\tAutor\tFecha de publicación\tLink\tIdioma\tFuente\tCategoría");
				bw.newLine();
				
				for (int i = 0; i < arrTitulos.size(); i++) {
					
					detector = DetectorFactory.create();
					detector.append((String)arrTitulos.get(i));
					
					bw.write(arrTitulos.get(i) + "\t");
					bw.write(arrDescripciones.get(i) + "\t");
					bw.write(arrAutores.get(i) + "\t");
					bw.write(arrFechas.get(i).replace("-", "/") + "\t");
					bw.write(arrLinks.get(i) + "\t");
					bw.write(detector.detect().toUpperCase() + "\t");
					bw.write(getFuente() + "\t");
					bw.write(getCategoria());
					bw.newLine();
					
				}
				
				bw.close();	
				
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(null, "No existe el directorio indicado", null, JOptionPane.ERROR_MESSAGE);
			}	
			
		}
			
		//Amacenamiento de datos en Mongo
		MongoClient mongo = new MongoClient();
		DB database = mongo.getDB(getDatabase());
		DBCollection collection = database.getCollection(getCollection());
		
		int contador = 0;
		
		for (int i = 0; i < arrTitulos.size(); i++) {
									
			DBObject queryLink = new QueryBuilder().start().put("Link").is(arrLinks.get(i)).get();
			
			DBCursor resultadoLink = collection.find(queryLink);
			
			if (resultadoLink.count() == 0) {
				
				detector = DetectorFactory.create();
				detector.append((String)arrTitulos.get(i));
				
				BasicDBObject objeto = new BasicDBObject();
				
				objeto.put("Título", arrTitulos.get(i));
				objeto.put("Descripción", arrDescripciones.get(i));
				objeto.put("Autor", arrAutores.get(i));
				objeto.put("Fecha de publicación", arrFechas.get(i).replace("-", "/"));
				objeto.put("Link", arrLinks.get(i));
				objeto.put("Idioma", detector.detect().toUpperCase());
				objeto.put("Fuente", getFuente());
				objeto.put("Categoría", getCategoria());
				
				collection.insert(objeto);
			}
			
			else
				contador++;
			
		}
		
		setRespuesta("Se han importado " + (arrTitulos.size() - contador) + " registros de " + getFuente() + " en la colección " + getCollection().toUpperCase()
				+ " de la base de datos " + getDatabase().toUpperCase() + ". Había " + contador + " elementos ya existentes en la colección");			
								
	}
	
}
