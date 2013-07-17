/**
 * 
 */
package ch.thn.commandop.validator;

import ch.thn.commandop.CmdLnItem;

/**
 * @author thomas
 *
 */
public class StringLengthValidator extends CommandOPValidator {
	
	private int minLength = 0;
	private int maxLength = 0;
	
	
	public StringLengthValidator(int minLength, int maxLength) {
		this.minLength = minLength;
		this.maxLength = maxLength;
	}
	
	public StringLengthValidator(int minLength) {
		this(minLength, 0);
	}

	@Override
	public boolean validate(CmdLnItem item, String newValue, int multiValuePos) {
		
		if (minLength != 0 && newValue.length() < minLength) {
			setErrorMessage("Value too short (minimum " + minLength + " characters needed).");
			return false;
		}
		
		if (maxLength != 0 && newValue.length() > maxLength) {
			setErrorMessage("Value too long (maximum " + maxLength + " characters allowed).");
			return false;
		}
		
		return true;
		
	}

}
