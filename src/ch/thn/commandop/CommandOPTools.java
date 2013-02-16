/**
 * 
 */
package ch.thn.commandop;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author thomas
 *
 */
public class CommandOPTools {
	
	public final static String ITEM_VALUE_SEPARATOR = "=";
	public final static String OPTIONSPREFIX_SHORT = "-";
	public final static String OPTIONSPREFIX_LONG = OPTIONSPREFIX_SHORT + OPTIONSPREFIX_SHORT;
	
	
	public static String removeOptionPrefix(String optionString) {
		return optionString.replaceAll("^[" + OPTIONSPREFIX_SHORT + "]{1}", "").replaceAll("^[" + OPTIONSPREFIX_LONG + "]{1}", "");
	}
	
	
	public static boolean isOption(String argsString) {
		return (argsString.startsWith(OPTIONSPREFIX_LONG));
	}
	
	public static boolean isShortOption(String argsString) {
		return (argsString.startsWith(OPTIONSPREFIX_SHORT) && !argsString.startsWith(OPTIONSPREFIX_LONG));
	}
	
	
	public static String getOption(String argsString) {
		if (isOption(argsString) || isShortOption(argsString)) {
			return getName(removeOptionPrefix(argsString));
		} else {
			return null;
		}
	}
	
	
	public static String getOptionValue(String argsString) {
		if (isOption(argsString) || isShortOption(argsString)) {
			return getValue(removeOptionPrefix(argsString));
		} else {
			return null;
		}
	}
	
	
	public static boolean isParameter(String argsString) {
		return !isOption(argsString) && !isShortOption(argsString);
	}
	
	
	public static String getParameter(String argsString) {
		if (isParameter(argsString)) {
			return getName(argsString);
		} else {
			return null;
		}
	}
	
	
	public static String getParameterValue(String argsString) {
		if (isParameter(argsString)) {
			return getValue(argsString);
		} else {
			return null;
		}
	}	
	
	
	private static String getName(String argsString) {
		if (argsString.contains(ITEM_VALUE_SEPARATOR)) {
			//Returns everything in front of the first ITEM_VALUE_SEPARATOR
			return argsString.split(ITEM_VALUE_SEPARATOR)[0];
		} else {
			return argsString;
		}
	}
	
	
	private static String getValue(String argsString) {
		if (argsString.contains(ITEM_VALUE_SEPARATOR)) {
			//Returns everything after the first ITEM_VALUE_SEPARATOR
			return argsString.substring(argsString.indexOf(ITEM_VALUE_SEPARATOR) + 1);
		} else {
			return null;
		}
	}
	
	/**
	 * Takes the map and puts all the existing items in a linked list, recursively 
	 * following child-items if there are any
	 * 
	 * @param items
	 * @return
	 */
	public static LinkedList<CmdLnItem> createFlatList(HashMap<String, CmdLnItem> items) {
		
		LinkedList<CmdLnItem> itemsFlat = new LinkedList<CmdLnItem>();
		
		for (CmdLnItem item : items.values()) {
			itemsFlat.add(item);
			
			if (item.hasChildren()) {
				itemsFlat.addAll(createFlatList(item.getChildren()));
			}
		}
		
		
		return itemsFlat;
	}

}
