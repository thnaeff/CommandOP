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

import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class CommandOPPrinter {
	
	private static final String ALIGN_LOCATION = "##";
	
	private CommandOP cmdop = null;
	
	/**
	 * Printer-class with some methods to display the {@link CommandOP}-status 
	 * and the parsed items
	 * 
	 * @param cmdop
	 */
	public CommandOPPrinter(CommandOP cmdop) {
		this.cmdop = cmdop;
	}
	
	
	/**
	 * Returns a formatted list of the pre-parsed items
	 * 
	 * @param flat
	 * @return
	 */
	public String getPreParsed(boolean flat) {
		StringBuilder preparsed = new StringBuilder();
		
		PreParsedChain chain = cmdop.getPreParsedChain();
				
		while (chain != null) {
			preparsed.append(chain.getName() + CommandOPTools.ITEM_VALUE_SEPARATOR + chain.getValue());
			
			if (chain.isOption() || chain.isShortOption()) {
				preparsed.append(" (option)");
			} else {
				preparsed.append(" (param)");
			}
			
			if (flat) {
				preparsed.append(" ");
			} else {
				preparsed.append("\n");
				
				for (int i = 0; i <= chain.getChainPos(); i++) {
					preparsed.append("  ");
				}
			}
			
			chain = chain.getNext();
		}
		
		return preparsed.toString();
		
	}
	
	
	/**
	 * Returns some help text for the usage with the defined items
	 * 
	 * @return
	 */
	public String getHelpText() {
		String s = "Command line help:\n";
		
		s = s + getDefinedItems(false, false, true, true);
		
		return s;
	}
	
	
	/**
	 * Returns a formatted list showing the defined items with value (if selected) and 
	 * description
	 * 
	 * @param flat
	 * @param withValue
	 * @param withDescription
	 * @param hideHidden
	 * @return
	 */
	public String getDefinedItems(boolean flat, boolean withValue, 
			boolean withDescription, boolean hideHidden) {
		
		//Do not use cmdop.getChildren() here, because the no-option-parameters 
		//should appear in front of the rest
		LinkedHashMap<String, CmdLnBase> all = new LinkedHashMap<String, CmdLnBase>();
		all.putAll(cmdop.getParameters());
		all.putAll(cmdop.getOptions());
		
		LinkedList<StringBuilder> lines = new LinkedList<StringBuilder>();
		LinkedList<CmdLnBase> flatList = CommandOPTools.createFlatList(all);
		
		int longestLine = 0;
		
		
		for (CmdLnBase item : flatList) {
			
			if (hideHidden && item.isHiddenInPrint()) {
				continue;
			}
			
			if (item.isAlias()) {
				continue;
			}
			
			StringBuilder line = new StringBuilder();
			
			//Short options (only if no flat output)
			if (!flat && item.hasAlias()) {				
				for (CmdLnBase alias : item.getAlias().values()) {
					if (!alias.isShortOption()) {
						continue;
					}
					
					line.append(CommandOPTools.OPTIONSPREFIX_SHORT);	
					line.append(alias.getName());
					line.append(", ");
				}
			}
			
			//Prefix and insets
			if (item.isOption()) {
				line.append(CommandOPTools.OPTIONSPREFIX_LONG);
			} else if (item.isShortOption()) {
				line.append(CommandOPTools.OPTIONSPREFIX_SHORT);
			} else if (item.isParameter() && !flat) {
				if (item.hasParent()) {
					line.append(" ");
				}
				
				//Insets for higher levels
				for (int i = 0; i < item.getLevel(); i++) {
					line.append("   ");
				}
			}
			
			//Optional
			if (!item.isMandatory() && item.isParameter()) {
				line.append("[");
			}
			
//			//Mandatory
//			if (item.isMandatory()) {
//				line.append("*");
//			}
			
			line.append(item.getName());
			
			//Optional
			if (!item.isMandatory() && item.isParameter()) {
				line.append("]");
			}
			
			//Value
			if (withValue) {
				line.append(CommandOPTools.ITEM_VALUE_SEPARATOR);
				
				//Whether the item is a multi value item or not, all the values
				//are shown. If its not a multi value item, there is only one value to show
				for (int i = 0; i < item.getNumOfValues(); i++) {
					line.append(item.getValue(i));
					
					if (i < item.getNumOfValues() - 1) {
						line.append(" ");
					}
				}
				
				if (item.getDefaultValue() != null) {
					line.append(" (" + item.getDefaultValue() + ")");
				}
			}
			
			//Aliases
			if (item.hasAlias()) {
				String commaToAppend = null;
				StringBuilder sbAliases = new StringBuilder();
				
				for (CmdLnBase alias : item.getAlias().values()) {
					if (alias.isShortOption()) {
						continue;
					}
					
					if (commaToAppend != null) {
						sbAliases.append(commaToAppend);
						commaToAppend = null;
					}
					
					sbAliases.append(alias.getName());
					commaToAppend = ", ";
				}
				
				if (sbAliases.length() > 0) {
					line.append(" (");
					line.append(sbAliases);
					line.append(") ");
				}
			}
			
			//The line length has to be taken before the comment is added
			if (line.length() > longestLine) {
				longestLine = line.length();
			}
			
			//Description
			if (!flat && withDescription) {
				String desc = item.getDescription();
				
				if (desc != null && desc.length() > 0) {
					line.append(ALIGN_LOCATION + item.getDescription());
				}
			}
			
			lines.add(line);
			
		}
		
		
		StringBuilder output = new StringBuilder();
		
		//Insets for descriptions
		for (StringBuilder sb : lines) {
			
			if (flat) {
				output.append(sb.toString());
				output.append(" ");
			} else if (withDescription) {
				String space = CommandOPTools.makeRightAlignSpace(longestLine + 5, sb.indexOf(ALIGN_LOCATION), true);
				output.append(sb.toString().replace(ALIGN_LOCATION, space));
				output.append("\n");
			} else {
				output.append(sb.toString());
				output.append("\n");
			}
			
		}
		
		return output.toString();
	}
	
	
	/**
	 * Returns the plain command line string
	 * 
	 * @return
	 */
	public String getArgs() {
		String[] args = cmdop.getArgs();
		
		if (args == null) {
			return "Not yet parsed";
		}
		
		StringBuilder s = new StringBuilder();
		
		for (int i = 0; i < args.length; i++) {
			s.append(args[i]);
			s.append(" ");
		}
		
		return s.toString();
		
	}
	
	

}
