package task5;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/html; charset=UTF-8");
		PrintWriter out = resp.getWriter();

		// Формирование даты
		String currentDateTime = USER_DATEFORMAT.format(Calendar.getInstance().getTime());
		out.print(htmlTemplate.replace("%MESSAGE%", currentDateTime));
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

	}

	// Метод инициализации сервлета
	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		// Считываие параметра с расположением статического ресурса
		String resource = servletConfig.getInitParameter("demoPage");
		demoPageLocation =  servletConfig.getServletContext().getRealPath(resource);
		
		// Считывание данных из шаблона в переменную
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream(demoPageLocation), StandardCharsets.UTF_8))) {

	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append(System.lineSeparator());
	            line = br.readLine();
	        }
	        htmlTemplate = sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static final SimpleDateFormat USER_DATEFORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	private String htmlTemplate;
	private static String demoPageLocation;
	private static final long serialVersionUID = 5219320155836069023L;
}
