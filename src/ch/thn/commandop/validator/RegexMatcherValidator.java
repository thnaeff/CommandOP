/**
 * 
 */
package ch.thn.commandop.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.thn.commandop.CmdLnItem;

/**
 * @author thomas
 *
 */
public class RegexMatcherValidator extends CommandOPValidator {
	
	private Pattern pattern = null;
	
	
	public RegexMatcherValidator(String regexPattern) {
		pattern = Pattern.compile(regexPattern);
		
	}
	

	@Override
	public boolean validate(CmdLnItem item, String newValue, int multiValuePos) {
		
		if (newValue == null) {
			return true;
		}
		
		Matcher matcher = pattern.matcher(newValue);
		
		if (matcher.matches()) {
			return true;
		}
		
		return false;
		
	}

}
