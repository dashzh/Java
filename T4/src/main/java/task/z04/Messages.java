package task.z04;

import java.text.MessageFormat;

public interface Messages {

	public interface Info {
		String PROGRAM_NAME = "Terminal";
		String LAST_LOGGED_IN_USERS = "Last logged in users : ";
		String LONG_AGE_NOT_LOGGING_IN_USERS = "Long ago not logging in users : ";
		String NOT_AUTHORIZED_MENU = MessageFormat.format(
			"Menu :\n" +
			"Enter {0} for print all messages\n" +
			"Enter {1} for count all users\n" +
			"Enter {2} for print info about 10 last logged in users\n" +
			"Enter {3} for print info about 10 long ago not logging in users\n" +
			"Enter {4} for authorization\n" +
			"Enter {5} for print menu\n" +
			"Enter {6} for exit\n",
			TerminalApp.Action.PRINT_ALL,
			TerminalApp.Action.COUNT_USERS,
			TerminalApp.Action.PRINT_LAST_10,
			TerminalApp.Action.PRINT_LATE_10,
			TerminalApp.Action.LOGIN,
			TerminalApp.Action.PRINT_NA_MENU,
			TerminalApp.Action.EXIT
		);

		String AUTHORIZED_MENU = MessageFormat.format(
				"Menu :\n" +
				"Enter {0} for print all current user messages\n" +
				"Enter {1} for insert new message\n" +
				"Enter {2} for print menu\n" +
				"Enter {3} for deauthorization\n",
				TerminalApp.Action.PRINT_ALL_MINE,
				TerminalApp.Action.INSERT_MESSAGE,
				TerminalApp.Action.PRINT_MENU,
				TerminalApp.Action.LOG_OUT
			);

		String LOGIN_SUCCESS = "You are logged in";
		String LOGOUT_SUCCESS = "You are logged out";
	}

	public interface Error {
		String INVALID_INPUT = "Input is invalid";
		String INVALID_LOGIN_OR_PASSWORD = "Login or password are invalid";
	}

	String LINE = "=============================";
	String LOGO_SCREEN = MessageFormat.format("{0}{0}\n||\t\t\t{1}\t\t\t||\n{0}{0}", LINE, Info.PROGRAM_NAME);
	String LOGIN_PURPOSE = "Please enter login name : ";
	String PASSWORD_PURPOSE = "Please enter password : ";
	String YOUR_CHOISE = "your choise : ";
	String TYPE_YOUR_MESSAGE = "Type your message : ";
	String TOTAL_REGISTERED_USERS = "Total registered users : ";
}
