package task.z06.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractServlet extends HttpServlet {

	protected void printInfo(PrintWriter out, String content, String loginBlock) {
		// Формируем дату
		String currentDateTime = USER_DATEFORMAT.format(Calendar.getInstance().getTime());
		String body = htmlBody;
		body = body .replace("%TIME%", currentDateTime);
		body = body.replace("%CONTENT%", content);
		body = body.replace("%LOGIN_BLOCK%", loginBlock);
		out.print(body);
	}

	protected void seeOther(HttpServletResponse resp, String pathOfOtherToSee)
	{
	    resp.setStatus(HttpServletResponse.SC_SEE_OTHER);
	    resp.setHeader("Location", pathOfOtherToSee);       
	}

	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		ServletContext context = servletConfig.getServletContext();
		// Считываем параметр с расположением ресурса тела сервлета
		String resource = servletConfig.getInitParameter("bodyTemplate");
		if (resource == null)
			return;
		String bodyLocation = context.getRealPath(resource);
		try {
			readBodyFromFile(new File(bodyLocation));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void readBodyFromFile(File file) throws IOException {
		try (BufferedReader br = new BufferedReader(
			new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
			}
			htmlBody = sb.toString();
		}
	}

	
	public static final SimpleDateFormat USER_DATEFORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	private String htmlBody;
	private static final long serialVersionUID = -4963965068610023723L;
}
