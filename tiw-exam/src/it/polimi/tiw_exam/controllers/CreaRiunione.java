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

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Utente utente = (Utente) request.getSession().getAttribute("utente");
		Riunione riunione = new Riunione();
		int idRiunione;
		
		UtenteDAO utenteDAO = new UtenteDAO(utente.getId(), connection);
		
		int num_tentativi = Integer.parseInt(request.getParameter("num_tentativi"));
		
		String titolo = request.getParameter("titolo");
		String data = request.getParameter("data");
		String ora = request.getParameter("ora");
		String durata = request.getParameter("durata");
		int num_max_partecipanti = Integer.parseInt(request.getParameter("num_max_p"));
		int host = Integer.parseInt(request.getParameter("host"));
		riunione.setTitolo(titolo);
		riunione.setData(data);
		riunione.setOra(ora);
		riunione.setDurata(durata);
		riunione.setNum_max_partecipanti(num_max_partecipanti);
		riunione.setHost(host);
		
		ArrayList<Integer> listaInvitati = new ArrayList<>();
		List<Utente> daInvitare = null;
		
		for(String s : request.getParameterValues("id_invitato")) {
			listaInvitati.add(Integer.parseInt(s));
		}
		
		try {
			daInvitare = utenteDAO.trovaPersoneDaInvitare();
		} catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Errore nel caricamento");
		}
		
		if(listaInvitati.size() > num_max_partecipanti) {
			num_tentativi++;
			
			String path;
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			
			if(num_tentativi == 3) {
				path = "/WEB-INF/Errore.html";
			} else {
				path = "/WEB-INF/Anagrafica.html";
				
				ctx.setVariable("lista_invitati", listaInvitati);
				ctx.setVariable("daInvitare", daInvitare);
				ctx.setVariable("DatiRiunione", riunione);
				ctx.setVariable("diff", listaInvitati.size() - num_max_partecipanti);
				ctx.setVariable("num_tentativi", num_tentativi);
			}
			
			templateEngine.process(path, ctx, response.getWriter());
		} else {			
			try {
				utenteDAO.creaRiunione(titolo, data, ora, durata, num_max_partecipanti, host);
				idRiunione = utenteDAO.trovaIDRiunione();
				RiunioneDAO riunioneDAO = new RiunioneDAO(idRiunione, connection);
				for(int i : listaInvitati) {
					riunioneDAO.addPartecipante(i);
				}
			}
			catch(SQLException e) {
				response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Errore nella creazione della riunione");
			}
			
			String path = getServletContext().getContextPath();
			String target = "/GoToHomePage";
			
			path = path + target;
			
			response.sendRedirect(path);
		}
		
	}
	

}
