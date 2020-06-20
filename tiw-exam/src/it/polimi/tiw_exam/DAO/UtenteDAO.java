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
		String query = "SELECT id, username FROM utente WHERE username = ? AND password = ?";

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
		String query = "INSERT INTO riunione (titolo, data, ora, durata, ora_fine, num_max_partecipanti, host) VALUES (?, ?, ?, ?, ?, ?, ?)";
		
		String ora_frammenti[] = ora.split(":");
		String durata_frammenti[] = durata.split(":");
		
		int ora_fine_frammenti[] = new int[3];
		
		for(int i = 0; i < ora_frammenti.length; i++) {
			ora_fine_frammenti[i] = Integer.parseInt(ora_frammenti[i]) + Integer.parseInt(durata_frammenti[i]);
		}
		
		if(ora_fine_frammenti[2] > 59) {
			ora_fine_frammenti[2] -= 60;
			ora_fine_frammenti[1]++;
		}
		
		if(ora_fine_frammenti[1] > 59) {
			ora_fine_frammenti[1] -= 60;
			ora_fine_frammenti[0]++;
		}
		
		String ora_fine = ora_fine_frammenti[0] + ":" + ora_fine_frammenti[1] + ":" + ora_fine_frammenti[2];
		
		System.out.println(ora_fine);
		
		try(PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1,  titolo);
			pstatement.setString(2,  data);
			pstatement.setString(3,  ora);
			pstatement.setString(4,  durata);
			pstatement.setString(5, ora_fine);
			pstatement.setInt(6, num_max_partecipanti);
			pstatement.setInt(7, host);
			
			pstatement.executeUpdate();
		}
	}
	
	public List<Riunione> trovaMieRiunioni() throws SQLException {
		
		List<Riunione> mieRiunioni = new ArrayList<Riunione>();
		String query = "SELECT * FROM riunione WHERE host = ? AND (? < data OR (? = data AND ? < ora_fine))";
		
		Date date = new Date();
		SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd@HH:mm:ss");
		String formatted = formatter1.format(date);
	    
		String[] date_time = formatted.split("@");
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, this.id);
			pstatement.setString(2, date_time[0]);
			pstatement.setString(3, date_time[0]);
			pstatement.setString(4, date_time[1]);
			
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
		String query = "SELECT R.id, R.titolo, R.data, R.ora, R.durata, R.num_max_partecipanti, R.host FROM partecipanti AS P JOIN riunione AS R ON P.id_riunione = R.id WHERE P.id_partecipante = ? AND (? < R.data OR (? = R.data AND ? < R.ora_fine))";
		
		Date date = new Date();
		SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd@HH:mm:ss");
		String formatted = formatter1.format(date);
	    
		String[] date_time = formatted.split("@");
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, this.id);
			pstatement.setString(2, date_time[0]);
			pstatement.setString(3, date_time[0]);
			pstatement.setString(4, date_time[1]);
			
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
	
	public List<Utente> trovaPersoneDaInvitare() throws SQLException {
		List<Utente> daInvitare = new ArrayList<Utente>();
		String query = "SELECT id, nome, cognome FROM utente WHERE id != ?";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, this.id);
			
			try(ResultSet result = pstatement.executeQuery();) {
				while(result.next()) {
					Utente utente = new Utente();
					
					utente.setId(result.getInt("id"));
					utente.setNome(result.getString("nome"));
					utente.setCognome(result.getString("cognome"));
					
					daInvitare.add(utente);
				}
			}
		}
		
		return daInvitare;
	}
	
	public int trovaIDRiunione() throws SQLException{
		int id = 0;
		String query = "SELECT id FROM riunione WHERE host = ? ORDER BY id DESC LIMIT 1";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, this.id);

			try(ResultSet result = pstatement.executeQuery();) {
				while(result.next()) {
					id = result.getInt("id");
				}
			}
		}
		return id;
	}

}
