package socketServeur;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class StationTramInsert {
	
	public void insertStation (JSONObject JsonRecu, Connection c) throws ParseException, UnsupportedEncodingException, SQLException {

		StringBuffer sb = new StringBuffer();

		// lecture du JSON afin de mettre chaque ligne en cha�ne de caract�re
		InputStream inputStream = FileReader.class.getClassLoader().getSystemResourceAsStream("stations.json"); 
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8")); 
		try {
			String temp; 
			while ((temp = bufferedReader.readLine()) != null) 
				sb.append(temp); 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace(); 
			}
		}
		String myjsonstring = sb.toString(); 

		JSONParser parser = new JSONParser(); 
		JSONArray json = (JSONArray) parser.parse(myjsonstring); 

		for (int i = 0; i < json.size(); i++) {
			JSONObject jsonObject = (JSONObject) json.get(i);
			Long id = (Long) (jsonObject.get("id_station"));
			int stationId = id.intValue(); 
			String stationName = String.valueOf(jsonObject.get("nom_station")); 
			String position =  String.valueOf(jsonObject.get("position")); 
		
			System.out.println("Parcours de la liste des stations, nom de la station " + stationName); 
		
			
			PreparedStatement stmt3 = c.prepareStatement("insert into station (id_station, nom_station, position) values (?,?,?);");
			// the request takes name and first name already retrieved 
			stmt3.setInt(1, stationId);
			stmt3.setString(2, stationName);
			stmt3.setString(3,position);
			// query execution 
			
			System.out.println("recup�ration des donn�es"); 


			JSONObject obj=new JSONObject(); 

			// if (insertion bien pass�) => executer les lignes suivantes sinon dire erreur
			if(stmt3.executeUpdate()>=1) {
				obj.put("reponse",String.valueOf("insertion reussi"));
				obj.put("id_station",stationId);
				obj.put("nom_station", stationName);
				obj.put("position",position);
				
				System.out.println("Insertion des lignes en base faite"); 
			}
			else {
				obj.put("reponse",String.valueOf("erreur lors de l'insertion"));
			}
			System.out.println(obj);

		}
		
	}
	
	public void insertPersStation (JSONObject JsonRecu, Connection c) throws ParseException, UnsupportedEncodingException, SQLException {

		StringBuffer sb = new StringBuffer();

		// lecture du JSON afin de mettre chaque ligne en cha�ne de caract�re
		InputStream inputStream = FileReader.class.getClassLoader().getSystemResourceAsStream("personStations.json"); 
		BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(inputStream, "UTF-8")); 
		try {
			String temp; 
			while ((temp = bufferedReader2.readLine()) != null) 
				sb.append(temp); 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bufferedReader2.close();
			} catch (IOException e) {
				e.printStackTrace(); 
			}
		}
		String myjsonstring = sb.toString(); 

		JSONParser parser = new JSONParser(); 
		JSONArray json = (JSONArray) parser.parse(myjsonstring); 

		for (int i = 0; i < json.size(); i++) {
			JSONObject jsonObject = (JSONObject) json.get(i);
			String position =  String.valueOf(jsonObject.get("position")); 
			Long id = (Long) (jsonObject.get("qte_pers"));
			int personQty = id.intValue();
			Long id2 = (Long) (jsonObject.get("id_station"));
			int stationId = id2.intValue();
			String stationName =  String.valueOf(jsonObject.get("nom_station")); 
			Timestamp date = Timestamp.valueOf((String) jsonObject.get("date_mesure")); 
			
			System.out.println("Parcours de la liste des personnes par station, nombre de personnes par station " + personQty); 
		
			
			PreparedStatement stmt3 = c.prepareStatement("insert into frequentation_station_tram (position, qte_pers, id_station, nom_station, date_mesure) values (?,?,?,?,?);");
			// the request takes name and first name already retrieved 
			stmt3.setString(1, position);
			stmt3.setInt(2,personQty);
			stmt3.setInt(3, stationId); 
			stmt3.setString(4, stationName);
			stmt3.setTimestamp(5, date);
			// query execution 
			
			System.out.println("recup�ration des donn�es"); 


			JSONObject obj=new JSONObject(); 

			// if (insertion bien pass�) => executer les lignes suivantes sinon dire erreur
			if(stmt3.executeUpdate()>=1) {
				obj.put("reponse",String.valueOf("insertion reussi"));
				obj.put("position",position);
				obj.put("qte_pers",personQty);
				obj.put("id_station",stationId);
				obj.put("nom_station",stationName);
				obj.put("date_mesure", date);
				
				System.out.println("Insertion des lignes en base faite"); 
			}
			else {
				obj.put("reponse",String.valueOf("erreur lors de l'insertion"));
			}
			System.out.println(obj);

		}
		
	}

}
