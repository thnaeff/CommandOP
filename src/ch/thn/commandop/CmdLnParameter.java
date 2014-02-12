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

import ch.thn.commandop.validator.CommandOPValidator;


/**
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class CmdLnParameter extends CmdLnValue {

	/**
	 * 
	 * @param name
	 * @param defaultValue
	 * @param description
	 * @see CmdLnBase#CmdLnBase(String, String, String)
	 */
	public CmdLnParameter(String name, String defaultValue, String description) {
		super(name, defaultValue, description);
	}
	
	/**
	 * 
	 * @param name
	 * @param description
	 * @see CmdLnBase#CmdLnBase(String, String)
	 */
	public CmdLnParameter(String name, String description) {
		super(name, null, description);
	}
	
	/**
	 * 
	 * @param name
	 * @see CmdLnBase#CmdLnBase(String)
	 */
	public CmdLnParameter(String name) {
		super(name, null, null);
	}
	
	/**
	 * 
	 * @see CmdLnBase#CmdLnBase()
	 */
	public CmdLnParameter() {
		super(null, null);
	}
	
	@Override
	public CmdLnParameter addParameter(String name, String defaultValue,
			String description) {
		return super.addParameter(name, defaultValue, description);
	}
	
	@Override
	public CmdLnParameter addParameter(String name, String description) {
		return super.addParameter(name, description);
	}
	
	@Override
	public CmdLnParameter addParameters(CmdLnParameter... items) {
		super.addParameters(items);
		return this;
	}
	
	/**
	 * Adds an alias to this item. An alias can be used instead of the 
	 * item's name.
	 * 
	 * @param aliasName
	 * @return
	 */
	public CmdLnParameter addAlias(String aliasName) {
		
		CmdLnParameter item = null;
		
		if (getParentInternal() instanceof CommandOP) {
			//It's an option, so its parent is ComandOP
			item = ((CommandOP)getParentInternal()).addOption(aliasName, null);
		} else {
			//It's a parameter, so its parent is not ComandOP
			item = getParentInternal().addParameter(aliasName, null);
		}
		
		item.setAliasOf(this);
		
		alias.put(aliasName, item);
		
		//Return the alias
		return item;
	}
	
	
	
	/**
	 * Sets the mandatory-flag.<br>
	 * A mandatory item needs to be given (only the item, not the 
	 * value. See isValueRequired() for that matter). The flag of 
	 * a mandatory item is only checked if its parent item is given
	 * too.
	 * 
	 * @return
	 */
	public CmdLnParameter setMandatory() {
		isMandatory = true;
		return this;
	}
	
	
	/**
	 * Sets the status of the boolean-flag.<br>
	 * A boolean item behaves in a special way when 
	 * setting or getting its value.
	 * 
	 * @return
	 */
	public CmdLnParameter setAsBoolean() {
		isBoolean = true;
		return this;
	}
	
	/**
	 * Sets the status of the value-required-flag.<br>
	 * An item which requires a value has to be given at least with 
	 * an item-value-separator ({@link CommandOPTools}.ITEM_VALUE_SEPARATOR), 
	 * followed by nothing (an empty string) or any other string.
	 * 
	 * @return
	 */
	public CmdLnParameter setValueRequired() {
		this.isValueRequired = true;
		return this;
	}
	
	/**
	 * Sets the status of the hidden-flag.<br>
	 * If an item is set to be hidden, it does not show up when 
	 * printing the structure with the ({@link CommandOPPrinter}).
	 * 
	 * @return
	 */
	public CmdLnParameter setHiddenInPrint() {
		this.isHiddenInPrint = true;
		return this;
	}
	
	
	/**
	 * Sets the validator for this item. The validator is called when 
	 * the item is processed.
	 * 
	 * @param validator
	 * @return
	 */
	public CmdLnParameter setValidator(CommandOPValidator validator) {
		this.validator = validator;
		return this;
	}
	
	/**
	 * Defines this item as a multi-value-item. If an item is defined 
	 * as such, all the command line arguments which follow the item are 
	 * used as values, until the next defined item appears.
	 * 
	 * @return
	 */
	public CmdLnParameter setAsMultiValueItem() {
		this.isMultiValueItem = true;
		return this;
	}
	
	/**
	 * Defines this item as a multi-value-item. If an item is defined 
	 * as such, all the command line arguments which follow the item are 
	 * used as values, until the next defined item appears.<br>
	 * The min/max parameters define how many values are required/possible. 
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	protected CmdLnParameter setAsMultiValueItem(int min, int max) {
		this.isMultiValueItem = true;
		multiValueMin = min;
		multiValueMax = max;
		return this;
	}
	
	
	
}
