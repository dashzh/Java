package task.z04;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile.Section;

public class Properties {

	public static final String INI_FILE_NAME = "settings.ini";

	public Properties(String settingsPath) throws InvalidFileFormatException, FileNotFoundException, IOException {
		File settingsFile = new File(settingsPath + "\\" +INI_FILE_NAME);
		Ini ini = new Ini(new FileReader(settingsFile));
		Section section = ini.get("OPTIONS");
		ipAddress = section.get("IP ADDRESS");
		port = Integer.parseInt(section.get("PORT"));
		dbName = section.get("DB NAME");
		guestUser = section.get("GUEST USER");
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
