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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Vector;

import ch.thn.commandop.validator.CommandOPValidator;

/**
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public abstract class CmdLnBase {

	public static String OPTION_DESC = "option (--)";
	public static String SHORTOPTION_DESC = "short option (-)";
	public static String PARAMETER_DESC = "parameter";

	private CmdLnBase parent = null;
	private CmdLnBase aliasOf = null;	//The item of which this item is the alias of

	protected CommandOPValidator validator = null;

	protected LinkedHashMap<String, CmdLnBase> alias = null;		//The items which are set as alias
	protected LinkedHashMap<String, CmdLnBase> children = null;

	private String description = null;
	private String name = null;
	private String defaultValue = null;

	private Vector<String> values = null;

	private boolean isParsed = false;
	protected boolean isMandatory = false;
	private boolean isOption = false;
	private boolean isShortOption = false;
	private boolean isParameter = false;
	protected boolean isBoolean = false;
	protected boolean isValueRequired = false;
	private boolean isHiddenInPrint = false;
	protected boolean isMultiValueItem = false;

	private int level = 0;
	protected int multiValueMin = 0;
	protected int multiValueMax = 0;
	private int cmdLnPos = 0;


	/**
	 * 
	 * @param name
	 * @param defaultValue
	 * @param description
	 */
	protected CmdLnBase(String name, String defaultValue, String description) {
		this.name = name;
		this.defaultValue = defaultValue;
		this.description = description;

		values = new Vector<String>();
		children = new LinkedHashMap<String, CmdLnBase>();
		alias = new LinkedHashMap<String, CmdLnBase>();

	}

	/**
	 * 
	 * @param name
	 * @param description
	 */
	protected CmdLnBase(String name, String description) {
		this(name, null, description);
	}

	/**
	 * 
	 * @param name
	 */
	protected CmdLnBase(String name) {
		this(name, null, null);
	}

	/**
	 * 
	 */
	protected CmdLnBase() {
		this(null, null);
	}

	/**
	 * Resets the values and the parsed flag so that the item
	 * can be reused again for parsing.
	 * 
	 * 
	 */
	protected void reset() {
		values.clear();

		isParsed = false;
	}

	/**
	 * Resets only the isParsed flag
	 * 
	 */
	protected void resetIsParsed() {
		isParsed = false;
	}

	/**
	 * Sets the parent item of the tree
	 * 
	 * @param parent
	 */
	protected void setParent(CmdLnBase parent) {
		this.parent = parent;

		adjustLevel();
	}

	/**
	 * Returns this items parent item, or null if there is no parent
	 * 
	 * @return
	 */
	protected CmdLnBase getParent() {
		if (parent instanceof CommandOP) {
			return null;
		}

		return parent;
	}

	/**
	 * Returns this items parent item. If it is a top level item, the parent
	 * might be a {@link CommandOP} object
	 * 
	 * @return
	 */
	protected CmdLnBase getParentInternal() {
		return parent;
	}

	/**
	 * Returns the level of this item. The level defines the position
	 * in the tree (+1 of it's parent item)
	 * 
	 * @return
	 */
	protected int getLevel() {
		return level;
	}

	/**
	 * Adds the given items as children to this item
	 * 
	 * @param items
	 * @return
	 */
	protected CmdLnParameter addParameters(CmdLnParameter... items) {

		for (CmdLnParameter item : items) {
			children.put(item.getName(), item);
			item.setAsParameter();
			item.setParent(this);
		}

		return (CmdLnParameter) this;
	}

	/**
	 * Adds a new item with the given parameters to this item as child
	 * 
	 * @param name
	 * @param defaultValue
	 * @param description
	 * @return
	 */
	protected CmdLnParameter addParameter(String name, String defaultValue, String description) {
		CmdLnParameter child = new CmdLnParameter(name, defaultValue, description);
		children.put(name, child);
		child.setAsParameter();
		child.setParent(this);
		return child;
	}

	/**
	 * Adds a new item with the given parameters to this item as child
	 * 
	 * @param name
	 * @param description
	 * @return
	 */
	protected CmdLnParameter addParameter(String name, String description) {
		return addParameter(name, null, description);
	}

	/**
	 * Sets an alias for this item. The alias can be used instead of
	 * this item name.
	 * 
	 * @param aliasOf
	 */
	protected void setAliasOf(CmdLnBase aliasOf) {
		this.aliasOf = aliasOf;
	}

	/**
	 * Returns the item of which this item is the alias of
	 * 
	 * @return
	 */
	protected CmdLnBase getAliasOf() {
		return aliasOf;
	}

	/**
	 * Recreates the level numbers for all children of this item
	 */
	protected void adjustLevel() {

		if (parent != null && !(parent instanceof CommandOP)) {
			level = parent.getLevel() + 1;
		} else {
			level = 0;
		}

		for (CmdLnBase child : children.values()) {
			child.adjustLevel();
		}

	}

	/**
	 * Returns true if this item has the specified child item
	 * 
	 * @param childName
	 * @return
	 */
	protected boolean hasChild(String childName) {
		return children.containsKey(childName);
	}

	/**
	 * Returns true if this item has one or more child items
	 * 
	 * @return
	 */
	protected boolean hasChildren() {
		return !children.isEmpty();
	}

	/**
	 * Returns the child with the given name, or null if the child
	 * item does not exist. If the given name is an alias, the
	 * corresponding child item is returned.
	 * 
	 * @param childName
	 * @return
	 */
	protected CmdLnValue getChild(String childName) {
		if (!children.containsKey(childName)) {
			throw new CommandOPError("Child parameter '" + childName + "' is not defined in '" + getName() + "'.");
		}

		CmdLnBase item = children.get(childName);

		if (item.isAlias()) {
			return (CmdLnValue) item.getAliasOf();
		} else {
			return (CmdLnValue) item;
		}
	}

	/**
	 * Returns the list with all the children of this item
	 * (but it does only return this item's children, and does
	 * not go deeper)
	 * 
	 * @return
	 */
	protected LinkedHashMap<String, CmdLnBase> getChildren() {
		return children;
	}

	/**
	 * Returns the name of this item
	 * 
	 * @return
	 */
	protected String getName() {
		return name;
	}

	/**
	 * Returns the value of this item, or if no value is set the default value
	 * is returned. If the item is defined as multi-value-item, the first
	 * available value is returned (same as {@link #getValue(int)} with parameter 0)
	 * 
	 * @return
	 */
	protected String getValue() {
		return getValue(0);
	}

	/**
	 * Returns the value of this item which is on the given position. If the given
	 * position does not exist, null is returned.
	 * 
	 * @param multiValuePos
	 * @return
	 */
	protected String getValue(int multiValuePos) {
		if (aliasOf != null) {
			//This item is only the alias of another item
			return aliasOf.getValue(multiValuePos);
		}

		if (isParsed) {
			if (multiValuePos >= values.size()) {
				return null;
			}

			return values.get(multiValuePos);
		} else {
			if (isBoolean) {
				return "false";
			} else {
				return defaultValue;
			}
		}
	}

	/**
	 * Returns the default value, or null if no default
	 * value is set
	 * 
	 * @return
	 */
	protected String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Returns the description of this item, or null
	 * if not description has been set
	 * 
	 * @return
	 */
	protected String getDescription() {
		return description;
	}

	/**
	 * Sets the value<br>
	 * <br>value
	 * Special cases are:<br>
	 * The item is set as Boolean and the value is null -> The item-value is set to "true"<br>
	 * The item is set as Boolean and the value is not null -> Any value that equals "true"
	 * (ignoring case) sets the item-value to "true". Any other value sets the item-value
	 * to "false"<br>
	 * 
	 * @param value
	 * @return Returns an error message if setting the value failed, or null if
	 * everything was OK.
	 */
	protected String setValue(String value) {
		return setValue(value, 0);
	}

	/**
	 * Adds the value to the list of multi values<br>
	 * <br>value
	 * Special cases are:<br>
	 * The item is set as Boolean and the value is null -> The item-value is set to "true"<br>
	 * The item is set as Boolean and the value is not null -> Any value that equals "true"
	 * (ignoring case) sets the item-value to "true". Any other value sets the item-value
	 * to "false"<br>
	 * 
	 * @param value
	 * @return Returns an error message if setting the value failed, or null if
	 * everything was OK.
	 */
	protected String addMultiValue(String value) {
		return setValue(value, values.size());
	}

	/**
	 * Sets the value at the given position<br>
	 * <br>value
	 * Special cases are:<br>
	 * The item is set as Boolean and the value is null -> The item-value is set to "true"<br>
	 * The item is set as Boolean and the value is not null -> Any value that equals "true"
	 * (ignoring case) sets the item-value to "true". Any other value sets the item-value
	 * to "false"<br>
	 * 
	 * @param value
	 * @param multiValuePos
	 * @return Returns an info or error message if setting the value failed, or null if
	 * everything was OK. If it is a info message, it contains the "[INFO] " prefix
	 */
	private String setValue(String value, int multiValuePos) {

		if (aliasOf != null) {
			//This item is the alias of another item
			return aliasOf.setValue(value, multiValuePos);
		}

		if (isMultiValueItem) {
			if (value == null) {
				//No null-values for multi value items. Otherwise things like
				//"item=null value1 value2" happen which do not make sense
				return null;
			} else if (multiValueMax != 0 && values.size() >= multiValueMax) {
				//Limit the number of values if a value is set for multiValueMax
				return "Item '" + getName() + "' is limited to " + multiValueMax + " values.";
			}
		}

		//Do not parse an item twice (do not set a value twice) if its not
		//a multi value item
		if (isParsed && !isMultiValueItem) {
			return "[INFO] Item '" + getName() + "' occurs more than once. Only first occurrence is used.";
		}

		//Set the parsed flag already here. Even though the validation might fail
		//and the value is not set, it is useful to know that the item has been
		//parsed
		isParsed = true;

		if (validator != null) {
			if (!validator.validate(this, value, multiValuePos)) {
				return "[" + validator.getClass().getSimpleName() + "] Validation of item '" + getName() + "' with value '" + value + "' failed: " + validator.getErrorMessage();
			}
		}

		if (value == null) {
			if (isBoolean) {
				this.values.add("true");
			} else {
				this.values.add(null);
			}
		} else {
			if (isBoolean) {
				if (value.toLowerCase().equals("true")) {
					this.values.add("true");
				} else {
					this.values.add("false");
				}
			} else {
				this.values.add(value);
			}
		}

		return null;
	}

	/**
	 * Returns the status of the mandatory-flag.<br>
	 * A mandatory item needs to be given (only the item, not the
	 * value. See isValueRequired() for that matter). The flag of
	 * a mandatory item is only checked if its parent item is given
	 * too.
	 * 
	 * @return
	 */
	protected boolean isMandatory() {
		return isMandatory;
	}

	/**
	 * Returns the status of the boolean-flag.<br>
	 * A boolean item behaves in a special way when
	 * setting or getting its value.
	 * 	 *
	 * @return
	 */
	protected boolean isBoolean() {
		return isBoolean;
	}

	/**
	 * Returns true if this item is a child-item of some
	 * other item
	 * 
	 * @return
	 */
	protected boolean isChild() {
		if (parent instanceof CommandOP) {
			return false;
		} else {
			return parent != null;
		}
	}

	/**
	 * Sets the option-flag which indicates that this item
	 * is an option (an item with the long prefix {@link CommandOPTools}.OPTIONSPREFIX_LONG)
	 * 
	 * @return
	 */
	protected CmdLnBase setAsOption() {
		isOption = true;
		isShortOption = false;
		isParameter = false;
		return this;
	}

	/**
	 * Returns the option-flag which indicates that this item
	 * is an option (an item with the long prefix {@link CommandOPTools}.OPTIONSPREFIX_LONG)
	 * 
	 * @return
	 */
	protected boolean isOption() {
		return isOption;
	}

	/**
	 * Sets the short-option-flag which indicates that this item
	 * is a short-option (an item with the short prefix
	 * {@link CommandOPTools}.OPTIONSPREFIX_SHORT). Short options can also be
	 * combined, for example the short options 'a' and 'b' can be written as "-ab".
	 * 
	 * @return
	 */
	protected CmdLnBase setAsShortOption() {
		isOption = false;
		isShortOption = true;
		isParameter = false;
		return this;
	}

	/**
	 * Sets the short-option-flag which indicates that this item
	 * is a short-option (an item with the short prefix {@link CommandOPTools}.OPTIONSPREFIX_SHORT)
	 * 
	 * @return
	 */
	protected boolean isShortOption() {
		return isShortOption;
	}

	/**
	 * Sets the parameter-flag which indicates that this item
	 * is a parameter (an item with the no prefix)
	 * 
	 * @return
	 */
	protected CmdLnBase setAsParameter() {
		isOption = false;
		isShortOption = false;
		isParameter = true;
		return this;
	}

	/**
	 * Sets the parameter-flag which indicates that this item
	 * is a parameter (an item with the no prefix)
	 * 
	 * @return
	 */
	protected boolean isParameter() {
		return isParameter;
	}

	/**
	 * Returns a string which describes the type:
	 * - option
	 * - short option
	 * - parameter
	 * 
	 * @return
	 */
	public String getTypeDescString() {
		if (isOption) {
			return OPTION_DESC;
		} else if (isShortOption) {
			return SHORTOPTION_DESC;
		} else if (isParameter) {
			return PARAMETER_DESC;
		}

		return null;
	}

	/**
	 * This flag indicates if this item has been parsed or not. The item
	 * is considered as being parsed if a value has been set (any call to the
	 * setValue-method, thus even the setValue-method is called with a null-value
	 * this flag shows that the item has actually been parsed)
	 * 
	 * @return
	 */
	protected boolean isParsed() {
		return isParsed;
	}

	/**
	 * Returns the status of the value-required-flag.<br>
	 * An item which requires a value has to be given at least with
	 * an item-value-separator ({@link CommandOPTools}.ITEM_VALUE_SEPARATOR),
	 * followed by nothing (an empty string) or any other string.
	 * 
	 * @return
	 */
	protected boolean isValueRequired() {
		return isValueRequired;
	}

	/**
	 * Sets the status of the hidden-flag.<br>
	 * If an item is set to be hidden, it does not show up when
	 * printing the structure with the ({@link CommandOPPrinter}).
	 * 
	 * @return
	 */
	protected CmdLnBase setHiddenInPrint() {
		isHiddenInPrint = true;
		return this;
	}

	/**
	 * Returns the status of the hidden-flag.<br>
	 * If an item is set to be hidden, it does not show up when
	 * printing with the ({@link CommandOPPrinter}).
	 * 
	 * @return
	 */
	protected boolean isHiddenInPrint() {
		return isHiddenInPrint;
	}

	/**
	 * Returns true if this item is an alias of another item
	 * 
	 * @return
	 */
	protected boolean isAlias() {
		return aliasOf != null;
	}

	/**
	 * Returns true if this item has at least one alias
	 * 
	 * @return
	 */
	protected boolean hasAlias() {
		return alias.size() > 0;
	}

	/**
	 * Returns true if this item has an alias with the given name
	 * 
	 * @param aliasName
	 * @return
	 */
	protected boolean hasAlias(String aliasName) {
		return alias.containsKey(aliasName);
	}

	/**
	 * Returns the alias with the given name, or null
	 * if such an alias does not exist.
	 * 
	 * @param aliasName
	 * @return
	 */
	protected CmdLnBase getAlias(String aliasName) {
		return alias.get(aliasName);
	}

	/**
	 * Returns the map with all the alias
	 * 
	 * @return
	 */
	protected HashMap<String, CmdLnBase> getAlias() {
		return alias;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	protected boolean hasParent() {
		if (parent == null) {
			return false;
		}

		if (parent instanceof CommandOP) {
			return false;
		}

		return true;
	}

	/**
	 * Returns true if this item is defined as a multi-value-item. If an item is defined
	 * as such, all the command line arguments which follow the item are
	 * used as values, until the next defined item appears.
	 * 
	 * @return
	 */
	protected boolean isMultiValueItem() {
		return isMultiValueItem;
	}

	/**
	 * Returns the maximum value of possible items
	 * 
	 * @return
	 */
	protected int getMultiValuesRangeMax() {
		return multiValueMax;
	}

	/**
	 * Returns the minimum value of possible items
	 * 
	 * @return
	 */
	protected int getMultiValuesRangeMin() {
		return multiValueMin;
	}

	/**
	 * Returns the number of values. Only if this item is set as
	 * multi value item this number can be > 1
	 * 
	 * @return
	 */
	protected int getNumOfValues() {
		if (values.size() == 0) {
			//The default value (or null if no value is set) is always there
			return 1;
		}

		return values.size();
	}

	/**
	 * Sets the number which represents the position of this
	 * item in the command line string
	 * 
	 * @param cmdLnPos
	 */
	protected void setCmdLnPos(int cmdLnPos) {
		this.cmdLnPos = cmdLnPos;
	}

	/**
	 * Returns the number which represents the position of this
	 * item in the command line string
	 * 	 *
	 * @return
	 */
	protected int getCmdLnPos() {
		return cmdLnPos;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	protected CmdLnBase getNextItem() {
		if (parent == null) {
			return null;
		}

		LinkedList<CmdLnBase> list = CommandOPTools.createFlatList(parent.getChildren());

		int index = 0;

		if (isAlias()) {
			index = list.indexOf(getAliasOf()) + 1;
		} else {
			index = list.indexOf(this) + 1;
		}

		//Skip aliases
		while (index < list.size() && list.get(index).isAlias()) {
			index++;
		}

		if (list.size() > index && index >= 0) {
			return list.get(index);
		}

		//		//Continue with its children
		//		if (children.size() > 0) {
		//			CmdLnBase b = children.values().iterator().next();
		//			if (b.isAlias()) {
		//				return b.getAliasOf();
		//			} else {
		//				return b;
		//			}
		//		}

		return null;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	protected CmdLnBase getPreviousItem() {
		if (parent == null) {
			return null;
		}

		LinkedHashMap<String, CmdLnBase> parentChildren = parent.getChildren();

		LinkedList<CmdLnBase> list = new LinkedList<CmdLnBase>(parentChildren.values());

		int index = 0;

		if (isAlias()) {
			index = list.indexOf(getAliasOf()) - 1;
		} else {
			index = list.indexOf(this) - 1;
		}

		//Skip aliases
		while (index > 0 && list.get(index).isAlias()) {
			index--;
		}

		if (list.size() > index && index >= 0) {
			return list.get(index);
		}

		return null;
	}


	@Override
	public String toString() {
		//		return name + "=" + values + "(alias=" + alias +
		//				", isParsed=" + isParameter +
		//				", isMandatory=" + isMandatory +
		//				", isOption=" + isOption +
		//				", isShortOption=" + isShortOption +
		//				", isParameter=" + isParameter +
		//				", isBoolean=" + isBoolean +
		//				", isValueRequired=" + isValueRequired +
		//				", isHiddenInHelp=" + isHiddenInPrint +
		//				", isMultiValueItem=" + isMultiValueItem +
		//				", hasParent=" + hasParent() +
		//				(hasParent() ? ", parent=" + getParent().getName() : "") +
		//				")";

		return name + "=" + values + "(alias=" + alias + ")";
	}

}
