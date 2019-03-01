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

public class Infojobs extends Scraper {
	
	public Infojobs(String url, String database, String collection, String fuente, String categoria, String tipo, String direccion) {
		super(url, database, collection, fuente, categoria, tipo, direccion);
	}
	
	@Override
	public void scrap() throws IOException, LangDetectException {
		
		Document doc = Jsoup.connect(this.getURL()).get();

		Elements empleos = doc.getElementsByClass("content-top");
		
		List<String> arrTitulos = new ArrayList<>();
		List<String> arrCompañias = new ArrayList<>();
		List<String> arrLinks = new ArrayList<>();
		List<String> arrUbicaciones = new ArrayList<>();
		List<String> arrDescripciones = new ArrayList<>();
		List<String> arrSalarios = new ArrayList<>();
		List<String> arrFechas = new ArrayList<>();
		List<String> arrContratos = new ArrayList<>();
		List<String> arrJornadas = new ArrayList<>();
		
		for (Element empleo : empleos) {
			
			Elements titulos = empleo.getElementsByTag("h2");
			Elements compañias = empleo.getElementsByTag("h3");
			
			if (titulos.isEmpty())
				continue;
			if (compañias.isEmpty())
				arrCompañias.add("");
				
			
			for (int i = 0; i < titulos.size(); i++) {
				
				if (titulos.get(i).text().isEmpty())
					arrTitulos.add("");
				else
					arrTitulos.add(titulos.get(i).text());
				
				if (titulos.get(i).child(0).attr("href").isEmpty())
					arrLinks.add("");
				else {
					if (!titulos.get(i).child(0).attr("href").contains("https"))
						arrLinks.add("https:" + titulos.get(i).child(0).attr("href"));
					else
						arrLinks.add(titulos.get(i).child(0).attr("href"));
				}
				if (compañias.get(i).text().isEmpty())
					arrCompañias.add("");
				else
					arrCompañias.add(compañias.get(i).text());
				
				Element ubicacion = compañias.get(i).nextElementSibling().child(0);
				
				if (ubicacion.text().isEmpty())
					arrUbicaciones.add("");
				else
					arrUbicaciones.add(ubicacion.text());
				
				Element fecha = ubicacion.nextElementSibling().child(0);
				
				if (fecha.text().isEmpty())
					arrFechas.add("");
				else
					arrFechas.add(fecha.text());
				
				Element descripcion = ubicacion.parent().nextElementSibling();
				
				if (descripcion.text().isEmpty())
					arrDescripciones.add("");
				else
					arrDescripciones.add(descripcion.text());
				
				Element contrato = descripcion.nextElementSibling().child(0);
				
				if (contrato.text().isEmpty())
					arrContratos.add("");
				else
					arrContratos.add(contrato.text());
				
				Element jornada = contrato.nextElementSibling();
				
				if (jornada.text().isEmpty())
					arrJornadas.add("");
				else
					arrJornadas.add(jornada.text());
				
				Element salario = jornada.nextElementSibling();
				
				if (salario.text().isEmpty())
					arrSalarios.add("");
				else
					arrSalarios.add(salario.text());
				
			}
			
		}
											
		Detector detector;
		
		if (getQuiero_fichero().isSelected()) {
			
			try {
				
				//Creamos el fichero
				String rutaArchivo = getDireccion() + "." + getTipo();
				
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File(rutaArchivo)));
				bw.write("Título\tDescripción\tCompañía\tUbicación\tContrato\tJornada\tSalario\tFecha\tLink\tIdioma\tFuente\tCategoría");
				bw.newLine();
				
				for (int i = 0; i < arrTitulos.size(); i++) {
					
					detector = DetectorFactory.create();
					detector.append((String)arrDescripciones.get(i));
															
					bw.write(arrTitulos.get(i) + "\t");
					bw.write(arrDescripciones.get(i) + "\t");
					bw.write(arrCompañias.get(i) + "\t");
					bw.write(arrUbicaciones.get(i) + "\t");
					bw.write(arrContratos.get(i) + "\t");
					bw.write(arrJornadas.get(i) + "\t");
					bw.write(arrSalarios.get(i) + "\t");
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
		DB database = mongo.getDB(getDatabase());
		DBCollection collection = database.getCollection(getCollection());
		
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
				objeto.put("Contrato", arrContratos.get(i));
				objeto.put("Jornada", arrJornadas.get(i));
				objeto.put("Salario", arrSalarios.get(i));
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