package task.z04;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.ini4j.InvalidFileFormatException;

public class EntryPoint {

	public static Properties PROPERTIES = null;

	public static void main(String[] args) throws InvalidFileFormatException, FileNotFoundException, IOException {
		// ��������� ��������� �� �����
		PROPERTIES = new Properties(System.getProperty("user.dir"));
		TerminalApp app = new TerminalApp();
		app.start();
	}
}