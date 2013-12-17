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
	public static CmdLnParameter newParameter(String name, String description, String defaultValue) {
		return new CmdLnParameter(name, description, defaultValue);
	}
	
	/**
	 * 
	 * @param name
	 * @param description
	 * @return
	 */
	public static CmdLnParameter newParameter(String name, String description) {
		return new CmdLnParameter(name, description);
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public static CmdLnParameter newParameter(String name) {
		return new CmdLnParameter(name);
	}

}
