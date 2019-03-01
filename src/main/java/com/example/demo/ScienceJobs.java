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

public class ScienceJobs extends Scraper {
	
	public ScienceJobs(String url, String database, String collection, String fuente, String categoria, String tipo, String direccion) {
		super(url, database, collection, fuente, categoria, tipo, direccion);
	}
	
	@Override
	public void scrap() throws IOException, LangDetectException {
		
		Document doc = Jsoup.connect(this.getURL()).get();
		
		//Elements empleos = doc.getElementsByClass("lister cf block");
		Element lista = doc.getElementById("listing");
		//Elements empleos = doc.getElementsByClass("lister__details cf js-clickable");
		Elements empleos = lista.getElementsByTag("li");

		
		List<String> arrTitulos = new ArrayList<>();
		List<String> arrLinks = new ArrayList<>();
		List<String> arrUbicaciones = new ArrayList<>();
		List<String> arrSalarios = new ArrayList<>();
		List<String> arrCompañias = new ArrayList<>();
		List<String> arrDescripciones = new ArrayList<>();
		
		for (Element empleo : empleos) {
			
			Elements titulos = empleo.getElementsByTag("h3");
			Elements ubicaciones = empleo.getElementsByClass("lister__meta-item lister__meta-item--location");
			Elements salarios = empleo.getElementsByClass("lister__meta-item lister__meta-item--salary");
			Elements compañias = empleo.getElementsByClass("lister__meta-item lister__meta-item--recruiter");
			Elements descripciones = empleo.getElementsByClass("lister__description js-clamp-2");
			
			for (int i = 0; i < titulos.size(); i++) {
				
				if (titulos.get(i).text().isEmpty())
					arrTitulos.add("");
				else	
					arrTitulos.add(titulos.get(i).text());
				
				if (titulos.get(i).child(0).attr("href").isEmpty())
					arrLinks.add("");
				else {
					if (!titulos.get(i).child(0).attr("href").contains("http"))
						arrLinks.add("https://jobs.sciencecareers.org" + titulos.get(i).child(0).attr("href").replace(" ", "").replace("\r\n\t", "").replaceAll("\r\n\r\n\r\n\r\n", ""));
					else
						arrLinks.add(titulos.get(i).child(0).attr("href"));
				}
				
				
				if (ubicaciones.get(i).text().isEmpty())
					arrUbicaciones.add("");
				else
					arrUbicaciones.add(ubicaciones.get(i).text());
				
				if (salarios.get(i).text().isEmpty())
					arrSalarios.add("");
				else
					arrSalarios.add(salarios.get(i).text());
				
				if (compañias.get(i).text().isEmpty())
					arrCompañias.add("");
				else
					arrCompañias.add(compañias.get(i).text());
				
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
				bw.write("Título\tDescripción\tCompañía\tUbicación\tSalario\tLink\tIdioma\tFuente\tCategoría");
				bw.newLine();
				
				for (int i = 0; i < arrTitulos.size(); i++) {
					
					detector = DetectorFactory.create();
					detector.append((String)arrDescripciones.get(i));
													
					bw.write(arrTitulos.get(i) + "\t");
					bw.write(arrDescripciones.get(i) + "\t");
					bw.write(arrCompañias.get(i) + "\t");
					bw.write(arrUbicaciones.get(i) + "\t");
					bw.write(arrSalarios.get(i) + "\t");
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
				objeto.put("Compañía", arrCompañias.get(i));
				objeto.put("Ubicación", arrUbicaciones.get(i));
				objeto.put("Salario", arrSalarios.get(i));
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