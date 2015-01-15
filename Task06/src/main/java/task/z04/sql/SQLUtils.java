package task.z04.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.sql.DataSource;

import task.z04.entity.AppUser;
import task.z04.entity.UserMessage;
import task.z04.exception.NotSupportedArgumentTypeException;

/**
 * Вспомогательный класс содержащий рутины для работы с БД
 */
public class SQLUtils {

	public interface QueryTemplate {
		String UPD_LAST_LOGON_DATE_BY_ACCOUNT_ID = "update account set last_logon_date = current_timestamp where id = ?";
		String GET_ACCOUNT_ID_AND_USER_ID_BY_LOGIN = "select id, user_id from account where login = ?";
		String GET_NAME_AND_LAST_NAME_BY_USER_ID = "select name, last_name from appuser where id = ?";
		String GET_LAST_LOGON_DATE_BY_ACCOUNT_ID = "select last_logon_date from account where id = ?";
		String GET_ALL_MESSAGES_BY_USER_ID =
			"select m.id, m.creation_date, m.text from account a inner join appuser u on (u.id = a.user_id) inner join message m on (m.user_id = u.id) where u.id = ?";
		String GET_ALL_USERS = "select u.id, u.name, u.last_name, a.last_logon_date from account a inner join appuser u on (a.user_id = u.id)";
		String INS_NEW_MESSAGE_BY_USER_ID = "insert into message (id, user_id, creation_date, text) values (nextval('seq_message_id'), ?, current_timestamp, ?)";
	}

