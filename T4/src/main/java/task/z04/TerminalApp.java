package task.z04;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Scanner;

import javax.sql.DataSource;

import task.z04.entity.AppUser;
import task.z04.entity.UserMessage;
import task.z04.exception.AuthorizationException;
import task.z04.exception.DBStructuralIntegrityException;
import task.z04.sql.SQLUtils;

public class TerminalApp {

	/**
	 * Типы действий пользователя
	 */
	public interface Action {
		int PRINT_ALL			= 1;
		int COUNT_USERS		= 2;
		int PRINT_LAST_10	= 3;
		int PRINT_LATE_10	= 4;
		int LOGIN					= 5;
		int PRINT_NA_MENU	= 6;
		int EXIT						= 7;
		int PRINT_ALL_MINE	= 8;
		int INSERT_MESSAGE	= 9;
		int PRINT_MENU			= 10;
		int LOG_OUT				= 11;
	}

	private void insertNewMessage() throws SQLException {
		System.out.println(Messages.TYPE_YOUR_MESSAGE);
		String message = null;
		if (scanner.hasNext()) {
			message = scanner.nextLine().trim();
		}

		DataSource dataSource = DataSourceManager.getDataSource();
		SQLUtils.insertNewMessage(dataSource, DataSourceManager.getCurrentUser(), message);
		System.out.println();
	}
	
	private void printAllMessagesForCurrentUser() {
		DataSource dataSource = DataSourceManager.getDataSource();
		AppUser user = DataSourceManager.getCurrentUser();
		List<UserMessage> messages = SQLUtils.getAllMessages(dataSource, user);
		
		for (UserMessage msg : messages) {
			System.out.println(
				MessageFormat.format(
					"{0} {1} {2} {3}",
					user.getName(),
					user.getLastName(),
					SQLUtils.USER_DATEFORMAT.format(msg.getCreationDate().getTime()),
					msg.getText()
				)
			);
		}
		System.out.println();
	}
	
	private void performLogOut() throws DBStructuralIntegrityException {
		try {
			DataSourceManager.tryLogOut();
			if (!DataSourceManager.tryLogInAsGuest())
				throw new AuthorizationException(AuthorizationException.UNABLE_LOG_IN_AS_GUEST);
		} catch (AuthorizationException e) {
			throw new RuntimeException(e);
		}
		System.out.println(Messages.Info.LOGOUT_SUCCESS);
		printNotAuthorizedMenu();
	}
	
	private boolean performLogIn() throws DBStructuralIntegrityException {
		String login			= null;
		String password	= null;

		try {
			if (DataSourceManager.isLoggedAsGuest())
				DataSourceManager.tryLogOut();
		} catch (AuthorizationException e) {
			throw new RuntimeException(e);
		}
		
		System.out.print(Messages.LOGIN_PURPOSE);
		if (scanner.hasNext()) {
			login = scanner.nextLine().trim();
		}

		System.out.print(Messages.PASSWORD_PURPOSE);
		if (scanner.hasNext()) {
			password = scanner.nextLine().trim();
		}

		System.out.println();
		boolean loginSuccess = false;
		try {
			System.out.println(
				(loginSuccess = DataSourceManager.tryLogIn(login, password)) ?
					Messages.Info.LOGIN_SUCCESS : Messages.Error.INVALID_LOGIN_OR_PASSWORD
			);
			if (DataSourceManager.getCurrentUser() != null)
				printAuthorizedMenu();
			else
				printNotAuthorizedMenu();
		} catch (AuthorizationException e1) {
			throw new RuntimeException(e1);
		}

		System.out.println();
		return loginSuccess;
	}
	//Давно не осуществляли вход
	private void printLate10LoggedIn() {
		DataSource dataSource = DataSourceManager.getDataSource();
		List<AppUser> users = SQLUtils.getLateLoginUsers(dataSource, 10);
		System.out.println();
		System.out.println(Messages.Info.LONG_AGE_NOT_LOGGING_IN_USERS);
		for (int i = 0; i < users.size(); i++) {
			AppUser user = users.get(i);
			System.out.println(
				MessageFormat.format(
					"№{0} {1} {2} {3}",
					String.valueOf(i + 1),
					SQLUtils.USER_DATEFORMAT.format(user.getLastLogonDate().getTime()),
					user.getName(),
					user.getLastName()
				)
			);
		}
	}

