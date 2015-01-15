package task.z04;

import java.sql.Connection;
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
import task.z06.DoubleTuple;
import task.z06.servlet.MainServlet;

/**
 * Менеджер доступа к БД. Отвечает за вопросы авторизации пользователей.
 * Ориентирован на работу в однопоточной среде. 
 */
public final class DataSourceManager {

	public static DoubleTuple<DataSource, AppUser> tryLogIn(String loginName, String password)
		throws AuthorizationException, DBStructuralIntegrityException, SQLException {
		if (MainServlet.PROPERTIES.getGuestUser().equals(loginName))
			throw new AuthorizationException(AuthorizationException.UNABLE_LOG_IN_AS_GUEST);

		PGSimpleDataSource pgDataSource = new PGSimpleDataSource();
		pgDataSource.setServerName(MainServlet.PROPERTIES.getIpAddress());
		pgDataSource.setPortNumber(MainServlet.PROPERTIES.getPort());
		pgDataSource.setDatabaseName(MainServlet.PROPERTIES.getDbName());
		pgDataSource.setUser(loginName);
		pgDataSource.setPassword(password);

		AppUser user = performUserAuthorization(pgDataSource, loginName);
		return new DoubleTuple<DataSource, AppUser>(pgDataSource, user);
	}

	private static AppUser performUserAuthorization(DataSource pgDataSource, String loginName)
		throws DBStructuralIntegrityException, SQLException {
		List<String[]> results = new ArrayList<String[]>();
		List<String[]> times = new ArrayList<String[]>();
		AppUser user = null;
		int accountId = -1;
		int userId = -1;
		try {

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
		
	public static DataSource getGuestDataSource() { return guestDataSource; }
	
	private static DataSource initGuestDataSource() {
		PGSimpleDataSource dataSource = new PGSimpleDataSource();
		dataSource.setServerName(MainServlet.PROPERTIES.getIpAddress());
		dataSource.setPortNumber(MainServlet.PROPERTIES.getPort());
		dataSource.setDatabaseName(MainServlet.PROPERTIES.getDbName());
		dataSource.setUser(MainServlet.PROPERTIES.getGuestUser());
		dataSource.setPassword(MainServlet.PROPERTIES.getGuestUser());

		// Проверяем соединение с БД
		try (Connection con = dataSource.getConnection()) {} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return dataSource;
	}
	
	private static final DataSource guestDataSource = initGuestDataSource(); 
}