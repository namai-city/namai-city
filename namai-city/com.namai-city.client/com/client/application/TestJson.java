
package com.client.application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import org.json.simple.JSONObject;

import com.client.controller.SocketClient;
import com.commons.model.AccessServer;

import indicator.CarIndicator;
import indicator.PersonStationIndicator;
import indicator.SensorIndicator;
import indicator.SensorPolluantIndicator;
import indicator.StationIndicator;
import indicator.WarningIndicator;



public class TestJson {
	private SocketClient client = new SocketClient();
	public static Connection c; 
	private static String URL = "jdbc:postgresql://172.31.249.44:5432/NamaiDB";
	private static String login = "toto" ;
	private static String password = "toto";


	public static Connection createConnection() throws SQLException {
		try {

			return  DriverManager.getConnection (URL, login, password);
		} catch (SQLException e) {
			throw new SQLException("Can't create connection", e);
		}

	}

	public static void main(String [] args) {
		TestJson t = new TestJson();
		try {
			t.go();
		} catch (SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<SensorIndicator> goSensor() throws SQLException, IOException {
		client.startConnection(AccessServer.getSERVER(), AccessServer.getPORT_SERVER());
		JSONObject obj=new JSONObject();  //JSONObject creation

		obj.put("demandType", "SENSOR_INDICATOR");
		System.out.println("r�cup�ration du nombre de capteur par zone selon le type et la date"); 

		System.out.println(obj);
		JSONObject reponseSensor = client.sendMessage(obj);
		System.out.println("affichage rep : " + reponseSensor); 
		ArrayList<JSONObject> allSensors = new ArrayList<JSONObject>();// Creation d'un tableau de type SensorIndicator
		allSensors = (ArrayList<JSONObject>) reponseSensor.get("sensors");
		//System.out.println(allSensors.size()); 
		int nbTotal = 0;
		ArrayList<SensorIndicator> liste = new ArrayList<SensorIndicator>();
		for(int i = 0; i<allSensors.size();i++) { // Creating a loop to display all sensors in the table sensors
			SensorIndicator s = new SensorIndicator();
			s.convertFromJson(allSensors.get(i));
			liste.add(s);
			System.out.println("type: "+s.getType()+
					" | position: "+s.getPosition() +
					" | nombre de capteurs en ville : "+s.getSensorNb()); 

			nbTotal += s.getSensorNb();
		}
		System.out.println("==============> Nb total : " + nbTotal);

		client.stopConnection();
		return liste;

	}
	public ArrayList<StationIndicator> goStation() throws SQLException, IOException {
		client.startConnection(AccessServer.getSERVER(), AccessServer.getPORT_SERVER());
		JSONObject obj=new JSONObject();  //JSONObject creation


		obj.put("demandType", "STATION_INDICATOR");
		System.out.println("r�cup�ration du nombre de station par zone "); 
		System.out.println(obj);
		JSONObject reponseStation = client.sendMessage(obj);
		System.out.println("affichage rep : " + reponseStation); 
		ArrayList<JSONObject> allStations = new ArrayList<JSONObject>();// Creation d'un tableau de type StationIndicator
		allStations = (ArrayList<JSONObject>) reponseStation.get("stations");
		int nbTotalStation = 0;
		ArrayList<StationIndicator> liste = new ArrayList<StationIndicator>();
		for(int i = 0; i<allStations.size();i++) { // Creating a loop to display all sensors in the table historique_Alerte
			StationIndicator s = new StationIndicator(); 
			s.convertFromJson(allStations.get(i));
			liste.add(s);
			System.out.println(" | position : "+s.getPosition() +
					" | nombre de stations par zone dans la ville : "+ s.getStationNb()); 
			nbTotalStation += s.getStationNb();
		}
		System.out.println("==============> Nb total : " + nbTotalStation);		 
		client.stopConnection();
		return liste;
	}
	public ArrayList <Integer> getIdSensorPolluant () throws IOException, SQLException{
		client.startConnection(AccessServer.getSERVER(), AccessServer.getPORT_SERVER());
		JSONObject obj=new JSONObject();  //JSONObject creation
		obj.put("demandType", "getIdSensorPolluant");


		System.out.println(obj);
		JSONObject reponseIdPolluant = client.sendMessage(obj);
		System.out.println("affichage rep : " + reponseIdPolluant); 
		ArrayList<JSONObject> allIdPolluant = new ArrayList<JSONObject>();// Creation d'un tableau de type SensorIndicator
		allIdPolluant = (ArrayList<JSONObject>) reponseIdPolluant.get("sensorsIdPolluant");
		//System.out.println(allSensors.size()); 
		ArrayList<Integer> liste = new ArrayList<Integer>();
		for(int i = 0; i<allIdPolluant.size();i++) { // Creating a loop to display all sensors in the table sensors
			SensorPolluantIndicator s = new SensorPolluantIndicator(); 
			s.convertFromJson(allIdPolluant.get(i));
			liste.add(s.getId2());
			System.out.println("id: "+s.getId2()); 
		}

		client.stopConnection();
		return liste; 
	}

	public List<Integer> getThreshold (String polluant, int idCapteur) throws IOException {
		client.startConnection(AccessServer.getSERVER(), AccessServer.getPORT_SERVER());
		JSONObject obj=new JSONObject(); 

		System.out.println("quel est le polluant concern� : ");
		obj.put("nomPolluant",polluant);
		obj.put("demandType",String.valueOf("getThresholdSensorPolluant"));


		switch(polluant) {
		case "CO2":

			obj.put("Id",idCapteur); 
			System.out.println(obj);

			JSONObject reponseThresholdPolluant = client.sendMessage(obj);
			System.out.println("affichage rep : " + reponseThresholdPolluant); 
			ArrayList<JSONObject> allThreshold= new ArrayList<JSONObject>();// Creation d'un tableau de type SensorIndicator
			allThreshold = (ArrayList<JSONObject>) reponseThresholdPolluant.get("sensorsPolluantCo2"); 
			if(allThreshold==null) {
				System.out.println("retourner liste vide");
				client.stopConnection();
				return Arrays.asList(-1);

			}else {
				// Creating a loop to display all sensors in the table sensors
				SensorPolluantIndicator s = new SensorPolluantIndicator(); 
				s.convertFromJson(allThreshold.get(0));
				System.out.println("seuil_co2 : " + s.getCo2()); 
				client.stopConnection();
				return Arrays.asList(Integer.valueOf(s.getCo2()));
			}
		case "NO2" : 

			obj.put("Id",Integer.valueOf(idCapteur)); 
			System.out.println(obj);

			JSONObject reponseThresholdNo2 = client.sendMessage(obj);
			System.out.println("affichage rep : " + reponseThresholdNo2); 
			ArrayList<JSONObject> allThresholdNO2= new ArrayList<JSONObject>();// Creation d'un tableau de type SensorIndicator
			allThresholdNO2 = (ArrayList<JSONObject>) reponseThresholdNo2.get("sensorsPolluantNo2"); 
			if(allThresholdNO2==null) {
				System.out.println("retourner liste vide");
				client.stopConnection();
				return  Arrays.asList(-1);

			}else {
				SensorPolluantIndicator s = new SensorPolluantIndicator(); 
				s.convertFromJson(allThresholdNO2.get(0));
				System.out.println("seuil_co2 : " + s.getNo2()); 
				client.stopConnection();
				return Arrays.asList(Integer.valueOf(s.getNo2()));
			}

		case "PF" : 

			obj	.put("Id",Integer.valueOf(idCapteur)); 
			System.out.println(obj);

			JSONObject reponseThresholdPf = client.sendMessage(obj);
			System.out.println("affichage rep : " + reponseThresholdPf); 
			ArrayList<JSONObject> allThresholdPf= new ArrayList<JSONObject>();// Creation d'un tableau de type SensorIndicator
			allThresholdPf = (ArrayList<JSONObject>) reponseThresholdPf.get("sensorsPolluantPf"); 
			if(allThresholdPf==null) {
				System.out.println("retourner liste vide");
				client.stopConnection();
				return Arrays.asList(-1);

			}else {
				SensorPolluantIndicator s = new SensorPolluantIndicator(); 
				s.convertFromJson(allThresholdPf.get(0));
				System.out.println("seuil_pf : " + s.getPf()); 
				client.stopConnection();
				return Arrays.asList(Integer.valueOf(s.getPf()));
			}
		case "TMP" : 

			obj.put("Id",Integer.valueOf(idCapteur)); 
			System.out.println(obj);

			JSONObject reponseThresholdTmp = client.sendMessage(obj);
			System.out.println("affichage rep : " + reponseThresholdTmp); 
			ArrayList<JSONObject> allThresholdTmp= new ArrayList<JSONObject>();// Creation d'un tableau de type SensorIndicator
			allThresholdTmp = (ArrayList<JSONObject>) reponseThresholdTmp.get("sensorsPolluantTmp"); 
			if(allThresholdTmp==null) {
				client.stopConnection();
				return Arrays.asList(-1); 

			}else {
					SensorPolluantIndicator s = new SensorPolluantIndicator(); 
					s.convertFromJson(allThresholdTmp.get(0));
					System.out.println("temperature minimum  : " + s.getTmpMin() + 
							"| temperature maximum : " + s.getTmpMax()); 
					client.stopConnection();
					return Arrays.asList(Integer.valueOf(s.getTmpMin()),Integer.valueOf(s.getTmpMax()));
				}
		default:
			
			System.out.println("Unrocognized command");
			return Arrays.asList(-1);

			}	
}
	
	public ArrayList<SensorPolluantIndicator> getWarning(String nomPolluant, Integer idCapteur, List<Integer> listeSeuil) throws IOException {
	
		client.startConnection(AccessServer.getSERVER(), AccessServer.getPORT_SERVER());
		JSONObject obj=new JSONObject();  //JSONObject creation 

	obj.put("demandType", "getWarningPolluant");
	obj.put("fk_id_capteur",idCapteur);



	System.out.println(obj);
	JSONObject reponseWarning= client.sendMessage(obj);
	System.out.println("affichage rep : " + reponseWarning); 
	ArrayList<JSONObject> allWarningPolluant = new ArrayList<JSONObject>();// Creation d'un tableau de type SensorIndicator
	allWarningPolluant = (ArrayList<JSONObject>) reponseWarning.get("sensorsPolluantWarning");
	ArrayList<SensorPolluantIndicator> liste = new ArrayList<SensorPolluantIndicator>();
	ArrayList<SensorPolluantIndicator> listeResultat = new ArrayList<SensorPolluantIndicator>();
	//System.out.println(allSensors.size()); 

	for(int i = 0; i<allWarningPolluant.size();i++) { // Creating a loop to display all sensors in the table sensors
		SensorPolluantIndicator s = new SensorPolluantIndicator(); 
		s.convertFromJson(allWarningPolluant.get(i));
		liste.add(s);
		System.out.println("Id : " + s.getId2()+
				"| val_co2 : " + s.getCo2() +
				"| val_no2 : " + s.getNo2() +
				"| val_pf : " + s.getPf() +
				"| val_tmp : " + s.getTmp());
		
		
	
	}
	client.stopConnection();
	switch(nomPolluant) {
	case "CO2" :
		int seuilCO2 = listeSeuil.get(0);
		for(int i = 0;i<liste.size();i++) {
			if(Integer.valueOf(liste.get(i).getCo2())>seuilCO2) {
				listeResultat.add(liste.get(i));
			}
		}		
		break;
		
	case "NO2" :
		int seuilNO2 = listeSeuil.get(0);
		for(int i = 0;i<liste.size();i++) {
			if(Integer.valueOf(liste.get(i).getNo2())>seuilNO2) {
				listeResultat.add(liste.get(i));
			}
		}	
		break;
	
	case "PF" :
		int seuilPF = listeSeuil.get(0);
		for(int i = 0;i<liste.size();i++) {
			if(Integer.valueOf(liste.get(i).getPf())>seuilPF) {
				listeResultat.add(liste.get(i));
			}
		}	
		break;
	
	case "TMP" :
		int seuilTempMin = listeSeuil.get(0);
		int seuilTempMax = listeSeuil.get(1);
		for(int i = 0;i<liste.size();i++) {
			if(Integer.valueOf(liste.get(i).getPf())>seuilTempMax||Integer.valueOf(liste.get(i).getPf())<seuilTempMin) {
				listeResultat.add(liste.get(i));
			}
		}
		break;
	
	default :
			
		break;
	}
	return listeResultat;
	}
	

	

public void go() throws SQLException, IOException {

	// TODO Auto-generated method stub
	Scanner sc = new Scanner(System.in);

	while(true) { // Menu display
		System.out.println("########################### Menu Namai-city-client #########################");
		System.out.println("1: Afficher");
		System.out.println("2: Cr�er");
		System.out.println("3: Mettre � jour");
		System.out.println("4: Supprimer");
		System.out.println("5: Exit");
		System.out.println("6: Tentative de connexion � la BDD depuis le client ");
		System.out.println("7: r�cup�ration de l'indicateur du nombre de capteurs");
		System.out.println("8: r�cup�ration de l'indicateur du nombre de voitures par date dans la ville ");
		System.out.println("9: r�cup�ration de l'indicateur du nombre d'alertes");
		System.out.println("10: r�cup�ration de l'indicateur du nombre de stations");
		System.out.println("11: r�cup�ration de l'indicateur du nombre de personnes par station");
		System.out.println("12: ins�rer des donn�es dans la table Capteur");
		System.out.println("13: ins�rer des donn�es dans la table Frequentation_Voiture");
		System.out.println("14: ins�rer des donn�es dans la table station"); 
		System.out.println("15: ins�rer des donn�es dans la table Frequentation_station_tram"); 
		System.out.println("16: ins�rer des donn�es dans la table Historique_alerte"); 
		System.out.println("17: ins�rer des donn�es dans la table capteur_polluant");
		System.out.println("18: ins�rer des donn�es dans la table historique_capteurpol"); 
		System.out.println("19: r�cup�ration de l'indicateur du nombre d'alertes SensorPolluant");
		System.out.println("20: affichage de l'id des capteurs ");
		System.out.println("21: affichage des polluants des capteurs ");
		System.out.println("22: affichage des alertes");
		System.out.println("23 : nombre de bornes ");
		System.out.println("24 : nombre de capteurs polluants ");
		System.out.println("25 : ins�rer des donn�es dans la table bornes");
		System.out.println("########################### Menu Namai-city-client #########################");

		client.startConnection(AccessServer.getSERVER(), AccessServer.getPORT_SERVER());
		JSONObject obj=new JSONObject();  //JSONObject creation
		String rep = sc.nextLine();


		switch (rep) {
		case "1":
			System.out.println("########################### Menu Namai-city-client #########################");
			System.out.println("1: Afficher tout les utilisateurs");
			System.out.println("2: afficher un utilisateur en particulier");
			System.out.println("########################### Menu Namai-city-client #########################");	
			Scanner choice = new Scanner(System.in);
			rep = choice.nextLine(); 
			switch(rep) {
			case "1":
				System.out.println("########################### SELECT #########################");
				obj.put("demandType",String.valueOf("SELECT"));
				obj.put("Id",Integer.valueOf(0)); 
				System.out.println(obj);
				JSONObject reponseAll = client.sendMessage(obj);
				ArrayList<JSONObject> allUsers = new ArrayList<JSONObject>();
				allUsers = (ArrayList<JSONObject>) reponseAll.get("users");
				for(int i = 0; i<allUsers.size();i++) {
					System.out.println("id: "+allUsers.get(i).get("Id")+
							" | nom: "+allUsers.get(i).get("nom")+
							" | prenom: "+allUsers.get(i).get("prenom"));
				}			 
				client.stopConnection();  
				break;


			case "2":
				choice = new Scanner(System.in);
				System.out.println("########################### SELECT #########################");
				System.out.println("quel est l'id de l'utilisateur � afficher ? ");
				int repSelect = 0;
				try {
					repSelect = choice.nextInt();
				}
				catch(InputMismatchException e){
					System.out.println("probleme de saisi de l'id");
					break;
				}
				obj.put("demandType",String.valueOf("SELECT"));
				obj.put("Id",Integer.valueOf(repSelect)); 
				System.out.println(obj);
				JSONObject reponse = client.sendMessage(obj);
				if(reponse.containsKey("reponse")) {
					System.out.println(reponse.get("reponse"));
				}
				else {					
					String name = (String) reponse.get("nom");  
					String prenom = (String) reponse.get("prenom");  
					long idCaste = (long) reponse.get("Id");
					int id = (int) idCaste;
					System.out.println("voici les informations de l'utilisateur: \n" + name +"\n" + prenom + "\n "+id+ "\n");  
				}
				client.stopConnection();  
				break;
			}
			break;

		case "2":
			// requete insertion dans table utilisateur
			System.out.println("########################### INSERT #########################");
			System.out.println("saisissez les informations de l'utilisateur:");
			System.out.println("nom:");
			String nom = sc.nextLine();
			System.out.println("pr�nom:");
			String prenom = sc.nextLine();
			obj.put("demandType",String.valueOf("INSERT"));
			obj.put("nom",String.valueOf(nom));
			obj.put("prenom",String.valueOf(prenom));
			System.out.println(obj);
			JSONObject reponse = client.sendMessage(obj);
			String repServer = (String) reponse.get("reponse");  
			if(repServer.equals("insertion r�ussi")) {
				String prenomInsert = (String) reponse.get("prenom");  
				String nomInsert = (String) reponse.get("nom");
				System.out.println(repServer +"\n voici les informations insere: \n" + prenomInsert + "\n " + nomInsert  + "\n");  
			}
			else {
				System.out.println(repServer +"\n");
			}
			client.stopConnection();
			break; 

		case "3": 
			// requete pour mettre �  jour la table utilisateur 
			System.out.println("########################### UPDATE #########################");
			System.out.println("quel est l'id � modifier?"); 

			String id_update = sc.nextLine();
			Integer id_user_update = Integer.parseInt(id_update);
			System.out.print("le nom ? ");
			String nomUpdate = sc.nextLine(); 
			System.out.print("le prenom ? ");
			String prenomUpdate = sc.nextLine();
			obj.put("demandType",String.valueOf("UPDATE"));
			obj.put("nom",String.valueOf(nomUpdate));
			obj.put("prenom",String.valueOf(prenomUpdate));
			obj.put("Id",id_user_update);
			System.out.println(obj);
			JSONObject reponseUdpade = client.sendMessage(obj);
			String repServerUpdate = (String) reponseUdpade.get("reponse"); 
			if(repServerUpdate.contentEquals("mise � jour reussie")) {
				String prenomUpdate2 = (String) reponseUdpade.get("prenom");  
				String nomupdate2 = (String) reponseUdpade.get("nom");
				long idCaste = (long) reponseUdpade.get("Id");
				int idUpdate = (int) idCaste;
				System.out.println(repServerUpdate +"\n voici les donnees mises a jour: \n" + prenomUpdate2 + "\n " + nomupdate2  + "\n" + idUpdate);
			}
			else {
				System.out.println(repServerUpdate);
			}
			client.stopConnection();

			break; 

		case "4" : 
			// crud requete delete de la table en BDD (NamaiDB / toto) 
			System.out.println("########################### DELETE  #########################");
			System.out.println("quel est l'id de l'utilisateur � supprimer ?"); 
			String id_delete = sc.nextLine();
			Integer id_user_delete = Integer.parseInt(id_delete);


			obj.put("demandType",String.valueOf("DELETE"));

			obj.put("Id",String.valueOf(id_user_delete));
			System.out.println(obj);
			JSONObject reponseDelete = client.sendMessage(obj);
			String repServerDelete = (String) reponseDelete.get("reponse");  

			if(repServerDelete.equals("suppression r�ussie")) {
				long idCasteDelete = (long) reponseDelete.get("Id");
				int idDelete = (int) idCasteDelete;
				System.out.println(repServerDelete + "\n Voici l'id de le l'utilisateur � supprimer : " + idDelete);  
			}
			else {
				System.out.println(repServerDelete);
			}
			client.stopConnection();

			break; 


		case "5":
			// dans la partie exit fermeture de toutes les connexions et fermeture de la socket 
			System.out.println("########################### EXIT #########################");
			System.out.println("Merci de votre visite, A bientot!");
			client.stopConnection();
			System.exit(0);
			break;

		case "6": 
			//pour montrer que le client n'a pas acc�s a la BDD

			c = createConnection(); 
			System.out.println("nom:");
			String nomBDD = sc.nextLine();
			System.out.println("pr�nom:");
			String prenomBDD = sc.nextLine();

			PreparedStatement stmt3 = c.prepareStatement("insert into utilisateur(nom,prenom) values (?,?);");
			stmt3.setString(1, nomBDD);
			stmt3.setString(2,prenomBDD);
			stmt3.execute();
			break; 

		case "7": 
			System.out.println("########################### SENSOR INDICATOR #########################");
			obj.put("demandType", "SENSOR_INDICATOR");
			System.out.println("r�cup�ration du nombre de capteur par zone selon le type et la date"); 

			System.out.println(obj);
			JSONObject reponseSensor = client.sendMessage(obj);
			System.out.println("affichage rep : " + reponseSensor); 
			ArrayList<JSONObject> allSensors = new ArrayList<JSONObject>();// Creation d'un tableau de type SensorIndicator
			allSensors = (ArrayList<JSONObject>) reponseSensor.get("sensors");
			//System.out.println(allSensors.size()); 
			int nbTotal = 0;
			for(int i = 0; i<allSensors.size();i++) { // Creating a loop to display all sensors in the table sensors
				SensorIndicator s = new SensorIndicator(); 
				s.convertFromJson(allSensors.get(i));
				System.out.println("type: "+s.getType()+
						" | position: "+s.getPosition() +
						" | date: Pas encore recuperee " + 
						" | nombre de capteurs en ville : "+s.getSensorNb()); 

				nbTotal += s.getSensorNb();
			}
			System.out.println("==============> Nb total : " + nbTotal);

			client.stopConnection();

			break; 

		case "8": 
			System.out.println("########################### CAR INDICATOR #########################");

			obj.put("demandType", "CAR_INDICATOR");
			System.out.println("r�cup�ration du nombre de voitures par la date"); 


			System.out.println(obj);

			JSONObject reponseAll1 = client.sendMessage(obj);
			System.out.println("affichage rep : " + reponseAll1); 
			ArrayList<JSONObject> allCars = new ArrayList<JSONObject>();// Creation d'un tableau de type CarIndicator
			allCars = (ArrayList<JSONObject>) reponseAll1.get("cars");
			int nbCars = 0; 
			for(int i = 0; i<allCars.size();i++) { // Creating a loop to display all sensors in the table Frequentation_Voiture
				CarIndicator s = new CarIndicator(); 
				s.convertFromJson(allCars.get(i));
				System.out.println(" | nombre de voitures: "+ s.getCarsNb() +
						" | date: " + s.getDate() +
						" | nombre de voitures totale par date pas recuperer "); 
				nbCars += s.getCarNbGlobal(); 

			}
			System.out.println("==============> Nb total : " + nbCars);	
			client.stopConnection();
			break; 


		case "9": 
			System.out.println("########################### WARNING INDICATOR #########################");
			obj.put("demandType", "WARNING_INDICATOR");

			System.out.println("r�cup�ration du nombre d'alertes par DATE"); 

			System.out.println(obj);
			JSONObject reponseAll2 = client.sendMessage(obj);
			System.out.println("affichage rep : " + reponseAll2);
			ArrayList<JSONObject> allWarnings = new ArrayList<JSONObject>();// Creation d'un tableau de type WarningIndicator
			allWarnings = (ArrayList<JSONObject>) reponseAll2.get("warnings");
			for(int i = 0; i<allWarnings.size();i++) { // Creating a loop to display all sensors in the table historique_Alerte
				WarningIndicator s = new WarningIndicator(); 
				s.convertFromJson(allWarnings.get(i));
				System.out.println("position: "+ s.getPosition() + 
						" | date : "+ s.getDateStart()+
						" | nombre d'alertes dans la ville : "+s.getWarningNb()); 
			}			 
			client.stopConnection();

			break; 


		case "10": 
			System.out.println("########################### STATION INDICATOR #########################");


			obj.put("demandType", "STATION_INDICATOR");
			System.out.println("r�cup�ration du nombre de station par zone "); 
			System.out.println(obj);
			JSONObject reponseStation = client.sendMessage(obj);
			System.out.println("affichage rep : " + reponseStation); 
			ArrayList<JSONObject> allStations = new ArrayList<JSONObject>();// Creation d'un tableau de type StationIndicator
			allStations = (ArrayList<JSONObject>) reponseStation.get("stations");
			int nbTotalStation = 0;
			for(int i = 0; i<allStations.size();i++) { // Creating a loop to display all sensors in the table historique_Alerte
				StationIndicator s = new StationIndicator(); 
				s.convertFromJson(allStations.get(i));
				System.out.println(" | position : "+s.getPosition() +
						" | nombre de stations par zone dans la ville : "+ s.getStationNb()); 
				nbTotalStation += s.getStationNb();
			}
			System.out.println("==============> Nb total : " + nbTotalStation);		 
			client.stopConnection();

			break; 

		case "11": 
			System.out.println("###########################  PERSON PER STATION INDICATOR #########################");
			obj.put("demandType", "PERSON_STATION_INDICATOR");
			System.out.println("r�cup�ration du nombre de personne par zone utilisant le tram"); 


			System.out.println(obj);
			JSONObject reponsePersStation = client.sendMessage(obj);
			ArrayList<JSONObject> allStationsPers = new ArrayList<JSONObject>();// Creation d'un tableau de type StationIndicator
			allStationsPers = (ArrayList<JSONObject>) reponsePersStation.get("PersonStations");
			int nbTotalPersStation = 0; 
			for(int i = 0; i<allStationsPers.size();i++) { // Creating a loop to display all sensors in the table historique_Alerte
				PersonStationIndicator s = new PersonStationIndicator(); 
				s.convertFromJson(allStationsPers.get(i));
				System.out.println(" position  : "+ s.getPosition()+
						" | le nombre de personne dans cette station  : "+ s.getPersonQty() +
						" | date :  Pas encore recuperee" + 
						" | nombre de personne dans cette zone  : " + s.getPersNb());
				nbTotalPersStation += s.getPersNb();
			}	
			System.out.println("==============> Nb total : " + nbTotalPersStation);
			client.stopConnection();

			break; 

		case "12": 
			System.out.println("########################### INSERT SENSOR #########################");
			obj.put("demandType",String.valueOf("MOCK_SENSOR_INSERT"));


			JSONObject reponseInsertSensor = client.sendMessage(obj);
			String repServerSensor = (String) reponseInsertSensor.get("reponse");
			System.out.println(repServerSensor);

			client.stopConnection();
			break; 

		case "13" : 
			System.out.println("########################### INSERT CAR #########################");
			obj.put("demandType",String.valueOf("MOCK_CAR_INSERT"));


			JSONObject reponseInsertCar = client.sendMessage(obj);
			String repServerCar = (String) reponseInsertCar.get("reponse");
			System.out.println(repServerCar);

			client.stopConnection();
			break; 

		case "14": 
			System.out.println("########################### INSERT STATION #########################");
			obj.put("demandType",String.valueOf("MOCK_STATION_INSERT"));


			JSONObject reponseInsertStation = client.sendMessage(obj);
			String repServerStation = (String) reponseInsertStation.get("reponse");
			System.out.println(repServerStation);

			client.stopConnection();
			break; 

		case "15": 
			System.out.println("########################### INSERT  PERS STATION #########################");
			obj.put("demandType",String.valueOf("MOCK_PERS_STATION_INSERT"));


			JSONObject reponseInsertPersStation = client.sendMessage(obj);
			String repServerPersStation = (String) reponseInsertPersStation.get("reponse");
			System.out.println(repServerPersStation);

			client.stopConnection();
			break; 

		case "16": 
			System.out.println("########################### INSERT HISTORICAL WARNING #########################");
			obj.put("demandType",String.valueOf("MOCK_WARNING_HISTORICAL_INSERT"));


			JSONObject reponseInsertWarning = client.sendMessage(obj);
			String repServerWarning = (String) reponseInsertWarning.get("reponse");
			System.out.println(repServerWarning);

			client.stopConnection();
			break; 

		case "17" : 
			System.out.println("########################### INSERT SENSOR POLLUANT #########################");
			obj.put("demandType",String.valueOf("MOCK_SENSOR_POLLUANT_INSERT"));
			JSONObject reponseInsertSensorPolluant = client.sendMessage(obj); 
			String repServerPolluant = (String) reponseInsertSensorPolluant.get("reponse"); 
			System.out.println(repServerPolluant); 


			client.stopConnection();
			break; 

		case "18": 
			System.out.println("########################### INSERT WARNING #########################");
			obj.put("demandType",String.valueOf("MOCK_HISTORICAL_SENSOR_POLLUANT_INSERT"));
			JSONObject reponseInsertHistoricalSensorPolluant = client.sendMessage(obj); 
			String repServerHistoricalPolluant = (String) reponseInsertHistoricalSensorPolluant.get("reponse"); 
			System.out.println(repServerHistoricalPolluant); 

			client.stopConnection();
			break; 

		case "19": 
			System.out.println("########################### SENSOR  POLLUANT INDICATOR #########################");
			obj.put("demandType", "SENSOR_POLLUANT_INDICATOR");


			System.out.println(obj);
			JSONObject reponseSensorPolluant = client.sendMessage(obj);
			System.out.println("affichage rep : " + reponseSensorPolluant); 
			ArrayList<JSONObject> allSensorsPolluant = new ArrayList<JSONObject>();// Creation d'un tableau de type SensorIndicator
			allSensorsPolluant = (ArrayList<JSONObject>) reponseSensorPolluant.get("sensorsPolluant");
			//System.out.println(allSensors.size()); 
			int nbTotal2 = 0;
			for(int i = 0; i<allSensorsPolluant.size();i++) { // Creating a loop to display all sensors in the table sensors
				SensorPolluantIndicator s = new SensorPolluantIndicator(); 
				s.convertFromJson(allSensorsPolluant.get(i));
				System.out.println("localisation: "+s.getLocalisation() +
						" | date: Pas encore recuperee " + 
						" | nombre d'alertes en ville : "+s.getWarningNb()+
						"| valeur du CO2 : " + s.getCo2()+
						"| valeur des particules fines : " + s.getPf()+
						" | valeur du NO2 : " + s.getNo2()+
						" | Temp�rature: " + s.getTmp()); 

				nbTotal2 += s.getWarningNb();
			}
			System.out.println("==============> Nb total : " + nbTotal2);

			client.stopConnection();

			break; 

		case "20" : 
			System.out.println("########################### SENSOR  POLLUANT INDICATOR #########################");
			obj.put("demandType", "getIdSensorPolluant");


			System.out.println(obj);
			JSONObject reponseIdPolluant = client.sendMessage(obj);
			System.out.println("affichage rep : " + reponseIdPolluant); 
			ArrayList<JSONObject> allIdPolluant = new ArrayList<JSONObject>();// Creation d'un tableau de type SensorIndicator
			allIdPolluant = (ArrayList<JSONObject>) reponseIdPolluant.get("sensorsIdPolluant");
			//System.out.println(allSensors.size()); 

			for(int i = 0; i<allIdPolluant.size();i++) { // Creating a loop to display all sensors in the table sensors
				SensorPolluantIndicator s = new SensorPolluantIndicator(); 
				s.convertFromJson(allIdPolluant.get(i));
				System.out.println("id: "+s.getId2()); 
			}

			client.stopConnection();

			break; 

		case "21" : 
			System.out.println("########################### SEUIL CAPTEUR POLLUANT #########################");
			System.out.println("quel est le polluant concern� : ");
			Scanner polluantChoice = new Scanner(System.in);
			rep = polluantChoice.nextLine(); 

			obj.put("nomPolluant",String.valueOf(rep));
			obj.put("demandType",String.valueOf("getThresholdSensorPolluant"));


			switch(rep) {
			case "CO2":
				System.out.println("quel est l'id du capteur polluant concern� ?");
				int repSelect = 0;
				try {
					repSelect = polluantChoice.nextInt();
				}
				catch(InputMismatchException e){
					System.out.println("probleme de saisi de l'id");
					break;
				}

				obj.put("Id",Integer.valueOf(repSelect)); 
				System.out.println(obj);

				JSONObject reponseThresholdPolluant = client.sendMessage(obj);
				System.out.println("affichage rep : " + reponseThresholdPolluant); 
				ArrayList<JSONObject> allThreshold= new ArrayList<JSONObject>();// Creation d'un tableau de type SensorIndicator
				allThreshold = (ArrayList<JSONObject>) reponseThresholdPolluant.get("sensorsPolluantCo2"); 
				if(allThreshold==null) {
					System.out.println("retourner liste vide");
					client.stopConnection();

				}else {
					for(int i = 0; i<allThreshold.size();i++) { // Creating a loop to display all sensors in the table sensors
						SensorPolluantIndicator s = new SensorPolluantIndicator(); 
						s.convertFromJson(allThreshold.get(i));
						System.out.println("seuil_co2 : " + s.getCo2()); 
					}
				}

				client.stopConnection();

				break;


			case "NO2" : 
				System.out.println("quel est l'id du capteur polluant concern� ?");
				int repNo2 = 0;
				try {
					repNo2 = polluantChoice.nextInt();
				}
				catch(InputMismatchException e){
					System.out.println("probleme de saisi de l'id");
					break;
				}

				obj.put("Id",Integer.valueOf(repNo2)); 
				System.out.println(obj);

				JSONObject reponseThresholdNo2 = client.sendMessage(obj);
				System.out.println("affichage rep : " + reponseThresholdNo2); 
				ArrayList<JSONObject> allThresholdNO2= new ArrayList<JSONObject>();// Creation d'un tableau de type SensorIndicator
				allThresholdNO2 = (ArrayList<JSONObject>) reponseThresholdNo2.get("sensorsPolluantNo2"); 
				if(allThresholdNO2==null) {
					System.out.println("retourner liste vide");
					client.stopConnection();

				}else {
					for(int i = 0; i<allThresholdNO2.size();i++) { // Creating a loop to display all sensors in the table sensors
						SensorPolluantIndicator s = new SensorPolluantIndicator(); 
						s.convertFromJson(allThresholdNO2.get(i));
						System.out.println("seuil_co2 : " + s.getNo2()); 
					}
				}

				client.stopConnection();
				break; 

			case "PF" : 
				System.out.println("quel est l'id du capteur polluant concern� ?");
				int repPF = 0;
				try {
					repPF = polluantChoice.nextInt();
				}
				catch(InputMismatchException e){
					System.out.println("probleme de saisi de l'id");
					break;
				}

				obj.put("Id",Integer.valueOf(repPF)); 
				System.out.println(obj);

				JSONObject reponseThresholdPf = client.sendMessage(obj);
				System.out.println("affichage rep : " + reponseThresholdPf); 
				ArrayList<JSONObject> allThresholdPf= new ArrayList<JSONObject>();// Creation d'un tableau de type SensorIndicator
				allThresholdPf = (ArrayList<JSONObject>) reponseThresholdPf.get("sensorsPolluantPf"); 
				if(allThresholdPf==null) {
					System.out.println("retourner liste vide");
					client.stopConnection();

				}else {
					for(int i = 0; i<allThresholdPf.size();i++) { // Creating a loop to display all sensors in the table sensors
						SensorPolluantIndicator s = new SensorPolluantIndicator(); 
						s.convertFromJson(allThresholdPf.get(i));
						System.out.println("seuil_pf : " + s.getPf()); 
					}
				}

				client.stopConnection();


				break; 

			case "TMP" : 
				System.out.println("quel est l'id du capteur polluant concern� ?");
				int repTmp = 0;
				try {
					repTmp = polluantChoice.nextInt();
				}
				catch(InputMismatchException e){
					System.out.println("probleme de saisi de l'id");
					break;
				}

				obj.put("Id",Integer.valueOf(repTmp)); 
				System.out.println(obj);

				JSONObject reponseThresholdTmp = client.sendMessage(obj);
				System.out.println("affichage rep : " + reponseThresholdTmp); 
				ArrayList<JSONObject> allThresholdTmp= new ArrayList<JSONObject>();// Creation d'un tableau de type SensorIndicator
				allThresholdTmp = (ArrayList<JSONObject>) reponseThresholdTmp.get("sensorsPolluantTmp"); 
				if(allThresholdTmp==null) {
					System.out.println("retourner liste vide");
					client.stopConnection();

				}else {
					for(int i = 0; i<allThresholdTmp.size();i++) { // Creating a loop to display all sensors in the table sensors
						SensorPolluantIndicator s = new SensorPolluantIndicator(); 
						s.convertFromJson(allThresholdTmp.get(i));
						System.out.println("temperature minimum  : " + s.getTmpMin() + 
								"| temperature maximum : " + s.getTmpMax()); 
					}

				}
				client.stopConnection();
				break; 
			}
			break; 

		case "22": 
			System.out.println("########################### ALERTE  INDICATOR #########################");
			System.out.println("quel est l'id du capteur polluant concern� : "); 
			String idfk= sc.nextLine();
			Integer PolluantAlerte= Integer.parseInt(idfk); 

			obj.put("demandType", "getWarningPolluant");
			obj.put("fk_id_capteur",Integer.valueOf(PolluantAlerte));



			System.out.println(obj);
			JSONObject reponseWarning= client.sendMessage(obj);
			System.out.println("affichage rep : " + reponseWarning); 
			ArrayList<JSONObject> allWarningPolluant = new ArrayList<JSONObject>();// Creation d'un tableau de type SensorIndicator
			allWarningPolluant = (ArrayList<JSONObject>) reponseWarning.get("sensorsPolluantWarning");
			//System.out.println(allSensors.size()); 

			for(int i = 0; i<allWarningPolluant.size();i++) { // Creating a loop to display all sensors in the table sensors
				SensorPolluantIndicator s = new SensorPolluantIndicator(); 
				s.convertFromJson(allWarningPolluant.get(i));
				System.out.println("Id : " + s.getId2()+
						"| val_co2 : " + s.getCo2() +
						"| val_no2 : " + s.getNo2() +
						"| val_pf : " + s.getPf() +
						"| val_tmp : " + s.getTmp());
			}

			client.stopConnection();

			break; 
			
		case "23": 
			System.out.println("########################### SENSOR INDICATOR #########################");
			obj.put("demandType", "SENSOR_INDICATOR2");
			System.out.println("r�cup�ration du nombre de bornes par position"); 

			System.out.println(obj);
			JSONObject reponseBorne = client.sendMessage(obj);
			System.out.println("affichage rep : " + reponseBorne); 
			ArrayList<JSONObject> allBornes = new ArrayList<JSONObject>();// Creation d'un tableau de type SensorIndicator
			allBornes = (ArrayList<JSONObject>) reponseBorne.get("bornes");
			//System.out.println(allSensors.size()); 
			int nbBornes = 0;
			for(int i = 0; i<allBornes.size();i++) { // Creating a loop to display all sensors in the table sensors
				SensorIndicator s = new SensorIndicator(); 
				s.convertFromJson(allBornes.get(i));
				System.out.println(" position: "+s.getLocalisation() +
						" | nombre de capteurs en ville : "+s.getBorneNb()); 

				nbBornes += s.getBorneNb();
			}
			System.out.println("==============> Nb total : " + nbBornes);

			client.stopConnection();

			break; 
			
		case "24": 
			System.out.println("########################### SENSOR INDICATOR #########################");
			obj.put("demandType", "SENSOR_INDICATOR3");
			System.out.println("r�cup�ration du nombre de capteur par zone selon le type et la date"); 

			System.out.println(obj);
			JSONObject reponseCapPolluant = client.sendMessage(obj);
			System.out.println("affichage rep : " + reponseCapPolluant); 
			ArrayList<JSONObject> allPolluant = new ArrayList<JSONObject>();// Creation d'un tableau de type SensorIndicator
			allPolluant = (ArrayList<JSONObject>) reponseCapPolluant.get("sensorPolluant");
			//System.out.println(allSensors.size()); 
			int nbPolluant = 0;
			for(int i = 0; i<allPolluant.size();i++) { // Creating a loop to display all sensors in the table sensors
				SensorIndicator s = new SensorIndicator(); 
				s.convertFromJson(allPolluant.get(i));
				System.out.println(" position: "+ s.getLocalisation()+
						" | nombre de capteurs en ville : "+s.getSensorPolluantNb() ); 

				nbPolluant += s.getSensorPolluantNb();
			}
			System.out.println("==============> Nb total : " + nbPolluant);

			client.stopConnection();

			break; 

		case "25": 
			System.out.println("########################### INSERT SENSOR #########################");
			obj.put("demandType",String.valueOf("MOCK_BORNE_INSERT"));


			JSONObject reponseInsertBorne = client.sendMessage(obj);
			String repServerBorne = (String) reponseInsertBorne.get("reponse");
			System.out.println(repServerBorne);

			client.stopConnection();
			break; 




		default:
			System.out.println("Unrocognized command");
			break;


		}

	}
}
}

