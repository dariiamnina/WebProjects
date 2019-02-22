# Database management system
##Client-Server applications in Java

This is a database management system in form of several distributed versions and web-services, with the use of sql queries:

1. database (kernel of the system, includes classes for creation, selection, editing of the databases)
2. Undistributed (local version of the application, sql queries)
3. rmi (distributed version of system - Client program and Server program)
4. REST (access to the data with the hierarchic structure and HTTP queries - GET POST DELETE)
5. spring (using spring framework and AJAX)

The supported data types are int, float, string, char, date, dataInvl(interval).
The possible operations include sorting by a key and others are basic.

The result of query (AJAX, REST) is displayed as a raw JSON file, but can be organized in a nice html.
Use gradle for test.
