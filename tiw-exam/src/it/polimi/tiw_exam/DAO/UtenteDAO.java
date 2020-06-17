package it.polimi.tiw_exam.DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw_exam.beans.Utente;



public class UtenteDAO {
	private Connection connection;
	
	public UtenteDAO(Connection connection) {
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
	
	
	public void addUtente ( int id, String username, String password, String mail, String nome, String cognome, Date data) throws SQLException{
		String query = "INSERT INTO utente (username, password, mail, nome, cognome, data) VALUES (?, ?, ?, ?, ?, ?)";
		
		try(PreparedStatement pstatement = connection.prepareStatement(query)){
			pstatement.setString(1, username);
			pstatement.setString(2, password);
			pstatement.setString(3, mail);
			pstatement.setString(4, nome);
			pstatement.setString(5, cognome);
			pstatement.setDate(6, data);
			
			pstatement.executeUpdate();
		}
		
	}
}
