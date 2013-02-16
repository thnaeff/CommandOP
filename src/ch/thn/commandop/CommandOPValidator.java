/**
 * 
 */
package ch.thn.commandop;

/**
 * @author thomas
 *
 */
public interface CommandOPValidator {
	
	/**
	 * Validates the currently parsed item
	 * 
	 * @param item The actual item. The value is not yet set for this item, it is 
	 * set when {@link #validate(CmdLnItem, String, int)} returns <code>true</code>
	 * @param newValue This is the value which will be set for the item
	 * @param multiValuePos This is the position of the value. If the item is not 
	 * defined as multi-value-item, multiValuePos is always 0. If the item is defined 
	 * as multi-value-item, multiValuePos contains the number of the value, starting with 0
	 * @return This method should return <code>true</code> if the value newValue is valid, and 
	 * <code>false</code> if the value is not valid
	 */
	public boolean validate(CmdLnItem item, String newValue, int multiValuePos);

}
