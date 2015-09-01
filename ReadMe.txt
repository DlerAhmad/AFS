Team 12: Vidya, Haritha, and Ahmad
Project: Distributed File System

How to run the system:

	1- Run the following servers on Glados:
		-AuthServer
		-TicketGrantingServer
		-ServiceServer

	2- Run Main program on any machine

	3- Some login info: (available in userInfo.txt)

		-username:alice
		-password:mypass
		
		-username:bob
		-password:passw

Key notes:
	- Any “append” operation needs to be done after an “open” operation.		
	- In case any host or port needs to be changed, modifications can 
	be made in Constants.java