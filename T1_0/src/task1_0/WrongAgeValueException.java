package task1_0;

public class WrongAgeValueException extends Exception {

	public static final String ERROR_MESSAGE = "Defined age value isn't proper";

	public WrongAgeValueException() {
		super(ERROR_MESSAGE);
	}

	private static final long serialVersionUID = 729754568637105012L;
}