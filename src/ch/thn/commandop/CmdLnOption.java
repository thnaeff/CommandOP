/**
 * 
 */
package ch.thn.commandop;


/**
 * 
 * 
 * Format follows the GNU format as much as possible: 
 * http://www.gnu.org/software/guile/manual/html_node/Command-Line-Format.html
 * 
 * @author thomas
 *
 */
public class CmdLnOption extends CmdLnParameter {

	/**
	 * 
	 * @param name
	 * @param defaultValue
	 * @param description
	 * @see CmdLnBase#CmdLnBase(String, String, String)
	 */
	public CmdLnOption(String name, String defaultValue, String description) {
		super(name, defaultValue, description);
	}
	
	/**
	 * 
	 * @param name
	 * @param description
	 * @see CmdLnBase#CmdLnBase(String, String)
	 */
	public CmdLnOption(String name, String description) {
		super(name, null, description);
	}
	
	/**
	 * 
	 * @param name
	 * @see CmdLnBase#CmdLnBase(String)
	 */
	public CmdLnOption(String name) {
		super(name, null, null);
	}
	
	/**
	 * 
	 * @see CmdLnBase#CmdLnBase()
	 */
	public CmdLnOption() {
		super(null, null);
	}
	
	/**
	 * A short option is an item with the short prefix {@link CommandOPTools#OPTIONSPREFIX_SHORT}.
	 * Short options can be added to regular options to give a more convenient way 
	 * for writing the options.<br>
	 * Short options can also be combined, for example the short options 'a' and 
	 * 'b' can be written as "-ab".
	 * 
	 * @param shortName
	 * @return
	 */
	public CmdLnOption addShortOption(Character shortName) {		
		addAlias(shortName.toString()).setAsShortOption();
		return this;
	}
	
	
	
}
