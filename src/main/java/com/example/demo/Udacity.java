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

public class Udacity extends Scraper {
	
	public Udacity(String url, String database, String collection, String fuente, String categoria, String tipo, String direccion) {
		super(url, database, collection, fuente, categoria, tipo, direccion);
	}
	
	@Override
	public void scrap() throws IOException, LangDetectException {
		
		Document doc = Jsoup.connect(this.getURL()).get();
				
		List<String> arrTitulos = new ArrayList<>();
		List<String> arrLinks = new ArrayList<>();
		List<String> arrDescripciones = new ArrayList<>();
		List<String> arrAptitudes = new ArrayList<>();
		List<String> arrColaboraciones = new ArrayList<>();
		
		Elements cursos = doc.getElementsByClass("course-summary-card row row-gap-medium catalog-card nanodegree-card ng-star-inserted");
		
		for (Element curso : cursos) {	
			
			Elements titulos = curso.getElementsByTag("h3");
			Elements descripciones = curso.getElementsByClass("card__expander--summary mb-1");
			Elements skills = curso.getElementsByClass("skills ng-star-inserted");
			Elements colaboraciones = curso.getElementsByClass("hidden-sm-down ng-star-inserted");
			
			if (titulos.isEmpty())
				continue;
			if (descripciones.isEmpty())
				arrDescripciones.add("");
			if (skills.isEmpty())
				arrAptitudes.add("");
			if (colaboraciones.isEmpty())
				arrColaboraciones.add("");
			
			for (int i = 0; i < titulos.size(); i++) {
				
				if (titulos.get(i).text().isEmpty())
					arrTitulos.add("");
				else
					arrTitulos.add(titulos.get(i).text());
				
				if (titulos.get(i).child(0).attr("href").isEmpty())
					arrLinks.add("");
				else
					arrLinks.add("https://eu.udacity.com" + titulos.get(i).child(0).attr("href"));
				
			}
			
			for (Element descripcion : descripciones) {
				if (descripcion.text().isEmpty())
					arrDescripciones.add("");
				else
					arrDescripciones.add(descripcion.text());
			}
			
			for (Element skill : skills) {
				
				if (skill.child(1).text().isEmpty())
					arrAptitudes.add("");
				else
					arrAptitudes.add(skill.child(1).text());
				
			}
			
			for (Element colaboracion : colaboraciones) {
				
				if (colaboracion.child(1).text().isEmpty())
					arrColaboraciones.add("");
				else
					arrColaboraciones.add(colaboracion.child(1).text());
				
			}
			
		}			
						
		Detector detector;
		
		if (getQuiero_fichero().isSelected()) {
			
			try {
				
				//Creamos el fichero
				String rutaArchivo = getDireccion() + "." + getTipo();
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File(rutaArchivo)));
				bw.write("Título\tDescripción\tConocimientos a adquirir\tColaboradores\tLink\tIdioma\tFuente\tCategoría");
				bw.newLine();
				
				for (int i = 0; i < arrTitulos.size(); i++) {
					
					detector = DetectorFactory.create();
					detector.append((String)arrDescripciones.get(i));
							
					bw.write(arrTitulos.get(i) + "\t");
					bw.write(arrDescripciones.get(i) + "\t");
					bw.write(arrAptitudes.get(i)+ "\t");
					bw.write(arrColaboraciones.get(i) + "\t");
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
				objeto.put("Descripción", arrDescripciones.get(i));
				objeto.put("Conocimientos a adquirir", arrAptitudes.get(i));
				objeto.put("Colaboradores", arrColaboraciones.get(i));
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
