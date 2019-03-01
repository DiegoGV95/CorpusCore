package com.example.demo;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneLayout;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoSocketException;
import com.mongodb.MongoSocketOpenException;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import javax.swing.JComboBox;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import java.awt.Label;
import java.awt.Color;
import java.awt.TextField;
import javax.swing.JButton;
import java.awt.Panel;
import javax.swing.JCheckBox;
import java.awt.CardLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import net.miginfocom.swing.MigLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

@SpringBootApplication
public class GraphicWindow {
	@Setter
	@Getter
	private static String URL;
	@Setter
	@Getter
	private static String direccion;
	@Setter
	@Getter
	private static String nombre;
	@Setter
	@Getter
	private static String database;
	@Setter
	@Getter
	private static String collection;
	
	private Vector<TextField> arrURL = new Vector();
	private Vector<TextField> arrDatabases = new Vector();
	private Vector<TextField> arrCollections = new Vector();
	private Vector<TextField> arrDirectorios = new Vector();
	private Vector<TextField> arrNombres = new Vector();
	
	private Vector<JComboBox> arrCategorias = new Vector();
	private Vector<JComboBox> arrFuentes = new Vector();
	private Vector<JComboBox> arrDatabasesCB = new Vector();
	private Vector<JComboBox> arrCollectionsCB = new Vector();
	
	private Vector<JRadioButton> arrBotonesNewDatabase = new Vector();
	private Vector<JRadioButton> arrBotonesExistingDatabase = new Vector();
	private Vector<JRadioButton> arrBotonesNewCollection = new Vector();
	private Vector<JRadioButton> arrBotonesExistingCollection = new Vector();
	