	private void printAuthorizedMenu() {
		System.out.println();
		System.out.print(Messages.Info.AUTHORIZED_MENU);
	}
	
	private void printNotAuthorizedMenu() {
		System.out.println();
		System.out.print(Messages.Info.NOT_AUTHORIZED_MENU);
	}
	 
	private void printLast10LoggedIn() {
		DataSource dataSource = DataSourceManager.getDataSource();
		List<AppUser> users = SQLUtils.getLastLoginUsers(dataSource, 10);
		System.out.println();
		System.out.println(Messages.Info.LAST_LOGGED_IN_USERS);
		for (int i = 0; i < users.size(); i++) {
			AppUser user = users.get(i);
			System.out.println(
				MessageFormat.format(
					"№{0} {1} {2} {3}",
					String.valueOf(i + 1),
					SQLUtils.USER_DATEFORMAT.format(user.getLastLogonDate().getTime()),
					user.getName(),
					user.getLastName()
				)
			);
		}
	}
	//Зарегистрированные пользователи
	private void countUsers() {
		DataSource dataSource = DataSourceManager.getDataSource();
		int size = SQLUtils.getAllUsers(dataSource).size();
		System.out.println();
		System.out.println(Messages.TOTAL_REGISTERED_USERS + size);
	}
	
	private void printAllMessages() {
		DataSource dataSource = DataSourceManager.getDataSource();
		List<AppUser> users = SQLUtils.getAllUsers(dataSource);
		System.out.println();
		for (AppUser user : users) {
			List<UserMessage> messages = SQLUtils.getAllMessages(dataSource, user);
			for (UserMessage msg : messages) {
				System.out.println(
					MessageFormat.format(
						"{0} {1} {2} {3}",
						user.getName(),
						user.getLastName(),
						SQLUtils.USER_DATEFORMAT.format(msg.getCreationDate().getTime()),
						msg.getText()
					)
				);
			}
		}
	}
	
	/**
	 * Запускает приложение терминала
	 */
	public void start() {
		if (stopped)
			return;
		System.out.println(Messages.LOGO_SCREEN);
		printNotAuthorizedMenu();
		System.out.print(Messages.YOUR_CHOISE);

		try {
			if (!DataSourceManager.tryLogInAsGuest())
				throw new AuthorizationException(AuthorizationException.UNABLE_LOG_IN_AS_GUEST);

			boolean exit = false;
			boolean isAuthorized = false;
			do {
				int minAction = isAuthorized ? Action.PRINT_ALL_MINE : Action.PRINT_ALL;
				int maxAction = isAuthorized ? Action.LOG_OUT : Action.EXIT;
				switch (getActionFromUser(minAction, maxAction)) {
					case Action.PRINT_ALL :			printAllMessages();						break;
					case Action.COUNT_USERS :		countUsers();							break;
					case Action.PRINT_LAST_10 :		printLast10LoggedIn();					break;
					case Action.PRINT_LATE_10 : 	printLate10LoggedIn();					break;
					case Action.LOGIN :				isAuthorized = performLogIn();			break;
					case Action.PRINT_NA_MENU : 	printNotAuthorizedMenu();				break;
					case Action.EXIT :				exit = true;							break;
					case Action.PRINT_ALL_MINE : 	printAllMessagesForCurrentUser();		break;
					case Action.INSERT_MESSAGE : 	insertNewMessage();						break;
					case Action.PRINT_MENU :		printAuthorizedMenu();					break;
					case Action.LOG_OUT : {
							performLogOut();
							isAuthorized = false;
						}
						break;
					default:;
				}
				if (!exit)
					System.out.print(Messages.YOUR_CHOISE);
			} while (!exit);
		} catch (AuthorizationException | DBStructuralIntegrityException | SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	private int getActionFromUser(int minValue, int maxValue) {
		int choiseNumber = 0;
		boolean pass = false;
		do {
			if (scanner.hasNext()) {
				String choise = scanner.nextLine().trim();
				try {
					choiseNumber = Integer.parseInt(choise);
					if (choiseNumber >= minValue  && choiseNumber <= maxValue)
						pass = true;
					else
						throw new NumberFormatException();
				} catch (NumberFormatException e) {
					System.out.println(Messages.Error.INVALID_INPUT);
					System.out.print(Messages.YOUR_CHOISE);
				}
			}
		} while (!pass);
		return choiseNumber;
	}

	private boolean stopped;
	private Scanner scanner = new Scanner(System.in);
}