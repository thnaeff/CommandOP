CODE WILL FOLLOW SOON...



CommandOP
==========
[Commandline Option Parser]

<p>
With CommandOP you get a small Java library which parses command lines. The main advantage of CommandOP is that options and parameters 
can be structured in a tree-like construct, unlike other command line parsers which only support flat structures.
</p>

<p>
Command line parsing prepares command line arguments (http://en.wikipedia.org/wiki/Command-line_argument#Arguments) in a way that 
they are easily accessible by program logic, including different validation steps to ensure the command line arguments match 
the requirements. Following steps are needed:<br />
1. Defining the possible arguments (and defining if they are mandatory, boolean, ...)<br />
2. Processing the text which is passed via the command line, making it easily accessible (parsing and validation)<br />
3. Program logic using the parsed result (Retreaving values, checking for the exisence of parameters, ...)<br />
Steps 1 and 3 have to be implemented in the user code. Step 2 is done with CommandOP.
</p>

Example of flat structure
---------------------------
<p>
The following example shows the parsing of a flat structure, using a fictitious example.
</p>
<p>
Lets assume the following options are defined in the source code as possible options:
</p>
<pre><code>option1, option2, option3, a, b, c, d
</code></pre>

<p>
Here are two possible command lines we want to parse, with long (--) and short (-) options:
</p>
<pre><code>--option1 o11 o12 o13 --option2=o2 -abc
</code></pre>
<pre><code>option1 o11 o12 o13 option2 o2 -abc
</code></pre>

<p>
After parsing, the values for the given options can be retrieved:
</p>
<pre><code>getOption("option1")  -> map [o11 o12 o13]
getOption("option2")	-> o2
getOption("option3")	-> null
getOption("a")	-> true
getOption("b")	-> true
getOption("c")	-> true
getOption("d")	-> false
</code></pre>

<p>
This is a very easy approach and might fit many needs. But how about more complex structures? How about an application that you can start in server/client mode, and each mode has specific arguments? Or an application which has mandatory items which are only mandatory if previous items are given, or even items which need to or must not occur together? CommandOP tries to solve such more complex issues, and at the same time provides an easy way to use it for simple parsing of flat structures as shown in the example above.
</p>

The power of CommandOP
-----------------------
<p>
Lets look at an extended server/client example to show the power of CommandOP:
</p>

<p>
Assuming that the following tree structure is defined in the source code (corresponds to step 1 mentioned at the beginning):
</p>
<pre><code>server				(only allowed if client not given)
	port			(mandatory, if server is given)
client				(only allowed if server not given)
	host			(mandatory, if client is given)
	port			(mandatory, if client is given)
maxConnections		(default=5)
timeout				(default=30)
a					(defined as boolean)
b					(not defined as boolean)
omulti				(option defined as multi value item)
pmulti				(parameter at option-level defined as multi value item)
</code></pre>

<p>
As the list shows, server->port, client->host and client->port are defined as mandatory. Also, an exclude-relationship between server and client is defined to prevent them to be passed together. maxConnections and timeout have a default value which is used in case the item is not present. This tree structure gives the ability to have mandatory items which are only mandatory if the parent item is given. server->port for example is only mandatory if the application is started as server.
</p>

<p>
Now lets look at some command line examples and what happens when they are parsed
</p>

<p>
<b>Example 1:</b><br />
</p>
<pre><code>--server port=12345 --maxConnections=10 --timeout=20
</code></pre>
<p>
Parsing-output:<br />
none, because it should be parsed without errors
</p>
Accessing the values:<br />
<pre><code>getOption("server").getParameter("port").getValue() = 12345
getOption("client") = null
getOption("maxConnections").getValue() = 10
getOption("timeout").getValue() = 20
</code></pre>

<br />
<p>
<b>Example 2:</b><br />
</p>
<pre><code>--client=my_client host=localhost port=12345 --maxConnections=10
</code></pre>
<p>
Parsing-output:<br />
none, because it should be parsed without errors
</p>
<p>
Accessing the values:
</p>
<pre><code>getOption("client").getValue() = my_client
getOption("client").getParameter("host").getValue() = localhost
getOption("client").getParameter("port").getValue() = 12345
getOption("server") = null
getOption("maxConnections").getValue() = 10
getOption("timeout").getValue() = 30
getOption("a") = false
getOption("b") = null
</code></pre>

<br />
<p>
<b>Example 3:</b>
</p>
<pre><code>--client host=localhost port=12345 --server port=12345
</code></pre>
<p>
Parsing-output:<br />
!!!
</p>
<p>
Accessing the values:
</p>
Even though an error occurs, the values can still be accessed as described in Example 1 and Example 2


<br />
<p>
<b>Example 4:</b>
</p>
<pre><code>pmulti p1 p2 p3 --omulti o1 o2 o3 -ab --client host=localhost
</code></pre>
<p>
Parsing-output:<br />
!!!
</p>
<p>
Accessing the values:
</p>
Even though an error occurs, the values can still be accessed as described in Example 1 and Example 2

<pre><code>getParameter("pmulti").getValue(0) = p1
getParameter("pmulti").getValue(1) = p2
getParameter("pmulti").getValue(3) = p3
getOption("omulti").getValue(0) = o1
getOption("omulti").getValue(1) = o2
getOption("omulti").getValue(3) = o3
getOption("a") = true
getOption("b") = null
</code></pre>



More features:
---------------
<p>
<ul>
<li>generate help/usage and parsing info, in tree or flat form</li>
<li>hide parameters so they do not show up in the help</li>
<li>one or more alias for each item</li>
<li>Write your own validator</li>
</ul>
</p>
