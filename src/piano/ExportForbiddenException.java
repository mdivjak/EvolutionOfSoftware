package piano;

public class ExportForbiddenException extends Exception {
	public String toString() {
		return "Export is forbidden!";
	}
}
