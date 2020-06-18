package it.polimi.tiw_exam.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw_exam.DAO.RiunioneDAO;
import it.polimi.tiw_exam.DAO.UtenteDAO;
import it.polimi.tiw_exam.beans.Riunione;
import it.polimi.tiw_exam.beans.Utente;


@WebServlet("/CreaRiunione")
public class CreaRiunione extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;   
  
    public CreaRiunione() {
        super();
        
    }
    
    public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");

		try {
			ServletContext context = getServletContext();
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);

		} catch (ClassNotFoundException e) {
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't get db connection");
		}
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Riunione riunione = new Riunione();
		Utente utente = (Utente) request.getSession().getAttribute("utente");
		int idRiunione;
		
		riunione = (Riunione) request.getAttribute("DatiRiunione");
		
		UtenteDAO utenteDAO = new UtenteDAO(utente.getId(), connection);
		
		try {
			utenteDAO.creaRiunione(riunione.getTitolo(), riunione.getData(), riunione.getOra(), riunione.getDurata(), riunione.getNum_max_partecipanti(), utente.getId());
			idRiunione = utenteDAO.trovaIDRiunione();
			RiunioneDAO riunioneDAO = new RiunioneDAO(idRiunione, connection);
		}
		catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Errore nella creazione della riunione");
		}
		
		String loginPath = "WEB-INF/HomePage.html";
		
		response.sendRedirect(loginPath);
	}

}
