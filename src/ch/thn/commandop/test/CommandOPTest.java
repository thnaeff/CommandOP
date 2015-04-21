package ch.thn.commandop.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Properties;

import ch.thn.commandop.CmdLnValue;
import ch.thn.commandop.CommandOP;
import ch.thn.commandop.CommandOPError;
import ch.thn.commandop.CommandOPFactory;
import ch.thn.commandop.CommandOPGroup;
import ch.thn.commandop.CommandOPPrinter;
import ch.thn.commandop.validator.NumberValidator;

public class CommandOPTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		//- On the command line, an option is followed by its parameters.
		//- Parameters can also be defined without a parent option. Those "optionless" parameters
		//  have to appear at the beginning of the command line.

		//		args = "optionless1 optionless2=value -abc --option1 param11 param12=value --option2=a param21=a param211=211 param22=value -s - --  stuff --multivalue cmd1 cmd2".split(" ");
		//		args = "x=test y z -ca --atest=aliasvalue --option1 param12=123 --option3 unknownparam=value3 multivalue1 multivalue2 --option2=o2 param21=21 param211=p211 param22=22".split(" ");
		args = "optionless2 --atest=test --option3 value1 value2 value3 param31=31 param311=311 param32=32 --option2=o2".split(" ");
		//		args = "--help".split(" ");


		CommandOP cmdop = new CommandOP();

		NumberValidator numvalidator = new NumberValidator();

		//Just a parameter (without -- or - prefix). This parameter is directly added
		//to the main CommandOP object which makes it a "optionless" parameter
		cmdop.addParameter("optionless2", "somevalue", "Just a parameter without option").setMandatory();

		cmdop.addParameter("help", "Shows this command line help").addAlias("?").addAlias("h").setAsBoolean();

		cmdop.addOption("help", "Shows this command line help").addShortAlias('h').setAsBoolean();

		cmdop.addParameter("property4", "A parameter from the properties file").setMandatory();
		cmdop.addParameter("property3", "Another parameter from the properties file").setMandatory().setAsBoolean();

		cmdop.addOption("multivalue", "Multiple values, once from a properties file and once from the command line").setAsMultiValueItem();

		//An option (with -- long or - short prefix). An option can have any number
		//of parameters as children
		cmdop.addOption("a", "short param a, boolean").setAsBoolean().setMandatory();

		cmdop.addOption("c", "def", "short param c, not boolean").setValueRequired();

		cmdop.addOption("x", "defx", "some param x");

		cmdop.addOption("withaliases", "with aliases").addAlias("atest").addAlias("atest2");

		//An option with its own child parameters
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

		cmdop.addOption("option3", "").setAsMultiValueItem()
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

		cmdop.exceptionAtFirstError(false);

		//Parsing properties
		Properties properties_multivalue = new Properties();
		try {
			properties_multivalue.load(new FileInputStream("Properties_multivalue.ini"));

			try {
				if (!cmdop.parse(cmdop.getOption("multivalue"), properties_multivalue, false)) {
					printMsgs("--> Parsing errors", cmdop.getErrorMessages(), System.out);
				}
			} catch (CommandOPError e) {
				e.printStackTrace();
			}

			printMsgs("--> Parsing info", cmdop.getInfoMessages(), System.out);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		printInfo(cmdop);

		System.out.println("===========================================================");

		//Parsing properties
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("Properties.ini"));

			try {
				if (!cmdop.parse(properties, false)) {
					printMsgs("--> Parsing errors", cmdop.getErrorMessages(), System.out);
				}
			} catch (CommandOPError e) {
				e.printStackTrace();
			}

			printMsgs("--> Parsing info", cmdop.getInfoMessages(), System.out);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		printInfo(cmdop);

		System.out.println("===========================================================");

		//Parsing command line arguments
		try {
			if (!cmdop.parse(args, false)) {
				printMsgs("--> Parsing errors", cmdop.getErrorMessages(), System.out);
			}
		} catch (CommandOPError e) {
			e.printStackTrace();
		}

		printMsgs("--> Parsing info", cmdop.getInfoMessages(), System.out);

		printInfo(cmdop);

		System.out.println("------------");

		CmdLnValue item0 = cmdop.getParameter("optionless2");
		System.out.println(item0.getName() + "=" + item0.getValue());

		CmdLnValue item1 = cmdop.getOption("atest");
		System.out.println(item1.getName() + "=" + item1.getValue());

		CmdLnValue item2 = cmdop.getOption("option2").getChild("param21").getChild("param211");
		System.out.println(item2.getName() + "=" + item2.getValue());

		Properties p = cmdop.toProperties();
		try {
			p.store(new FileOutputStream("Properties_out.ini"), "test");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	/**
	 * 
	 * 
	 * @param cmdop
	 */
	public static void printInfo(CommandOP cmdop) {
		CommandOPPrinter printer = new CommandOPPrinter(cmdop);

		System.out.println("args=" + printer.getArgs());
		System.out.println(" ");
		System.out.println("preParsed: " + printer.getPreParsed(true));
		System.out.println(" ");
		System.out.println(printer.getPreParsed(false));

		System.out.println("definedItems: " + printer.getDefinedItems(true, false, false, false));
		System.out.println(" ");
		System.out.println(printer.getDefinedItems(false, true, false, false));

		System.out.println(cmdop.getUnknownArguments());
	}

	/**
	 * 
	 * @param title
	 * @param list
	 */
	public static void printMsgs(String title, List<String> list, PrintStream stream) {
		if (list.size() > 0) {
			stream.println(title);

			for (String s : list) {
				stream.println(s);
			}

			stream.println(" ");
		}
	}

}
