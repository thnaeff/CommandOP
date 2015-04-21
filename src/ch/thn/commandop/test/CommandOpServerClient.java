package ch.thn.commandop.test;

import java.util.LinkedList;

import ch.thn.commandop.CmdLnValue;
import ch.thn.commandop.CommandOP;
import ch.thn.commandop.CommandOPError;
import ch.thn.commandop.CommandOPFactory;
import ch.thn.commandop.CommandOPGroup;
import ch.thn.commandop.CommandOPPrinter;

public class CommandOpServerClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		args = "--server port=12345 --maxConnections=10 --timeout=20".split(" ");
		//		args = "--client=my_client host=localhost port=67890 --maxConnections=10".split(" ");
		//		args = "--client host=localhost port=67890 -ab=value --server port=12345".split(" ");
		//		args = "pmulti p1 p2 p3 --omulti o1 o2 o3 -ab --client host=localhost".split(" ");

		CommandOP cmdop = new CommandOP();

		cmdop.addOption("server", "only allowed if client not given").setAsBoolean()
		.addParameters(
				CommandOPFactory.newParameter("port", "mandatory, if server is given").setMandatory()
				, CommandOPFactory.newParameter("dummy", "just another non-mandatory parameter")
				);
		cmdop.addOption("client", "only allowed if server not given").setAsBoolean()
		.addParameters(
				CommandOPFactory.newParameter("host", "mandatory, if client is given").setMandatory()
				, CommandOPFactory.newParameter("port", "mandatory, if client is given").setMandatory()
				);
		cmdop.addOption("maxConnections", "5", "default=5");
		cmdop.addOption("timeout", "30", "default=30");
		cmdop.addOption("aaa", "defined as boolean").addShortAlias('a').setAsBoolean();
		cmdop.addOption("bbb", "not defined as boolean").addShortAlias('b').setValueRequired();
		cmdop.addOption("omulti", "option defined as multi value item").setAsMultiValueItem();
		cmdop.addParameter("pmulti", "parameter at option-level defined as multi value item").setAsMultiValueItem();

		CommandOPGroup group = new CommandOPGroup(cmdop, "server_client", CommandOPGroup.MODE_EXCLUDE);
		group.addMember("server");
		group.addMember("client");

		cmdop.addGroup(group);



		cmdop.exceptionAtFirstError(false);

		try {
			if (!cmdop.parse(args, false)) {
				LinkedList<String> errors = cmdop.getErrorMessages();

				for (String s : errors) {
					System.err.println(s);
				}
			}
		} catch (CommandOPError e) {
			e.printStackTrace();
		}


		CommandOPPrinter printer = new CommandOPPrinter(cmdop);

		System.out.println("args=" + printer.getArgs());
		System.out.println("preParsed: " + printer.getPreParsed(true));
		System.out.println(printer.getPreParsed(false));

		System.out.println("definedItems: " + printer.getDefinedItems(true, false, false, false));
		System.out.println(printer.getDefinedItems(false, true, false, false));

		System.out.println(printer.getHelpText());

		System.out.println("------------");

		CmdLnValue item0 = cmdop.getOption("server");
		System.out.println(item0.getName() + "=" + item0.getValue());

		CmdLnValue item01 = cmdop.getOption("server").getChild("port");
		System.out.println(item01.getName() + "=" + item01.getValue());


		CmdLnValue item1 = cmdop.getOption("client");
		System.out.println(item1.getName() + "=" + item1.getValue());

		CmdLnValue item11 = cmdop.getOption("client").getChild("host");
		System.out.println(item11.getName() + "=" + item11.getValue());

		CmdLnValue item12 = cmdop.getOption("client").getChild("port");
		System.out.println(item12.getName() + "=" + item12.getValue());



		CmdLnValue item2 = cmdop.getOption("maxConnections");
		System.out.println(item2.getName() + "=" + item2.getValue());

		CmdLnValue item3 = cmdop.getOption("timeout");
		System.out.println(item3.getName() + "=" + item3.getValue());

		CmdLnValue item4 = cmdop.getOption("a");
		System.out.println(item4.getName() + "=" + item4.getValue());

		CmdLnValue item5 = cmdop.getOption("b");
		System.out.println(item5.getName() + "=" + item5.getValue());

		CmdLnValue item6 = cmdop.getOption("omulti");
		System.out.println(item6.getName() + "=" + item6.getValue());

		CmdLnValue item7 = cmdop.getParameter("pmulti");
		System.out.println(item7.getName() + "=" + item7.getValue());

	}

}
