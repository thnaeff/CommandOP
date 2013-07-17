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
		
		LinkedList<StringBuilder> lines = new LinkedList<StringBuilder>();
		LinkedList<CmdLnItem> flatList = CommandOPTools.createFlatList(items);
		
		int longestLine = 0;
		
		
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
			
			StringBuilder line = new StringBuilder();
			
//			if (!flat) {
//				defineditems.append(item.getCmdLnPos() + ". ");
//			}
			
			if (flat) {
				if (item.isOption()) {
					line.append(CommandOPTools.OPTIONSPREFIX_LONG);
				} else if (item.isShortOption()) {
					line.append(CommandOPTools.OPTIONSPREFIX_SHORT);
				}
			} else {
				for (int i = 0; i < item.getLevel(); i++) {
					line.append("   ");
				}
			}
			
			if (item.isMandatory()) {
				line.append("*");
			}
			
			line.append(item.getName());
			
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
			
			String commaToAppend = null;
			if (item.hasAlias()) {
				line.append(" [");
				
				for (CmdLnItem alias : item.getAlias().values()) {
					
					if (commaToAppend != null) {
						line.append(commaToAppend);
						commaToAppend = null;
					}
					
					line.append(alias.getName());
					commaToAppend = ", ";
				}
				
				line.append("] ");
			}
			
			//The line length has to be taken before the comment is added
			if (line.length() > longestLine) {
				longestLine = line.length();
			}
			
			if (!flat) {
				String desc = item.getDescription();
				
				if (desc != null && desc.length() > 0) {
					line.append(ALIGN_LOCATION + item.getDescription());
				}
			}
			
			lines.add(line);
			
		}
		
		
		StringBuilder output = new StringBuilder();
		
		for (StringBuilder sb : lines) {
			
			if (flat) {
				output.append(sb.toString());
				output.append(" ");
			} else {
				String space = CommandOPTools.makeRightAlignSpace(longestLine + 5, sb.indexOf(ALIGN_LOCATION), true);
				output.append(sb.toString().replace(ALIGN_LOCATION, space));
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
