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

public class JobToday extends Scraper {
	
	public JobToday(String url, String database, String collection, String fuente, String categoria, String tipo, String direccion) {
		super(url, database, collection, fuente, categoria, tipo, direccion);
	}
	
	@Override
	public void scrap() throws IOException, LangDetectException {
		
		Document doc = Jsoup.connect(this.getURL()).get();
		
		Elements jobs = doc.getElementsByClass("_-packages-module-feed-web-src-components-FeedItem-__FeedItemJob___root");
		
		List<String> arrTitulos = new ArrayList<>();
		List<String> arrEmpresas = new ArrayList<>();
		List<String> arrDescripciones = new ArrayList<>();
		List<String> arrUbicaciones = new ArrayList<>();
		List<String> arrJornadas = new ArrayList<>();
		
		for (Element job : jobs) {
			
			Elements titulos = job.getElementsByClass("rn-14lw9ot rn-f1odvy rn-156q2ks rn-11yh6sk rn-buy8e9");
			Elements empresas = job.getElementsByClass("rn-13yce4e rn-fnigne rn-ndvcnb rn-gxnn5r rn-deolkf rn-1471scf rn-14xgk7a rn-a023e6 rn-o11vmf rn-ebii48 rn-16dba41 rn-t9a87b "
					+ "rn-1mnahxq rn-61z16t rn-11wrixw rn-d0pm55 rn-dnmrzs rn-11yh6sk rn-buy8e9 rn-wk8lta rn-9aemit rn-1mdbw0j rn-gy4na3 rn-bauka4 rn-1udbk01 rn-3s2u2q rn-qvutc0");
			Elements descripciones = job.getElementsByClass("rn-14lw9ot rn-11yh6sk rn-buy8e9");
			Elements ubicaciones = job.getElementsByClass("rn-13yce4e rn-fnigne rn-ndvcnb rn-gxnn5r rn-deolkf rn-pann0z rn-1471scf rn-16y2uox rn-1wbh5a2 rn-1ro0kt6 rn-14xgk7a rn-a023e6 "
					+ "rn-o11vmf rn-ebii48 rn-16dba41 rn-14yzgew rn-1mnahxq rn-p1pxzi rn-1f6r7vd rn-7o8qx1 rn-dnmrzs rn-11yh6sk rn-buy8e9 rn-wk8lta rn-9aemit rn-1mdbw0j rn-gy4na3 rn-bauka4 rn-1udbk01 rn-3s2u2q rn-qvutc0");
			Elements jornadas = job.getElementsByClass("rn-1oszu61 rn-1efd50x rn-14skgim rn-rull8r rn-mm0ijv rn-13yce4e rn-fnigne rn-ndvcnb rn-gxnn5r rn-deolkf rn-6koalj rn-1mlwlqe rn-18u37iz rn-1wbh5a2 rn-1w6e6rj rn-61z16t "
					+ "rn-p1pxzi rn-11wrixw rn-l71dzp rn-ifefl9 rn-bcqeeo rn-wk8lta rn-9aemit rn-1mdbw0j rn-gy4na3 rn-bnwqim rn-1lgpqti");
			
			if (titulos.isEmpty())
				continue;
			if (empresas.isEmpty())
				arrEmpresas.add("");
			if (descripciones.isEmpty())
				arrDescripciones.add("");
			if (ubicaciones.isEmpty())
				arrUbicaciones.add("");
			if (jornadas.isEmpty())
				arrJornadas.add("");
			
			for (int i = 0; i < titulos.size(); i++) {
				
				if (titulos.get(i).text().isEmpty())
					arrTitulos.add("");
				else
					arrTitulos.add(titulos.get(i).text());
				
				if (empresas.get(i).text().isEmpty())
					arrEmpresas.add("");
				else
					arrEmpresas.add(empresas.get(i).text());
				
				if (descripciones.get(i).text().isEmpty())
					arrDescripciones.add("");
				else
					arrDescripciones.add(descripciones.get(i).text());
				
				if (ubicaciones.get(i).text().isEmpty())
					arrUbicaciones.add("");
				else
					arrUbicaciones.add(ubicaciones.get(i).text());
				
				if (jornadas.get(i).text().isEmpty())
					arrJornadas.add("");
				else
					arrJornadas.add(jornadas.get(i).text());
				
			}
			
		}
						
		Detector detector;
		
		if (getQuiero_fichero().isSelected()) {
			
			try {
				
				//Creamos el fichero
				String rutaArchivo = getDireccion() + "." + getTipo();
				
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File(rutaArchivo)));
				bw.write("Título\tEmpresa\tDescripción\tTipo de jornada\tUbicación\tIdioma\tFuente\tCategoría");
				bw.newLine();
				
				for (int i = 0; i < arrTitulos.size(); i++) {
					
					detector = DetectorFactory.create();
					detector.append((String)arrTitulos.get(i));
							
					bw.write(arrTitulos.get(i) + "\t");
					bw.write(arrEmpresas.get(i) + "\t");
					bw.write(arrDescripciones.get(i) + "\t");
					bw.write(arrJornadas.get(i) + "\t");
					bw.write(arrUbicaciones.get(i) + "\t");
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
			
			DBObject queryDescripcion = new QueryBuilder().start().put("Link").is(arrDescripciones.get(i)).get();
			
			DBCursor resultadoDescripcion = collection.find(queryDescripcion);
			
			if (resultadoDescripcion.count() == 0) {
				
				detector = DetectorFactory.create();
				detector.append((String)arrTitulos.get(i));
				
				BasicDBObject objeto = new BasicDBObject();
				
				objeto.put("Título", arrTitulos.get(i));
				objeto.put("Empresa", arrEmpresas.get(i));
				objeto.put("Descripción", arrDescripciones.get(i));
				objeto.put("Tipo de jornada", arrJornadas.get(i));
				objeto.put("Ubicación", arrUbicaciones.get(i));
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