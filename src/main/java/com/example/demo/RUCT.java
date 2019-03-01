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

public class RUCT extends Scraper {
	
	public RUCT(String url, String database, String collection, String fuente, String categoria, String tipo, String direccion) {
		super(url, database, collection, fuente, categoria, tipo, direccion);
	}
	
	@Override
	public void scrap() throws IOException, LangDetectException {
		
		Document doc = Jsoup.connect(this.getURL()).get();
				
		Element tabla = doc.getElementById("estudio");
		Element body = tabla.child(1);
		
		Elements estudios = tabla.getElementsByTag("tr");
		
		List<String> arrTitulos = new ArrayList<>();
		List<String> arrLinks = new ArrayList<>();
		List<String> arrNiveles = new ArrayList<>();
		List<String> arrCodigos = new ArrayList<>();
		List<String> arrEstados = new ArrayList<>();
		List<String> arrUniversidades = new ArrayList<>();
		
		for (Element estudio : estudios) {
			
			Element codigo = estudio.child(0);
			Element titulo = estudio.child(1);
			Element universidad = estudio.child(2);
			Element nivel = estudio.child(3);
			Element estado = estudio.child(4);
			
			arrCodigos.add(codigo.text());
			arrTitulos.add(titulo.text());
			arrLinks.add("https:///www.educacion.gob.es" + titulo.child(0).attr("href"));
			arrUniversidades.add(universidad.text());
			arrNiveles.add(nivel.text());
			arrEstados.add(estado.text());
			
		}
		
		Detector detector;
		
		if (getQuiero_fichero().isSelected()) {
			
			try {
				
				//Creamos el fichero
				String rutaArchivo = getDireccion() + "." + getTipo();
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File(rutaArchivo)));
				bw.write("Título\tCódigo\tUniversidad\tNivel Académico\tEstado\tLink\tIdioma\tFuente\tCategoría");
				bw.newLine();
				
				for (int i = 0; i < arrTitulos.size(); i++) {
					
					detector = DetectorFactory.create();
					detector.append((String)arrTitulos.get(i));
							
					bw.write(arrTitulos.get(i) + "\t");
					bw.write(arrCodigos.get(i) + "\t");
					bw.write(arrUniversidades.get(i) + "\t");
					bw.write(arrNiveles.get(i) + "\t");
					bw.write(arrEstados.get(i) + "\t");
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
				detector.append((String)arrTitulos.get(i));
				
				BasicDBObject objeto = new BasicDBObject();
				
				objeto.put("Título", arrTitulos.get(i));
				objeto.put("Código", arrCodigos.get(i));
				objeto.put("Universidad", arrUniversidades.get(i));
				objeto.put("Nivel académico", arrNiveles.get(i));
				objeto.put("Estado", arrEstados.get(i));
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
