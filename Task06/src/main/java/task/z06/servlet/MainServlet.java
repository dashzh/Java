package task.z06.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import task.z04.ContentProvider;
import task.z04.Properties;
import task.z04.entity.AppUser;
import task.z04.sql.SQLUtils;
import task.z06.DoubleTuple;


public class MainServlet extends AbstractServlet {

	public static final String MAIN_SERVLET = "mainServlet";
	
	/**
	 * Типы действий пользователя
	 */
	public interface Action {
		int PRINT_ALL			= 1;
		int COUNT_USERS		= 2;
		int PRINT_LAST_10	= 3;
		int PRINT_LATE_10	= 4;
		int LOGIN					= 5;
		int PRINT_ALL_MINE	= 6;
		int INSERT_MESSAGE	= 7;
		int LOG_OUT				= 8;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/html; charset=UTF-8");
		HttpSession session = req.getSession();
		DoubleTuple<DataSource, AppUser> loginData = (DoubleTuple<DataSource, AppUser>) session.getAttribute("dbauth");
		
		String content = ContentProvider.printAllMessages();
		String additional = (loginData != null) ?
			ContentProvider.printAddMessageBlock() : ContentProvider.printLoginBlock() ;

		printInfo(resp.getWriter(), content, additional);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html; charset=UTF-8");
		HttpSession session = req.getSession();
		DoubleTuple<DataSource, AppUser> loginData = (DoubleTuple<DataSource, AppUser>) session.getAttribute("dbauth");

		String postMsg = req.getParameter("postmsg");
		String choise = req.getParameter("choise");
		String logout = req.getParameter("logout");
		String additional = (loginData != null) ? ContentProvider.printAddMessageBlock() : ContentProvider.printLoginBlock(); 
		String content = new String();

		if (logout != null && loginData != null) {
			session.removeAttribute("dbauth");
			seeOther(resp, MainServlet.MAIN_SERVLET);
		} else if (postMsg != null && loginData != null) {
			String message = req.getParameter("message");
			try {
				SQLUtils.insertNewMessage(loginData.first, loginData.second, message);
			} catch (SQLException e) {}
			seeOther(resp, MainServlet.MAIN_SERVLET);
		} else if (choise != null) {
			try {
				switch (Integer.parseInt(choise)) {
					case Action.PRINT_ALL :			content = ContentProvider.printAllMessages();				break;
					case Action.COUNT_USERS :	content = ContentProvider.countUsers();						break;
					case Action.PRINT_LAST_10 :	content = ContentProvider.printLoggingLog(10, true);	break;
					case Action.PRINT_LATE_10 :	content = ContentProvider.printLoggingLog(10, false);	break;
					default :;
				}
			} catch (NumberFormatException e) {}
		}

		printInfo(resp.getWriter(), content, additional);
	}

	// Метод инициализации сервлета
	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		initProperties(servletConfig);
	}

	private void initProperties(ServletConfig config) {
		String ipAddress = config.getInitParameter("Host");
		String dbName = config.getInitParameter("DataBaseName");
		String guestUser = config.getInitParameter("Login");
		int port = Integer.parseInt(config.getInitParameter("Port"));
		PROPERTIES = new Properties(ipAddress, port, dbName, guestUser);
	}

	public static Properties PROPERTIES = null;
	private static final long serialVersionUID = 5219320155836069023L;
}
