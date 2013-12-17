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
	/** At least one item has to be given */
	public static final int MODE_INCLUDE_ONE = 1;
	/** None or only one of the given items can be given */
	public static final int MODE_EXCLUDE = 2;
	/** At least one but no more of the given items can be given */
	public static final int MODE_EXCLUDE_ONE = 3;
	
	
	private String name = null;
	
	private int mode = -1;
	
	private LinkedHashMap<String, CmdLnBase> items = null;
	
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
		
		items = new LinkedHashMap<String, CmdLnBase>();
		
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
	public boolean addMember(CmdLnBase item) {
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
	 * @throws CommandOPError
	 */
	public boolean addMember(String itemName) {
		
		LinkedList<CmdLnBase> flatList = CommandOPTools.createFlatList(cmdop.getChildren());
		
		CmdLnBase itemToAdd = null;

		for (CmdLnBase item : flatList) {
			if (item.getName().equals(itemName)) {
				if (itemToAdd == null) {
					itemToAdd = item;
				} else {
					throw new CommandOPError("Adding item with name '" + itemName + "' to group " + name + " failed. Ambiguous item name.");
				}
			}
		}
		
		if (itemToAdd == null) {
			throw new CommandOPError("Adding item with name '" + itemName + "' to group " + name + " failed. Item with this name not found.");
		}
		
		items.put(itemToAdd.getName(), itemToAdd);
		
		return true;
		
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	protected LinkedHashMap<String, CmdLnBase> getItems() {
		return items;
	}

	
	@Override
	public String toString() {
		return name + ": " + items;
	}
	
	
}
