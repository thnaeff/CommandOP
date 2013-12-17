/**
 * 
 */
package ch.thn.commandop;

/**
 * @author thomas
 *
 */
public class PreParsedItem {
	
	
	private boolean isParameter = false;
	private boolean isOption = false;
	private boolean isShortOption = false;
	
	private String name = null;
	private String value = null;
	
	
	public PreParsedItem(String arg) {
		
		if (arg.startsWith(CommandOPTools.OPTIONSPREFIX_LONG)) {
			isOption = true;
			
			name = CommandOPTools.getOption(arg);
			value = CommandOPTools.getOptionValue(arg);
		} else if (arg.startsWith(CommandOPTools.OPTIONSPREFIX_SHORT)) {
			isShortOption = true;
			
			name = CommandOPTools.getOption(arg);
			value = CommandOPTools.getOptionValue(arg);
		} else {
			isParameter = true;
			
			name = CommandOPTools.getParameter(arg);
			value = CommandOPTools.getParameterValue(arg);
		}
		
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public boolean isParameter() {
		return isParameter;
	}
	
	public boolean isOption() {
		return isOption;
	}
	
	public boolean isShortOption() {
		return isShortOption;
	}
	
	public String getName() {
		return name;
	}
	
	public String getValue() {
		return value;
	}
	
	

}
