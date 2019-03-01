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

public class EURES extends Scraper {
	
	public EURES(String url, String database, String collection, String fuente, String categoria, String tipo, String direccion) {
		super(url, database, collection, fuente, categoria, tipo, direccion);
	}
	
	@Override
	public void scrap() throws IOException, LangDetectException {
		
		Document doc = Jsoup.connect(this.getURL()).get();
		
		BufferedWriter bw1 = new BufferedWriter(new FileWriter(new File("/Users/diegogarcia-viana/Desktop/EURES.txt")));
		bw1.write(doc.html());
		bw1.close();
		
		Elements empleos = doc.getElementsByTag("job-container");
		
		if (empleos.isEmpty())
			System.out.println("Vacío");
		
		List<String> arrTitulos = new ArrayList<>();
		List<String> arrCategorias = new ArrayList<>();
		List<String> arrDescripciones = new ArrayList<>();
		
		//No entra en el bucle
		for (Element empleo : empleos) {
						
			Elements titulos = empleo.getElementsByTag("jv-title");
			Elements categorias = empleo.getElementsByClass("ng-binding ng-scope");
			Elements descripciones = empleo.getElementsByClass("description record-description ng-binding");
			
			for (int i = 0; i < titulos.size(); i++) {
				
				if (titulos.get(i).text().isEmpty())
					continue;
				else
					arrTitulos.add(titulos.get(i).text());
				
				if (categorias.get(i).text().isEmpty())
					arrCategorias .add("");
				else
					arrCategorias.add(categorias.get(i).text());
				
				if (descripciones.get(i).text().isEmpty())
					arrDescripciones.add("");
				else
					arrDescripciones.add(descripciones.get(i).text());
				
			}
			
		}		
						
		Detector detector;
		
		if (getQuiero_fichero().isSelected()) {
			
			try {
				
				//Creamos el fichero
				String rutaArchivo = getDireccion() + "." + getTipo();
				
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File(rutaArchivo)));
				bw.write("Título\tCategoría\tDescripción\tIdioma\tFuente\tCategoría");
				bw.newLine();
				
				for (int i = 0; i < arrTitulos.size(); i++) {
					
					detector = DetectorFactory.create();
					detector.append((String)arrTitulos.get(i));
							
					bw.write(arrTitulos.get(i) + "\t");
					bw.write(arrCategorias.get(i) + "\t");
					bw.write(arrDescripciones.get(i) + "\t");
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
				objeto.put("Categoría", arrCategorias.get(i));
				objeto.put("Descripción", arrDescripciones.get(i));
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
