package task.z06.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Base64;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import task.z04.DataSourceManager;
import task.z04.entity.AppUser;
import task.z04.exception.AuthorizationException;
import task.z04.exception.DBStructuralIntegrityException;
import task.z06.DoubleTuple;

public class AuthorizedServlet extends AbstractServlet {

	public static final String AUTHORIZATION_SERVLET = "authServlet";
	
	private DoubleTuple<String, String> extractCredentials(HttpServletRequest request) {
		String encodedValue =  request.getParameter("input-cred");
		if (encodedValue == null || encodedValue.isEmpty())
			return null;
		byte[] decodedValue = Base64.getDecoder().decode(encodedValue);
		String decoded = new String(decodedValue);
		String login = decoded.substring(0, decoded.indexOf(":"));
		String password = decoded.substring(decoded.indexOf(":") + 1);
		DoubleTuple<String, String> credentials = new DoubleTuple<String, String>(login, password);

		return credentials;
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/html; charset=UTF-8");
		HttpSession session = req.getSession();
	    if (session.getAttribute("dbauth") == null) {
	    	DoubleTuple<String, String> credentials = extractCredentials(req);
	    	if (credentials == null)
	    		seeOther(resp, MainServlet.MAIN_SERVLET);
	    	try {
	    		// Осуществление входа в БД
	    		DoubleTuple<DataSource, AppUser> loginData = DataSourceManager.tryLogIn(credentials.first, credentials.second);
	    		session.setAttribute("dbauth", loginData);
	    	} catch (SQLException | DBStructuralIntegrityException | AuthorizationException e) {
	    		PrintWriter writer = resp.getWriter();
	    		e.printStackTrace(writer);
	    	}
	    }
		seeOther(resp, MainServlet.MAIN_SERVLET);	
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		seeOther(resp, MainServlet.MAIN_SERVLET);	
	}
	
	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
	}

	private static final long serialVersionUID = -3988422067060656591L;
}
