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

public class RiunioneDAO {
	private int id;
	private Connection connection;
	
	public RiunioneDAO(int id, Connection connection) {
		this.id = id;
		this.connection = connection;
	}
	
}
