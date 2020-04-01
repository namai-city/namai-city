package socketServeur;

import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import connectionPool.DataSource;

import java.io.*; 
import java.net.*;


public class ThreadClient extends Thread {
	private Socket clientSocket; 
	public PrintWriter outJson;
	private BufferedReader inJson;
	private Connection c; 


	public ThreadClient(Socket socket, Connection connection) {
		this.clientSocket = socket;
		this.c = connection; 

	}

	public void run()  {
		try {
			outJson = new PrintWriter(clientSocket.getOutputStream(), true);
			inJson  = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));  
			
			// processing part of Json 

			outJson = new PrintWriter(clientSocket.getOutputStream(), true);
			inJson = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			String resp = inJson.readLine();
			System.out.println("bonjour je viens de r�cuperer le JSON");
			System.out.println(resp);
			Object obj=JSONValue.parse(resp); 
			System.out.println("bonjour je parse le JSON");
			System.out.println(resp);
			JSONObject jsonObject = (JSONObject) obj;  
			System.out.println("bonjour je viens de parser le JSON");
			System.out.println(resp);
			
			obj = crud(jsonObject); 
			// Once the Json had been processed, closing the socket and releasing the connection

			outJson.println(obj);
			DataSource.releaseConnection(c); 
			inJson.close();
			outJson.close();
			clientSocket.close();
		} catch (IOException | SQLException e) {}
	}
	
	
// crud method allowing to according to customer's choice (select / insert/ update / delete) to do the request
	private Object crud(JSONObject JsonRecu) throws SQLException {

		if(JsonRecu.get("demandType").equals("SELECT")) {
			long idcaste = Long.valueOf(JsonRecu.get("Id").toString());
			int idJson = (int) idcaste;
			System.out.println("bonjour voici le ID recu apres traitement");
			System.out.println(idJson);
			if(idJson == 0) {
				
				PreparedStatement stmt1 = c.prepareStatement("select * from utilisateur;");
				ResultSet rs2 = stmt1.executeQuery();
				
				JSONObject obj=new JSONObject();
				// creation of users list 
				ArrayList<JSONObject> listUsers = new ArrayList<JSONObject>();
			
				while (rs2.next()) {
					JSONObject user=new JSONObject();
					// recovery of each user's data (id/ name/ first name) 
					user.put("Id", rs2.getInt("id_user"));
					user.put("nom", rs2.getString("nom"));
					user.put("prenom", rs2.getString("prenom"));
					
					// adding each user to the list already created
					listUsers.add(user);
					
				}
				System.out.println("voici l'arrayList : ");
				// displaying the list 
				System.out.println(listUsers);
				
				obj.put("users", listUsers);
				System.out.println("voici le json envoy� avec le select All: ");
				// displaying the Json
				System.out.println(obj);
				return obj; 
			}
			else {
				PreparedStatement stmtJson = c.prepareStatement("select * from utilisateur where id_user = ?");
				stmtJson.setInt(1, idJson);
				ResultSet jsonResponse = stmtJson.executeQuery();
				JSONObject obj=new JSONObject(); 
				
				while (jsonResponse.next()) {
					//recovery of the data of the user in question 
					obj.put("Id",jsonResponse.getInt("id_user"));
					obj.put("nom",jsonResponse.getString("nom"));
					obj.put("prenom",jsonResponse.getString("prenom"));
				}
				// displaying the json 
				System.out.println(obj);
				return obj; 
			}
		}

		if(JsonRecu.get("demandType").equals("INSERT")) {
			System.out.println("Je suis rentr� dans la requ�te INSERT"); 
// recovery of data that the client had completed (name / first name
			String nomInsert =(String) JsonRecu.get("nom");
			String prenomInsert =(String) JsonRecu.get("prenom");
			System.out.println("bonjour voici les donnees recu apres traitement");
			System.out.println(nomInsert +  " " + prenomInsert);
			
			PreparedStatement stmt3 = c.prepareStatement("insert into utilisateur(nom,prenom) values (?,?);");
			// the request takes name and first name already retrieved 
			stmt3.setString(1, nomInsert);
			stmt3.setString(2,prenomInsert);
			// query execution 
			stmt3.execute();
			
			JSONObject obj=new JSONObject(); 
			
			// if (insertion bien pass�) => executer les lignes suivantes sinon dire erreur
			obj.put("reponse",String.valueOf("insertion r�ussi"));
			obj.put("nom",String.valueOf(nomInsert));
			obj.put("prenom",String.valueOf(prenomInsert));
			
			System.out.println(obj);
			return obj; 
		}



		if(JsonRecu.get("demandType").equals("UPDATE")) {
			System.out.println("Je suis rentr� dans la requete UPDATE");
			String nomUpdate =(String) JsonRecu.get("nom");
			String prenomUpdate =(String) JsonRecu.get("prenom");
			System.out.println("J'accede a la donn�e id ");
			long idcaste = Long.valueOf(JsonRecu.get("Id").toString());
			int idJson = (int) idcaste;
			System.out.println("acces r�ussi");
			System.out.println("bonjour voici les donnees recu apres traitement");
			System.out.println(nomUpdate + prenomUpdate + idJson);
			PreparedStatement stmt4 = c.prepareStatement("update utilisateur set nom= ?, prenom = ? where id_user = ?;  ");
			stmt4.setString(1, nomUpdate);
			stmt4.setString(2,prenomUpdate);
			System.out.println("je met le idJson dans la requete "); 
			stmt4.setInt(3, idJson);
			System.out.println("l'id est bien mis"); 
			stmt4.execute();
			JSONObject obj=new JSONObject(); 


			//if (update  bien pass�) => executer les lignes suivantes sinon dire erreur
			obj.put("reponse",String.valueOf("mise � jour r�ussie"));
			obj.put("nom",String.valueOf(nomUpdate));
			obj.put("prenom",String.valueOf(prenomUpdate));
			obj.put("Id", Integer.valueOf(idJson));

			System.out.println(obj);
			return obj; 
		}

		if(JsonRecu.get("demandType").equals("DELETE")) {

			long idcaste = Long.valueOf(JsonRecu.get("Id").toString());
			int idJson = (int) idcaste;
			System.out.println("bonjour voici l'ID � supprimer");
			System.out.println(idJson);
			PreparedStatement stmt5 = c.prepareStatement("delete from utilisateur where id_user = ?");
			stmt5.setInt(1, idJson);
			stmt5.execute();

			JSONObject obj=new JSONObject(); 

			obj.put("reponse",String.valueOf("suppression r�ussie"));
			obj.put("Id", Integer.valueOf(idJson));
			System.out.println(obj);
			return obj; 
		}
		
		// Case where no if is checked 
		return new JSONObject();
	}

}
