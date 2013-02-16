/**
 * 
 */
package ch.thn.commandop;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author thomas
 *
 */
public class CommandOPPrinter {
	
	
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
			
			if (chain.isOption()) {
				preparsed.append(" [option]");
			} else {
				preparsed.append(" [param]");
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
		
		s = s + getDefinedItems(false, false, true);
		
		return s;
	}
	
	
	/**
	 * Returns a formatted list showing the defined items with value (if selected) and 
	 * description
	 * 
	 * @param flat
	 * @param withValue
	 * @param hideHidden
	 * @return
	 */
	public String getDefinedItems(boolean flat, boolean withValue, boolean hideHidden) {
		
		HashMap<String, CmdLnItem> items = cmdop.getItems();
		
		StringBuilder defineditems = new StringBuilder();
		
		LinkedList<CmdLnItem> flatList = CommandOPTools.createFlatList(items);
		
		for (CmdLnItem item : flatList) {
			
			if (hideHidden && item.isHiddenInHelp()) {
				continue;
			}
			
			if (item.isAlias()) {
				continue;
			}
			
//			if (parsedAndBooleanOnly) {
//				if (!item.isParsed() && !item.isBoolean()) {
//					continue;
//				}
//			}
			
//			if (!flat) {
//				defineditems.append(item.getCmdLnPos() + ". ");
//			}
			
			if (flat) {
				if (item.isOption()) {
					defineditems.append(CommandOPTools.OPTIONSPREFIX_LONG);
				} else if (item.isShortOption()) {
					defineditems.append(CommandOPTools.OPTIONSPREFIX_SHORT);
				}
			} else {
				for (int i = 0; i < item.getLevel(); i++) {
					defineditems.append("\t");
				}
			}
			
			if (item.isMandatory()) {
				defineditems.append("*");
			}
			
			defineditems.append(item.getName());
			
			if (withValue) {
				defineditems.append(CommandOPTools.ITEM_VALUE_SEPARATOR);
				
				//Whether the item is a multi value item or not, all the values
				//are shown. If its not a multi value item, there is only one value to show
				for (int i = 0; i < item.getNumOfValues(); i++) {
					defineditems.append(item.getValue(i));
					
					if (i < item.getNumOfValues() - 1) {
						defineditems.append(" ");
					}
				}
				
				defineditems.append(" (" + item.getDefaultValue() + ")");
			}
			
			String commaToAppend = null;
			if (item.hasAlias()) {
				defineditems.append(" [");
				
				for (CmdLnItem alias : item.getAlias().values()) {
					
					if (commaToAppend != null) {
						defineditems.append(commaToAppend);
						commaToAppend = null;
					}
					
					defineditems.append(alias.getName());
					commaToAppend = ", ";
				}
				
				defineditems.append("] ");
			}
			
			if (!flat) {
				defineditems.append("\t\t" + item.getDescription());
			}
			
			if (flat) {
				defineditems.append(" ");
			} else {
				defineditems.append("\n");
			}
			
			
		}
		
		return defineditems.toString();
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
