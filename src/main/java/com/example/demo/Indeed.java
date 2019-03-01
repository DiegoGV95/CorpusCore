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

public class Indeed extends Scraper {
	
	public Indeed(String url, String database, String collection, String fuente, String categoria, String tipo, String direccion) {
		super(url, database, collection, fuente, categoria, tipo, direccion);
	}
	
	@Override
	public void scrap() throws IOException, LangDetectException {
		
		Document doc = Jsoup.connect(this.getURL()).get();
				
		List<String> arrTitulos = new ArrayList<>();
		List<String> arrLinks = new ArrayList<>();
		List<String> arrCompañias = new ArrayList<>();
		List<String> arrUbicaciones = new ArrayList<>();
		List<String> arrDescripciones = new ArrayList<>();
		
		Element columna = doc.getElementById("resultsCol");
		Elements empleos = doc.getElementsByClass("jobsearch-SerpJobCard row result clickcard");
		
		for (Element empleo : empleos) {	
			
			Elements titulos = empleo.getElementsByClass("jobtitle turnstileLink");
			Elements titulos2 = empleo.getElementsByTag("h2");
			Elements compañias = empleo.getElementsByClass("company");
			Elements ubicaciones = empleo.getElementsByClass("location");
			Elements descripciones = empleo.getElementsByClass("summary");
			
			if (titulos.isEmpty() && titulos2.isEmpty())
				continue;
			else {
				
				for (Element titulo : titulos) {
					if (titulo.attr("title").isEmpty())
						arrTitulos.add("");
					else
						arrTitulos.add(titulo.attr("title"));
					
					if (titulo.attr("href").isEmpty())
						arrLinks.add("");
					else
						arrLinks.add("www.indeed.es" + titulo.attr("href"));
				}
				
				for (Element titulo : titulos2) {
					if (titulo.child(0).attr("title").isEmpty())
						arrTitulos.add("");
					else
						arrTitulos.add(titulo.child(0).attr("title"));
					
					if (titulo.child(0).attr("href").isEmpty())
						arrLinks.add("");
					else
						arrLinks.add("www.indeed.es" + titulo.child(0).attr("href"));
				}
				
			}
			
			if (compañias.isEmpty())
				arrCompañias.add("");
			else {
				
				for (Element compañia : compañias) {
					if (compañia.text().isEmpty())
						arrCompañias.add("");
					else
						arrCompañias.add(compañia.text());
				}
				
			}
			
			if (ubicaciones.isEmpty())
				arrUbicaciones.add("");
			else {
				
				for (Element ubicacion : ubicaciones) {
					if (ubicacion.text().isEmpty())
						arrUbicaciones.add("");
					else
						arrUbicaciones.add(ubicacion.text());
				}
				
			}
			
			if (descripciones.isEmpty())
				arrDescripciones.add("");
			else {
				
				for (Element descripcion : descripciones) {
					if (descripcion.text().isEmpty())
						arrDescripciones.add("");
					else
						arrDescripciones.add(descripcion.text());
				}
				
			}
			
		}			
		
		Detector detector;
		
		if (getQuiero_fichero().isSelected()) {
			
			try {
				
				//Creamos el fichero
				String rutaArchivo = getDireccion() + "." + getTipo();
				
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File(rutaArchivo)));
				bw.write("Título\tUbicación\tLink\tIdioma\tFuente\tCategoría");
				bw.newLine();
				
				for (int i = 0; i < arrTitulos.size(); i++) {
					
					detector = DetectorFactory.create();
					detector.append((String)arrDescripciones.get(i));
							
					bw.write(arrTitulos.get(i) + "\t");
					/*bw.write(arrDescripciones.get(i) + "\t");
					bw.write(arrCompañias.get(i) + "\t");*/
					bw.write(arrUbicaciones.get(i)+ "\t");
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
				objeto.put("Compañía", arrCompañias.get(i));
				objeto.put("Ubicación", arrUbicaciones.get(i));
				objeto.put("Descripción", arrDescripciones.get(i));
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
