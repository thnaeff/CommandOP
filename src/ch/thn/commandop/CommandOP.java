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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;


/**
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class CommandOP extends CmdLnBase {
	
	
	private PreParsedChain chainHead = null;
	
	private LinkedHashMap<String, CmdLnOption> options = null;
	private LinkedHashMap<String, CmdLnParameter> parameters = null;
	private LinkedHashMap<String, PreParsedItem> unknownArguments = null;
	
	private LinkedList<CommandOPGroup> groups = null;
	private LinkedList<String> errors = null;
	
	private String[] args = null;
	
	private boolean exceptionAtFirstError = false;
	
	
	/**
	 * CommandlineOptionParser<br>
	 * <br>
	 * 
	 */
	public CommandOP() {
		
		options = new LinkedHashMap<String, CmdLnOption>();
		parameters = new LinkedHashMap<String, CmdLnParameter>();
		groups = new LinkedList<CommandOPGroup>();
		unknownArguments = new LinkedHashMap<String, PreParsedItem>();
		errors = new LinkedList<String>();
		
	}
	
	/**
	 * Adds a new option with the given parameters. This option will 
	 * be the root-item for further items
	 * 
	 * @param name
	 * @param defaultValue
	 * @param description
	 * @return
	 */
	public CmdLnOption addOption(String name, String defaultValue, String description) {
		
		if (options.containsKey(name)) {
			throw new CommandOPError("The name '" + name + "' is already used as option. Can not add parameter.");
		} else if (alias.containsKey(name)) {
			throw new CommandOPError("The name '" + name + "' is already used as alias. Can not add parameter.");
		} else if (parameters.containsKey(name)) {
			throw new CommandOPError("The name '" + name + "' is already used as parameter.  Can not add parameter.");
		}
		
		CmdLnOption i = new CmdLnOption(name, defaultValue, description);
		i.setAsOption();
		i.setParent(this);
		options.put(name, i);
		children.put(name, i);
		return i;
	}
	
	/**
	 * Adds a new option with the given parameters. This option will 
	 * be the root-item for further items
	 * 
	 * @param name
	 * @param description
	 * @return
	 */
	public CmdLnOption addOption(String name, String description) {
		return addOption(name, null, description);
	}
	
	/**
	 * Adds a new parameter with the given values (as root-item).
	 * 
	 * @param name
	 * @param defaultValue
	 * @param description
	 * @return
	 */
	@Override
	public CmdLnParameter addParameter(String name, String defaultValue, String description) {
		
		if (options.containsKey(name)) {
			throw new CommandOPError("The name '" + name + "' is already used as option. Can not add parameter.");
		} else if (alias.containsKey(name)) {
			throw new CommandOPError("The name '" + name + "' is already used as alias. Can not add parameter.");
		} else if (parameters.containsKey(name)) {
			throw new CommandOPError("The name '" + name + "' is already used as parameter.  Can not add parameter.");
		}
		
		
		CmdLnParameter i = new CmdLnParameter(name, defaultValue, description);
		i.setAsParameter();
		i.setParent(this);
		parameters.put(name, i);
		children.put(name, i);
		return i;
	}
	
	/**
	 * Adds a new parameter with the given values (as root-item).
	 * 
	 * @param name
	 * @param description
	 * @return
	 */
	@Override
	public CmdLnParameter addParameter(String name, String description) {
		return addParameter(name, null, description);
	}
	
	/**
	 * Returns the first object of the pre-parsed chain.
	 * As long as parse() has not been called, this 
	 * method will return null.
	 * 
	 * @return
	 */
	protected PreParsedChain getPreParsedChain() {
		return chainHead;
	}
	
	/**
	 * Returns all the options
	 * 
	 * @return
	 */
	protected HashMap<String, CmdLnOption> getOptions() {
		return options;
	}
	
	/**
	 * Returns all the parameters which have been defined at root-level 
	 * (non-option-parameters).
	 * 
	 * @return
	 */
	protected HashMap<String, CmdLnParameter> getParameters() {
		return parameters;
	}
	
	/**
	 * Returns the command-line parameter string which has been 
	 * given as parameter when calling parse()
	 * 
	 * @return
	 */
	protected String[] getArgs() {
		return args;
	}
	
	/**
	 * Adds the given group to the list of groups
	 * 
	 * @param group
	 */
	public void addGroup(CommandOPGroup group) {
		groups.add(group);
	}
	
	/**
	 * Returns the option with the given name. If the given name 
	 * is an alias, the corresponding item is returned.
	 * 
	 * @param option
	 * @return
	 * @throws CommandOPError
	 */
	public CmdLnValue getOption(String option) {
		if (!options.containsKey(option)) {
			throw new CommandOPError("Item " + option + " has not been defined as option.");
		}
		
		CmdLnParameter item = options.get(option);
		
		if (!item.isOption() && !item.isShortOption()) {
			throw new CommandOPError("Item " + option + " is not a short or long option.");
		}
		
		if (item.isAlias()) {
			return (CmdLnValue) item.getAliasOf();
		} else {
			return item;
		}
	}
	
	/**
	 * Returns true if the option exists and has been parsed
	 * 
	 * @param option
	 * @return
	 */
	public boolean hasOption(String option) {
		CmdLnBase item = options.get(option);
		
		if (item.isParameter()) {
			return false;
		}
		
		return item.isParsed();
	}
	
	/**
	 * Returns the parameter with the given name. If the given name 
	 * is an alias, the corresponding item is returned.
	 * 
	 * @param option
	 * @return
	 * @throws CommandOPError
	 */
	public CmdLnValue getParameter(String parameter) {
		if (!parameters.containsKey(parameter)) {
			throw new CommandOPError("Item " + parameter + " has not been defined as parameter.");
		}
		
		CmdLnParameter item = parameters.get(parameter);
		
		if (!item.isParameter()) {
			throw new CommandOPError("Item " + parameter + " is not a parameter.");
		}
		
		if (item.isAlias()) {
			return (CmdLnValue) item.getAliasOf();
		} else {
			return item;
		}
	}
	
	/**
	 * 
	 * 
	 * @param parameter
	 * @return
	 */
	public boolean hasParameter(String parameter) {
		CmdLnBase item = parameters.get(parameter);

		if (item == null || !item.isParameter()) {
			return false;
		}
		
		return item.isParsed();
	}
	
	/**
	 * Returns <code>true</code> if there are any arguments found during the parsing 
	 * process which were not defined
	 * 
	 * @return
	 */
	public boolean hasUnknownArguments() {
		return (unknownArguments.size() > 0);
	}
	
	/**
	 * The returned map contains all the arguments which were found during parsing 
	 * and were not defined
	 * 
	 * @return
	 */
	public LinkedHashMap<String, PreParsedItem> getUnknownArguments() {
		return unknownArguments;
	}
	
	/**
	 * Returns all the error messages of the errors which occured during parsing
	 * 
	 * @return
	 */
	public LinkedList<String> getErrorMessages() {
		return errors;
	}
	
	/**
	 * This method does all the steps required for the parsing.
	 * 
	 * @param args
	 * @return <code>true</code> if parsing went through without any errors, or 
	 * <code>false</code> if there are parsing errors.
	 * @throws CommandOPError
	 */
	public boolean parse(String[] args) throws CommandOPError {
		this.args = args;
		chainHead = null;
		
		//Reset items in case the parsing has already been executed once 
		//on this object
		
		unknownArguments.clear();
		
		for (CmdLnOption item : options.values()) {
			item.reset();
		}
		
		for (CmdLnParameter item : parameters.values()) {
			item.reset();
		}
				
		createPreParsedChain(args);
		
		postParse();
		
		validate();
		
		return !(errors.size() > 0);
	}
	
	
	/**
	 * This method validates the parsed items
	 * 
	 * @throws CommandOPError
	 */
	private void validate() throws CommandOPError {
		
		LinkedList<CmdLnBase> itemsFlat = CommandOPTools.createFlatList(children);
		
		for (CmdLnBase item : itemsFlat) {
			//Mandatory
			//If the item has a parent item, only validate it if the parent item is parsed too
			if (!item.hasParent() || (item.hasParent() && item.getParentInternal().isParsed())) {
				if (item.isMandatory() && !item.isParsed()) {
					error("Item '" + item.getName() + "' is mandatory");
				}
			}
			
			//Required value
			if (item.isValueRequired() && item.isParsed() && item.getValue() == null) {
				error("Item '" + item.getName() + "' requires a value");
			}
			
			//Minimum number of values
			if (item.isParsed() && item.isMultiValueItem() 
					&& item.getNumOfValues() < item.getMultiValuesRangeMin()) {
				error("Item '" + item.getName() + "' needs at least " + 
					item.getMultiValuesRangeMin() + " values.");
			}
		}
		
		
		
		for (CommandOPGroup group : groups) {

			if (group.getMode() == CommandOPGroup.MODE_EXCLUDE 
					|| group.getMode() == CommandOPGroup.MODE_EXCLUDE_ONE) {
				//The given group items can not appear together
				
				int numOfExisting = 0;
				for (CmdLnBase item : group.getItems().values()) {
					
					if (item.isParsed()) {
						numOfExisting++;
						
						if (numOfExisting > 1) {
							//Another one has been found already
							error("More than one item of the EXCLUDE-group '" + group.getName() + "' found. " +
									"Only one of the following items is allowed: " + group.getItems().keySet());
							break;
						}
						
					}
					
				}
				
				if (group.getMode() == CommandOPGroup.MODE_EXCLUDE_ONE && numOfExisting == 0) {
					error("The EXCLUDE_ONE-group '" + group.getName() + 
							"' needs at least one (but not more) of its items. " +
							"Items in the group are: " + group.getItems().keySet());
				}
				
			} else if (group.getMode() == CommandOPGroup.MODE_INCLUDE 
					|| group.getMode() == CommandOPGroup.MODE_INCLUDE_ONE) {
				//The given group items have to be given together
				
				boolean hasOneParsedItem = false;
				
				for (CmdLnBase item : group.getItems().values()) {
					if (group.getMode() == CommandOPGroup.MODE_INCLUDE ) {
						if (!item.isParsed()) {
							//One of the group items does not exist
							error("One or more items of the INCLUDE-group '" + group.getName() + 
									"' are missing. Needed items are: " + group.getItems().keySet());
							break;
						}
					} else if (group.getMode() == CommandOPGroup.MODE_INCLUDE_ONE) {
						if (item.isParsed()) {
							hasOneParsedItem = true;
							break;
						}
					}
				}
				
				if (!hasOneParsedItem) {
					//At least one of the group items is needed, but there was none
					error("No item of the INCLUDE-group '" + group.getName() + 
							"' has been found. At least one of these items is needed: " + group.getItems().keySet());
				}
				
			}
			
		}
		
	}
	
	
	/**
	 * Follows the pre parsed chain of options/parameters one by one, looking 
	 * for the corresponding defined item (or its alias). If a defined item is found, 
	 * its value is set.
	 * 
	 * @throws CommandOPError
	 */
	private void postParse() throws CommandOPError {
		
		PreParsedChain currentChain = chainHead;
		CmdLnBase currentItem = null;
		CmdLnBase previousItem = null;
		boolean noOptionParameters = true;		
		
		while (currentChain != null) {
			
			if (currentChain.isOption() || currentChain.isShortOption() || noOptionParameters) {
				if (currentItem != null) {
					previousItem = currentItem;
				}
				
				//It's an option or a NoOptionParameters, so take the item from the root
				currentItem = children.get(currentChain.getName());
				
				if (noOptionParameters) {
					//Run as noOptionParameters until the first option is found
					//NoOptionParameters can be at the beginning of the command line 
					//as parameters without an option as parent
					noOptionParameters = !(currentChain.isOption() || currentChain.isShortOption());
				}
			} else {
				if (currentItem == null) {
					if (previousItem == null) {
						previousItem = children.get(currentChain.getName());
					}
					
					//If no item has been found before, then continue with the previous item
					currentItem = previousItem;
				} else {
					previousItem = currentItem;
				}
				
				
				//It's not an option, so either it is a child of the current item
				//or it's an item on the same level or on a higher level
				if (currentItem.hasChild(currentChain.getName())) {
					currentItem = currentItem.getChild(currentChain.getName());
				} else {
					currentItem = currentItem.getParentInternal();
					boolean found = false;
					
					//Check all children and children of parents
					while (currentItem != null) {
						if (currentItem.hasChild(currentChain.getName())) {
							currentItem = currentItem.getChild(currentChain.getName());
							found = true;
							break;
						}
						
						currentItem = currentItem.getParent();
					}
					
					if (!found) {
						//The item has not been found in the defined items. It 
						//must be a value of a multi value item
						currentItem = null;
					}
					
				}
			}
			
			
			//If a defined item has been found, set its data
			if (currentItem != null) {
				
				if (currentChain.isOption() != currentItem.isOption()) {
					if (currentChain.isOption()) {
						error("Item " + currentChain.getName() + " is given as option (--) on the command line, but not defined as one.");
					} else {
						error("Item " + currentChain.getName() + " is defined as option (--), but not given as one on the command line.");
					}
				} else if (currentChain.isShortOption() != currentItem.isShortOption()) {
					if (currentChain.isShortOption()) {
						error("Item " + currentChain.getName() + " is given as short option (-) on the command line, but not defined as one.");
					} else {
						error("Item " + currentChain.getName() + " is defined as short option (-), but not given as one on the command line.");
					}
				} else if (currentChain.isParameter() != currentItem.isParameter()) {
					if (currentChain.isParameter()) {
						error("Item " + currentChain.getName() + " is given as parameter on the command line, but not defined as one.");
					} else {
						error("Item " + currentChain.getName() + " is defined as parameter, but not given as one on the command line.");
					}				}
				
				String errormsg = currentItem.setValue(currentChain.getValue());
				
				if (errormsg != null) {
					error(errormsg);
				} else {
					currentItem.setCmdLnPos(currentChain.getChainPos());
				}
			} else {
				if (previousItem != null && previousItem.isMultiValueItem()) {
					//If the previous item was a multi value item and the current item 
					//has not been found, it is assumed that the current item is actually 
					//a value of the multi value item, thus the name is the value
					
					String value = null;
					if (currentChain.getValue() != null) {
						//The values of the multi value item are given as "name1=value1 name2=value2"
						value = currentChain.getValue();
					} else {
						//The values of the multi value item are given as "value1 value2"
						value = currentChain.getName();
					}
					
					String errormsg = previousItem.addMultiValue(value);
					
					if (errormsg != null) {
						error(errormsg);
					}			
				} else {
					//If there is a value it should have been assigned already
					//If there is not next item, nothing else can be added
					if (currentChain.getValue() != null || previousItem == null || previousItem.getNextItem() == null) {
						if (previousItem != null) {
							if (previousItem.isParameter() && previousItem.hasParent()) {
								error("Unknown argument '" + currentChain.getName() + "' given for option '" + previousItem.getParent().getName() + "'");
							} else if (previousItem.isOption() || previousItem.isShortOption()) {
								error("Unknown argument '" + currentChain.getName() + "' given for option '" + previousItem.getName() + "'");
							} else {
								error("Unknown argument '" + currentChain.getName() + "' given as no-option-parameter.");
							}
						}
						
						unknownArguments.put(currentChain.getName(), currentChain);
					} else {
						//No value given -> it is just assumed that the given item 
						//is only the value for the next item
						currentItem = previousItem.getNextItem();
						currentItem.setValue(currentChain.getName());
					}
				}
			}
			
			//Take the next item from the post parsed chain
			currentChain = currentChain.getNext();
		}
		
	}
	
	
	/**
	 * Goes through all the given command line arguments and puts them 
	 * into objects, chained up after each other in the order in which they 
	 * are given on the command line. When a new {@link PreParsedChain}-object 
	 * is created, short options are split up, the 
	 * item name and value are split and a flag is set whether it is an 
	 * option, a short option or a parameter.
	 * 
	 * @param args The command line arguments
	 */
	private void createPreParsedChain(String[] args) {
		
		PreParsedChain lastChain = null;
		
		for (int i = 0; i < args.length; i++) {
			String a = args[i];
			
			if (a == null || a.length() == 0) {
				continue;
			}
			
			if (CommandOPTools.isShortOption(a)) {
				//SHORT option
				
				String shortOptions = CommandOPTools.getOption(a);
				
				String shortOptionsValue = CommandOPTools.getOptionValue(a);
				
				
				//Use each character as option
				for (int j = 0; j < shortOptions.length(); j++) {
					lastChain = new PreParsedChain(CommandOPTools.OPTIONSPREFIX_SHORT + shortOptions.charAt(j), lastChain);
					
					if (chainHead == null) {
						chainHead = lastChain;
					}
				}
				
				//The last option can have a value
				if (shortOptionsValue != null && shortOptionsValue.length() > 0) {
					lastChain.setValue(shortOptionsValue);
				}
				
			} else {
				//LONG option
				//PARAMETER
				
				PreParsedChain tempChain = new PreParsedChain(a, lastChain);
				
				if (tempChain.getName() != null && tempChain.getName().length() > 0) {
					lastChain = tempChain;
					
					if (chainHead == null) {
						chainHead = lastChain;
					}
				}
				
			}
			
			
		}
		
	}
	
	/**
	 * 
	 * 
	 * @param errorMessage
	 * @throws CommandOPError
	 */
	private void error(String errorMessage) throws CommandOPError {
		errors.add(errorMessage);
		if (exceptionAtFirstError) {
			throw new CommandOPError(errorMessage);
		}
	}
	
	/**
	 * 
	 * 
	 * @param e
	 */
	public void exceptionAtFirstError(boolean e) {
		exceptionAtFirstError = e;
	}
	
	

}
