# CommandOP
**Commandline (CLI) Option Parser**

With CommandOP you get a small Java library which parses [command line (CLI) arguments](http://en.wikipedia.org/wiki/Command-line_argument#Arguments). The library offers a wide range of possibilities, from simple flat command lines to structured parsing and grouping. The library tries to follow the GNU command line style as much as possible.

The main advantage (compared to other command line parsers) of CommandOP is that options and parameters can be structured in a unlimited tree-like construct. This allows for if-then relations (parameters are only allowed if their parent parameter/option is given). Include and exclude groups are also supported. Many other command line parsers only support the traditional flat structures (often just with optional/mandatory options). Options in CommandOP are given as key/value pairs (--option o2=v2 o1=v1...) and CommandOP supports variable argument lists with a defined min/max number of arguments (e.g. --option value1 value2 value3).

Features:
* Short and long options (-v/--version, plus short options can be combined, e.g. -abc instead of -a -b -c)
* Tagged (--filename=Text.txt) and untagged/boolean (--version) arguments
* If-then relations
* Include and exclude option groups
* Boolean items
* Mandatory items and items with a mandatory value
* Default values
* Value validators
* Aliases (e.g. --help, -h, ?)
* Multi value items (--valuelist=a b c d, or --valuelist=a --valuelist=b...)
* Textual output printing for formatted command line help
* Parsing parameters from arrays, lists and maps (helpful for including [Properties](https://docs.oracle.com/javase/tutorial/essential/environment/properties.html))
* Choose between using the first or the last occurrence of a command line item
* Repeated parsing of command line options possible. Parsed options can be consolidated or overwritten
* Retrieve map of key-value pairs



Command line parsing prepares command line arguments in a way that they are easily accessible by program logic. CommandOP parses and validates those arguments using a user defined structure. Following steps are needed for the parsing:

1. Use CommandOP to define the possible arguments (and define if they are mandatory, boolean, ...)
2. Let CommandOP parse and validate the text which is passed via the command line, making it easily accessible to the program logic
3. The program logic can now use the parsed result (retrieve values, check for the exisence of parameters, ...)

-------------------------------------------------

## Example of a traditional flat structure

The following example shows the parsing of a flat command line arguments structure, using a fictitious example.

Lets assume the following options are defined in the source code as possible options:
```
option1, option2, option3, a, b, c, d
```


Here is a possible command lines we want to parse, with long (--) and short (-) options:

```
--option1 o11 o12 o13 --option2=o2 -abc
```


After parsing, the values for the given options can be retrieved and they could look like the following:
```
getOption("option1")  -> map [o11 o12 o13]
getOption("option2")	-> o2
getOption("option3")	-> null
getOption("a")	-> true
getOption("b")	-> true
getOption("c")	-> true
getOption("d")	-> false
```

This is a very easy approach and might fit many needs. But how about more complex structures? How about an application that you can start in server/client mode, and each mode has different and specific arguments? Or an application which has mandatory items which are only mandatory if previous items are given, or even items which need to or must not occur together? CommandOP solves such more complex issues, and at the same time provides an easy way to use it for simple parsing of flat structures as shown in the example above.


## The power of CommandOP

Lets look at an extended server/client example to show the power of CommandOP:

Assuming that the following tree structure is defined in the source code:

```
server                   (boolean, only allowed if client not given)
	port               (mandatory, if server is given)
client                   (boolean, only allowed if server not given)
	host               (mandatory, if client is given)
	port               (mandatory, if client is given)
maxConnections           (default=5)
timeout                  (default=30)
aaa                      (defined as boolean, with short option a)
bbb                      (not defined as boolean, with short option b)
omulti                   (option defined as multi value item)
pmulti                   (parameter at option-level defined as multi value item)
```


As the list shows, server->port, client->host and client->port are defined as mandatory. Also, an exclude-relationship between server and client is defined to prevent them to be passed together. maxConnections and timeout have a default value which is used in case the item is not present. This tree structure gives the ability to have mandatory items which are only mandatory if the parent item is given. server->port for example is only mandatory if the application is started as server.


### Creating the structure

The following code creates an instance of `CommandOP` and defines the structure from the example above.
```java
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
cmdop.addOption("aaa", "defined as boolean").addShortOption('a').setAsBoolean();
cmdop.addOption("bbb", "not defined as boolean").addShortOption('b').setValueRequired();
cmdop.addOption("omulti", "option defined as multi value item").setAsMultiValueItem();
cmdop.addParameter("pmulti", "parameter at option-level defined as multi value item").setAsMultiValueItem();

CommandOPGroup group = new CommandOPGroup(cmdop, "server_client", CommandOPGroup.MODE_EXCLUDE);
group.addMember("server");
group.addMember("client");

cmdop.addGroup(group);
```


### Parsing

Command line arguments can be parsed with `cmdop.parse(args)`. However, since there could be parsing errors there are two possibilities to catch these errors.

1. Use `cmdop.getErrorMessages()` to get all the error messages (`parse(args)` returns `false` if there are any parsing errors). This parses the whole file and stores the error messages for retrieval.
2. Set `cmdop.exceptionAtFirstError(true)` and surround `parse(args)` with a try-catch block to catch eventual parsing errors. This stops the parsing when the first error occures and throws a `CommandOPError`.

CommandOP also generates informational messages (for example for unknown parameters, mismatching definitions etc.). These messages can be retrieved with `cmdop.getInfoMessages()` and mostly show skipped items. An error message is generated if an item is found but it does not match the item definitions, an informational message is generated if an item is ignored.


### Printing useful information

The `CommandOPPrinter` class has some useful methods to print information about the structure and the parsing. The printing can be requested in flat or structured form.
* `printer.getArgs()` returns all the given the command line arguments as string
* `printer.getPreParsed(boolean)` returns a string showing the pre-parsed state. The pre-parser parses *all* the options and parameters and their values from the given command line arguments and tries to categorize them as \[option\] or \[param\]
* `printer.getDefinedItems(boolean, boolean, boolean, boolean)` Shows the whole structure and parsed values as it has been defined. Optional items are enclosed in brackets []. The boolean method parameters can be used to show a flat structure, to show/hide values and descriptions and to hide parameters/options which have been set to `setHiddenInPrint()`.
* `printer.getHelpText()` simply calls `getDefinedItems(...)` with the right flags set to create a pretty help text output

Here is the output of `printer.getDefinedItems(true, false, false, false)` and `getHelpText()`:
```
[pmulti] --server port [dummy] --client host port --maxConnections --timeout --aaa --bbb --omulti
Command line help:
[pmulti] . . . . . . parameter at option-level defined as multi value item
--server . . . . . . only allowed if client not given
    port . . . . . . mandatory, if server is given
    [dummy]. . . . . just another non-mandatory parameter
--client . . . . . . only allowed if server not given
    host . . . . . . mandatory, if client is given
    port . . . . . . mandatory, if client is given
--maxConnections . . default=5
--timeout. . . . . . default=30
-a, --aaa. . . . . . defined as boolean
-b, --bbb. . . . . . not defined as boolean, requires a value
--omulti . . . . . . option defined as multi value item
```

### Accessing the values

Parameters can be retrieved as command line values `CmdLnValue`. Here is a short example:
```java
CmdLnValue item0 = cmdop.getOption("server");
System.out.println(item0.getName() + "=" + item0.getValue());

CmdLnValue item01 = cmdop.getOption("server").getChild("port");
System.out.println(item01.getName() + "=" + item01.getValue());

CmdLnValue item1 = cmdop.getParameter("timeout");
System.out.println(item1.getName() + "=" + item1.getValue());

CmdLnValue item2 = cmdop.getParameter("a");
System.out.println(item2.getName() + "=" + item2.getValue());
```

*Hint: use `cmdop.hasOption("server")` or `cmdop.hasParameter("timeout")` to check if the option/parameter has been parsed.*

A second possibility is to retrieve all parsed command line options and parameters 
as a map. The `CommandOP` class and the `CmdLnValue` class offer the method `toMap()` 
which returns a map containing the child options/parameters, with their names as key and 
the value(s) as `CmdLnValue`.



## Command line examples

Here are some command line examples and their parsing output, using the structure 
example from above

**Example 1:**

```
--server port=12345 --maxConnections=10 --timeout=20
```

*Parsing errors:*<br>
none

*Accessing the values:*

```java
CmdLnValue item0 = cmdop.getOption("server");			//true
CmdLnValue item01 = cmdop.getOption("server").getChild("port");	//12345
CmdLnValue item1 = cmdop.getOption("client");			//false
CmdLnValue item11 = cmdop.getOption("client").getChild("host");	//null
CmdLnValue item12 = cmdop.getOption("client").getChild("port");	//null
CmdLnValue item2 = cmdop.getOption("maxConnections");		//10
CmdLnValue item3 = cmdop.getOption("timeout");			//20
CmdLnValue item4 = cmdop.getParameter("a");			//false
CmdLnValue item5 = cmdop.getParameter("b");			//null
CmdLnValue item6 = cmdop.getOption("omulti");			//null
CmdLnValue item7 = cmdop.getParameter("pmulti");		//null
```

**Example 2:**

```
--client=my_client host=localhost port=12345 --maxConnections=10
```

*Parsing errors:*<br>
none


*Accessing the values:*

```java
CmdLnValue item0 = cmdop.getOption("server");			//false
CmdLnValue item01 = cmdop.getOption("server").getChild("port");	//null
CmdLnValue item1 = cmdop.getOption("client");			//false -> because if a string (also an empty string) is given for a boolean, it results in "false". To get a true, do not use a value (--client instead of --client=someString)
CmdLnValue item11 = cmdop.getOption("client").getChild("host");	//localhost
CmdLnValue item12 = cmdop.getOption("client").getChild("port");	//67890
CmdLnValue item2 = cmdop.getOption("maxConnections");		//10
CmdLnValue item3 = cmdop.getOption("timeout");			//30 -> the default value
CmdLnValue item4 = cmdop.getParameter("a");			//false
CmdLnValue item5 = cmdop.getParameter("b");			//null
CmdLnValue item6 = cmdop.getOption("omulti");			//null
CmdLnValue item7 = cmdop.getParameter("pmulti");		//null
```

**Example 3:**

```
--client host=localhost port=12345 -ab=value --server port=12345
```

*Parsing errors:*<br>
More than one item of the EXCLUDE-group 'server_client' found. Only one of the following items is allowed: \[server, client\]


*Accessing the values:*<br>
Even though an error occurs, the values can still be accessed. The program logic has to decide 
what to do if errors occur.

```java
CmdLnValue item0 = cmdop.getOption("server");			//true
CmdLnValue item01 = cmdop.getOption("server").getChild("port");	//12345
CmdLnValue item1 = cmdop.getOption("client");			//true
CmdLnValue item11 = cmdop.getOption("client").getChild("host");	//localhost
CmdLnValue item12 = cmdop.getOption("client").getChild("port");	//67890
CmdLnValue item2 = cmdop.getOption("maxConnections");		//5
CmdLnValue item3 = cmdop.getOption("timeout");			//30 -> the default value
CmdLnValue item4 = cmdop.getParameter("a");			//true
CmdLnValue item5 = cmdop.getParameter("b");			//value
CmdLnValue item6 = cmdop.getOption("omulti");			//null
CmdLnValue item7 = cmdop.getParameter("pmulti");		//null
```


**Example 4:**

```
pmulti p1 p2 p3 --omulti o1 o2 o3 -ab --client host=localhost
```

*Parsing errors:*<br>
Item 'port' is mandatory<br>
Item 'bbb' requires a value


*Accessing the values:*<br>
Even though an error occurs, the values can still be accessed. The program logic has to decide 
what to do if errors occur.

```java
CmdLnValue item0 = cmdop.getOption("server");			//false
CmdLnValue item01 = cmdop.getOption("server").getChild("port");	//null
CmdLnValue item1 = cmdop.getOption("client");			//true
CmdLnValue item11 = cmdop.getOption("client").getChild("host");	//localhost
CmdLnValue item12 = cmdop.getOption("client").getChild("port");	//null
CmdLnValue item2 = cmdop.getOption("maxConnections");		//5
CmdLnValue item3 = cmdop.getOption("timeout");			//30
CmdLnValue item4 = cmdop.getParameter("a");			//false
CmdLnValue item5 = cmdop.getParameter("b");			//null

CmdLnValue item8 = cmdop.getParameter("pmulti").getValue(0);	//p1
CmdLnValue item9 = cmdop.getParameter("pmulti").getValue(1);	//p2
CmdLnValue item10 = cmdop.getParameter("pmulti").getValue(3);	//p3
CmdLnValue item11 = cmdop.getOption("omulti").getValue(0);	//o1
CmdLnValue item12 = cmdop.getOption("omulti").getValue(1);	//o2
CmdLnValue item13 = cmdop.getOption("omulti").getValue(3);	//o3
```



## More features:

* Generate help/usage and parsing info, in tree or flat form
* Hide parameters so they do not show up in the help
* One or more alias for each item
* One or more short options for each option
* Write your own validator by extending `CommandOPValidator` and add it to any command line item with `setValidator(...)`


