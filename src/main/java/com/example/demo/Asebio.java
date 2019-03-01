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

public class Asebio extends Scraper {

	public Asebio(String URL, String database, String collection, String fuente, String categoria, String tipo, String direccion) {
		super(URL, database, collection, fuente, categoria, tipo, direccion);
	}
	
	@Override
	public void scrap() throws IOException, LangDetectException {
		
		Document doc = Jsoup.connect(getURL()).get();
		
		Element editorial = doc.getElementById("editorial-main");
		Elements articulos_laterales = doc.getElementsByClass("row news-asebio-item");
		Elements articulos_inferiores = doc.getElementsByClass("inner");
		
		List<String> arrTitulos = new ArrayList<>();
		List<String> arrLinks = new ArrayList<>();
		List<String> arrDescripciones = new ArrayList<>();
		
		Elements titulos = editorial.getElementsByTag("h1");
		Elements links = editorial.getElementsByAttribute("href");
		
		for (Element titulo : titulos) {
			
			if (titulo.text().isEmpty())
				continue;
			else
				arrTitulos.add(titulo.text());
			
		}
		
		for (Element link : links) {
			
			if (link.attr("href").isEmpty())
				arrLinks.add("");
			else {
				if (link.attr("href").contains("www"))
					arrLinks.add(link.attr("href"));
				else
					arrLinks.add("www.asebio.com/es/" + link.attr("href"));
				
				try {
					
					Document doc2 = Jsoup.connect("http://www.asebio.com/es/" + link.attr("href")).get();
					
					Element contenido = doc2.getElementById("contenidos");
					
					Elements descripciones = contenido.getElementsByTag("p");
					
					String descripcion = "";
					
					for (Element desc : descripciones) {
						
						if (desc.text().isEmpty())
							descripcion += "";
						else
							descripcion += desc.text();
						
					}
					
					if (descripcion.isEmpty())
						arrDescripciones.add("");
					else
						arrDescripciones.add(descripcion);
				
				} catch (IllegalArgumentException e) {
					JOptionPane.showMessageDialog(null, "Error con los links para obtener las descripciones", null, JOptionPane.ERROR_MESSAGE);
				}
			}			
			
		}
		
		for (Element articulo : articulos_laterales) {
			
			Element titulo;
			
			if (articulo.children().size() < 2)
				titulo = articulo.child(0).child(0).child(0);
			else
				titulo = articulo.child(1).child(0); //class="col-md-8 col-sm-9 col-xs-12" --> a
			
			if (titulo.text().isEmpty())
				continue;
			else
				arrTitulos.add(titulo.text());
			
			if (titulo.attr("href").isEmpty())
				arrLinks.add("");
			else {
				if (titulo.attr("href").contains("www"))
					arrLinks.add(titulo.attr("href"));
				else
					arrLinks.add("www.asebio.com/es/" + titulo.attr("href"));
				
				try {
					
					Document doc2 = Jsoup.connect("http://www.asebio.com/es/" + titulo.attr("href")).get();
					
					Element contenido = doc2.getElementById("contenidos");
					
					Elements descripciones = contenido.getElementsByTag("p");
					
					String descripcion = "";
					
					for (Element desc : descripciones) {
						
						if (desc.text().isEmpty())
							descripcion += "";
						else
							descripcion += desc.text();
						
					}
					
					if (descripcion.isEmpty())
						arrDescripciones.add("");
					else
						arrDescripciones.add(descripcion);
				
				} catch (IllegalArgumentException e) {
					JOptionPane.showMessageDialog(null, "Error con los links para obtener las descripciones", null, JOptionPane.ERROR_MESSAGE);
				}
			}
			
		}
		
		for (Element seccion : articulos_inferiores) {
			
			Elements articulos = seccion.getElementsByClass("col-md-2 col-sm-4 ");
			Elements articulos2 = seccion.getElementsByClass("col-md-2 col-sm-4 hidden-sm");
			
			for (Element articulo : articulos) {
				
				Elements titulos2 = articulo.getElementsByTag("p");
				
				for (Element titulo : titulos2) {
					
					if (titulo.text().isEmpty())
						continue;
					else
						arrTitulos.add(titulo.text());
					
					if (titulo.child(0).attr("href").isEmpty())
						arrLinks.add("");
					else {
						if (titulo.child(0).attr("href").contains("www"))
							arrLinks.add(titulo.attr("href"));
						else
							arrLinks.add("www.asebio.com/es/" + titulo.child(0).attr("href"));
						
						try {
							
							Document doc2 = Jsoup.connect("http://www.asebio.com/es/" + titulo.child(0).attr("href")).get();
							
							Element contenido = doc2.getElementById("contenidos");
							
							Elements descripciones = contenido.getElementsByTag("p");
							
							String descripcion = "";
							
							for (Element desc : descripciones) {
								
								if (desc.text().isEmpty())
									descripcion += "";
								else
									descripcion += desc.text();
								
							}
							
							if (descripcion.isEmpty())
								arrDescripciones.add("");
							else
								arrDescripciones.add(descripcion);
						
						} catch (IllegalArgumentException e) {
							JOptionPane.showMessageDialog(null, "Error con los links para obtener las descripciones", null, JOptionPane.ERROR_MESSAGE);
						}
					}
					
				}
				
				
			}
			
			for (Element articulo : articulos2) {
				
				Element titulo = articulo.child(0);
				
				if (titulo.text().isEmpty())
					continue;
				else
					arrTitulos.add(titulo.text());
				
				if (titulo.child(0).attr("href").isEmpty())
					arrLinks.add("");
				else {
					if (titulo.child(0).attr("href").contains("www"))
						arrLinks.add(titulo.attr("href"));
					else
						arrLinks.add("www.asebio.com/es/" + titulo.child(0).attr("href"));
					
					try {
						
						Document doc2 = Jsoup.connect("http://www.asebio.com/es/" + titulo.child(0).attr("href")).get();
						
						Element contenido = doc2.getElementById("contenidos");
						
						Elements descripciones = contenido.getElementsByTag("p");
						
						String descripcion = "";
						
						for (Element desc : descripciones) {
							
							if (desc.text().isEmpty())
								descripcion += "";
							else
								descripcion += desc.text();
							
						}
						
						if (descripcion.isEmpty())
							arrDescripciones.add("");
						else
							arrDescripciones.add(descripcion);
					
					} catch (IllegalArgumentException e) {
						JOptionPane.showMessageDialog(null, "Error con los links para obtener las descripciones", null, JOptionPane.ERROR_MESSAGE);
					}
				}
								
			}
			
		}
				
		Detector detector;
		
		if (getQuiero_fichero().isSelected()) {
			
			try {
				
				//Creamos el fichero
				String rutaArchivo = getDireccion() + "." + getTipo();
				
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File(rutaArchivo)));
				bw.write("Título\tDescripción\tLink\tIdioma\tFuente\tCategoría");
				bw.newLine();
				
				for (int i = 0; i < arrTitulos.size(); i++) {
					
					detector = DetectorFactory.create();
					detector.append((String)arrDescripciones.get(i));
							
					bw.write(arrTitulos.get(i) + "\t");
					bw.write(arrDescripciones.get(i) + "\t");
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
