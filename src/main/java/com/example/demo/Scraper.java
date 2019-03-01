package com.example.demo;

import java.io.IOException;

import javax.swing.JCheckBox;

import org.hibernate.validator.constraints.URL;
import com.cybozu.labs.langdetect.LangDetectException;
import lombok.*;

@Data
@NoArgsConstructor

public class Scraper {
	
	private String URL;
	private String database;
	private String collection;
	private String fuente;
	private String categoria;
	private String tipo;
	private String direccion;
	private String respuesta;	
	private JCheckBox quiero_fichero;
	
	public Scraper(String URL, String database, String collection, String fuente, String categoria, String tipo, String direccion) {
		this.URL = URL;
		this.database = database;
		this.collection = collection;
		this.fuente = fuente;
		this.categoria = categoria;
		this.tipo = tipo;
		this.direccion = direccion;
	}
	
	public void scrap() throws IOException, LangDetectException {};

}
