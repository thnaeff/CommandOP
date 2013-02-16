/**
 * 
 */
package ch.thn.commandop;

/**
 * @author thomas
 *
 */
public class CommandOPFactory {
	
	public static CmdLnItem newParameter(String name, String description, String defaultValue) {
		return new CmdLnItem(name, description, defaultValue);
	}
	
	public static CmdLnItem newParameter(String name, String description) {
		return new CmdLnItem(name, description);
	}

}
