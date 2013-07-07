/**
 * 
 */
package ch.thn.commandop.validator;

import java.io.File;

import ch.thn.commandop.CmdLnItem;
import ch.thn.commandop.CommandOPValidator;

/**
 * @author thomas
 *
 */
public class PathValidator extends CommandOPValidator {
	
	private boolean mustExist = true;
	
	
	
	public PathValidator(boolean mustExist, boolean isDirectory) {
		this.mustExist = mustExist;
		
	}

	@Override
	public boolean validate(CmdLnItem item, String newValue, int multiValuePos) {
		
		if (newValue == null) {
			return false;
		}
		
		File f = new File(newValue);
		
		if (mustExist) {
			return f.exists();
		} else {
			return !f.exists();
		}
				
	}

}
