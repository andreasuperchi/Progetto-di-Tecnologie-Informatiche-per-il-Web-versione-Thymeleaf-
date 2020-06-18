package it.polimi.tiw_exam.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.polimi.tiw_exam.beans.Riunione;
import it.polimi.tiw_exam.beans.Utente;

public class UtenteDAO {
	private int id;
	private Connection connection;
	
	public UtenteDAO(Connection connection) {
		this.connection = connection;
	}
	
	public UtenteDAO(int id, Connection connection) {
		this.id = id;
		this.connection = connection;
	}
	
	public Utente checkCredentials(String username, String password) throws SQLException {
		String query = "SELECT id, username, role FROM utente WHERE username = ? AND password = ?";

		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, username);
			pstatement.setString(2, password);

			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) { // corrispondenza non trovata
					return null;
				} else {
					result.next();
					Utente user = new Utente();
					user.setId(result.getInt("id"));
					user.setUsername(result.getString("username"));
					return user;
				}
			}
		}
	}
	
	
	public void addUtente (String username, String password, String mail, String nome, String cognome, String data) throws SQLException{
		String query = "INSERT INTO utente (username, password, mail, nome, cognome, data_nascita) VALUES (?, ?, ?, ?, ?, ?)";
		
		try(PreparedStatement pstatement = connection.prepareStatement(query)){
			pstatement.setString(1, username);
			pstatement.setString(2, password);
			pstatement.setString(3, mail);
			pstatement.setString(4, nome);
			pstatement.setString(5, cognome);
			pstatement.setString(6, data);
			
			pstatement.executeUpdate();
		}
		
	}
	
	public void creaRiunione(String titolo, String data, String ora, String durata, int num_max_partecipanti, int host) throws SQLException {
		
		String query = "INSERT INTO riunione (id, titolo, data, ora, durata, num_max_partecipanti, host) VALUES (?, ?, ?, ?, ?, ?, ?)";
		
		try(PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, this.id);
			pstatement.setString(2,  titolo);
			pstatement.setString(3,  data);
			pstatement.setString(4,  ora);
			pstatement.setString(5,  durata);
			pstatement.setInt(6, num_max_partecipanti);
			pstatement.setInt(7, host);
			
			pstatement.executeUpdate();
		}
	}
	
	public List<Riunione> trovaMieRiunioni() throws SQLException {
		
		List<Riunione> mieRiunioni = new ArrayList<Riunione>();
		String query = "SELECT * FROM riunione WHERE host = ? AND (? < data OR (? = data AND ? < ora + durata))";
		
		SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");  
	    Date date = new Date();
	    SimpleDateFormat formatter2 = new SimpleDateFormat("HH:mm:ss");  
	    Date time = new Date();
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, this.id);
			pstatement.setString(2, formatter1.format(date));
			pstatement.setString(3, formatter1.format(date));
			pstatement.setString(4, formatter2.format(time));
			
			try(ResultSet result = pstatement.executeQuery();) {
				while(result.next()) {
					Riunione riunione = new Riunione();
					
					riunione.setId(result.getInt("id"));
					riunione.setTitolo(result.getString("titolo"));
					riunione.setData(result.getString("data"));
					riunione.setOra(result.getString("ora"));
					riunione.setDurata(result.getString("durata"));
					riunione.setNum_max_partecipanti(result.getInt("num_max_partecipanti"));
					riunione.setHost(result.getInt("host"));
					
					mieRiunioni.add(riunione);
				}
			}
		}
		
		return mieRiunioni;
	}
	
	public List<Riunione> trovaRiunioniACuiSonoStatoInvitato() throws SQLException {
		
		List<Riunione> invitoRiunioni = new ArrayList<Riunione>();
		String query = "SELECT R.id, R.titolo, R.data, R.ora, R.durata, R.num_max_partecipanti, R.host"
				+ "FROM partecipanti AS P JOIN riunione AS R ON P.id_riunione = R.id"
				+ "WHERE P.id_partecipante = ? AND (? < R.data OR (? = R.data AND ? < R.ora + R.durata))";
		
		SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");  
	    Date date = new Date();
	    SimpleDateFormat formatter2 = new SimpleDateFormat("HH:mm:ss");  
	    Date time = new Date();
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, this.id);
			pstatement.setString(2, formatter1.format(date));
			pstatement.setString(3, formatter1.format(date));
			pstatement.setString(4, formatter2.format(time));
			
			try(ResultSet result = pstatement.executeQuery();) {
				while(result.next()) {
					Riunione riunione = new Riunione();
					
					riunione.setId(result.getInt("id"));
					riunione.setTitolo(result.getString("titolo"));
					riunione.setData(result.getString("data"));
					riunione.setOra(result.getString("ora"));
					riunione.setDurata(result.getString("durata"));
					riunione.setNum_max_partecipanti(result.getInt("num_max_partecipanti"));
					riunione.setHost(result.getInt("host"));
					
					invitoRiunioni.add(riunione);
				}
			}
		}
		
		return invitoRiunioni;
	}

}
