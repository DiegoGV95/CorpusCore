package com.example.demo;

import java.awt.EventQueue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

//@SpringBootApplication

public class WebScrapingWithSpringApplication {

	public static void main(String[] args) throws LangDetectException {
		SpringApplication.run(WebScrapingWithSpringApplication.class, args);
	}
	
	/*public static void abrirVentana() throws LangDetectException {
		
		DetectorFactory.loadProfile("/Users/diegogarcia-viana/Desktop/Java/WebScrapingWithSpring/profiles");
		EventQueue.invokeLater(new GraphicWindow());
		
	}*/
	
	/*public void run(String... args) throws Exception {
		
		DetectorFactory.loadProfile("/Users/diegogarcia-viana/Desktop/Java/WebScraping/profiles");
		
		EventQueue.invokeLater(new VentanaGrafica());
		
	}*/
	
}
