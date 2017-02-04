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

import java.util.Map;

/**
 * Just another name for a {@link CmdLnParameter} or a {@link CmdLnOption} to
 * have a common type when retrieving values. Also, this type exposes many methods
 * for public access.
 * 
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class CmdLnValue extends CmdLnItem {

	/**
	 * 
	 * @param name
	 * @param defaultValue
	 * @param description
	 * @see CmdLnItem#CmdLnBase(String, String, String)
	 */
	protected CmdLnValue(String name, String defaultValue, String description) {
		super(name, defaultValue, description);
	}

	/**
	 * 
	 * @param name
	 * @param description
	 * @see CmdLnItem#CmdLnBase(String, String)
	 */
	protected CmdLnValue(String name, String description) {
		super(name, null, description);
	}

	/**
	 * 
	 * @param name
	 * @see CmdLnItem#CmdLnBase(String)
	 */
	protected CmdLnValue(String name) {
		super(name, null, null);
	}

	/**
	 * 
	 * @see CmdLnItem#CmdLnBase()
	 */
	protected CmdLnValue() {
		super(null, null);
	}


	@Override
	public CmdLnValue getChild(String childName) {
		return super.getChild(childName);
	}

	@Override
	public Map<String, CmdLnValue> getChildren() {
		return super.getChildren();
	}

	@Override
	public String getValue() {
		return super.getValue();
	}

	@Override
	public String getValue(int multiValuePos) {
		return super.getValue(multiValuePos);
	}

	@Override
	public String getDefaultValue() {
		return super.getDefaultValue();
	}

	@Override
	public int getCmdLnPos() {
		return super.getCmdLnPos();
	}

	@Override
	public String getDescription() {
		return super.getDescription();
	}

	@Override
	public int getLevel() {
		return super.getLevel();
	}

	@Override
	public String getName() {
		return super.getName();
	}

	@Override
	public int getNumOfValues() {
		return super.getNumOfValues();
	}

	@Override
	public boolean isBoolean() {
		return super.isBoolean();
	}

	@Override
	public boolean isMandatory() {
		return super.isMandatory();
	}

	@Override
	public boolean isMultiValueItem() {
		return super.isMultiValueItem();
	}

	@Override
	public boolean isShortOption() {
		return super.isShortOption();
	}

	@Override
	protected boolean isParsed() {
		return super.isParsed();
	}


}