	private Vector<JCheckBox> arrQuieroFichero = new Vector();
	//Vector donde se guardarán las casillas de cada una de las fuentes 
	private Vector<JCheckBox> arrSelecciones = new Vector();

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GraphicWindow window = new GraphicWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @throws IOException 
	 * @throws UnknownHostException 
	 *  
	 */
	public GraphicWindow() throws LangDetectException, UnknownHostException, IOException {
		
		DetectorFactory.loadProfile("/Users/diegogarcia-viana/Desktop/Trabajo/Java/WebScrapingWithSpring/langdetect-09-13-2011/profiles");
		//DetectorFactory.loadProfile("/Users/diegogarcia-viana/Desktop/Java/WebScraping/profiles");

		initialize();
		
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	private void initialize() throws UnknownHostException, IOException {		
		
		frame = new JFrame();
		frame.setBounds(150, 200, 1100, 350);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBounds(0, 0, 900, 150);
		
		frame.getContentPane().add(scrollPane);
		
		JPanel panel = new JPanel();
		scrollPane.setViewportView(panel);
		
		JButton scrapBoton = new JButton("Scrap");
		scrapBoton.setBounds(950, 35, 117, 29);
		frame.getContentPane().add(scrapBoton);
		
		JButton borrarBoton = new JButton("Borrar");
		borrarBoton.setBounds(950, 76, 117, 29);
		frame.getContentPane().add(borrarBoton);
		
		JButton añadirBoton = new JButton("+");
		añadirBoton.setBounds(985, 116, 40, 29);
		frame.getContentPane().add(añadirBoton);
		
		/*JButton quitarBoton = new JButton("-");
		quitarBoton.setBounds(985, 156, 40, 29);
		frame.getContentPane().add(quitarBoton);*/
		
		JCheckBox seleccionarTodo = new JCheckBox();
		seleccionarTodo.setBounds((int)scrapBoton.getLocation().getX() - 30, (int)scrapBoton.getLocation().getY() - 30, 25, 25);
		frame.getContentPane().add(seleccionarTodo);
		
		JLabel textoSeleccionarTodo = new JLabel("Seleccionar todos");
		textoSeleccionarTodo.setBounds((int)seleccionarTodo.getLocation().getX() + 28, (int)seleccionarTodo.getLocation().getY() + 8, 175, 10);
		frame.getContentPane().add(textoSeleccionarTodo);
		
		ImageIcon logoTaiger = new ImageIcon("/Users/diegogarcia-viana/Desktop/Trabajo/Estado del proyecto/Logo Taiger.png");
		JLabel taiger = new JLabel();
		taiger.setIcon(logoTaiger);
		taiger.setBounds((int)añadirBoton.getLocation().getX(), (int)añadirBoton.getLocation().getY() + 40, 40, 40);
		frame.getContentPane().add(taiger);
		
		ImageIcon logoMinisterio = new ImageIcon("/Users/diegogarcia-viana/Desktop/Trabajo/Estado del proyecto/Logo Ministerio.gif");
		JLabel ministerio = new JLabel();
		ministerio.setIcon(logoMinisterio);
		ministerio.setBounds((int)taiger.getLocation().getX() - 50, (int)taiger.getLocation().getY() + 50, 135, 40);
		frame.getContentPane().add(ministerio);
					
		JComboBox categorias = new JComboBox();
		categorias.setModel(new DefaultComboBoxModel(new String[] {"", "Mercado Laboral", "Productos e Iniciativas", "Noticias e Innovación Divulgativa", "Oferta Formativa Educativa", "Salud"}));
		arrCategorias.add(categorias);
					
		JComboBox fuentes = new JComboBox();
		arrFuentes.add(fuentes);
	
		JLabel url_1 = new JLabel("		URL");
				
		TextField introURL = new TextField();
		introURL.setForeground(Color.BLACK);
		introURL.setBackground(Color.WHITE);
		arrURL.add(introURL);
		
		JLabel database_1 = new JLabel("		Base de datos");
		
		TextField introDatabase = new TextField();
		introDatabase.setForeground(Color.BLACK);
		introDatabase.setBackground(Color.WHITE);
		introDatabase.setVisible(true);
		arrDatabases.add(introDatabase);
		
		JRadioButton newDatabase = new JRadioButton("Nueva");
		newDatabase.setSelected(true);
		arrBotonesNewDatabase.add(newDatabase);
		
		JRadioButton existingDatabase = new JRadioButton("Existente");
		arrBotonesExistingDatabase.add(existingDatabase);
		
		JComboBox listaDatabases = new JComboBox();
		listaDatabases.setVisible(false);
		arrDatabasesCB.add(listaDatabases);
		
		TextField introCollection = new TextField();
		introCollection.setForeground(Color.BLACK);
		introCollection.setBackground(Color.WHITE);
		introCollection.setVisible(true);
		arrCollections.add(introCollection);
		
		JLabel collection_1 = new JLabel("		Colección");
		
		JRadioButton newCollection = new JRadioButton("Nueva");
		newCollection.setSelected(true);
		arrBotonesNewCollection.add(newCollection);
		
		JRadioButton existingCollection = new JRadioButton("Existente");
		arrBotonesExistingCollection.add(existingCollection);
		
		JComboBox listaCollections = new JComboBox();
		listaCollections.setVisible(false);
		arrCollectionsCB.add(listaCollections);
		
		JLabel directorio_1 = new JLabel("Ruta directorio");
		directorio_1.setVisible(false);
		
		TextField introDirectorio = new TextField();
		introDirectorio.setForeground(Color.BLACK);
		introDirectorio.setBackground(Color.WHITE);
		introDirectorio.setVisible(false);
		arrDirectorios.add(introDirectorio);
		
		JCheckBox quiero_fichero = new JCheckBox("Obtener fichero tsv");
		arrQuieroFichero.add(quiero_fichero);
		
		JLabel nombre_1 = new JLabel("Nombre del fichero");
		nombre_1.setVisible(false);
		
		TextField introNombre = new TextField();
		introNombre.setForeground(Color.BLACK);
		introNombre.setBackground(Color.WHITE);
		introNombre.setVisible(false);
		arrNombres.add(introNombre);
		
		JCheckBox seleccionar = new JCheckBox("Seleccionar");
		arrSelecciones.add(seleccionar);
		
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		
		//Columnas
		GroupLayout.SequentialGroup sequentialH = layout.createSequentialGroup();
		//Filas
		GroupLayout.SequentialGroup sequentialV = layout.createSequentialGroup();
		
		GroupLayout.ParallelGroup parallelH1 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		parallelH1.addComponent(url_1);
		parallelH1.addComponent(database_1);
		parallelH1.addComponent(collection_1);
		parallelH1.addComponent(quiero_fichero);
		
		GroupLayout.ParallelGroup parallelH2 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		GroupLayout.SequentialGroup sequentialH2 = layout.createSequentialGroup();
		GroupLayout.ParallelGroup parallelH2_1 = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
		GroupLayout.ParallelGroup parallelH2_2 = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
		
		parallelH2_1.addComponent(categorias);
		parallelH2_1.addComponent(newDatabase);
		parallelH2_1.addComponent(newCollection);
		parallelH2_2.addComponent(fuentes);
		parallelH2_2.addComponent(existingDatabase);
		parallelH2_2.addComponent(existingCollection);
		parallelH2_2.addComponent(directorio_1);
		parallelH2_2.addComponent(nombre_1);
		
		sequentialH2.addGroup(parallelH2_1);
		sequentialH2.addGroup(parallelH2_2);
		
		parallelH2.addGroup(sequentialH2);
		
		GroupLayout.ParallelGroup parallelH3 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);

		parallelH3.addComponent(introURL);
		parallelH3.addComponent(introDatabase);
		parallelH3.addComponent(introCollection);
		parallelH3.addComponent(introDirectorio);
		parallelH3.addComponent(introNombre);
		
		parallelH3.addComponent(listaDatabases);
		parallelH3.addComponent(listaCollections);
		
		parallelH3.addComponent(seleccionar);
				
		sequentialH.addGroup(parallelH1);
		sequentialH.addGroup(parallelH2);
		sequentialH.addGroup(parallelH3);
 				
		GroupLayout.ParallelGroup parallelV1 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		parallelV1.addComponent(categorias);
		parallelV1.addComponent(fuentes);
		parallelV1.addComponent(seleccionar);
		
		GroupLayout.ParallelGroup parallelV2 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		parallelV2.addComponent(url_1);
		parallelV2.addComponent(introURL);
		
		GroupLayout.ParallelGroup parallelV3 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		parallelV3.addComponent(database_1);
		parallelV3.addComponent(newDatabase);
		parallelV3.addComponent(existingDatabase);
		parallelV3.addComponent(introDatabase);
		parallelV3.addComponent(listaDatabases);
		
		GroupLayout.ParallelGroup parallelV4 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		parallelV4.addComponent(collection_1);
		parallelV4.addComponent(newCollection);
		parallelV4.addComponent(existingCollection);
		parallelV4.addComponent(introCollection);
		parallelV4.addComponent(listaCollections);
		
		GroupLayout.ParallelGroup parallelV5 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		parallelV5.addComponent(directorio_1);
		parallelV5.addComponent(introDirectorio);
		parallelV5.addComponent(quiero_fichero);
		
		GroupLayout.ParallelGroup parallelV6 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		parallelV6.addComponent(nombre_1);
		parallelV6.addComponent(introNombre);
		
		sequentialV.addGroup(parallelV1);
		sequentialV.addGroup(parallelV2);
		sequentialV.addGroup(parallelV3);
		sequentialV.addGroup(parallelV4);
		sequentialV.addGroup(parallelV5);
		sequentialV.addGroup(parallelV6);
		
		layout.setHorizontalGroup(sequentialH);
		layout.setVerticalGroup(sequentialV);
		
		MongoClient mongo = new MongoClient();
		
		try {
			
			mongo.getAddress();
			
			ActionListener showDatabaseTextField = new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent ae) {
					
					JRadioButton boton = (JRadioButton) ae.getSource();
									
					switch (boton.getText()) {
									
					case "Nueva":
						if (boton.isSelected()) {
							existingDatabase.setSelected(false);
							introDatabase.setVisible(true);
							listaDatabases.setVisible(false);
						}
						else 
							introDatabase.setVisible(false);
						break;
						
					case "Existente":
						if (boton.isSelected()) {
							newDatabase.setSelected(false);
							introDatabase.setText("");
							introDatabase.setVisible(false);
							listaDatabases.setVisible(true);
							List<String> databases = mongo.getDatabaseNames();
							listaDatabases.removeAllItems();
							listaDatabases.addItem("");
							for (String database : databases) {
								if (!database.equals("admin") && !database.equals("config") && !database.equals("local")) 
									listaDatabases.addItem(database);
							}
							
						}
						else {
							listaDatabases.setVisible(false);
							introDatabase.setVisible(true);
							newDatabase.setSelected(true);
						}
						break;
					
					}
										
				}
				
			};
			
