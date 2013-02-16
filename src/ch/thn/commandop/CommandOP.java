/**
 * 
 */
package ch.thn.commandop;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * @author thomas
 *
 */
public class CommandOP extends CmdLnItem {
	
	
	private PreParsedChain chainHead = null;
	
	private LinkedHashMap<String, CmdLnItem> items = null;
	private LinkedHashMap<String, PreParsedItem> unknownArguments = null;
	
	private LinkedList<CommandOPGroup> groups = null;
	
	private String[] args = null;
	
	
	/**
	 * CommandlineOptionParser<br>
	 * <br>
	 * 
	 */
	public CommandOP() {
		
		items = new LinkedHashMap<String, CmdLnItem>();
		groups = new LinkedList<CommandOPGroup>();
		unknownArguments = new LinkedHashMap<String, PreParsedItem>();
		
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
	public CmdLnItem addOption(String name, String defaultValue, String description) {
		CmdLnItem i = new CmdLnItem(name, defaultValue, description);
		i.setAsOption();
		i.setParent(this);
		items.put(name, i);
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
	public CmdLnItem addOption(String name, String description) {
		return addOption(name, null, description);
	}
	
	/**
	 * Adds a new parameter with the given values (as root-item).
	 * 
	 * @param item
	 * @param defaultValue
	 * @param description
	 * @return
	 */
	public CmdLnItem addParameter(String item, String defaultValue, String description) {
		CmdLnItem i = new CmdLnItem(item, defaultValue, description);
		i.setAsParameter();
		i.setParent(this);
		items.put(item, i);
		return i;
	}
	
	/**
	 * Adds a new parameter with the given values (as root-item).
	 * 
	 * @param item
	 * @param description
	 * @return
	 */
	public CmdLnItem addParameter(String item, String description) {
		return addParameter(item, null, description);
	}
	
	/**
	 * Adds the given items as parameter (as root-item)
	 * 
	 * @param items
	 * @return
	 */
	public CmdLnItem addParameters(CmdLnItem... items) {
		
		for (CmdLnItem item : items) {
			this.items.put(item.getName(), item);
			item.setAsParameter();
			item.setParent(this);
		}
		
		return this;
		
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
	 * Returns all the root-items (options and/or parameters 
	 * defined on root-level)
	 * 
	 * @return
	 */
	protected HashMap<String, CmdLnItem> getItems() {
		return items;
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
	 */
	public CmdLnItem getOption(String option) {
		CmdLnItem item = items.get(option);
		
		if (item.isParameter()) {
			return null;
		}
		
		if (item.isAlias()) {
			return item.getAliasOf();
		} else {
			return item;
		}
	}
	
	/**
	 * 
	 * @param option
	 * @return
	 */
	public boolean hasOption(String option) {
		CmdLnItem item = items.get(option);
		
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
	 */
	public CmdLnItem getParameter(String parameter) {
		CmdLnItem item = items.get(parameter);
		
		if (!item.isParameter()) {
			return null;
		}
		
		if (item.isAlias()) {
			return item.getAliasOf();
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
		CmdLnItem item = items.get(parameter);

		if (!item.isParameter()) {
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
	 * This method does all the steps required for the parsing.
	 * 
	 * @param args
	 * @return
	 */
	public boolean parse(String[] args) {
		this.args = args;
		chainHead = null;
		
		boolean error = false;
		
		createPreParsedChain(args);
		
		if (!postParse()) {
			error = true;
		}
		
		if (!validate()) {
			error = true;
		}
		
		return !error;
	}
	
	
	/**
	 * This method validates the parsed items
	 * 
	 * @return
	 */
	private boolean validate() {
		
		LinkedList<CmdLnItem> itemsFlat = CommandOPTools.createFlatList(items);
		boolean error = false;
		
		for (CmdLnItem item : itemsFlat) {
			//Mandatory
			//If the item has a parent item, only validate it if the parent item is parsed too
			if (item.isMandatory() && !item.isParsed() 
					&& (item.getParent() == null || item.getParent().isParsed())) {
				System.err.println("CommandOP> Mandatory item '" + item.getName() + "' not found");
				error = true;
			}
			
			//Required value
			if (item.isValueRequired() && item.isParsed() && item.getValue() == null) {
				System.err.println("CommandOP> Item '" + item.getName() + "' requires a value");
				error = true;
			}
			
			//Minimum number of values
			if (item.isParsed() && item.isMultiValueItem() 
					&& item.getNumOfValues() < item.getMultiValuesRangeMin()) {
				System.err.println("CommandOP> Item '" + item.getName() + "' needs at least " + item.getMultiValuesRangeMin() + " values.");
				error = true;
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
							System.err.println("CommandOP> More than one item of the EXCLUDE-group '" + group.getName() + "' found.");
							error = true;
							break;
						}
						
					}
					
				}
				
				if (group.getMode() == CommandOPGroup.MODE_EXCLUDE_ONE && numOfExisting == 0) {
					System.err.println("CommandOP> The EXCLUDE_ONE-group '" + group.getName() + "' needs at least one (but not more) of its items.");
					error = true;
				}
				
			} else if (group.getMode() == CommandOPGroup.MODE_INCLUDE) {
				//All of the given group items have to be given
				
				for (CmdLnItem item : group.getItems().values()) {
					if (!item.isParsed()) {
						//One of the group items does not exist
						System.err.println("CommandOP> One or more items of the INCLUDE-group '" + group.getName() + "' are missing.");
						error = true;
						break;
					}
				}
				
			}
			
		}

		return !error;
		
	}
	
	
	/**
	 * Follows the pre parsed chain of options/parameters one by one, looking 
	 * for the corresponding defined item. If a defined item is found, the value 
	 * and the flag whether it is an option, a short option or a parameter is set
	 * 
	 * @return
	 */
	private boolean postParse() {
		
		PreParsedChain currentChain = chainHead;
		CmdLnItem currentItem = null;
		CmdLnItem previousItem = null;
		boolean noOptionParameters = true;
		boolean error = false;
		
		
		while (currentChain != null) {
			
			if (currentChain.isOption() || noOptionParameters) {
				if (currentItem != null) {
					previousItem = currentItem;
				}
				
				//It's an option, so take the item from the root
				currentItem = items.get(currentChain.getName());
				
				//Run as noOptionParameters until the first option is found
				noOptionParameters = !currentChain.isOption();
			} else {
				if (currentItem == null) {
					if (previousItem == null) {
						previousItem = items.get(currentChain.getName());
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
					currentItem = currentItem.getParent();
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
						currentItem = null;
					}
					
				}
			}
			
			
			
			//If a defined item has been found, set its data
			if (currentItem != null) {
				//Setting the type needs to be before setting the value, 
				//because setting the value might depend on the type
				
				if (currentChain.isOption()) {
					currentItem.setAsOption();
				} else if (currentChain.isShortOption()) {
					currentItem.setAsShortOption();
				} else if (currentChain.isParameter()) {
					currentItem.setAsParameter();
				}
				
				if (!currentItem.setValue(currentChain.getValue())) {
					error = true;
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
					
					if (!previousItem.addMultiValue(value)) {
						error = true;
					}			
				} else {
					System.err.println("CommandOP> Unknown argument '" + currentChain.getName() + "' given");
					unknownArguments.put(currentChain.getName(), currentChain);
					error = true;
				}
			}
			
			//Take the next item from the post parsed chain
			currentChain = currentChain.getNext();
		}
		
		return !error;
		
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
			
			if (CommandOPTools.isShortOption(args[i])) {
				//SHORT option
				
				String shortOptions = CommandOPTools.getOption(args[i]);
				
				//Use each character as option
				for (int j = 0; j < shortOptions.length(); j++) {
					lastChain = new PreParsedChain(CommandOPTools.OPTIONSPREFIX_SHORT + shortOptions.charAt(j), lastChain);
					
					if (chainHead == null) {
						chainHead = lastChain;
					}
				}
			} else {
				//LONG option
				//PARAMETER
				
				PreParsedChain tempChain = new PreParsedChain(args[i], lastChain);
				
				if (tempChain.getName() != null && tempChain.getName().length() > 0) {
					lastChain = tempChain;
					
					if (chainHead == null) {
						chainHead = lastChain;
					}
				}
				
			}
			
			
		}
		
	}
	
	
	

}
