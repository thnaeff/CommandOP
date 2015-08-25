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
 * A parameter is a child item of an option ({@link CmdLnOption}). A parameter can
 * have a value.
 *
 * One exception is an "optionless" parameter. An "optionless" parameter can be added
 * directly to the main {@link CommandOP} object.
 *
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class CmdLnParameter extends CmdLnValue {

	/**
	 *
	 * @param name
	 * @param defaultValue
	 * @param description
	 * @see CmdLnItem#CmdLnBase(String, String, String)
	 */
	public CmdLnParameter(String name, String defaultValue, String description) {
		super(name, defaultValue, description);
		setAsParameter();
	}

	/**
	 *
	 * @param name
	 * @param description
	 * @see CmdLnItem#CmdLnBase(String, String)
	 */
	public CmdLnParameter(String name, String description) {
		super(name, null, description);
		setAsParameter();
	}

	/**
	 *
	 * @param name
	 * @see CmdLnItem#CmdLnBase(String)
	 */
	public CmdLnParameter(String name) {
		super(name, null, null);
		setAsParameter();
	}

	/**
	 *
	 * @see CmdLnItem#CmdLnBase()
	 */
	public CmdLnParameter() {
		super(null, null);
		setAsParameter();
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

		if (alias.containsKey(aliasName)) {
			throw new CommandOPError("Alias with the name '" + aliasName + "' already exists. Can not add alias.");
		}

		//Adds an alias parameter
		CmdLnParameter item = getParentInternal().addParameter(aliasName, null);
		item.setAliasOf(this);

		alias.put(aliasName, item);

		//Return actual parameter and not the alias
		return this;
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
	@Override
	public CmdLnParameter setHiddenInPrint() {
		super.setHiddenInPrint();
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

	/**
	 * Sets the type string for this parameter. The type string is a descriptive
	 * string which is shown in the command line help output to describe the
	 * type of the value.
	 *
	 * @param typeString
	 * @return
	 */
	public CmdLnParameter setTypeString(String typeString) {
		this.typeString = typeString;
		return this;
	}


}
