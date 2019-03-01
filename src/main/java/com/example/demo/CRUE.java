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

public class CRUE extends Scraper {

	public CRUE(String URL, String database, String collection, String fuente, String categoria, String tipo, String direccion) {
		super(URL, database, collection, fuente, categoria, tipo, direccion);
	}

	@Override
	public void scrap() throws IOException, LangDetectException {
		
		Document doc = Jsoup.connect(getURL()).get();
		
		List<String> arrTitulos = new ArrayList<>();
		List<String> arrLinks = new ArrayList<>();
		
		Element body = doc.getElementById("layoutsTable");
		
		Elements titulos = body.getElementsByAttribute("href");
		
		for (Element titulo : titulos) {
			
			if (titulo.text().isEmpty())
				continue;
			else
				arrTitulos.add(titulo.text());
			
			if (titulo.attr("href").isEmpty())
				arrLinks.add("");
			else {
				if (!titulo.attr("href").contains("http"))
					arrLinks.add("www.crue.org" + titulo.attr("href"));
				else
					arrLinks.add(titulo.attr("href"));
			}
			
		}

		
		Detector detector;
		
		if (getQuiero_fichero().isSelected()) {
			
			//Creamos el fichero
			String rutaArchivo = getDireccion() + "." + getTipo();
			
			BufferedWriter bw; 

			try {
				
				bw = new BufferedWriter(new FileWriter(new File(rutaArchivo)));
				bw.write("Título\tLink\tIdioma\tFuente\tCategoría");
				bw.newLine();
				
				for (int i = 0; i < arrTitulos.size(); i++) {
					
					detector = DetectorFactory.create();
					detector.append((String)arrTitulos.get(i));
					
					bw.write(arrTitulos.get(i) + "\t");
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
