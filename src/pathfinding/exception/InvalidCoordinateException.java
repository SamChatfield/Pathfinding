package pathfinding.exception;

@SuppressWarnings("serial")
public class InvalidCoordinateException extends Exception {
	
	public InvalidCoordinateException() {
		super();
	}
	
	public InvalidCoordinateException(String message) {
		super(message);
	}
	
	public InvalidCoordinateException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public InvalidCoordinateException(Throwable cause) {
		super(cause);
	}
	
	public String getMessage() {
		return super.getMessage();
	}
	
}
