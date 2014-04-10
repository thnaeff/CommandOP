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


/**
 * 
 * 
 * Format follows the GNU format as much as possible: 
 * http://www.gnu.org/software/guile/manual/html_node/Command-Line-Format.html
 * 
 * @author Thomas Naeff (github.com/thnaeff)
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
	public CmdLnOption addShortAlias(Character shortName) {		
		addAlias(shortName.toString());
		alias.get(shortName.toString()).setAsShortOption();
		return this;
	}
	
	/**
	 * Adds an alias to this item. An alias can be used instead of the 
	 * item's name.
	 * 
	 * @param aliasName
	 * @return 
	 * @throws CommandOPError if an alias with the given name already exists
	 */
	public CmdLnParameter addAlias(String aliasName) {
		
		if (alias.containsKey(aliasName)) {
			throw new CommandOPError("Alias with the name '" + aliasName + "' already exists. Can not add alias.");
		}
		
		CmdLnParameter item = ((CommandOP)getParentInternal()).addOption(aliasName, null);
		
		item.setAliasOf(this);
		
		alias.put(aliasName, item);
		
		//Return actual parameter and not the alias
		return this;
	}
	
	
}
