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

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * Commandline Option Parser<br />
 * <br />
 * A small Java library which parses command line arguments. The main advantage of CommandOP
 * is that options and parameters can be structured in a unlimited tree-like construct.
 * This allows for if-then relations (parameters are only allowed if their parent
 * parameter/option is given). Include and exclude groups are also supported. Many
 * other command line parsers only support the traditional flat structures (often
 * just with optional/mandatory options). Furthermore, options in CommandOP can
 * either be given in their defined order (--option v1 v1...) or as key/value
 * pairs (--option o2=v2 o1=v1...) and CommandOP supports variable argument lists
 * with a defined min/max number of arguments.
 * <br />
 * <br />
 * Features:<br />
 * - Short (-) and long (--) options (short options can be combined, e.g. -abc instead of -a -b -c)<br />
 * - If-then relations<br />
 * - Include and exclude groups<br />
 * - Boolean items<br />
 * - Mandatory items and items with a mandatory value<br />
 * - Value validators<br />
 * - Aliases<br />
 * 
 * 
 * 
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class CommandOP extends CmdLnItem {


	private PreParsedChain chainHead = null;

	/**
	 * Only the options
	 */
	private LinkedHashMap<String, CmdLnOption> options = null;

	/**
	 * 
	 */
	private LinkedHashMap<String, PreParsedItem> unknownArguments = null;

	private LinkedList<CommandOPGroup> groups = null;
	private LinkedList<String> errors = null;
	private LinkedList<String> info = null;

	private List<String> args = null;

	private boolean exceptionAtFirstError = false;


	/**
	 * CommandlineOptionParser<br>
	 * <br>
	 * 
	 */
	public CommandOP() {

		options = new LinkedHashMap<String, CmdLnOption>();
		groups = new LinkedList<CommandOPGroup>();
		unknownArguments = new LinkedHashMap<String, PreParsedItem>();
		errors = new LinkedList<String>();
		info = new LinkedList<String>();

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
		CmdLnOption i = new CmdLnOption(name, defaultValue, description);
		i.setParent(this);
		options.put(name, i);
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
		return super.addParameter(name, defaultValue, description);
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
		return super.addParameter(name, null, description);
	}

	/**
	 * Returns the parameter with the given name, or null if the parameter
	 * does not exist. If the given name is an alias, the corresponding
	 * parameter is returned.
	 * 
	 * @param name
	 * @return
	 */
	public CmdLnValue getParameter(String name) {
		return super.getChild(name);
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
	 * Returns the command-line parameter string list which has been
	 * given as parameter when calling parse()
	 * 
	 * @return
	 */
	protected List<String> getArgs() {
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
	 * Returns true if the option has been defined and parsed
	 * 
	 * @param option
	 * @return
	 */
	public boolean hasOption(String option) {
		CmdLnItem item = options.get(option);

		if (item == null || item.isParameter()) {
			return false;
		}

		return item.isParsed();
	}

	/**
	 * Returns true if the parameter has been defined and parsed
	 * 
	 * @param parameter
	 * @return
	 */
	public boolean hasParameter(String parameter) {
		CmdLnItem item = children.get(parameter);

		if (item == null || item.isOption()) {
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
		return unknownArguments.size() > 0;
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
	 * Returns all the error messages of the errors which occurred during the
	 * last parsing.
	 * 
	 * @return
	 */
	public LinkedList<String> getErrorMessages() {
		return errors;
	}

	/**
	 * Returns all the info messages of the errors which occurred during the
	 * last parsing.
	 * 
	 * @return
	 */
	public LinkedList<String> getInfoMessages() {
		return info;
	}

	/**
	 * Resets all options and parameters (clears all values and resets all states)
	 * 
	 */
	@Override
	public void reset() {
		unknownArguments.clear();

		for (CmdLnOption item : options.values()) {
			item.reset();
			resetAll(item.getChildrenInternal());
		}

		resetAll(getChildrenInternal());

		super.reset();
	}

	/**
	 * Resets all items in the map and also all children
	 * 
	 * @param children
	 */
	private void resetAll(HashMap<String, CmdLnItem> children) {
		for (CmdLnItem child : children.values()) {
			child.reset();

			//Reset children of the current item
			resetAll(child.getChildrenInternal());
		}
	}

	/**
	 * This method parses the command line arguments which have to be given in
	 * the list as "name=value" strings (or just "name" for boolean parameters).
	 * 
	 * 
	 * @param item The item under which the command line arguments/properties should be parsed
	 * @param argsList The command line arguments/properties
	 * @param overwriteParsed If set to <code>true</code>, items which have already
	 * been parsed by a previous call to parse() will be overwritten. If set to <code>false</code>,
	 * items which have already been parsed will not be overwritten.
	 * @return <code>true</code> if parsing went through without any errors, or
	 * <code>false</code> if there are parsing errors.
	 * @throws CommandOPError
	 */
	public boolean parse(CmdLnItem item, List<String> argsList, boolean overwriteParsed) throws CommandOPError {
		args = argsList;
		chainHead = null;

		errors.clear();
		info.clear();

		//If an item is given, the path to that item is created by following the
		//path upwards and creating all parameters and options with values.
		while (item != null && item != this) {
			String valuePart = "";
			if (item.getValue() != null) {
				valuePart = "=" + item.getValue();
			}

			//Going upwards adding each part to the front of the list
			argsList.add(0, item.getCmdLnTypePrefix() + item.getName() + valuePart);
			item = item.getParent();
		}

		createPreParsedChain(argsList);

		postParse(overwriteParsed);

		validate();

		return !(errors.size() > 0);
	}

	/**
	 * This method parses the command line arguments which have to be given in
	 * the list as "name=value" strings (or just "name" for boolean parameters).
	 * 
	 * 
	 * @param argsList The command line arguments/properties
	 * @param overwriteParsed If set to <code>true</code>, items which have already
	 * been parsed by a previous call to parse() will be overwritten. If set to <code>false</code>,
	 * items which have already been parsed will not be overwritten.
	 * @return <code>true</code> if parsing went through without any errors, or
	 * <code>false</code> if there are parsing errors.
	 * @throws CommandOPError
	 */
	public boolean parse(List<String> argsList, boolean overwriteParsed) throws CommandOPError {
		return parse(null, argsList, overwriteParsed);
	}

	/**
	 * This method parses the command line arguments which have to be given in
	 * the array as "name=value" strings (or just "name" for boolean parameters).
	 * 
	 * 
	 * @param argsArray The command line arguments/properties
	 * @param overwriteParsed If set to <code>true</code>, items which have already
	 * been parsed by a previous call to parse() will be overwritten. If set to <code>false</code>,
	 * items which have already been parsed will not be overwritten.
	 * @return <code>true</code> if parsing went through without any errors, or
	 * <code>false</code> if there are parsing errors.
	 * @return
	 * @throws CommandOPError
	 */
	public boolean parse(String[] argsArray, boolean overwriteParsed) throws CommandOPError {
		return parse(null, Arrays.asList(argsArray), true);
	}

	/**
	 * This method parses the command line arguments which have to be given in
	 * the array as "name=value" strings (or just "name" for boolean parameters).
	 * 
	 * 
	 * @param item The item under which the command line arguments/properties should be parsed
	 * @param argsArray The command line arguments/properties
	 * @param overwriteParsed If set to <code>true</code>, items which have already
	 * been parsed by a previous call to parse() will be overwritten. If set to <code>false</code>,
	 * items which have already been parsed will not be overwritten.
	 * @return <code>true</code> if parsing went through without any errors, or
	 * <code>false</code> if there are parsing errors.
	 * @return
	 * @throws CommandOPError
	 */
	public boolean parse(CmdLnItem item, String[] argsArray, boolean overwriteParsed) throws CommandOPError {
		return parse(item, Arrays.asList(argsArray), true);
	}

	/**
	 * This method parses the command line arguments which have to be given in
	 * the map as "name"=>"value" pairs (or just "name"=>null for boolean parameters).
	 * 
	 * 
	 * @param argsMap The command line arguments/properties
	 * @param overwriteParsed If set to <code>true</code>, items which have already
	 * been parsed by a previous call to parse() will be overwritten. If set to <code>false</code>,
	 * items which have already been parsed will not be overwritten.
	 * @return <code>true</code> if parsing went through without any errors, or
	 * <code>false</code> if there are parsing errors.
	 * @return
	 * @throws CommandOPError
	 */
	public boolean parse(Map<Object, Object> argsMap, boolean overwriteParsed) throws CommandOPError {
		return parse(null, CommandOPTools.mapToKeyValueList(argsMap), true);
	}

	/**
	 * This method parses the command line arguments which have to be given in
	 * the map as "name"=>"value" pairs (or just "name"=>null for boolean parameters).
	 * 
	 * 
	 * @param item The item under which the command line arguments/properties should be parsed
	 * @param argsMap The command line arguments/properties
	 * @param overwriteParsed If set to <code>true</code>, items which have already
	 * been parsed by a previous call to parse() will be overwritten. If set to <code>false</code>,
	 * items which have already been parsed will not be overwritten.
	 * @return <code>true</code> if parsing went through without any errors, or
	 * <code>false</code> if there are parsing errors.
	 * @return
	 * @throws CommandOPError
	 */
	public boolean parse(CmdLnItem item, Map<Object, Object> argsMap, boolean overwriteParsed) throws CommandOPError {
		return parse(item, CommandOPTools.mapToKeyValueList(argsMap), true);
	}

	/**
	 * This method validates the parsed items
	 * 
	 * @throws CommandOPError
	 */
	private void validate() throws CommandOPError {

		LinkedList<CmdLnItem> itemsFlat = CommandOPTools.createFlatList(this);

		for (CmdLnItem item : itemsFlat) {
			//Mandatory
			//If the item has a parent item, only validate it if the parent item is parsed too
			if (!item.hasParent() || item.hasParent() && item.getParentInternal().isParsed()) {
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
				for (CmdLnItem item : group.getItems().values()) {

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

				for (CmdLnItem item : group.getItems().values()) {
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
	 * @param overwriteParsed
	 * @throws CommandOPError
	 */
	private void postParse(boolean overwriteParsed) throws CommandOPError {

		PreParsedChain currentChain = chainHead;
		CmdLnItem currentItem = null;
		CmdLnItem previousItem = null;

		while (currentChain != null) {

			if (currentChain.isOption() || currentChain.isShortOption()) {
				//If it is an option it means that the "tree" starts from the beginning because an
				//option is the first item.

				//It's an option, so take the item from the root
				currentItem = options.get(currentChain.getName());

				//An option is the beginning of a "tree". Set as previous item for next loop.
				previousItem = currentItem;
			} else if (previousItem == null) {
				//If it is not an option or short option, but there is no previous item,
				//it means it is a non-option-parameter.

				//It's a no-option-parameter, so take the item from the root
				currentItem = children.get(currentChain.getName());

				//A non-option-parameter does not have a parent
				previousItem = null;
			} else {
				//Parameter of an option

				//Check if previous item has the current chain name as child. If yes,
				//use it. If no, travel up the "tree" to see where the parent of the current
				//chain name is (if there is one)
				if (previousItem.hasChild(currentChain.getName())) {
					currentItem = previousItem.getChild(currentChain.getName());

					//Set as previous item for next loop.
					previousItem = currentItem;
				} else {
					//The previous item does not have the current chain name as child.
					//-> Start looking at the parent of the previous item
					currentItem = previousItem.getParentInternal();
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
						//The item has not been found in the defined items
						currentItem = null;
					} else {
						//Set as previous item for next loop.
						previousItem = currentItem;
					}

				}


			}



			//If a defined item has been found, set its data
			if (currentItem != null) {

				//Check if current command line item type matches the type of the defined item
				if (currentChain.isOption() != currentItem.isOption()
						|| currentChain.isShortOption() != currentItem.isShortOption()
						|| currentChain.isParameter() != currentItem.isParameter()) {

					info("Item " + currentItem.getName() + " is defined as " + currentItem.getTypeDescString() + ", but it is given as " + currentChain.getTypeDescString() + " on the command line. Item ignored.");

					//"Item not found"
					currentItem = null;
				} else {
					//Only set value if not yet parsed or overwrite allowed
					if (!currentItem.isParsed() || overwriteParsed) {
						String errormsg = currentItem.setValue(currentChain.getValue());

						if (errormsg != null) {
							//The returned string from setValue might contain the [INFO] prefix
							if (errormsg.startsWith("[INFO] ")) {
								info(errormsg.substring("[INFO] ".length()));
							} else {
								error(errormsg);
							}
						} else {
							currentItem.setCmdLnPos(currentChain.getChainPos());
						}
					}
				}



			} else {
				//Defined item not found. It could be the value of a multi value item.

				//It can only be the value of a multi value item if the previous item
				//is such a multi value item and if the current item is not given as
				//name=value pair
				if (previousItem != null && previousItem.isMultiValueItem()
						&& currentChain.getValue() == null) {
					//If the previous item was a multi value item and the current item
					//has not been found, it is assumed that the current item is actually
					//a value of the multi value item, thus the name is the value

					//The values of the multi value item are given as "value1 value2"
					String value = currentChain.getName();


					String errormsg = previousItem.addMultiValue(value);

					if (errormsg != null) {
						//The returned string from setValue might contain the [INFO] prefix
						if (errormsg.startsWith("[INFO] ")) {
							info(errormsg.substring("[INFO] ".length()));
						} else {
							error(errormsg);
						}
					}
				} else {
					//It is not the value of a multi value item

					if (previousItem != null) {
						//There was a previous item
						info("Unknown " + currentChain.getTypeDescString() + " '" + currentChain.getName() + "' given after " + previousItem.getTypeDescString() + " " + previousItem.getName());
					} else {
						//There was no previous item
						info("Unknown " + currentChain.getTypeDescString() + " '" + currentChain.getName() + "' given");
					}

					unknownArguments.put(currentChain.getName(), currentChain);
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
	private void createPreParsedChain(List<String> args) {

		PreParsedChain lastChain = null;

		Arrays.asList(args);
		for (String a : args) {

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
	 * @param infoMessage
	 * @throws CommandOPError
	 */
	private void info(String infoMessage) {
		info.add(infoMessage);
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
