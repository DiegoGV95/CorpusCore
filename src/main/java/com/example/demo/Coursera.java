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

public class Coursera extends Scraper {
	
	public Coursera(String url, String database, String collection, String fuente, String categoria, String tipo, String direccion) {
		super(url, database, collection, fuente, categoria, tipo, direccion);
	}
	
	@Override
	public void scrap() throws IOException, LangDetectException {
		
		Document doc = Jsoup.connect(this.getURL()).get();
		
		Elements listas = doc.getElementsByTag("ul");
		
		List<String> arrTitulos = new ArrayList<>();
		List<String> arrLinks = new ArrayList<>();
		List<String> arrTipos = new ArrayList<>();
		List<String> arrCompañias = new ArrayList<>();
		
		for (Element lista : listas) {
			
			Elements cursos = lista.getElementsByTag("li");
			
			for (Element curso : cursos) {
				
				Elements contenidos = curso.getElementsByClass("card-info");
				
				for (Element contenido : contenidos) {
					
					Element titulo = contenido.child(0);
					Element tipo = contenido.child(1);
					Element compañia = contenido.child(3);
					
					if (titulo.text().isEmpty())
						arrTitulos.add("");
					else
						arrTitulos.add(titulo.text());
					
					if (curso.child(0).child(0).attr("href").isEmpty())
						arrLinks.add("");
					else
						arrLinks.add("https://www.coursera.org/courses?query=java" + curso.child(0).child(0).attr("href"));
					
					if (tipo.text().isEmpty())
						arrTipos.add("");
					else
						arrTipos.add(tipo.text());
					
					if (compañia.text().isEmpty())
						arrCompañias.add("");
					else
						arrCompañias.add(compañia.text());
					
				}
				
			}
			
		}		
												
		Detector detector;
		
		if (getQuiero_fichero().isSelected()) {

			try {
				
				//Creamos el fichero
				String rutaArchivo = getDireccion() + "." + getTipo();
				
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File(rutaArchivo)));
				bw.write("Título\tTipo\tCompañia\tLink\tIdioma\tFuente\tCategoría");
				bw.newLine();
				
				for (int i = 0; i < arrTitulos.size(); i++) {
					
					detector = DetectorFactory.create();
					detector.append((String)arrTitulos.get(i));
															
					bw.write(arrTitulos.get(i) + "\t");
					bw.write(arrTipos.get(i) + "\t");
					bw.write(arrCompañias.get(i) + "\t");
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
				objeto.put("Tipo", arrTipos.get(i));
				objeto.put("Compañía", arrCompañias.get(i));
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