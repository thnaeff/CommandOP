package ch.thn.commandop.test;

import java.util.LinkedList;

import ch.thn.commandop.CommandOPException;
import ch.thn.commandop.CmdLnItem;
import ch.thn.commandop.CommandOP;
import ch.thn.commandop.CommandOPFactory;
import ch.thn.commandop.CommandOPGroup;
import ch.thn.commandop.CommandOPPrinter;
import ch.thn.commandop.validator.NumberValidator;

public class CommandOPTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		args = "optionless1 optionless2=value -abc --option1 param11 param12=value --option2 param21 param211 param22=value -s - --  stuff ".split(" ");
//		args = "-ca --atest=aliasvalue --option1 param12=123 --option3 unknownparam=value --option2=o2 param21=21 param211=p211 param22".split(" ");
//		args = "optionless2 --atest=test --option3 value1 value2 value3 param31=31 param311=311 param32=32 --option2=o2".split(" ");
				
		
		CommandOP cmdop = new CommandOP();
		
		NumberValidator numvalidator = new NumberValidator();
		
		cmdop.addParameter("optionless2", "somevalue", "Just a parameter without option").setMandatory();
		
		cmdop.addOption("a", "short param a").setAsBoolean().setMandatory();
		
		cmdop.addOption("c", "def", "short param c").setValueRequired();
		
		cmdop.addOption("x", "defx", "some param x");
		
		cmdop.addOption("withaliases", "with aliases").addAlias("atest").addAlias("atest2");
		
		cmdop.addOption("option2", "default", "An option with children")
				.addParameters(
				CommandOPFactory.newParameter("param21", "").setValidator(numvalidator)
						.addParameters(
						CommandOPFactory.newParameter("param211", "def211", "").setValueRequired()
						), 
				CommandOPFactory.newParameter("param22", "").setValueRequired(), 
				CommandOPFactory.newParameter("param23", ""), 
				CommandOPFactory.newParameter("param24", "").setValueRequired()
						.addParameters(
						CommandOPFactory.newParameter("param241", "").setMandatory()
						)
				);
		
		cmdop.addOption("option3", "").setAsMultiValueItem(true)
				.addParameters(
				CommandOPFactory.newParameter("param31", "")
					.addParameters(
					CommandOPFactory.newParameter("param311", "")
					), 
				CommandOPFactory.newParameter("param32", "")
				);
		
		CommandOPGroup group1 = new CommandOPGroup(cmdop, "group1", CommandOPGroup.MODE_EXCLUDE);
		group1.addMember("a");
		group1.addMember("c");
		
		CommandOPGroup group2 = new CommandOPGroup(cmdop, "group2", CommandOPGroup.MODE_INCLUDE);
		group2.addMember("c");
		group2.addMember("x");
		
		cmdop.addGroup(group1);
		cmdop.addGroup(group2);
		
		cmdop.exceptionAtFirstError(true);
		try {
			if (!cmdop.parse(args)) {
				System.err.println("---> Parsing errors");
				LinkedList<String> errors = cmdop.getErrorMessages();
				for (String s : errors) {
					System.err.println(s);
				}
			}
		} catch (CommandOPException e) {
			e.printStackTrace();
		}
		
		
		CommandOPPrinter printer = new CommandOPPrinter(cmdop);
		
		System.out.println("args=" + printer.getArgs());
		System.out.println("preParsed: " + printer.getPreParsed(true));
		System.out.println(printer.getPreParsed(false));
		
		System.out.println("definedItems: " + printer.getDefinedItems(true, false, false));
		System.out.println(printer.getDefinedItems(false, true, false));
		
		System.out.println("------------");
		
		CmdLnItem item0 = cmdop.getParameter("optionless2");
		System.out.println(item0.getName() + "=" + item0.getValue());
		
		CmdLnItem item1 = cmdop.getOption("atest");
		System.out.println(item1.getName() + "=" + item1.getValue());
		
		CmdLnItem item2 = cmdop.getOption("option2").getChild("param21").getChild("param211");
		System.out.println(item2.getName() + "=" + item2.getValue());

	}

}
