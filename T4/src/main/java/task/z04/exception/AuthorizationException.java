package task.z04.exception;

public class AuthorizationException extends Exception {

	public static final String ALREADY_LOGGED = "The user is already logged"; 
	public static final String ALREADY_LOGGED_OUT = "The user is already logged out";
	public static final String UNABLE_LOG_IN_AS_GUEST = "Unable log in as guest";

	public AuthorizationException(String message) {
		super(message);
	}

	private static final long serialVersionUID = -5693299109025450763L;
}
