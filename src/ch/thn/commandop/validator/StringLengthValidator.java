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
public class StringLengthValidator extends CommandOPValidator {
	
	private int minLength = 0;
	private int maxLength = 0;
	
	
	public StringLengthValidator(int minLength, int maxLength) {
		this.minLength = minLength;
		this.maxLength = maxLength;
	}
	
	public StringLengthValidator(int minLength) {
		this(minLength, 0);
	}

	@Override
	public boolean validate(CmdLnItem item, String newValue, int multiValuePos) {
		
		if (minLength != 0 && newValue.length() < minLength) {
			setErrorMessage("Value too short (minimum " + minLength + " characters needed).");
			return false;
		}
		
		if (maxLength != 0 && newValue.length() > maxLength) {
			setErrorMessage("Value too long (maximum " + maxLength + " characters allowed).");
			return false;
		}
		
		return true;
		
	}

}
