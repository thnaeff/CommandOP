/**
 * 
 */
package ch.thn.commandop;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Vector;

/**
 * @author thomas
 *
 */
public class CmdLnItem {
	
	//TODO allow multi var items, like "server 65001 localhost"
	//maybe look up variables from the parameter until the next known option/parameter?
	
	
	private CmdLnItem parent = null;
	private CmdLnItem aliasOf = null;	//The item of which this item is the alias of
	
	private CommandOPValidator validator = null;
	
	private LinkedHashMap<String, CmdLnItem> alias = null;		//The items which are set as alias
	private LinkedHashMap<String, CmdLnItem> children = null;
	
	private String description = null;
	private String name = null;
	private String defaultValue = null;
	
	private Vector<String> values = null;
	
	private boolean isParsed = false;
	private boolean isMandatory = false;
	private boolean isOption = false;
	private boolean isShortOption = false;
	private boolean isParameter = false;
	private boolean isBoolean = false;
	private boolean isValueRequired = false;
	private boolean isHiddenInHelp = false;
	private boolean isMultiValueItem = false;
	
	private int level = 0;
	private int multiValueMin = 0;
	private int multiValueMax = 0;
	private int cmdLnPos = 0;
	
	
	/**
	 * 
	 * @param name
	 * @param defaultValue
	 * @param description
	 */
	public CmdLnItem(String name, String defaultValue, String description) {
		this.name = name;
		this.defaultValue = defaultValue;
		this.description = description;
		
		values = new Vector<String>();
		children = new LinkedHashMap<String, CmdLnItem>();
		alias = new LinkedHashMap<String, CmdLnItem>();
		
	}
	
	/**
	 * 
	 * @param name
	 * @param description
	 */
	public CmdLnItem(String name, String description) {
		this(name, null, description);
	}
	
	/**
	 * 
	 */
	public CmdLnItem() {
		this(null, null);
	}
	
	/**
	 * Sets the parent item of the tree
	 * 
	 * @param parent
	 */
	protected void setParent(CmdLnItem parent) {
		this.parent = parent;
		
		adjustLevel();
	}
	
	/**
	 * Returns this items parent item
	 * 
	 * @return
	 */
	public CmdLnItem getParent() {
		return parent;
	}
	
	/**
	 * Returns the level of this item. The level defines the position 
	 * in the tree (+1 of it's parent item)
	 * 
	 * @return
	 */
	public int getLevel() {
		return level;
	}
	
	/**
	 * Adds the given items as children to this item
	 * 
	 * @param items
	 * @return
	 */
	public CmdLnItem addParameters(CmdLnItem... items) {
		
		for (CmdLnItem item : items) {
			children.put(item.getName(), item);
			item.setAsParameter();
			item.setParent(this);
		}
		
		return this;
		
	}
	
