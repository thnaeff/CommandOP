/**
 * 
 */
package ch.thn.commandop;

/**
 * @author thomas
 *
 */
public class CommandOPFactory {
	
	/**
	 * 
	 * @param name
	 * @param description
	 * @param defaultValue
	 * @return
	 */
	public static CmdLnItem newParameter(String name, String description, String defaultValue) {
		return new CmdLnItem(name, description, defaultValue);
	}
	
	/**
	 * 
	 * @param name
	 * @param description
	 * @return
	 */
	public static CmdLnItem newParameter(String name, String description) {
		return new CmdLnItem(name, description);
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public static CmdLnItem newParameter(String name) {
		return new CmdLnItem(name);
	}

}