			ActionListener showCollectionTextField = new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent ae) {
					
					JRadioButton boton = (JRadioButton) ae.getSource();
					
					switch (boton.getText()) {
					
					case "Nueva":
						if (boton.isSelected()) {
							existingCollection.setSelected(false);
							introCollection.setVisible(true);
							listaCollections.setVisible(false);
						}
						else if (existingCollection.isSelected())
							introCollection.setVisible(false);
						break;
						
					case "Existente":
						try {
							if (boton.isSelected() && !newDatabase.isSelected()) {
								newCollection.setSelected(false);
								introCollection.setText("");
								introCollection.setVisible(false);
								listaCollections.setVisible(true);
								listaCollections.removeAllItems();
								listaCollections.addItem("");
								DB database = mongo.getDB(listaDatabases.getSelectedItem().toString());
								Set<String> collections = database.getCollectionNames();
								List<String> lista = new ArrayList<>();
								for (String collection : collections) {
									listaCollections.addItem(collection);
								}
								
							}
							else if (newDatabase.isSelected()) {
								JOptionPane.showMessageDialog(null, "No existen bases de datos creadas o seleccionadas", null, JOptionPane.ERROR_MESSAGE);
								listaCollections.setVisible(false);
								listaDatabases.setVisible(false);
								newDatabase.setSelected(true);
								newCollection.setSelected(true);
								existingDatabase.setSelected(false);
								existingCollection.setSelected(false);	
								introCollection.setVisible(true);
								introDatabase.setVisible(true);
							}
						
						} catch (IllegalArgumentException e) {
							JOptionPane.showMessageDialog(null, "No existen bases de datos creadas o seleccionadas", null, JOptionPane.ERROR_MESSAGE);
							listaCollections.setVisible(false);
							listaDatabases.setVisible(false);
							newDatabase.setSelected(true);
							newCollection.setSelected(true);
							existingDatabase.setSelected(false);
							existingCollection.setSelected(false);	
							introCollection.setVisible(true);
							introDatabase.setVisible(true);
						} catch (NullPointerException e) {
							JOptionPane.showMessageDialog(null, "No existen bases de datos creadas o seleccionadas", null, JOptionPane.ERROR_MESSAGE);
							listaCollections.setVisible(false);
							listaDatabases.setVisible(false);
							newDatabase.setSelected(true);
							newCollection.setSelected(true);
							existingDatabase.setSelected(false);
							existingCollection.setSelected(false);	
							introCollection.setVisible(true);
							introDatabase.setVisible(true);
						}
						
						break;
					
					}
					
				}
				
			};
			
			ActionListener scrapAction = new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent ae) {
					
					try {
						
						//Boolean para saber si existe database o coleccion
						boolean existeDatabase = false;
						boolean existeColeccion = false;
						
						//Boolean para saber si se ha creado
						boolean base_ya_creada = false;
						boolean coleccion_ya_creada = false;
						
						boolean proceder_database = false;
						boolean proceder_coleccion = false;
						
						for (int i = 0; i < arrURL.size(); i++) {
														
							if (arrSelecciones.get(i).isSelected()) {
								
								Controller controller = new Controller();
								
								setURL(arrURL.get(i).getText());
								
								//Si está seleccionado la casilla de nueva database
								if (!arrBotonesExistingDatabase.get(i).isSelected()) {
									
									setDatabase(arrDatabases.get(i).getText());
									List<String> databasesExistentes = mongo.getDatabaseNames();
									
									//Comprueba si la base de datos ya existe
									for (String databaseExistente : databasesExistentes) {
										existeDatabase = controller.comprobarDatabase(getDatabase(), databaseExistente);
									}
									
									if (existeDatabase == true) {
										
										if (base_ya_creada == false) 
											proceder_database = false;
										
										else 
											base_ya_creada = true;
										
									}
									else  {
										proceder_database = true;
										base_ya_creada = true;
									}
									
								}
								
								//Si está seleccionada la casilla de elegir database existente
								else {
									setDatabase(arrDatabasesCB.get(i).getSelectedItem().toString());
									proceder_database = true;
								}
								
								//Si está seleccionada la casilla de nueva colección
								if (!arrBotonesExistingCollection.get(i).isSelected()) {
									
									setCollection(arrCollections.get(i).getText());
									Set<String> coleccionesExistentes = mongo.getDB(getDatabase()).getCollectionNames();
									
									for (String coleccionExistente : coleccionesExistentes) {
										existeColeccion = controller.comprobarDatabase(getCollection(), coleccionExistente);
									}
									
									if (existeColeccion == true) {
										
										if (coleccion_ya_creada == false) 
											proceder_coleccion = false;
										
										else  
											proceder_coleccion = true;
										
									}
									else  {
										proceder_coleccion = true;
										coleccion_ya_creada = true;
									}
									
								}
								
								//Si está seleccionada la casilla de colección existente
								else {
									setCollection(arrCollectionsCB.get(i).getSelectedItem().toString());
									proceder_coleccion = true;
								}
								
								setDireccion(arrDirectorios.get(i).getText());
								setNombre(arrNombres.get(i).getText());
								
								controller = new Controller(arrCategorias.get(i).getSelectedItem().toString(), arrFuentes.get(i).getSelectedItem().toString(), getURL(), getDatabase(), getCollection(), getDireccion(), getNombre(), arrQuieroFichero.get(i));
								
								//Comprobamos si la URL introducida corresponde a la fuente seleccionada
								boolean coincidencia = controller.comprobar(controller.getURL(), arrFuentes.get(i).getSelectedItem().toString());
																								
								if (coincidencia && proceder_database && proceder_coleccion) 
									controller.scrap();
								else if (coincidencia && !proceder_database)
									JOptionPane.showMessageDialog(null, "La base de datos '" + getDatabase() + "' ya existe, selecciónala o indica otro nombre", null, JOptionPane.ERROR_MESSAGE);
								else if (coincidencia && !proceder_coleccion)
									JOptionPane.showMessageDialog(null, "La colección '" + getCollection()  + "' ya existe en la base de datos '" + getDatabase() + "', selecciónala o indica otro nombre", null, JOptionPane.ERROR_MESSAGE);
								else if (!coincidencia)
									JOptionPane.showMessageDialog(null, "La url introducida no corresponde a la fuente seleccionada", null, JOptionPane.ERROR_MESSAGE);
								
							}	
							
						}
											
					} catch (IOException e) {
						e.printStackTrace();
					} catch (LangDetectException e) {
						e.printStackTrace();
					}
					
				}
				
			};
			
			ActionListener borrarAction = new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent ae) {
						
					for (int i = 0; i < arrURL.size(); i++) {
						
						arrURL.get(i).setText("");
						arrDatabases.get(i).setText("");
						arrCollections.get(i).setText("");
						arrDirectorios.get(i).setText("");
						arrNombres.get(i).setText("");
						arrBotonesNewDatabase.get(i).setSelected(false);
						arrBotonesExistingDatabase.get(i).setSelected(false);
						arrBotonesNewCollection.get(i).setSelected(false);
						arrBotonesExistingCollection.get(i).setSelected(false);
						arrDatabases.get(i).setVisible(false);
						arrCollections.get(i).setVisible(false);
						arrDatabasesCB.get(i).setVisible(false);
						arrCollectionsCB.get(i).setVisible(false);
						arrSelecciones.get(i).setSelected(false);
						seleccionarTodo.setSelected(false);
						
					}

				}
				
			};
			
			ActionListener cambiarFuentes = new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent ae) { 
					
					String fonts[] = {""};
					
					switch (categorias.getSelectedItem().toString()) {
					case "Mercado Laboral":
						String f1[] = {"","CornerJob","TechnoJobs","Nature Career","Science Careers","Euraxess","CDTI","Linkedin","JobToday","EmpleoPost","Monster","SEPE"};
						fonts = f1;
						break;
					case "Oferta Formativa Educativa":
						String f2[] = {"","Coursera","Miríadax","Udacity","CRUE"};
						fonts = f2;
						break;
					case "Productos e Iniciativas":
						String f3[] = {"","Kickstarter","Zenodo"};
						fonts = f3;
						break;
					case "Noticias e Innovación Divulgativa":
						String f4[] = {"","El Mundo","ABC","20 Minutos","TechCrunch"};
						fonts = f4;
						break;
					case "Salud":
						String f5[] = {"","FarmaIndustria","Asebio"};
						fonts = f5;
						break;
					}
					
					fuentes.removeAllItems();
					
					for (String fuente : fonts) {
						fuentes.addItem(fuente);
					}
					
				}
				
			};
			
			ActionListener añadirURL = new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent ae) {
					
					//Modificamos la ventana 2 veces para que se actualize la información del JScrollPane
					frame.setBounds(150, 200, 1000, 350);
					frame.setBounds(150, 200, 1100, 350);
					
					scrollPane.setBounds(0, 0, 900, 300);
										
					JComboBox categorias2 = new JComboBox();
					categorias2.setModel(new DefaultComboBoxModel(new String[] {"", "Mercado Laboral", "Productos e Iniciativas", "Noticias e Innovación Divulgativa", "Oferta Formativa Educativa", "Salud"}));
					arrCategorias.add(categorias2);
					
					JComboBox fuentes2 = new JComboBox();
					arrFuentes.add(fuentes2);
									
					JLabel url2 = new JLabel("		URL");
					TextField introURL2 = new TextField();
					introURL2.setForeground(Color.BLACK);
					introURL2.setBackground(Color.WHITE);
					arrURL.add(introURL2);
					
					JLabel database2 = new JLabel("		Base de datos");
					JRadioButton newDatabase2 = new JRadioButton("Nueva");
					newDatabase2.setSelected(true);
					arrBotonesNewDatabase.add(newDatabase2);
					
					JRadioButton existingDatabase2 = new JRadioButton("Existente");
					arrBotonesExistingDatabase.add(existingDatabase2);
					
					TextField introDatabase2 = new TextField();
					introDatabase2.setForeground(Color.BLACK);
					introDatabase2.setBackground(Color.WHITE);
					arrDatabases.add(introDatabase2);
					
					JComboBox listaDatabases2 = new JComboBox();
					listaDatabases2.setVisible(false);
					arrDatabasesCB.add(listaDatabases2);
					
					JLabel collection2 = new JLabel("		Colección");
					
					JRadioButton newCollection2 = new JRadioButton("Nueva");
					newCollection2.setSelected(true);
					arrBotonesNewCollection.add(newCollection2);
					
					JRadioButton existingCollection2 = new JRadioButton("Existente");
					arrBotonesExistingCollection.add(existingCollection2);
					
					TextField introCollection2 = new TextField();
					introCollection2.setForeground(Color.BLACK);
					introCollection2.setBackground(Color.WHITE);
					arrCollections.add(introCollection2);
									
					JComboBox listaCollections2 = new JComboBox();
					listaCollections2.setVisible(false);
					arrCollectionsCB.add(listaCollections2);
					
					JLabel directorio2 = new JLabel("Ruta directorio");
					directorio2.setVisible(false);
					
					TextField introDirectorio2 = new TextField();
					introDirectorio2.setForeground(Color.BLACK);
					introDirectorio2.setBackground(Color.WHITE);
					introDirectorio2.setVisible(false);
					arrDirectorios.add(introDirectorio2);
					
					JLabel nombre2 = new JLabel("Nombre del fichero");
					nombre2.setVisible(false);
									
					TextField introNombre2 = new TextField();
					introNombre2.setForeground(Color.BLACK);
					introNombre2.setBackground(Color.WHITE);
					introNombre2.setVisible(false);
					arrNombres.add(introNombre2);
					
					JCheckBox quiero_fichero2 = new JCheckBox("Obtener fichero tsv");
					arrQuieroFichero.add(quiero_fichero2); 
					
					JCheckBox seleccionar2 = new JCheckBox("Seleccionar");
					arrSelecciones.add(seleccionar2);
					
					parallelH1.addComponent(url2);
					parallelH1.addComponent(database2);
					parallelH1.addComponent(collection2);
					parallelH1.addComponent(quiero_fichero2);
					
					parallelH2_1.addComponent(categorias2);
					parallelH2_1.addComponent(newDatabase2);
					parallelH2_1.addComponent(newCollection2);
					
					parallelH2_2.addComponent(fuentes2);
					parallelH2_2.addComponent(existingDatabase2);
					parallelH2_2.addComponent(existingCollection2);
					parallelH2_2.addComponent(directorio2);
					parallelH2_2.addComponent(nombre2);
					
					parallelH3.addComponent(introURL2);
					parallelH3.addComponent(introDatabase2);
					parallelH3.addComponent(introCollection2);
					parallelH3.addComponent(introDirectorio2);
					parallelH3.addComponent(introNombre2);
					
					parallelH3.addComponent(listaDatabases2);
					parallelH3.addComponent(listaCollections2);
					
					parallelH3.addComponent(seleccionar2);
					
					GroupLayout.ParallelGroup parallelV12 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
					parallelV12.addComponent(categorias2);
					parallelV12.addComponent(fuentes2);
					parallelV12.addComponent(seleccionar2);
					
					GroupLayout.ParallelGroup parallelV22 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
					parallelV22.addComponent(url2);
					parallelV22.addComponent(introURL2);
					
					GroupLayout.ParallelGroup parallelV32 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
					parallelV32.addComponent(database2);
					parallelV32.addComponent(newDatabase2);
					parallelV32.addComponent(existingDatabase2);
					parallelV32.addComponent(introDatabase2);
					parallelV32.addComponent(listaDatabases2);
					
					GroupLayout.ParallelGroup parallelV42 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
					parallelV42.addComponent(collection2);
					parallelV42.addComponent(newCollection2);
					parallelV42.addComponent(existingCollection2);
					parallelV42.addComponent(introCollection2);
					parallelV42.addComponent(listaCollections2);
					
					GroupLayout.ParallelGroup parallelV52 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
					parallelV52.addComponent(directorio2);
					parallelV52.addComponent(introDirectorio2);
					parallelV52.addComponent(quiero_fichero2);
					
					GroupLayout.ParallelGroup parallelV62 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
					parallelV62.addComponent(nombre2);
					parallelV62.addComponent(introNombre2);
					
					sequentialV.addGroup(parallelV12);
					sequentialV.addGroup(parallelV22);
					sequentialV.addGroup(parallelV32);
					sequentialV.addGroup(parallelV42);
					sequentialV.addGroup(parallelV52);
					sequentialV.addGroup(parallelV62); 
													
					ActionListener cambiarFuentes2 = new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent ae) { 
							
							String fonts[] = {""};
							
							switch (categorias2.getSelectedItem().toString()) {
							case "Mercado Laboral":
								String f1[] = {"","CornerJob","TechnoJobs","Nature Career","Science Careers","Euraxess","CDTI","Microsoft Academic","Infojobs","Linkedin","Semantic Scholar","Indeed","JobToday","EmpleoPost","Monster","SEPE"};
								fonts = f1;
								break;
							case "Oferta Formativa Educativa":
								String f2[] = {"","Coursera","Miríadax","Edx","OCW","Udacity","CRUE","RUCT"};
								fonts = f2;
								break;
							case "Productos e Iniciativas":
								String f3[] = {"","Kickstarter","Indiegogo","Zenodo"};
								fonts = f3;
								break;
							case "Noticias e Innovación Divulgativa":
								String f4[] = {"","El Mundo","ABC","20 Minutos","TechCrunch"};
								fonts = f4;
								break;
							case "Salud":
								String f5[] = {"","FarmaIndustria","Asebio"};
								fonts = f5;
								break;
							}
							
							fuentes2.removeAllItems();
							
							for (String fuente : fonts) {
								fuentes2.addItem(fuente);
							}
							
						}
						
					}; 
					
					ActionListener showDatabaseTextField2 = new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent ae) {
							
							JRadioButton boton = (JRadioButton) ae.getSource();
											
							switch (boton.getText()) {
											
							case "Nueva":
								if (boton.isSelected()) {
									existingDatabase2.setSelected(false);
									introDatabase2.setVisible(true);
									listaDatabases2.setVisible(false);
								}
								else 
									introDatabase2.setVisible(false);
								break;
								
							case "Existente":
								if (boton.isSelected()) {
									newDatabase2.setSelected(false);
									introDatabase2.setText("");
									introDatabase2.setVisible(false);
									listaDatabases2.setVisible(true);
									List<String> databases2 = mongo.getDatabaseNames();
									listaDatabases2.removeAllItems();
									listaDatabases2.addItem("");
									for (String database2 : databases2) {
										if (!database2.equals("admin") && !database2.equals("config") && !database2.equals("local")) 
											listaDatabases2.addItem(database2);
									}
									
								}
								else {
									listaDatabases2.setVisible(false);
									introDatabase2.setVisible(true);
									newDatabase2.setSelected(true);
								}
								break;
							
							}
												
						}
						
					};
					
					ActionListener showCollectionTextField2 = new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent ae) {
							
							JRadioButton boton = (JRadioButton) ae.getSource();
							
							switch (boton.getText()) {
							
							case "Nueva":
								if (boton.isSelected()) {
									existingCollection2.setSelected(false);
									introCollection2.setVisible(true);
									listaCollections2.setVisible(false);
								}
								else if (existingCollection2.isSelected())
									introCollection2.setVisible(false);
								break;
								
							case "Existente":
								try {
									
									if (boton.isSelected()) {
										newCollection2.setSelected(false);
										introCollection2.setText("");
										introCollection2.setVisible(false);
										listaCollections2.setVisible(true);
										listaCollections2.removeAllItems();
										listaCollections2.addItem("");
										DB database2 = mongo.getDB(listaDatabases2.getSelectedItem().toString());
										Set<String> collections2 = database2.getCollectionNames();
										for (String collection2 : collections2) {
											listaCollections2.addItem(collection2);
										}
									}
									else {
										listaCollections2.setVisible(false);
										introCollection.setVisible(true);
										newCollection.setSelected(true);
									}
								
								} catch (IllegalArgumentException e) {
									JOptionPane.showMessageDialog(null, "No existen bases de datos creadas o seleccionadas", null, JOptionPane.ERROR_MESSAGE);
									listaCollections2.setVisible(false);
									listaDatabases2.setVisible(false);
									newDatabase2.setSelected(true);
									newCollection2.setSelected(true);
									existingDatabase2.setSelected(false);
									existingCollection2.setSelected(false);	
									introCollection2.setVisible(true);
									introDatabase2.setVisible(true);
								} catch (NullPointerException e) {
									JOptionPane.showMessageDialog(null, "No existen bases de datos creadas o seleccionadas", null, JOptionPane.ERROR_MESSAGE);
									listaCollections2.setVisible(false);
									listaDatabases2.setVisible(false);
									newDatabase2.setSelected(true);
									newCollection2.setSelected(true);
									existingDatabase2.setSelected(false);
									existingCollection2.setSelected(false);	
									introCollection2.setVisible(true);
									introDatabase2.setVisible(true);
								}
								
								break;
							
							}
							
						}
						
					};
					
					ActionListener habilitarFichero2 = new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent ae) {
							
							JCheckBox boton = (JCheckBox) ae.getSource();
							if (boton.isSelected()) {
								directorio2.setVisible(true);
								nombre2.setVisible(true);
								introDirectorio2.setVisible(true);
								introNombre2.setVisible(true);
							}
							else {
								directorio2.setVisible(false);
								nombre2.setVisible(false);
								introDirectorio2.setVisible(false);
								introNombre2.setVisible(false);
								directorio2.setText("");
								nombre2.setText("");
							}
							
						}
						
					};
					
					categorias2.addActionListener(cambiarFuentes2);
					newDatabase2.addActionListener(showDatabaseTextField2);
					existingDatabase2.addActionListener(showDatabaseTextField2);
					newCollection2.addActionListener(showCollectionTextField2);
					existingCollection2.addActionListener(showCollectionTextField2);
					quiero_fichero2.addActionListener(habilitarFichero2);
																	
				}
				
			};
			
			ActionListener habilitarFichero = new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent ae) {
					
					JCheckBox boton = (JCheckBox) ae.getSource();
					if (boton.isSelected()) {
						directorio_1.setVisible(true);
						nombre_1.setVisible(true);
						introDirectorio.setVisible(true);
						introNombre.setVisible(true);
					}
					else {
						directorio_1.setVisible(false);
						nombre_1.setVisible(false);
						introDirectorio.setVisible(false);
						introNombre.setVisible(false);
						directorio_1.setText("");
						nombre_1.setText("");
					}
					
				}
				
			};
			
			ActionListener seleccionarTodoAction = new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent ae) {
					
					JCheckBox seleccionarTodo = (JCheckBox) ae.getSource();
					
					if (seleccionarTodo.isSelected()) {
						
						for (JCheckBox seleccion : arrSelecciones) {
							seleccion.setSelected(true);
						}
						
					}
					
					else {
						
						for (JCheckBox seleccion : arrSelecciones) {
							seleccion.setSelected(false);
						}
					}
					
				}
				
			};
			
			categorias.addActionListener(cambiarFuentes);	
			newDatabase.addActionListener(showDatabaseTextField);
			existingDatabase.addActionListener(showDatabaseTextField);
			newCollection.addActionListener(showCollectionTextField);
			existingCollection.addActionListener(showCollectionTextField);		
			quiero_fichero.addActionListener(habilitarFichero);
			scrapBoton.addActionListener(scrapAction);
			borrarBoton.addActionListener(borrarAction);
			añadirBoton.addActionListener(añadirURL);
			seleccionarTodo.addActionListener(seleccionarTodoAction);
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error, Mongo no está activo", null, JOptionPane.ERROR_MESSAGE);
			//Terminamos el programa
			System.exit(0);
		}
		
	}
	
}