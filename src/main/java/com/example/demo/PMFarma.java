package com.example.demo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.jsoup.nodes.Element;

public class PMFarma extends Scraper {
	
	public PMFarma(String url, String database, String collection, String fuente, String categoria, String tipo, String direccion) {
		super(url, database, collection, fuente, categoria, tipo, direccion);
	}
	
	@Override
	public void scrap() throws IOException, LangDetectException {
		
		Document doc = Jsoup.connect(this.getURL()).get();
		
		Elements articulos = doc.getElementsByClass("noticia col-md-12 p0 mb15");
		
		List<String> arrTitulos = new ArrayList<>();
		List<String> arrLinks = new ArrayList<>();
		List<String> arrFechas = new ArrayList<>();
		List<String> arrDescripciones = new ArrayList<>();
		List<String> arrCategorias = new ArrayList<>();
		
		for (Element articulo : articulos) {
			
			Elements titulos = articulo.getElementsByClass("titulo tit_section_blue");
			Elements fechas = articulo.getElementsByClass("fecha_articulo");
			Elements descripciones = articulo.getElementsByClass("entradilla");
			Elements categorias = articulo.getElementsByClass("categorias_footer_contenido");
			
			if (titulos.isEmpty())
				continue;
			if (fechas.isEmpty())
				arrFechas.add("");
			
			for (int i = 0; i < titulos.size(); i++) {
				
				if (titulos.get(i).text().isEmpty())
					continue;
				else
					arrTitulos.add(titulos.get(i).text());
				
				if (titulos.get(i).child(0).attr("href").isEmpty())
					arrLinks.add("");
				else {
					if (!titulos.get(i).child(0).attr("href").contains("http"))
						arrLinks.add("https://www.pmfarma.es" + titulos.get(i).child(0).attr("href"));
					else
						arrLinks.add(titulos.get(i).child(0).attr("href"));
				}
				
				if (fechas.get(i).text().isEmpty())
					arrFechas.add("");
				else
					arrFechas.add(fechas.get(i).text());
				
				if (descripciones.get(i).text().isEmpty())
					arrDescripciones.add("");
				else
					arrDescripciones.add(descripciones.get(i).text());
				
				Elements cats = categorias.get(i).getElementsByTag("a");
				String cadena = "";
				for (int j = 0; j < cats.size(); j++) {
					if (j < cats.size() - 1)
						cadena += cats.get(j).text() + ", ";
					else
						cadena += cats.get(j).text();
				}
				
				if (cadena.isEmpty())
					arrCategorias.add("");
				else
					arrCategorias.add(cadena);
				
			}
			
		}
		
		Detector detector;
		
		if (getQuiero_fichero().isSelected()) {
			
			try {
				
				//Creamos el fichero
				String rutaArchivo = getDireccion() + "." + getTipo();
				
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File(rutaArchivo)));
				bw.write("Título\tFecha\tDescripción\tCategoría\tLink\tIdioma\tFuente\tCategoría");
				bw.newLine();
				
				for (int i = 0; i < arrTitulos.size(); i++) {
					
					detector = DetectorFactory.create();
					detector.append((String)arrDescripciones.get(i));
							
					bw.write(arrTitulos.get(i) + "\t");
					bw.write(arrFechas.get(i).replace("-", "/") + "\t");
					bw.write(arrDescripciones.get(i) + "\t");
					bw.write(arrCategorias.get(i) + "\t");
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
		
		MongoClient mongo = new MongoClient();
		DB db = mongo.getDB(getDatabase());
		DBCollection collection = db.getCollection(getCollection());
		
		int contador = 0;
		
		for (int i = 0; i < arrTitulos.size(); i++) {
			
			DBObject queryLink = new QueryBuilder().start().put("Link").is(arrLinks.get(i)).get();
			
			DBCursor resultadoLink = collection.find(queryLink);
			
			if (resultadoLink.count() == 0) {
				
				detector = DetectorFactory.create();
				detector.append((String)arrDescripciones.get(i));
				
				BasicDBObject objeto = new BasicDBObject();
				
				objeto.put("Título", arrTitulos.get(i));
				objeto.put("Fecha", arrFechas.get(i).replace("-", "/"));
				objeto.put("Descripción", arrDescripciones.get(i));
				objeto.put("Categoría", arrCategorias.get(i));
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
