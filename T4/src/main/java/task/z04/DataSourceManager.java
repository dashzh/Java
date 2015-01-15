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
 * �������� ������� � ��. �������� �� ������� ����������� �������������.
 * ������������ �� ������ � ������������ �����. 
 */
public final class DataSourceManager {

	private static DataSource dataSource;
	private static AppUser currentUser;

	/**
	 * ��������� ����������� � �� �� ����� ��������� ������������
	 * @return <b>true</b> � ������ �������� ����������� � <b>false</b> �����
	 * @throws AuthorizationException � ������ ����� �������������� ����������� ��� ��������������� �������������
	 */	
	public static boolean tryLogInAsGuest() throws AuthorizationException {
		try {
			return loggedAsGuest = tryLogIn(EntryPoint.PROPERTIES.getGuestUser(), EntryPoint.PROPERTIES.getGuestUser());	
		} catch (DBStructuralIntegrityException e) { // ��������� ���������� �.�. ��� �� ����� ���� ����������
			return false;
		}
	}

	private static boolean loggedAsGuest = false;

	/**
	 * ���������� ������� ���� �������� �� ������� ������������ ��������
	 * @returns ������� ���� �������� �� ������� ������������ ��������
	*/
	public static boolean isLoggedAsGuest() { return loggedAsGuest; }


	/**
	 * ��������� ����������� ������������ � ���������� ������� / ������� � ��.
	 * @param loginName �����
	 * @param password ������
	 * @return <b>true</b> � ������ �������� ����������� � <b>false</b> �����
	 * @throws AuthorizationException � ������ ����� �������������� ����������� ��� ��� ��������������� ������������
	 * @throws DBStructuralIntegrityException � ������� ����� � �� ����������� ������������ � ��������� �������
	 * ��� � �� ������ ������ ������������ � ��������� ������� 
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
			
			//��������� �������� select �� ���������� ������� � ���������� ����������� � �� �������������� ��������� ���������� ������. 
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

			// ��������� ����� ����� ������������. ��������� �������� ������� ������ ����������� �� �������.
			SQLUtils.executeUpdate(
				pgDataSource,
				SQLUtils.QueryTemplate.UPD_LAST_LOGON_DATE_BY_ACCOUNT_ID,
				new Object[]{ accountId }
			);
			// �������� ����� �������� ������� ����� ������������
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
	 * ���������� �������� ������������ �������.
	 * @return ������� ������������ ������� ��� <b>null</b> � ������ ���� ����������� ��� 
	 * ������������ �� ���� ������������.
	 */
	public static AppUser getCurrentUser() { return currentUser; } 

	/**
	 * ��������� ����� �� �������� ������ �����������.
	 * @throws AuthorizationException � ������ ����� ����� �� �������� ������ ����������� ��� ��� ��������
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
	 * ���������� �������� ������ ��� ������ � ��. ��� ���� ����� �������� �������� ������ ����������
	 * �������������� ���������������� {@link DataSourceManager#tryLogIn}
	 * @return �������� ������ ��� ������ � ��.
	 */
	public static DataSource getDataSource() { return dataSource; }

	private DataSourceManager(){}
}