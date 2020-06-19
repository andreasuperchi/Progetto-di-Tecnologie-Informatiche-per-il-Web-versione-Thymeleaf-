package it.polimi.tiw_exam.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw_exam.DAO.UtenteDAO;
import it.polimi.tiw_exam.beans.Riunione;
import it.polimi.tiw_exam.beans.Utente;

@WebServlet("/GoToAnagrafica")
public class GoToAnagrafica extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;

    public GoToAnagrafica() {
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
			throw new UnavailableException("Errore nel caricamento del driver del database.");
		} catch (SQLException e) {
			throw new UnavailableException("Errore nella connessione al database.");
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Utente utente = (Utente) request.getSession().getAttribute("utente");
		int host = utente.getId();
		ArrayList<Integer> lista_invitati = new ArrayList<>();		
		UtenteDAO uDAO = new UtenteDAO(host,connection);
		List<Utente> daInvitare = null;
		int num_tentativi = 0;
		
		String titolo = request.getParameter("titolo");
		String data = request.getParameter("data");
		String ora = request.getParameter("ora");
		String durata = request.getParameter("durata");
		int num_max_partecipanti = Integer.parseInt(request.getParameter("numero_max_partecipanti"));
		
		Riunione riunione = new Riunione();
		riunione.setTitolo(titolo);
		riunione.setData(data);
		riunione.setOra(ora);
		riunione.setDurata(durata);
		riunione.setNum_max_partecipanti(num_max_partecipanti);
		riunione.setHost(host);
		
		try {
			daInvitare = uDAO.trovaPersoneDaInvitare();
		} catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Errore nel caricamento");
		}
		
		String path = "WEB-INF/Anagrafica.html";
		
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("DatiRiunione", riunione);
		ctx.setVariable("daInvitare", daInvitare);
		ctx.setVariable("lista_invitati", lista_invitati);
		ctx.setVariable("num_tentativi", num_tentativi);
		
		templateEngine.process(path, ctx, response.getWriter());
	}

}
