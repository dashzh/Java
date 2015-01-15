package task.z04;

public class Properties {

	public Properties(String ipAddress, int port, String dbName, String guestUser) {
		this.ipAddress = ipAddress;
		this.port = port;
		this.dbName = dbName;
		this.guestUser = guestUser;
	}

	public String getIpAddress() {
		return ipAddress;
	}
	public int getPort() {
		return port;
	}
	public String getDbName() {
		return dbName;
	}
	public String getGuestUser() {
		return guestUser;
	}

	private String ipAddress;
	private int port;
	private String dbName;
	private String guestUser;
}
