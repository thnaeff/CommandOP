/**
 * 
 */
package ch.thn.commandop;

import java.util.LinkedHashMap;
import java.util.LinkedList;


/**
 * @author thomas
 *
 */
public class CommandOPGroup {
	
	/** All items have to be given */
	public static final int MODE_INCLUDE = 0;
	/** None or only one of the given items can be given */
	public static final int MODE_EXCLUDE = 1;
	/** At least one but no more of the given items can be given */
	public static final int MODE_EXCLUDE_ONE = 2;
	
	
	private String name = null;
	
	private int mode = -1;
	
	private LinkedHashMap<String, CmdLnItem> items = null;
	
	private CommandOP cmdop = null;
	
	
	/**
	 * Creates a new group with the given name and mode.
	 * 
	 * @param cmdop
	 * @param name
	 * @param mode
	 */
	public CommandOPGroup(CommandOP cmdop, String name, int mode) {
		this.name = name;
		this.mode = mode;
		this.cmdop = cmdop;
		
		items = new LinkedHashMap<String, CmdLnItem>();
		
	}
	
	/**
	 * Returns the mode of this group
	 * 
	 * @return
	 */
	public int getMode() {
		return mode;
	}
	
	/**
	 * Returns the name of this group
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Adds the given item as group member
	 * 
	 * @param item
	 * @return
	 */
	public boolean addMember(CmdLnItem item) {
		if (items.containsKey(item.getName())) {
			return false;
		}
		
		items.put(item.getName(), item);
		
		return true;
	}
	
	/**
	 * Adds the item with the given name as group member. All the defined 
	 * items are searched, looking for a matching name. If more than one item 
	 * have a matching name, adding fails. Thus, only unambiguous items can be 
	 * added with this method.
	 * 
	 * @param itemName
	 * @return
	 */
	public boolean addMember(String itemName) {
		
		LinkedList<CmdLnItem> flatList = CommandOPTools.createFlatList(cmdop.getItems());
		
		CmdLnItem itemToAdd = null;

		for (CmdLnItem item : flatList) {
			if (item.getName().equals(itemName)) {
				if (itemToAdd == null) {
					itemToAdd = item;
				} else {
					System.err.println("CommandOP> Adding item with name '" + itemName + "' to group " + name + " failed. Ambiguous item name.");
					return false;
				}
			}
		}
		
		if (itemToAdd == null) {
			System.err.println("CommandOP> Adding item with name '" + itemName + "' to group " + name + " failed. Item with this name not found.");
			return false;
		}
		
		items.put(itemToAdd.getName(), itemToAdd);
		
		return true;
		
	}
	
	
	protected LinkedHashMap<String, CmdLnItem> getItems() {
		return items;
	}

}