	/**
	 * Adds a new item with the given parameters to this item as child
	 * 
	 * @param name
	 * @param defaultValue
	 * @param description
	 * @return
	 */
	public CmdLnItem addParameter(String name, String defaultValue, String description) {
		CmdLnItem child = new CmdLnItem(name, defaultValue, description);
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
	public CmdLnItem addParameter(String name, String description) {
		return addParameter(name, null, description);
	}
	
	/**
	 * Adds an alias to this item. An alias can be used instead of the 
	 * item's name.
	 * 
	 * @param aliasName
	 * @return
	 */
	public CmdLnItem addAlias(String aliasName) {
		
		CmdLnItem item = null;
		
		if (getParent() instanceof CommandOP) {
			//It's an option, so its parent is ComandOP
			item = ((CommandOP)getParent()).addOption(aliasName, null);
		} else {
			//It's a parameter, so its parent is not ComandOP
			item = getParent().addParameter(aliasName, null);
		}
		
		item.setAliasOf(this);
		
		alias.put(aliasName, item);
		
		//Return this item and not the alias-item, because all 
		//settings should be done on the original item. The 
		//alias only holds the alias-name
		return this;
	}
	
	/**
	 * Sets an alias for this item. The alias can be used instead of 
	 * this item name.
	 * 
	 * @param aliasOf
	 */
	protected void setAliasOf(CmdLnItem aliasOf) {
		this.aliasOf = aliasOf;
	}
	
	/**
	 * Returns the item of which this item is the alias of
	 * 
	 * @return
	 */
	public CmdLnItem getAliasOf() {
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
		
		for (CmdLnItem child : children.values()) {
			child.adjustLevel();
		}
		
	}
	
	/**
	 * Returns true if this item has the specified child item
	 * 
	 * @param childName
	 * @return
	 */
	public boolean hasChild(String childName) {
		return children.containsKey(childName);
	}
	
	/**
	 * Returns true if this item has one or more child items
	 * 
	 * @return
	 */
	public boolean hasChildren() {
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
	public CmdLnItem getChild(String childName) {
		if (!children.containsKey(childName)) {
			return null;
		}
		
		CmdLnItem item = children.get(childName);
				
		if (item.isAlias()) {
			return item.getAliasOf();
		} else {
			return item;
		}
	}
	
	/**
	 * Returns the list with all the children of this item 
	 * (but it does only return this item's children, and does 
	 * not go deeper)
	 * 
	 * @return
	 */
	protected HashMap<String, CmdLnItem> getChildren() {
		return children;
	}
	
	/**
	 * Returns the name of this item
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the value of this item, or if no value is set the default value 
	 * is returned. If the item is defined as multi-value-item, the first 
	 * available value is returned (same as {@link #getValue(int)} with parameter 0)
	 * 
	 * @return
	 */
	public String getValue() {
		return getValue(0);
	}
	
	/**
	 * Returns the value of this item which is on the given position. If the given 
	 * position does not exist, null is returned.
	 * 
	 * @param multiValuePos
	 * @return
	 */
	public String getValue(int multiValuePos) {
		if (aliasOf != null) {
			//This item is only the alias of another item
			return aliasOf.getValue(multiValuePos);
		}
		
		if (isParsed) {
			if (multiValuePos > values.size()) {
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
	public String getDefaultValue() {
		return defaultValue;
	}
	
	/**
	 * Returns the description of this item, or null 
	 * if not description has been set
	 * 
	 * @return
	 */
	public String getDescription() {
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
	 * @return
	 */
	protected boolean setValue(String value) {
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
	 * @return
	 */
	protected boolean addMultiValue(String value) {
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
	 * @return
	 */
	private boolean setValue(String value, int multiValuePos) {

		if (aliasOf != null) {
			//This item is only the alias of another item
			return aliasOf.setValue(value, multiValuePos);
		}
				
		if (isMultiValueItem) {
			if (value == null) {
				//No null-values for multi value items. Otherwise things like 
				//"item=null value1 value2" happen which do not make sense
				return true;
			} else if (multiValueMax != 0 && values.size() >= multiValueMax) {
				//Limit the number of values if a value is set for multiValueMax
				System.err.println("CommandOP> Item '" + getName() + "' is limited to " + multiValueMax + " values.");
				return false;
			}
		}
		
		//Do not parse an item twice (do not set a value twice) if its not 
		//a multi value item
		if (isParsed && !isMultiValueItem) {
			System.err.println("CommandOP> Item '" + getName() + "' occurs more than once. Only first occurrence is used.");
			return false;
		}
		
		//Set the parsed flag already here. Even though the validation might fail 
		//and the value is not set, it is useful to know that the item has been 
		//parsed
		isParsed = true;
		
		if (validator != null) {
			if (!validator.validate(this, value, multiValuePos)) {
				System.err.println("CommandOP> Validation of item '" + getName() + "' with value '" + value + "' failed (validator was " + validator.getClass().getSimpleName() + ").");
				return false;
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
		
		return true;
	}
	
	/**
	 * Sets the mandatory-flag.<br>
	 * A mandatory item needs to be given (only the item, not the 
	 * value. See isValueRequired() for that matter). The flag of 
	 * a mandatory item is only checked if its parent item is given
	 * too.
	 * 
	 * @param mandatory
	 * @return
	 */
	public CmdLnItem setMandatory(boolean mandatory) {
		isMandatory = mandatory;
		return this;
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
	public boolean isMandatory() {
		return isMandatory;
	}
	
	/**
	 * Sets the status of the boolean-flag.<br>
	 * A boolean item behaves in a special way when 
	 * setting or getting its value.
	 * 
	 * @return
	 */
	public CmdLnItem setAsBoolean() {
		isBoolean = true;
		return this;
	}
	
	/**
	 * Returns the status of the boolean-flag.<br>
	 * A boolean item behaves in a special way when 
	 * setting or getting its value.
	 * 	 * 
	 * @return
	 */
	public boolean isBoolean() {
		return isBoolean;
	}
	
	/**
	 * Returns true if this item is a child-item of some 
	 * other item
	 * 
	 * @return
	 */
	public boolean isChild() {
		if (parent instanceof CommandOP) {
			return false;
		} else {
			return (parent != null);
		}
	}
	
	/**
	 * Sets the option-flag which indicates that this item 
	 * is an option (an item with the long prefix {@link CommandOPTools}.OPTIONSPREFIX_LONG)
	 */
	protected void setAsOption() {
		isOption = true;
		isShortOption = false;
		isParameter = false;
	}
	
	/**
	 * Returns the option-flag which indicates that this item 
	 * is an option (an item with the long prefix {@link CommandOPTools}.OPTIONSPREFIX_LONG)
	 * 
	 * @return
	 */
	public boolean isOption() {
		return isOption;
	}
	
	/**
	 * Sets the short-option-flag which indicates that this item 
	 * is a short-option (an item with the short prefix {@link CommandOPTools}.OPTIONSPREFIX_SHORT)
	 */
	protected void setAsShortOption() {
		isOption = false;
		isShortOption = true;
		isParameter = false;
	}
	
	/**
	 * Sets the short-option-flag which indicates that this item 
	 * is a short-option (an item with the short prefix {@link CommandOPTools}.OPTIONSPREFIX_SHORT)
	 * 
	 * @return
	 */
	public boolean isShortOption() {
		return isShortOption;
	}
	
	/**
	 * Sets the parameter-flag which indicates that this item 
	 * is a parameter (an item with the no prefix)
	 */
	protected void setAsParameter() {
		isOption = false;
		isShortOption = false;
		isParameter = true;
	}
	
	/**
	 * Sets the parameter-flag which indicates that this item 
	 * is a parameter (an item with the no prefix)
	 * 
	 * @return
	 */
	public boolean isParameter() {
		return isParameter;
	}
	
	/**
	 * This flag indicates if this item has been parsed or not. The item 
	 * is considered as being parsed if a value has been set (any call to the 
	 * setValue-method, thus even the setValue-method is called with a null-value 
	 * this flag shows that the item has actually been parsed)
	 * 
	 * @return
	 */
	public boolean isParsed() {
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
	public boolean isValueRequired() {
		return isValueRequired;
	}
	
	/**
	 * Sets the status of the value-required-flag.<br>
	 * An item which requires a value has to be given at least with 
	 * an item-value-separator ({@link CommandOPTools}.ITEM_VALUE_SEPARATOR), 
	 * followed by nothing (an empty string) or any other string.
	 * 
	 * @param isValueRequired
	 * @return
	 */
	public CmdLnItem setValueRequired(boolean isValueRequired) {
		this.isValueRequired = isValueRequired;
		return this;
	}
	
	/**
	 * Returns the status of the hidden-flag.<br>
	 * If an item is set to be hidden, it does not show up when 
	 * printing the help-text ({@link CommandOPPrinter}.getHelpText()).
	 * 
	 * @return
	 */
	public boolean isHiddenInHelp() {
		return isHiddenInHelp;
	}
	
	/**
	 * Sets the status of the hidden-flag.<br>
	 * If an item is set to be hidden, it does not show up when 
	 * printing the help-text ({@link CommandOPPrinter}.getHelpText()).
	 * 
	 * @param isHiddenInHelp
	 * @return
	 */
	public CmdLnItem setHiddenInHelp(boolean isHiddenInHelp) {
		this.isHiddenInHelp = isHiddenInHelp;
		return this;
	}
	
	/**
	 * Returns true if this item is an alias of another item
	 * 
	 * @return
	 */
	public boolean isAlias() {
		return (aliasOf != null);
	}
	
	/**
	 * Returns true if this item has at least one alias
	 * 
	 * @return
	 */
	public boolean hasAlias() {
		return (alias.size() > 0);
	}
	
	/**
	 * Returns true if this item has an alias with the given name
	 * 
	 * @param aliasName
	 * @return
	 */
	public boolean hasAlias(String aliasName) {
		return (alias.containsKey(aliasName));
	}
	
	/**
	 * Returns the alias with the given name, or null 
	 * if such an alias does not exist.
	 * 
	 * @param aliasName
	 * @return
	 */
	public CmdLnItem getAlias(String aliasName) {
		return alias.get(aliasName);
	}
	
	/**
	 * Returns the map with all the alias
	 * 
	 * @return
	 */
	protected HashMap<String, CmdLnItem> getAlias() {
		return alias;
	}
	
	/**
	 * Sets the validator for this item. The validator is called when 
	 * the item is processed.
	 * 
	 * @param validator
	 * @return
	 */
	public CmdLnItem setValidator(CommandOPValidator validator) {
		this.validator = validator;
		return this;
	}
	
	/**
	 * Defines this item as a multi-value-item. If an item is defined 
	 * as such, all the command line arguments which follow the item are 
	 * used as values, until the next defined item appears.
	 * 
	 * @param isMultiValueItem
	 * @return
	 */
	public CmdLnItem setAsMultiValueItem(boolean isMultiValueItem) {
		this.isMultiValueItem = isMultiValueItem;
		return this;
	}
	
	/**
	 * Returns true if this item is defined as a multi-value-item. If an item is defined 
	 * as such, all the command line arguments which follow the item are 
	 * used as values, until the next defined item appears.
	 * 
	 * @return
	 */
	public boolean isMultiValueItem() {
		return isMultiValueItem;
	}
	
	/**
	 * Defines how many values are required/possible. 
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	public CmdLnItem setMultiValuesRange(int min, int max) {
		multiValueMin = min;
		multiValueMax = max;
		return this;
	}
	
	/**
	 * Returns the maximum value of possible items
	 * 
	 * @return
	 */
	public int getMultiValuesRangeMax() {
		return multiValueMax;
	}
	
	/**
	 * Returns the minimum value of possible items
	 * 
	 * @return
	 */
	public int getMultiValuesRangeMin() {
		return multiValueMin;
	}
	
	/**
	 * Returns the number of values. Only if this item is set as 
	 * multi value item this number can be > 1
	 * 
	 * @return
	 */
	public int getNumOfValues() {
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
	public int getCmdLnPos() {
		return cmdLnPos;
	}
	

}
