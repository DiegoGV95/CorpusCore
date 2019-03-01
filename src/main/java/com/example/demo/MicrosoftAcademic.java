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

public class MicrosoftAcademic extends Scraper {
	
	public MicrosoftAcademic(String url, String database, String collection, String fuente, String categoria, String tipo, String direccion) {
		super(url, database, collection, fuente, categoria, tipo, direccion);
	}
	
	@Override
	public void scrap() throws IOException, LangDetectException {
		
		Document doc = Jsoup.connect(this.getURL()).get();
		
		//Elements articulos = doc.getElementsByClass("paper paper-mode-2 card");
		Elements articulos = doc.getElementsByTag("article");
		
		List<String> arrTitulos = new ArrayList<>();
		List<String> arrFechas = new ArrayList<>();
		List<String> arrAutores = new ArrayList<>();
		List<String> arrCampos = new ArrayList<>();
		List<String> arrayCampos = new ArrayList<>();
		
		for (Element articulo : articulos) {
			
			Elements titulos = doc.getElementsByClass("blue-title");
			Elements fechas = doc.getElementsByClass("paper-year");
			Elements autores = doc.getElementsByClass("paper-author-affiliation");
			Elements campos = doc.getElementsByClass("paper-fieldOfStudy");
			
			if (titulos.isEmpty())
				continue;
			if (fechas.isEmpty())
				arrFechas.add("");
			if (autores.isEmpty())
				arrAutores.add("");
			if (campos.isEmpty())
				arrCampos.add("");
			
			for (Element titulo : titulos) {
				arrTitulos.add(titulo.text());
			}
			
			for (Element fecha : fechas) {
				arrFechas.add(fecha.attr("title"));
			}
			
			for (Element autor : autores) {
				arrAutores.add(autor.child(0).text());
			}
			
			for (Element campo : campos) {
				
				String cadena = "";
				
				Elements cs = campo.getElementsByTag("li");
				for (int j = 0; j < cs.size(); j++) {
					if (j < cs.size() - 1)
						cadena += cs.get(j) + ", ";
					else
						cadena += cs.get(j);
				}
				if (cadena.isEmpty())
					arrCampos.add("");
				else
					arrCampos.add(cadena);
			}
			
		}
										
		Detector detector;
		
		if (getQuiero_fichero().isSelected()) {
			
			try {
				
				//Creamos el fichero
				String rutaArchivo = getDireccion() + "." + getTipo();
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File(rutaArchivo)));
				bw.write("Título\tFecha\tAutor\tCampos de estudio\tIdioma\tFuente\tCategoría");
				bw.newLine();
				
				for (int i = 0; i < arrTitulos.size(); i++) {
					
					detector = DetectorFactory.create();
					detector.append((String)arrTitulos.get(i));
											
					bw.write(arrTitulos.get(i) + "\t");
					bw.write(arrFechas.get(i).replace("-", "/") + "\t");
					bw.write(arrAutores.get(i) + "\t");
					bw.write(arrCampos.get(i) + "\t");
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
			
			DBObject queryTitulo = new QueryBuilder().start().put("Título").is(arrTitulos.get(i)).get();
			
			DBCursor resultadoTitulo = collection.find(queryTitulo);
			
			if (resultadoTitulo.count() == 0) {
				
				detector = DetectorFactory.create();
				detector.append((String)arrTitulos.get(i));
				
				BasicDBObject objeto = new BasicDBObject();
				
				objeto.put("Título", arrTitulos.get(i));
				objeto.put("Fecha", arrFechas.get(i).replace("-", "/"));
				objeto.put("Autor", arrAutores.get(i));
				objeto.put("Campos de estudio", arrCampos.get(i));
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
