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

public class SEPE extends Scraper {
	
	public SEPE(String url, String database, String collection, String fuente, String categoria, String tipo, String direccion) {
		super(url, database, collection, fuente, categoria, tipo, direccion);
	}
	
	@Override
	public void scrap() throws IOException, LangDetectException {
		
		Document doc = Jsoup.connect(this.getURL()).get();
		
		Elements empleos = doc.getElementsByClass("animated-fast fadeIn ng-scope");
		
		System.out.println(empleos.size());
		
		List<String> arrTitulos = new ArrayList<>();
		List<String> arrLinks = new ArrayList<>();
		List<String> arrUbicaciones = new ArrayList<>();
		List<String> arrFechas = new ArrayList<>();
		List<String> arrDescripciones = new ArrayList<>();
		List<String> arrSalarios = new ArrayList<>();
		List<String> arrEstudios = new ArrayList<>();
		
		for (Element empleo : empleos) {
			
			Element datos = empleo.child(0).child(0).child(0);
			
			Elements titulos = datos.getElementsByClass("col-xs-7 col-sm-9");
			Elements extras = datos.getElementsByClass("col-xs-12 ng-binding");
			Elements descripciones = datos.getElementsByClass("col-xs-12 margin-top-10 ng-binding");
			
			if (titulos.isEmpty())
				continue;
			
			for (Element titulo : titulos) {
				
				if (titulo.child(0).attr("title").isEmpty())
					continue;
				else
					arrTitulos.add(titulo.child(0).attr("title"));
				
				if (titulo.child(0).attr("href").isEmpty())
					arrLinks.add("");
				else {
					if (!titulo.child(0).attr("href").contains("http:"))
						arrLinks.add("https://www.empleate.gob.es" + titulo.child(0).attr("href").replace("#", ""));
					else
						arrLinks.add(titulo.child(0).attr("href").replaceAll("#", ""));
				}
				
			}
			
			for (Element extra : extras) {
				
				Element ubicacion = extra.child(0);
				Element fecha = extra.child(1);
				Element salario = extra.child(5);
				Element estudios = extra.child(8);
				
				if (ubicacion.text().isEmpty())
					arrUbicaciones.add("");
				else
					arrUbicaciones.add(ubicacion.text());
				
				if (fecha.text().isEmpty())
					arrFechas.add("");
				else
					arrFechas.add(fecha.text());
				
				if (salario.text().isEmpty())
					arrSalarios.add("");
				
				if (estudios.text().isEmpty())
					arrEstudios.add("");
				else
					arrEstudios.add(estudios.text());
				
			}
			
			for (Element descripcion : descripciones) {
				
				if (descripcion.text().isEmpty())
					arrDescripciones.add("");
				else
					arrDescripciones.add(descripcion.text());
				
			}
			
		}
		
		Detector detector;
		
		if (getQuiero_fichero().isSelected()) {
			
			try {
				
				//Creamos el fichero
				String rutaArchivo = getDireccion() + "." + getTipo();
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File(rutaArchivo)));
				bw.write("Título\tUbicación\tSalario\tEstudios\tDescripción\tFecha de publicación\tLink\tIdioma\tFuente\tCategoría");
				bw.newLine();
				
				for (int i = 0; i < arrTitulos.size(); i++) {
					
					detector = DetectorFactory.create();
					detector.append((String)arrDescripciones.get(i));
							
					bw.write(arrTitulos.get(i) + "\t");
					bw.write(arrUbicaciones.get(i) + "\t");
					bw.write(arrSalarios.get(i) + "\t");
					bw.write(arrEstudios.get(i) + "\t");
					bw.write(arrDescripciones.get(i) + "\t");
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
				objeto.put("Ubicación", arrUbicaciones.get(i));
				objeto.put("Salario", arrSalarios.get(i));
				objeto.put("Estudios", arrEstudios.get(i));
				objeto.put("Descripción", arrDescripciones.get(i));
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