	/**
	 * Добавляет новое сообщение указанного пользователя.
	 */
	public static void insertNewMessage(DataSource dataSource, AppUser user, String message) throws SQLException {
		try {
			SQLUtils.executeUpdate(
				dataSource,
				SQLUtils.QueryTemplate.INS_NEW_MESSAGE_BY_USER_ID,
				new Object[]{ user.getId(), message }
			);
		} catch (NotSupportedArgumentTypeException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Возвращает всех пользователей зарегистрированных в БД.
	 */
	public static List<AppUser> getAllUsers(DataSource dataSource) {
		List<AppUser> users = new ArrayList<AppUser>();
		List <String[]> usersInfo =  new ArrayList<String[]>();

		try {
			SQLUtils.executeQuery(
				dataSource,
				SQLUtils.QueryTemplate.GET_ALL_USERS,
				new Object[0],
				usersInfo
			);
		} catch (NotSupportedArgumentTypeException | SQLException e) {
			throw new RuntimeException(e);
		}

		for (String[] tuple : usersInfo) {
			Calendar calendar = null;
			try {
				calendar = parseDateFromPostgresFormat(tuple[3]);
			} catch (ParseException e) {}
			AppUser user = new AppUser(Integer.parseInt(tuple[0]), tuple[1], tuple[2], calendar);
			users.add(user);
		}
		
		return users;
	}

	public static List<AppUser> getLastLoginUsers(DataSource dataSource, int usersLimit) {
		List<AppUser> allUsers = getAllUsers(dataSource);
		TreeSet<AppUser> users = new TreeSet<AppUser>(Collections.reverseOrder());
		users.addAll(allUsers);

		List<AppUser> selected = new ArrayList<AppUser>();
		Iterator<AppUser> iterator = users.iterator();
		for (int i = 1; i <= usersLimit && iterator.hasNext(); i++)
			selected.add(iterator.next());

		return selected;
	}
	
	
	public static List<AppUser> getLateLoginUsers(DataSource dataSource, int usersLimit) {
		List<AppUser> allUsers = getAllUsers(dataSource);
		Set<AppUser> users = new TreeSet<AppUser>(allUsers);

		List<AppUser> selected = new ArrayList<AppUser>();
		Iterator<AppUser> iterator = users.iterator();
		for (int i = 1; i <= usersLimit && iterator.hasNext(); i++)
			selected.add(iterator.next());

		return selected;
	}
	
	/**
	 * Возвращает всех пользователей зарегистрированных в БД.
	  */
	public static List<UserMessage> getAllMessages(DataSource dataSource, AppUser user) {
		List<UserMessage> list = new ArrayList<UserMessage>();
		List<String[]> messages = new ArrayList<String[]>();

		try {
			SQLUtils.executeQuery(
				dataSource,
				SQLUtils.QueryTemplate.GET_ALL_MESSAGES_BY_USER_ID,
				new Object[]{ user.getId() },
				messages
			);
		} catch (NotSupportedArgumentTypeException | SQLException e) {
			throw new RuntimeException(e);
		}

		for (String[] tuple : messages) {
			Calendar calendar = null;
			try {
				calendar = parseDateFromPostgresFormat(tuple[1]);
			} catch (ParseException e) {}
			UserMessage msg = new UserMessage(Integer.parseInt(tuple[0]), user.getId(), calendar, tuple[2]);
			list.add(msg);
		}

		return list;
	}
	
	public static final SimpleDateFormat POSTGRES_DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat USER_DATEFORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	
	/**
	 * Парсит дату представленную во внутреннем представлении БД PostgreSQL
	 */
	public static Calendar parseDateFromPostgresFormat(String potentilaDate) throws ParseException {
		String potentialTime = potentilaDate.substring(0, potentilaDate.indexOf('.'));
		Date date = POSTGRES_DATEFORMAT.parse(potentialTime);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		return calendar;
	}
	
	// В случаях, когда выполняются запросы над не очень большим объемом данных, т.к.
	// они зачитывают все данные из результата :
	
	/**
	 * Выполняет операцию update по указанному шаблону с указанными аргументами в БД представленной
	 * указанным источником данных.
	 * @param ds Источник данных через который производится операция
	 * @param preparedQueryTemplate шаблон SQL запроса определенный в {@link SQLUtils#QueryTemplate}
	 * @param args аргументы для подстановки в запрос. Поддерживаются аргументы основных типов : int, long,
	 * float, double, boolean, String
	 * @throws SQLException в случае некорректного выражения результирующего SQL запроса
	 * @throws NotSupportedArgumentTypeException если передан аргумент неподдерживаемого типа
	 */
	public static void executeUpdate(DataSource ds, String preparedQueryTemplate, Object[] args)
		throws SQLException, NotSupportedArgumentTypeException {
		try (Connection connection = ds.getConnection()) {
			try (PreparedStatement statement = connection.prepareStatement(preparedQueryTemplate)) {
				setArgs(statement, args);
				statement.executeUpdate();
			}
		}		
	}

	/**
	 * Выполняет операцию select по указанному шаблону с указанными аргументами в БД представленной
	 * указанным источником данных. 
	 * @param ds Источник данных через который производится операция
	 * @param preparedQueryTemplate шаблон SQL запроса определенный в {@link SQLUtils#QueryTemplate}
	 * @param args аргументы для подстановки в запрос. Поддерживаются аргументы основных типов : int, long,
	 * float, double, boolean, String
	 * @param result список в который будет записан результат выполнения запроса
	 * @throws SQLException в случае некорректного выражения результирующего SQL запроса
	 * @throws NotSupportedArgumentTypeException если передан аргумент неподдерживаемого типа
	 */
	public static void executeQuery(DataSource ds, String preparedQueryTemplate, Object[] args, List<String[]> result)
		throws SQLException, NotSupportedArgumentTypeException {
		try (Connection connection = ds.getConnection()) {
			try (PreparedStatement statement = connection.prepareStatement(preparedQueryTemplate)) {
				setArgs(statement, args);
				try (ResultSet rs = statement.executeQuery()) {
					ResultSetMetaData metadata = rs.getMetaData();
					int columnsNumber = metadata.getColumnCount();
					while (rs.next()) {
						String[] tuple = new String[columnsNumber];
						for (int i = 1; i <= columnsNumber; i++)
							tuple[i - 1] = rs.getString(i);
						result.add(tuple);
					}
				}
			}
		}
	}
	
	private static void setArgs(PreparedStatement statement, Object[] args)
		throws NotSupportedArgumentTypeException, SQLException {
		for (int i = 0; i < args.length; i++) {
			if (args[i] instanceof Integer) {
				statement.setInt(i + 1, (Integer) args[i]);
			} else if (args[i] instanceof Long) {
				statement.setLong(i + 1, (Long) args[i]);
			} else if (args[i] instanceof Double) {
				statement.setDouble(i + 1, (Double) args[i]);
			} else if (args[i] instanceof Float) {
				statement.setFloat(i + 1, (Float) args[i]);
			} else if (args[i] instanceof Boolean) {
				statement.setBoolean(i + 1, (Boolean) args[i]);
			} else if (args[i] instanceof String) {
				statement.setString(i + 1, (String) args[i]);
			} else
				throw new NotSupportedArgumentTypeException();
		}		
	}
}
