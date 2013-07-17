/**
 * 
 */
package ch.thn.commandop.validator;

import ch.thn.commandop.CmdLnItem;

/**
 * @author thomas
 *
 */
public class NumberValidator extends CommandOPValidator {

	@Override
	public boolean validate(CmdLnItem item, String newValue, int multiValuePos) {
		
		if (newValue == null) {
			return false;
		}
		
		try {
			Integer.parseInt(newValue);
		} catch (NumberFormatException e) {
			setErrorMessage("Failed to parse value as integer");
			return false;
		}
		
		return true;
		
	}

}
