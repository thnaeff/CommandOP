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
package ch.thn.app.commandop;

/**
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class PreParsedItem {
	
	
	private boolean isParameter = false;
	private boolean isOption = false;
	private boolean isShortOption = false;
	
	private String name = null;
	private String value = null;
	
	/**
	 * 
	 * 
	 * @param arg
	 */
	public PreParsedItem(String arg) {
		
		if (arg.startsWith(CommandOPTools.OPTIONSPREFIX_LONG)) {
			isOption = true;
			
			name = CommandOPTools.getOption(arg);
			value = CommandOPTools.getOptionValue(arg);
		} else if (arg.startsWith(CommandOPTools.OPTIONSPREFIX_SHORT)) {
			isShortOption = true;
			
			name = CommandOPTools.getOption(arg);
			value = CommandOPTools.getOptionValue(arg);
		} else {
			isParameter = true;
			
			name = CommandOPTools.getParameter(arg);
			value = CommandOPTools.getParameterValue(arg);
		}
		
	}
	
	/**
	 * 
	 * 
	 * @param value
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public boolean isParameter() {
		return isParameter;
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public boolean isOption() {
		return isOption;
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public boolean isShortOption() {
		return isShortOption;
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public String getValue() {
		return value;
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
			return CmdLnItem.OPTION_DESC;
		} else if (isShortOption) {
			return CmdLnItem.SHORTOPTION_DESC;
		} else if (isParameter) {
			return CmdLnItem.PARAMETER_DESC;
		}
		
		return null;
	}
	
	@Override
	public String toString() {
		return "[" + name + "=>" + value + "]";
	}
	

}
