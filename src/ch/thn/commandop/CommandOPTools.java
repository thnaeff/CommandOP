/**
 *    Copyright 2013 Thomas Naeff (github.com/thnaeff)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package ch.thn.commandop;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class CommandOPTools {

	public final static String ITEM_VALUE_SEPARATOR = "=";
	public final static String OPTIONSPREFIX_SHORT = "-";
	public final static String OPTIONSPREFIX_LONG = OPTIONSPREFIX_SHORT + OPTIONSPREFIX_SHORT;

	/**
	 * 
	 * 
	 * @param optionString
	 * @return
	 */
	public static String removeOptionPrefix(String optionString) {
		return optionString.replaceAll("^[" + OPTIONSPREFIX_SHORT + "]{1}", "").replaceAll("^[" + OPTIONSPREFIX_LONG + "]{1}", "");
	}

	/**
	 * 
	 * 
	 * @param argsString
	 * @return
	 */
	public static boolean isOption(String argsString) {
		if (argsString == null) {
			return false;
		}

		return argsString.startsWith(OPTIONSPREFIX_LONG);
	}

	/**
	 * 
	 * 
	 * @param argsString
	 * @return
	 */
	public static boolean isShortOption(String argsString) {
		if (argsString == null) {
			return false;
		}

		return argsString.startsWith(OPTIONSPREFIX_SHORT) && !argsString.startsWith(OPTIONSPREFIX_LONG);
	}

	/**
	 * 
	 * 
	 * @param argsString
	 * @return
	 */
	public static String getOption(String argsString) {
		if (isOption(argsString) || isShortOption(argsString)) {
			return getName(removeOptionPrefix(argsString));
		} else {
			return null;
		}
	}

	/**
	 * 
	 * 
	 * @param argsString
	 * @return
	 */
	public static String getOptionValue(String argsString) {
		if (isOption(argsString) || isShortOption(argsString)) {
			return getValue(removeOptionPrefix(argsString));
		} else {
			return null;
		}
	}

	/**
	 * 
	 * 
	 * @param argsString
	 * @return
	 */
	public static boolean isParameter(String argsString) {
		return !isOption(argsString) && !isShortOption(argsString);
	}

	/**
	 * 
	 * 
	 * @param argsString
	 * @return
	 */
	public static String getParameter(String argsString) {
		if (isParameter(argsString)) {
			return getName(argsString);
		} else {
			return null;
		}
	}

	/**
	 * 
	 * 
	 * @param argsString
	 * @return
	 */
	public static String getParameterValue(String argsString) {
		if (isParameter(argsString)) {
			return getValue(argsString);
		} else {
			return null;
		}
	}

	/**
	 * 
	 * 
	 * @param argsString
	 * @return
	 */
	private static String getName(String argsString) {
		if (argsString.contains(ITEM_VALUE_SEPARATOR)) {
			//Returns everything in front of the first ITEM_VALUE_SEPARATOR
			return argsString.split(ITEM_VALUE_SEPARATOR)[0];
		} else {
			return argsString;
		}
	}

	/**
	 * 
	 * 
	 * @param argsString
	 * @return
	 */
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
	public static LinkedList<CmdLnItem> createFlatList(Collection<CmdLnItem> items) {

		LinkedList<CmdLnItem> itemsFlat = new LinkedList<CmdLnItem>();

		for (CmdLnItem item : items) {
			itemsFlat.add(item);

			if (item.hasChildren()) {
				itemsFlat.addAll(createFlatList(item.getChildrenInternal().values()));
			}
		}


		return itemsFlat;
	}

	/**
	 * Takes the map and puts all the existing items in a linked list, recursively
	 * following child-items if there are any
	 * 
	 * @param items
	 * @return
	 */
	public static LinkedList<CmdLnItem> createFlatList(CmdLnItem item) {
		LinkedList<CmdLnItem> items = new LinkedList<>();

		if (item instanceof CommandOP) {
			//Combine options and child parameters. Non-option-parameters first and then the options
			items.addAll(item.getChildrenInternal().values());
			items.addAll(((CommandOP)item).getOptions().values());
		} else {
			items.add(item);
		}

		return createFlatList(items);
	}

	/**
	 * Creates a comma separated list of all the strings in the list.
	 * 
	 * @param items
	 * @return
	 */
	public static String createItemsString(LinkedList<String> items) {
		StringBuffer sb = new StringBuffer();

		Iterator<String> i = items.iterator();
		while (i.hasNext()) {
			sb.append(i.next());

			if (i.hasNext()) {
				sb.append(", ");
			}
		}

		return sb.toString();
	}

	/**
	 * Returns the required number of spaces to align text further to the right
	 * 
	 * @param spaceFromLeft How many spaces from the left page border are needed?
	 * @param preStringLength The current length of the string on this line
	 * @return
	 */
	protected static String makeRightAlignSpace(int spaceFromLeft, int preStringLength, boolean showDots) {
		StringBuffer sb = new StringBuffer();

		int spaceNeeded = spaceFromLeft - preStringLength;

		if (spaceNeeded <= 0) {
			spaceNeeded = 3;
		}

		for (int i = spaceNeeded; i > 0; i--) {
			if (showDots) {
				if (i % 2 == 0) {
					sb.append(".");
				} else {
					sb.append(" ");
				}
			} else {
				sb.append(" ");
			}
		}

		return sb.toString();
	}


	/**
	 * Puts all properties into a list as key=value pairs. Takes the string representations
	 * of key and value.
	 * 
	 * @param properties
	 * @return
	 */
	public static LinkedList<String> mapToKeyValueList(Map<Object, Object> map) {
		LinkedList<String> list = new LinkedList<String>();
		Set<Entry<Object, Object>> entries = map.entrySet();

		for (Entry<Object, Object> entry : entries) {
			String s = entry.getKey().toString();

			//Only add a value if there is one
			Object value = entry.getValue();
			if (value != null) {
				String sValue = value.toString();
				if (sValue.length() > 0) {
					s = s + "=" + sValue.toString();
				}
			}

			//Somehow the properties entry set is always returned in reverse order
			//(reverse to the order in the properties file). However, since properties
			//are a set the order might not be guaranteed...
			list.add(0, s);
		}

		return list;
	}

}
