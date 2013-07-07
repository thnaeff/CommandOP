/**
 * 
 */
package ch.thn.commandop;

/**
 * @author thomas
 *
 */
public class CommandOPException extends Exception {
	private static final long serialVersionUID = -3720861165691387006L;

	private String errorMessage = null;
	
	public CommandOPException(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	
	public String getErrorMesage() {
		return errorMessage;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Throwable#toString()
	 */
	@Override
	public String toString() {
		return super.toString() + ": " + errorMessage;
	}
	
}
