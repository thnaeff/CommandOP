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

import ch.thn.commandop.CmdLnItem;

/**
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class NumberValidator extends CommandOPValidator {

	@Override
	public boolean validate(CmdLnItem item, String newValue, int multiValuePos) {
		
		if (newValue == null) {
			setErrorMessage("Failed to parse value as integer. Value is NULL");
			return false;
		}
		
		try {
			Integer.parseInt(newValue);
		} catch (NumberFormatException e) {
			setErrorMessage("Failed to parse value as integer. Value does not contain a parsable integer.");
			return false;
		}
		
		return true;
		
	}

}
