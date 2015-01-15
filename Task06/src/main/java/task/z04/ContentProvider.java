package task.z04;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.sql.DataSource;

import task.z04.entity.AppUser;
import task.z04.entity.UserMessage;
import task.z04.sql.SQLUtils;
import task.z06.DoubleTuple;
import task.z06.servlet.MainServlet;

public class ContentProvider {

	public static String printAddMessageBlock() {
		return insertMessageBlock;
	}

	private	final static String loginBlock =
		"<form id=\"auth-form\" action=\"authServlet\" method=\"get\"><div>" +
		"<table><thead></thead><tbody>" +
		"<tr><td>Логин</td><td><input type=\"text\" id=\"input-login\"></input></td>" +
		"<td><input type=\"submit\" value=\"Войти\" onClick=\"base64();\"></input></td>" +
		"</tr><tr><td>Пароль</td><td><input type=\"password\" id=\"input-pass\"></input>" +
		"</td><td></td></tr></tbody>" +
		"</table><script type=\"text/javascript\">" +
		"var base64 = function() {" +
		"var login = document.getElementById('input-login').value;" +
		"var pass = document.getElementById('input-pass').value;" +
		"document.getElementById('input-cred').value = window.btoa(login + ':' + pass);" + 
		"}</script>	<input type=\"hidden\" id=\"input-cred\" name=\"input-cred\"></input>" +
		"</div></form>";

	private final static String insertMessageBlock =
		"<form id=\"auth-form\" action=\"mainServlet\" method=\"post\"><div>" +
		"<input type=\"text\" name=\"message\">"+
		"</input><input type=\"submit\" name=\"postmsg\" value=\"Отправить\"></input>" +
		"</input><input type=\"submit\" name=\"logout\" value=\"Выйти\"></input>" +
		"</div></form>"; 

	public static String printLoginBlock() {
		return loginBlock;
	}
	
	public static String printAllMessagesOfUser(DoubleTuple<DataSource, AppUser> loginData) {
		return printAllMessagesOfUsers(loginData.first, new ArrayList<AppUser>(Arrays.asList(loginData.second)));
	}
	
	public static String printAllMessages() {
		DataSource dataSource = DataSourceManager.getGuestDataSource();
		return printAllMessagesOfUsers(dataSource, SQLUtils.getAllUsers(dataSource));	
	}
	
	private static String printAllMessagesOfUsers(DataSource dataSource, List<AppUser> users) {
		StringBuffer sb  = new StringBuffer();
		sb.append("<br><br><table align=\"left\" border=\"1\" cellpadding=\"1\" cellspacing=\"1\"><thead></thead><tbody>");

		Set<UserMessage> orderedMessages = new TreeSet<UserMessage>();
		for (AppUser user : users) {
			List<UserMessage> messages = SQLUtils.getAllMessages(dataSource, user);
			for (UserMessage message : messages) {
				message.setAuthor(user);
				orderedMessages.add(message);
			}
		}
		int i = 1;
		for (UserMessage message : orderedMessages) {
			String row = MessageFormat.format(
				"<tr><td>{0}</td><td>{1}</td><td>{2}</td><td>{3}</td><td>{4}</td></tr>",
				String.valueOf(i),
				message.getAuthor().getName(),
				message.getAuthor().getLastName(),
				MainServlet.USER_DATEFORMAT.format(message.getCreationDate().getTime()),
				message.getText()
			);
			sb.append(row);
			i++;
		}

		sb.append("</tbody></table>");
		return sb.toString();
	}

	public static String countUsers() {
		DataSource dataSource = DataSourceManager.getGuestDataSource();
		int size = SQLUtils.getAllUsers(dataSource).size();
		return MessageFormat.format("<br><br><p>Всего зарегистрированных пользователей : {0}<p>", size);
	}

	public static String printLoggingLog(int userLimit, boolean descOrder) {
		StringBuffer sb  = new StringBuffer();
		sb.append("<br><br><table align=\"left\" border=\"1\" cellpadding=\"1\" cellspacing=\"1\"><thead></thead><tbody>");
		DataSource dataSource = DataSourceManager.getGuestDataSource();
		List<AppUser> users = descOrder ? SQLUtils.getLastLoginUsers(dataSource, userLimit) :
			SQLUtils.getLateLoginUsers(dataSource, userLimit);
		for (int i = 0; i < users.size(); i++) {
			AppUser user = users.get(i);
			String row = MessageFormat.format(
				"<tr><td>№{0}</td><td>{1}</td><td>{2}</td><td>{3}</td></tr>",
				String.valueOf(i + 1),
				SQLUtils.USER_DATEFORMAT.format(user.getLastLogonDate().getTime()),
				user.getName(),
				user.getLastName()
			);
			sb.append(row);
		}
		sb.append("</tbody></table>");
		return sb.toString();
	}
}