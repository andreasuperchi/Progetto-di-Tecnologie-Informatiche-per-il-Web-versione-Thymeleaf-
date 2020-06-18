package it.polimi.tiw_exam.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw_exam.DAO.UtenteDAO;
import it.polimi.tiw_exam.beans.Riunione;
import it.polimi.tiw_exam.beans.Utente;


@WebServlet("/GoToHomePage")
public class GoToHomePage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	TemplateEngine templateEngine;

       
    /**
     * @see HttpServlet#HttpServlet()
     */
	
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
	
	
    public GoToHomePage() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Utente utente = null;
		HttpSession session = request.getSession();
		utente = (Utente) session.getAttribute("utente");
		UtenteDAO utenteDAO = new UtenteDAO(utente.getId(), connection);
		List <Riunione> riunioniInvitato = null;
		List <Riunione> riunioniIndette = null;
		
		try {
			//riunioniInvitato = utenteDAO.trovaRiunioniACuiSonoStatoInvitato();
			riunioniIndette = utenteDAO.trovaMieRiunioni();
		}
		catch (SQLException e){
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Loading Error!");
		}
		
		String path = "WEB-INF/HomePage.html";
		
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("riunioniIndette", riunioniIndette);
		ctx.setVariable("riunioniInvitato", riunioniInvitato);
		

		templateEngine.process(path, ctx, response.getWriter());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
