/**
 * 
 */
package ch.thn.commandop.test;

import ch.thn.commandop.CmdLnItem;
import ch.thn.commandop.CommandOPValidator;

/**
 * @author thomas
 *
 */
public class NumberValidator implements CommandOPValidator {

	@Override
	public boolean validate(CmdLnItem item, String newValue, int multiValuePos) {
		
		try {
			Integer.parseInt(newValue);
		} catch (NumberFormatException e) {
			return false;
		}
		
		return true;
		
	}

}
