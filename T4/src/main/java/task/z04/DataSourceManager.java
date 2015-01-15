package task.z04;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.sql.DataSource;

import org.postgresql.ds.PGSimpleDataSource;

import task.z04.entity.AppUser;
import task.z04.exception.AuthorizationException;
import task.z04.exception.DBStructuralIntegrityException;
import task.z04.exception.NotSupportedArgumentTypeException;
import task.z04.sql.SQLUtils;

/**
 * Менеджер доступа к БД. Отвечает за вопросы авторизации пользователей.
 */
public final class DataSourceManager {

	private static DataSource dataSource;
	private static AppUser currentUser;

	/**
	 * Выполняет подключение к БД от имени гостевого пользователя
	 * @return <b>true</b> в случае успешной авторизации и <b>false</b> иначе
	 * @throws AuthorizationException в случае когда осуществляется авторизация без предварительной деавторизации
	 */		
	public static boolean tryLogInAsGuest() throws AuthorizationException {
		try {
			return loggedAsGuest = tryLogIn(EntryPoint.PROPERTIES.getGuestUser(), EntryPoint.PROPERTIES.getGuestUser());	
		} catch (DBStructuralIntegrityException e) { // Ïîäàâëÿåì èñêëþ÷åíèå ò.ê. îíî íå ìîæåò áûòü âîçáóæäåíî
			return false;
		}
	}

	private static boolean loggedAsGuest = false;

	/**
	 * Возвращает признак того является ли текущий пользователь гостевым
	 * @returns признак того является ли текущий пользователь гостевым
	*/
	public static boolean isLoggedAsGuest() { return loggedAsGuest; }


	/**
	 * Выполняет подключение пользователя с указанными логином / паролем к БД.
	 * @param loginName логин
	 * @param password пароль
	 * @return <b>true</b> в случае успешной авторизации и <b>false</b> иначе
	 * @throws AuthorizationException в случае когда осуществляется авторизация для уже авторизованного пользователя
	 * @throws DBStructuralIntegrityException в случаях когда в БД отсутствует пользователь с указанным логином
	 * или в БД больше одного пользователя с указанным логином 
	 */
	public static boolean tryLogIn(String loginName, String password) throws AuthorizationException, DBStructuralIntegrityException {
		if (dataSource != null)
			throw new AuthorizationException(AuthorizationException.ALREADY_LOGGED);

		PGSimpleDataSource pgDataSource = new PGSimpleDataSource();
		pgDataSource.setServerName(EntryPoint.PROPERTIES.getIpAddress());
		pgDataSource.setPortNumber(EntryPoint.PROPERTIES.getPort());
		pgDataSource.setDatabaseName(EntryPoint.PROPERTIES.getDbName());
		pgDataSource.setUser(loginName);
		pgDataSource.setPassword(password);

		AppUser user = null;
		try {
			if (!EntryPoint.PROPERTIES.getGuestUser().equals(loginName))
				user = performUserAuthorization(pgDataSource, loginName);
		} catch (SQLException e) {
			return false;
		}

		currentUser = user;
		dataSource = pgDataSource;
		return true;
	}

	private static AppUser performUserAuthorization(DataSource pgDataSource, String loginName)
		throws DBStructuralIntegrityException, SQLException {
		List<String[]> results = new ArrayList<String[]>();
		List<String[]> times = new ArrayList<String[]>();
		AppUser user = null;
		int accountId = -1;
		int userId = -1;
		try {
			
			//Выполняет операцию select по указанному шаблону с указанными аргументами в БД представленной указанным источником данных. 
			SQLUtils.executeQuery(
				pgDataSource,
				SQLUtils.QueryTemplate.GET_ACCOUNT_ID_AND_USER_ID_BY_LOGIN,
				new Object[]{ loginName },
				results
			);

			if (results.size() != 1)
				throw new DBStructuralIntegrityException();

			accountId = Integer.parseInt(results.get(0)[0]);
			userId =  Integer.parseInt(results.get(0)[1]);
			results.clear();

			SQLUtils.executeQuery(
				pgDataSource,
				SQLUtils.QueryTemplate.GET_NAME_AND_LAST_NAME_BY_USER_ID,
				new Object[]{ userId },
				results
			);

			// Обновляем время входа пользователя. Генерация значения времени должно происходить на сервере.
			SQLUtils.executeUpdate(
				pgDataSource,
				SQLUtils.QueryTemplate.UPD_LAST_LOGON_DATE_BY_ACCOUNT_ID,
				new Object[]{ accountId }
			);
			// Получаем новое значение времени входа пользователя
			SQLUtils.executeQuery(
				pgDataSource,
				SQLUtils.QueryTemplate.GET_LAST_LOGON_DATE_BY_ACCOUNT_ID,
				new Object[]{ accountId },
				times
			);

			Calendar lastLogonDate = null;
			try {
				lastLogonDate = SQLUtils.parseDateFromPostgresFormat(times.get(0)[0]);
			} catch (ParseException e) {}

			user = new AppUser(userId, results.get(0)[0], results.get(0)[1], lastLogonDate);
		} catch (NotSupportedArgumentTypeException e) {
			throw new RuntimeException(e);
		}
		return user;
	}
	
	/**
	 * Возвращает текущего пользователя системы.
	 * @return текущий пользователь системы или <b>null</b> в случае если авторизация для 
	 * пользователя не была осуществлена.
	 */
	public static AppUser getCurrentUser() { return currentUser; } 

	/**
	 * Выполянет выход из текущего сеанса подключения.
	 * @throws AuthorizationException в случае когда выход из текущего сеанса подключения уже был совершен
	 */
	public static void tryLogOut() throws AuthorizationException {
		if (dataSource == null)
			throw new AuthorizationException(AuthorizationException.ALREADY_LOGGED_OUT);
		dataSource = null;
		currentUser = null;
		if (isLoggedAsGuest())
			loggedAsGuest = false;
	}

	/**
	 * Возвращает источник данных для работы с БД. Для того чтобы получить источник данных необходимо
	 * предварительно авторизироваться {@link DataSourceManager#tryLogIn}
	 * @return источник данных для работы с БД.
	 */
	public static DataSource getDataSource() { return dataSource; }

	private DataSourceManager(){}
}
