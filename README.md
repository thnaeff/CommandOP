CommandOP
==========
[Commandline Option Parser]

Command line parsing prepares command line arguments (http://en.wikipedia.org/wiki/Command-line_argument#Arguments) in a way that 
they are easily accessible by program logic, including different validation steps to ensure the command line arguments match 
the requirements. Following steps are needed:
1. Defining the possible arguments (and defining if they are mandatory, boolean, ...)
2. Processing the text which is passed via the command line, making it easily accessible (parsing and validation)
3. Program logic using the parsed result (Retreaving values, checking for the existence of parameters, ...)

Steps 1 and 3 have to be implemented in the user code. Step 2 is done with CommandOP.


With CommandOP you get a small Java library which parses command lines. The main advantage of CommandOP is that options and parameters 
can be structured in a tree-like construct, unlike other command line parsers which only support flat structures.


Example of flat structure
---------------------------
The following example shows the parsing of a flat structure, using a fictitious example. 

Lets assume the following options are defined in the source code as possible options:<br />
<code>
option1, option2, option3, a, b, c, d
</code>

Here are two possible command lines to parse, with long (--) and short (-) options:<br />
<code>
--option1 o11 o12 o13 --option2=o2 -abc<br />
</code>
<code>
option1 o11 o12 o13 option2 o2 -abc<br />
</code>

After parsing, the values for the given options can be retrieved:<br />
<code>
getOption("option1")  -> map [o11 o12 o13]
getOption("option2")	-> o2
getOption("option3")	-> null
getOption("a")	-> true
getOption("b")	-> true
getOption("c")	-> true
getOption("d")	-> false
</code>


This is a very easy approach and might fit many needs. But how about more complex structures? How about an application that you can start in server/client mode, and each mode has specific arguments? Or an application which has mandatory items which are only mandatory if previous items are given, or even items which need to or must not occur together? CommandOP tries to solve such issues, and at the same time provides an easy way to use it for simple parsing as shown in the example above.

The power of CommandOP
-----------------------
Lets look at an extended server/client example to show the power of CommandOP:

Assuming that the following tree structure is defined in the source code (corresponds to step 1 mentioned at the beginning):<br />
<code>
server				(only allowed if client not given)
  port				(mandatory)
client				(only allowed if server not given)
	host				(mandatory)
	port				(mandatory)
maxConnections		(default=5)
timeout				(default=30)
a						(defined as boolean)
b						(not defined as boolean)
omulti				(option defined as multi value item)
pmulti				(parameter at option-level defined as multi value item)
</code>


As the list shows, server->port, client->host and client->port are defined as mandatory. Also, an exclude-relationship between server and client is defined to prevent them to be passed together. maxConnections and timeout have a default value which is used in case the item is not present. This tree structure gives the ability to have mandatory items which are only mandatory if the parent item is given. server->port for example is only mandatory if the application is started as server.

Now lets look at some command line examples and what happens when they are parsed

Example 1:<br />
<code>
--server port=12345 --maxConnections=10 --timeout=20
</code>

Parsing-output:<br />
none

Accessing the values:<br />
<code>
getOption("server").getParameter("port").getValue() = 12345
getOption("client") = null
getOption("maxConnections").getValue() = 10
getOption("timeout").getValue() = 20
</code>


Example 2:<br />
<code>
--client=my_client host=localhost port=12345 --maxConnections=10
</code>

Parsing-output:<br />
none

Accessing the values:<br />
<code>
getOption("client").getValue() = my_client
getOption("client").getParameter("host").getValue() = localhost
getOption("client").getParameter("port").getValue() = 12345
getOption("server") = null
getOption("maxConnections").getValue() = 10
getOption("timeout").getValue() = 30
getOption("a") = false
getOption("b") = null
</code>


Example 3:<br />
<code>
--client host=localhost port=12345 --server port=12345
</code>

Parsing-output:<br />
!!!

Accessing the values:<br />
Even though an error occurs, the values can still be accessed as described in Example 1 and Example 2



Example 4:<br />
<code>
pmulti p1 p2 p3 --omulti o1 o2 o3 -ab --client host=localhost
</code>

Parsing-output:<br />
!!!

Accessing the values:<br />
Even though an error occurs, the values can still be accessed as described in Example 1 and Example 2

<code>
getParameter("pmulti").getValue(0) = p1
getParameter("pmulti").getValue(1) = p2
getParameter("pmulti").getValue(3) = p3
getOption("omulti").getValue(0) = o1
getOption("omulti").getValue(1) = o2
getOption("omulti").getValue(3) = o3
getOption("a") = true
getOption("b") = null
</code>



More features:<br />
generate help/usage and parsing info, in tree or flat form<br />
hide parameters so they do not show up in the help<br />
one or more alias for each item<br />
Write your own validator<br />
