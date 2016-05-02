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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.thn.commandop.CmdLnItem;

/**
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class RegexMatcherValidator extends CommandOPValidator {
	
	private Pattern pattern = null;
	
	private String messageInCaseOfError = null;
	
	/**
	 * 
	 * 
	 * @param regexPattern
	 */
	public RegexMatcherValidator(String regexPattern) {
		pattern = Pattern.compile(regexPattern);
		
	}
	
	/**
	 * 
	 * 
	 * @param regexPattern
	 * @param messageInCaseOfError The message to be set as error message if there 
	 * is an error.
	 */
	public RegexMatcherValidator(String regexPattern, String messageInCaseOfError) {
		pattern = Pattern.compile(regexPattern);
		this.messageInCaseOfError = messageInCaseOfError;
		
	}
	

	@Override
	public boolean validate(CmdLnItem item, String newValue, int multiValuePos) {
		
		if (newValue == null) {
			setErrorMessage(messageInCaseOfError);
			return false;
		}
		
		Matcher matcher = pattern.matcher(newValue);
		
		if (matcher.find()) {
			return true;
		}
		
		setErrorMessage(messageInCaseOfError);
		return false;
		
	}

}
