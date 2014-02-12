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
package ch.thn.commandop.validator;

import ch.thn.commandop.CmdLnBase;

/**
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public abstract class CommandOPValidator {
	
	private String errorMessage = null;
	
	/**
	 * Set an error message to describe the validation error
	 * 
	 * @param errorMessage
	 */
	protected void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	/**
	 * Returns the error messge which describes the validation error
	 * 
	 * @return
	 */
	public String getErrorMessage() {
		if (errorMessage == null) {
			return "";
		}
		
		return errorMessage;
	}
	
	/**
	 * Validates the currently parsed item
	 * 
	 * @param item The actual item. The value is not yet set for this item, it is 
	 * set when {@link #validate(CmdLnBase, String, int)} returns <code>true</code>
	 * @param newValue This is the value which will be set for the item
	 * @param multiValuePos This is the position of the value. If the item is not 
	 * defined as multi-value-item, multiValuePos is always 0. If the item is defined 
	 * as multi-value-item, multiValuePos contains the number of the value, starting with 0
	 * @return This method should return <code>true</code> if the value newValue is valid, and 
	 * <code>false</code> if the value is not valid
	 */
	public abstract boolean validate(CmdLnBase item, String newValue, int multiValuePos);

}
