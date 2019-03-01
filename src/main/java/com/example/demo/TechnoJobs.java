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

public class TechnoJobs extends Scraper {
	
	public TechnoJobs(String url, String database, String collection, String fuente, String categoria, String tipo, String direccion) {
		super(url, database, collection, fuente, categoria, tipo, direccion);
	}
	
	@Override
	public void scrap() throws IOException, LangDetectException {
		
		Document doc = Jsoup.connect(this.getURL()).get();
		
		Elements titulos = doc.getElementsByClass("job-ti");
		Elements descripciones = doc.getElementsByClass("job-body");
		Elements detalles = doc.getElementsByClass("job-details");
		
		List<String> arrTitulos = new ArrayList<>();
		List<String> arrLinks = new ArrayList<>();
		List<String> arrDescripciones = new ArrayList<>();
		List<String> arrFechas = new ArrayList<>();
		List<String> arrUbicaciones = new ArrayList<>();
		List<String> arrSalarios = new ArrayList<>();
		
		for (Element titulo : titulos) {
			
			Element t = titulo.child(0).child(0);
			
			arrTitulos.add(t.text());
			arrLinks.add("www.technojobs.co.uk" + t.attr("href"));
			
		}
		
		for (Element descripcion : descripciones) {
			
			arrDescripciones.add(descripcion.text());
			
		}
		
		for (Element detalle : detalles) {
			
			Element fecha = detalle.child(1);
			Element ubicacion = detalle.child(2);
			Element salario = detalle.child(3);
			
			arrFechas.add(fecha.attr("datetime"));
			arrUbicaciones.add(ubicacion.data());
			String d[] = detalle.text().split(" ");
			arrSalarios.add(d[d.length-3] + " " + d[d.length-2] + " " + d[d.length-1]);
			//arrSalarios.add(detalle.text());
			
		}
								
		Detector detector;
		
		if (getQuiero_fichero().isSelected()) {
			
			try {
				
				//Creamos el fichero
				String rutaArchivo = getDireccion() + "." + getTipo();
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File(rutaArchivo)));
				bw.write("Título\tDescripción\tSalario\tUbicación\tFecha\tLink\tIdioma\tFuente\tCategoría");
				bw.newLine();
				
				for (int i = 0; i < arrTitulos.size(); i++) {
					
					detector = DetectorFactory.create();
					detector.append((String)arrDescripciones.get(i));
									
					bw.write(arrTitulos.get(i) + "\t");
					bw.write(arrDescripciones.get(i) + "\t");
					bw.write(arrSalarios.get(i) + "\t");
					bw.write(arrUbicaciones.get(i) + "\t");
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
				objeto.put("Salario", arrSalarios.get(i));
				objeto.put("Ubicación", arrUbicaciones.get(i));
				objeto.put("Fecha", arrFechas.get(i).replace("-", "/"));
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