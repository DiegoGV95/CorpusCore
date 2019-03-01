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

public class Euraxess extends Scraper {
	
	public Euraxess(String url, String database, String collection, String fuente, String categoria, String tipo, String direccion) {
		super(url, database, collection, fuente, categoria, tipo, direccion);
	}
	
	@Override
	public void scrap() throws IOException, LangDetectException {
		
		Document doc = Jsoup.connect(this.getURL()).get();
		
		Elements ofertas = doc.getElementsByClass("view-content");
		
		List<String> arrTitulos = new ArrayList<>();
		List<String> arrLinks = new ArrayList<>();
		List<String> arrDeadlines = new ArrayList<>();
		List<String> arrCampos = new ArrayList<>();
		List<String> arrUbicaciones = new ArrayList<>();
		List<String> arrCompañias = new ArrayList<>();
		
		for (Element oferta : ofertas) {
			
			Elements titulos = oferta.getElementsByTag("h2");
			Elements listItems = oferta.getElementsByClass("list-items");
			
			for (int i = 0; i < titulos.size(); i++) {
				
				if (titulos.get(i).text().isEmpty())
					arrTitulos.add("");
				else
					arrTitulos.add(titulos.get(i).text());
				
				if (titulos.get(i).child(0).attr("href").isEmpty())
					arrLinks.add("");
				else
					arrLinks.add("https://euraxess.ec.europa.eu" + titulos.get(i).child(0).attr("href"));
				
				if (listItems.get(i).child(0).child(0).child(1).text().isEmpty())
					arrDeadlines.add("");
				else
					arrDeadlines.add(listItems.get(i).child(0).child(0).child(1).text());
				
				if (listItems.get(i).child(1).child(0).child(1).text().isEmpty())
					arrCampos.add("");
				else
					arrCampos.add(listItems.get(i).child(1).child(0).child(1).text());
				
				if (listItems.get(i).child(2).child(0).child(1).text().isEmpty())
					arrUbicaciones.add("");
				else
					arrUbicaciones.add(listItems.get(i).child(2).child(0).child(1).text());
				
				if (listItems.get(i).child(3).child(0).child(1).text().isEmpty())
					arrCompañias.add("");
				else
					arrCompañias.add(listItems.get(i).child(3).child(0).child(1).text());
				
			}
			
		}
		
						
		Detector detector;
		
		if (getQuiero_fichero().isSelected()) {
			
			try {
				
				//Creamos el fichero
				String rutaArchivo = getDireccion() + "." + getTipo();
				
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File(rutaArchivo)));
				bw.write("Título\tCampo de estudio\tDeadLine\tCompañía\tUbicación\tLink\tIdioma\tFuente\tCategoría");
				bw.newLine();
						
				for (int i = 0; i < arrTitulos.size(); i++) {
					
					detector = DetectorFactory.create();
					detector.append((String)arrTitulos.get(i));
															
					bw.write(arrTitulos.get(i) + "\t");
					bw.write(arrCampos.get(i) + "\t");
					bw.write(arrDeadlines.get(i) + "\t");
					bw.write(arrCompañias.get(i) + "\t");
					bw.write(arrUbicaciones.get(i) + "\t");
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
				objeto.put("Campo de estudio", arrCampos.get(i));
				objeto.put("Deadline", arrDeadlines.get(i));
				objeto.put("Compañía", arrCompañias.get(i));
				objeto.put("Ubicación", arrUbicaciones.get(i));
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
