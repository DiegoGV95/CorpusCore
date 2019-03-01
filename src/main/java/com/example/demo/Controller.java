package com.example.demo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.cybozu.labs.langdetect.LangDetectException;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Controller {
	
	private String categoria;
	private String fuente;
	private String URL;
	private String database;
	private String collection;
	private String directorio;
	private String nombre;
	private JCheckBox quiero_fichero;
		
	public void scrap() throws IOException, LangDetectException {
		
		String tipo = "tsv";
		
		File archivo = new File(getDirectorio());
		if (!archivo.exists())
			archivo.mkdir();
				
		int opcion = getFuente(getURL());
		
		Scraper scraper = new Scraper();
		
		switch (opcion) {
		
			case 0:
				scraper = new CornerJob(getURL(), getDatabase(), getCollection(), getFuente(), getCategoria(), tipo, getDirectorio() + "/" + getNombre());
				scraper.setQuiero_fichero(getQuiero_fichero());
				break;
			case 1:
				scraper = new TechnoJobs(getURL(), getDatabase(), getCollection(), getFuente(), getCategoria(), tipo, getDirectorio() + "/" + getNombre());
				scraper.setQuiero_fichero(getQuiero_fichero());
				break;
			case 2: 
				scraper = new NatureJobs(getURL(), getDatabase(), getCollection(), getFuente(), getCategoria(), tipo, getDirectorio() + "/" + getNombre());
				scraper.setQuiero_fichero(getQuiero_fichero());
				break;		
			case 3:
				scraper = new ScienceJobs(getURL(), getDatabase(), getCollection(), getFuente(), getCategoria(), tipo, getDirectorio() + "/" + getNombre());
				scraper.setQuiero_fichero(getQuiero_fichero());
				break;
			case 4:
				scraper = new Euraxess(getURL(), getDatabase(), getCollection(), getFuente(), getCategoria(), tipo, getDirectorio() + "/" + getNombre());
				scraper.setQuiero_fichero(getQuiero_fichero());
				break;
			case 5:
				scraper = new CDTI(getURL(), getDatabase(), getCollection(), getFuente(), getCategoria(), tipo, getDirectorio() + "/" + getNombre());
				scraper.setQuiero_fichero(getQuiero_fichero());
				break;
			case 6:
				scraper = new MicrosoftAcademic(getURL(), getDatabase(), getCollection(), getFuente(), getCategoria(), tipo, getDirectorio() + "/" + getNombre());
				scraper.setQuiero_fichero(getQuiero_fichero());
				break;
			case 7: 
				scraper = new Infojobs(getURL(), getDatabase(), getCollection(), getFuente(), getCategoria(), tipo, getDirectorio() + "/" + getNombre());
				scraper.setQuiero_fichero(getQuiero_fichero());
				break;
			case 8:
				scraper = new Linkedin(getURL(), getDatabase(), getCollection(), getFuente(), getCategoria(), tipo, getDirectorio() + "/" + getNombre());
				scraper.setQuiero_fichero(getQuiero_fichero());
				break;
			case 9:
				scraper = new Indeed(getURL(), getDatabase(), getCollection(), getFuente(), getCategoria(), tipo, getDirectorio() + "/" + getNombre());
				scraper.setQuiero_fichero(getQuiero_fichero());
				break;
			case 10:
				scraper = new SemanticScholar(getURL(), getDatabase(), getCollection(), getFuente(), getCategoria(), tipo, getDirectorio() + "/" + getNombre());
				scraper.setQuiero_fichero(getQuiero_fichero());
				break;
			case 11:
				scraper = new SEPE(getURL(), getDatabase(), getCollection(), getFuente(), getCategoria(), tipo, getDirectorio() + "/" + getNombre());
				scraper.setQuiero_fichero(getQuiero_fichero());
				break;
			case 12:
				scraper = new JobToday(getURL(), getDatabase(), getCollection(), getFuente(), getCategoria(), tipo, getDirectorio() + "/" + getNombre());
				scraper.setQuiero_fichero(getQuiero_fichero());
				break;
			case 31:
				scraper = new EmpleoPost(getURL(), getDatabase(), getCollection(), getFuente(), getCategoria(), tipo, getDirectorio() + "/" + getNombre());
				scraper.setQuiero_fichero(getQuiero_fichero());
				break;
			case 32:
				scraper = new Monster(getURL(), getDatabase(), getCollection(), getFuente(), getCategoria(), tipo, getDirectorio() + "/" + getNombre());
				scraper.setQuiero_fichero(getQuiero_fichero());
				break;
			case 13:
				scraper = new EURES(getURL(), getDatabase(), getCollection(), getFuente(), getCategoria(), tipo, getDirectorio() + "/" + getNombre());
				scraper.setQuiero_fichero(getQuiero_fichero());
				break;
				
			case 14:
				scraper = new Coursera(getURL(), getDatabase(), getCollection(), getFuente(), getCategoria(), tipo, getDirectorio() + "/" + getNombre());
				scraper.setQuiero_fichero(getQuiero_fichero());
				break;
			case 15:
				scraper = new Miriadax(getURL(), getDatabase(), getCollection(), getFuente(), getCategoria(), tipo, getDirectorio() + "/" + getNombre());
				scraper.setQuiero_fichero(getQuiero_fichero());
				break;
			case 16:
				scraper = new Udacity(getURL(), getDatabase(), getCollection(), getFuente(), getCategoria(), tipo, getDirectorio() + "/" + getNombre());
				scraper.setQuiero_fichero(getQuiero_fichero());
				break;
			case 17:
				scraper = new OCW(getURL(), getDatabase(), getCollection(), getFuente(), getCategoria(), tipo, getDirectorio() + "/" + getNombre());
				scraper.setQuiero_fichero(getQuiero_fichero());
				break;				
			case 18:
				scraper = new RUCT(getURL(), getDatabase(), getCollection(), getFuente(), getCategoria(), tipo, getDirectorio() + "/" + getNombre());
				scraper.setQuiero_fichero(getQuiero_fichero());
				break;
			case 19:
				scraper = new Edx(getURL(), getDatabase(), getCollection(), getFuente(), getCategoria(), tipo, getDirectorio() + "/" + getNombre());
				scraper.setQuiero_fichero(getQuiero_fichero());
				break;
			case 20:
				scraper = new Innovaticias(getURL(), getDatabase(), getCollection(), getFuente(), getCategoria(), tipo, getDirectorio() + "/" + getNombre());
				scraper.setQuiero_fichero(getQuiero_fichero());
				break;
			case 34:
				scraper = new CRUE(getURL(), getDatabase(), getCollection(), getFuente(), getCategoria(), tipo, getDirectorio() + "/" + getNombre());
				scraper.setQuiero_fichero(getQuiero_fichero());
				break;
				
			case 21:
				scraper = new ElMundo(getURL(), getDatabase(), getCollection(), getFuente(), getCategoria(), tipo, getDirectorio() + "/" + getNombre());
				scraper.setQuiero_fichero(getQuiero_fichero());
				break;
			case 22:
				scraper = new ABC(getURL(), getDatabase(), getCollection(), getFuente(), getCategoria(), tipo, getDirectorio() + "/" + getNombre());
				scraper.setQuiero_fichero(getQuiero_fichero());
				break;
			case 23:
				scraper = new Minutos(getURL(), getDatabase(), getCollection(), getFuente(), getCategoria(), tipo, getDirectorio() + "/" + getNombre());
				scraper.setQuiero_fichero(getQuiero_fichero());
				break;
			case 24:
				scraper = new ElPais(getURL(), getDatabase(), getCollection(), getFuente(), getCategoria(), tipo, getDirectorio() + "/" + getNombre());
				scraper.setQuiero_fichero(getQuiero_fichero());
				break;
			case 35:
				scraper = new TechCrunch(getURL(), getDatabase(), getCollection(), getFuente(), getCategoria(), tipo, getDirectorio() + "/" + getNombre());
				scraper.setQuiero_fichero(getQuiero_fichero());
				break;
			case 26:
				scraper = new FarmaIndustria(getURL(), getDatabase(), getCollection(), getFuente(), getCategoria(), tipo, getDirectorio() + "/" + getNombre());
				scraper.setQuiero_fichero(getQuiero_fichero());
				break;	
			case 27:
				scraper = new PMFarma(getURL(), getDatabase(), getCollection(), getFuente(), getCategoria(), tipo, getDirectorio() + "/" + getNombre());
				scraper.setQuiero_fichero(getQuiero_fichero());
				break;
			case 33:
				scraper = new Asebio(getURL(), getDatabase(), getCollection(), getFuente(), getCategoria(), tipo, getDirectorio() + "/" + getNombre());
				scraper.setQuiero_fichero(getQuiero_fichero());
				break;
				
			case 28:
				scraper = new Kickstarter(getURL(), getDatabase(), getCollection(), getFuente(), getCategoria(), tipo, getDirectorio() + "/" + getNombre());
				scraper.setQuiero_fichero(getQuiero_fichero());
				break;
			case 29:
				scraper = new Indiegogo(getURL(), getDatabase(), getCollection(), getFuente(), getCategoria(), tipo, getDirectorio() + "/" + getNombre());
				scraper.setQuiero_fichero(getQuiero_fichero());
				break;
			case 30:
				scraper = new Zenodo(getURL(), getDatabase(), getCollection(), getFuente(), getCategoria(), tipo, getDirectorio() + "/" + getNombre());
				scraper.setQuiero_fichero(getQuiero_fichero());
				break;
		
		}
		
		try {
			
			scraper.scrap();
			showResponse(scraper);
			
		} catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(null, "Error con la URL introducida", null, JOptionPane.ERROR_MESSAGE);
		}
		
	}
	
	public int getFuente(String url) {
		
		int retorno;
		
		//Mercado Laboral
		if (url.contains("cornerjob")) 
			retorno = 0;
		else if (url.contains("technojobs")) 
			retorno = 1;
		else if (url.contains("naturecareers")) 
			retorno = 2;
		else if (url.contains("sciencecareers")) 
			retorno = 3;
		else if (url.contains("euraxess")) 
			retorno = 4;
		else if (url.contains("cdti")) 
			retorno = 5;
		else if (url.contains("microsoft")) 
			retorno = 6;
		else if (url.contains("infojobs")) 
			retorno = 7;
		else if (url.contains("linkedin")) 
			retorno = 8;
		else if (url.contains("indeed")) 
			retorno = 9;
		else if (url.contains("semanticscholar")) 
			retorno = 10;
		else if (url.contains("empleate")) 
			retorno = 11;
		else if (url.contains("jobtoday")) 
			retorno = 12;
		else if (url.contains("eures"))
			retorno = 13;
		else if (url.contains("empleopost"))
			retorno = 31;
		else if (url.contains("monster"))
			retorno = 32;
		
		//Oferta Educativa
		else if (url.contains("coursera")) 
			retorno = 14;
		else if (url.contains("miriadax")) 
			retorno = 15;
		else if (url.contains("udacity")) 
			retorno = 16;
		else if(url.contains("ocw")) 
			retorno = 17;
		else if (url.contains("educacion.gob")) 
			retorno = 18;
		else if (url.contains("edx")) 
			retorno = 19;
		else if (url.contains("innovaticias"))
			retorno = 20;
		else if (url.contains("crue"))
			retorno = 34;

		
		//Noticias e Innovación Divulgativa
		else if (url.contains("elmundo")) 
			retorno = 21;
		else if (url.contains("abc")) 
			retorno = 22;
		else if (url.contains("20minutos"))
			retorno = 23;
		else if (url.contains("elpais"))
			retorno = 24;
		else if (url.contains("techcrunch"))
			retorno = 25;
		
		//Salud
		else if (url.contains("farmaindustria")) 
			retorno = 26;
		else if (url.contains("pmfarma"))
			retorno = 27;
		else if (url.contains("asebio"))
			retorno = 33;
		
		//Productos e Iniciativas
		else if (url.contains("kickstarter"))
			retorno = 28;
		else if (url.contains("indiegogo"))
			retorno = 29;
		else if (url.contains("zenodo"))
			retorno = 30;
				
		else
			retorno = -1;
		
		return retorno;
		
	}
	
	public void showResponse(Scraper scraper) {
		
		JOptionPane.showMessageDialog(null, scraper.getRespuesta(), null, JOptionPane.INFORMATION_MESSAGE);
		
	}
	
	public boolean comprobar(String url, String fuente) {
		
		if (!fuente.isEmpty()) {
			
			if (fuente.equals("SEPE")) {
				
				if (url.contains("empleate"))
					return true;
				
			}
			
			else {
				
				String source = fuente.toLowerCase();
				String palabras[] = source.split(" ");
				
				for (String palabra : palabras) {
					if (url.contains(palabra))
						return true;
				}
				
			}

		}
		
		return false;
		
	}
	
	public boolean comprobarDatabase(String database_nueva, String database_existente) {
		
		if (database_nueva.equals(database_existente))
			return true;
		return false;
		
	}
	
	public boolean comprobarColeccion(String collection_nueva, String collection_existente) {
		
		if (collection_nueva.equals(collection_existente))
			return true;
		return false;
		
	}

}
