package task.z03;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;

class InvalidSourceFileException extends Exception {
	private static final long serialVersionUID = -975862778679572336L;
}

class InvalidDestinationPathException extends Exception {
	private static final long serialVersionUID = 1139000537802285706L;
}

class IllegalProgramArgumentsException extends Exception {
	private static final long serialVersionUID = -3158796282688691975L;
}

public class Image {

	public enum FilePathSegment { DIRECTORY, FILE_NAME, FILE_EXTENSION }

	public static final String PROGRAM_NAME = "Image";
	public static final String RESIZED_FILE_POSTFIX = "-resized";

	public static void main(String[] args) {
		try {
			if (args.length != 4)
				throw new IllegalProgramArgumentsException();

			int width = Integer.parseInt(args[2]);
			int height = Integer.parseInt(args[3]);

			File sourceFile = new File(args[0]);
			if (!sourceFile.isFile())
				throw new InvalidSourceFileException();

			File destinationPath = new File(args[1]);
			if (!destinationPath.isDirectory())
				throw new InvalidDestinationPathException();

			Map<FilePathSegment, String> segments = new HashMap<FilePathSegment, String>();
			splitFilePath(sourceFile, segments);

			String fileExtension = segments.get(FilePathSegment.FILE_EXTENSION);

			File destinationFile = new File(
				MessageFormat.format("{0}\\{1}{2}{3}",
					segments.get(FilePathSegment.DIRECTORY),
					segments.get(FilePathSegment.FILE_NAME),
					RESIZED_FILE_POSTFIX,
					fileExtension
				)
			);

			// —читываем изображение из файла в буффер :
			BufferedImage img = ImageIO.read(sourceFile);
			// »змен€ем размеры изображени€ :
			img = Scalr.resize(img, Method.ULTRA_QUALITY, Mode.FIT_EXACT, width, height);
			// —охран€ем изображение :
			ImageIO.write(img, "jpg", destinationFile);
			System.out.println("File has been successfully created");

		} catch (IllegalProgramArgumentsException e) {
			//Cообщение с подсказкой об использовании программы :
			ShowHelp();
		} catch (NumberFormatException e) {
			System.out.println("wrong value of width or height");
		} catch (InvalidSourceFileException e) {
			System.out.println("source file is invalid");
		} catch (InvalidDestinationPathException e) {
			System.out.println("destination path is invalid");	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ‘рагментирует информацию о существующем файле на составл€ющие :
	 * директори€, им€ файла и расширение файла и записывает результат в распределитель result.
	 * @param path путь к искомому файлу;
	 * @param result распределитель, в который записываетс€ результат.
	 */
	public static void splitFilePath(File path, Map<FilePathSegment, String> result) {
		String name = path.getName();
		String extension = name.substring(name.lastIndexOf('.'));
		name = name.replaceAll(extension, "");

		result.put(FilePathSegment.DIRECTORY, path.getParent());
		result.put(FilePathSegment.FILE_EXTENSION, extension);
		result.put(FilePathSegment.FILE_NAME, name);
	}

	private static void ShowHelp() {
		String ShowHelp = 
			MessageFormat.format(
				"=== {0} ===\n{1}\n{2}\n{3}\n{4}\n\n",
				PROGRAM_NAME,
				"Program arguments :",
				"<source file> <destination path> <width in px> <height in px>",
				"Example :",
				"\"C:\\\\img\\\\file.jpg\" \"C:\\\\img\\\\resized\\\\\" 100 250"
			);
		System.out.println(ShowHelp);
	}
}