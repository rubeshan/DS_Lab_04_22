DS_Lab_04_22
============

Distributed System's fourth lab session

Lab#4 – Servlet Application.

Objective

In this lab you will apply the code you developed for lab 3 that used RMI to control the robot and modify it to work as a servlet. 
A. The Software System
Below is an architecture diagram of the distributed software system that you will develop in the lab.



 

This is a similar diagram to that presented in Lab 3 except the Robot Service is a servlet that is executed in a Web application and the client is now a web application executing in a web browser.
B. The GlassFish Tools Bundle for Eclipse and Servlets
For this lab you will require the use of a Web application server. The simplest approach to this is to download a J2EE Eclipse environment with a built in web application server. We will use the GlassFish tools bundle for Eclipse.
1.  Download the GlassFish Tools Bundle for Eclipse from http://dlc.sun.com.edgesuite.net/glassfish/eclipse/ or get a copy of the installation image from the TA and install the IDE.
2.	Once installed you can launch the IDE and create a workspace for your applications. 
3.	You will note that the interface for the IDE very familiar because it is basically an ECLIPSE environment. The most significant differences is that the Java SDK is the Java EE 6 rather than Java SE and there is support for the launching of web applications on to a Web Server.
4.	The Web server that automatically is installed is the GlassFish v3 Java EE 6 server. You can see this by selecting the Servers view that should display 2 deployment servers. A localhost HTTP preview (used for viewing Static Web Pages) and the GlassFish v3 Java EE 6 server.
5.	Whenever a servlet project is created you can run this project in the server.
Creating a simple servlet.

1.	Create a New “Dynamic Web Project” name it Servlet Example
2.	In this project crate a New “Web Servlet” name it SimpleServlet. If you use all the default settings you will end up with a Java servlet file that extends the HTTPServlet class that contains some simple code that will return back to a web client the name of the servlet when an HTTP GET or PUT command is sent to the server.
3.	To deploy this servlet to the GlassFish server it is simply a matter of selecting the SimpleServlet.java file in the navigator and choosing the Run icon. You will then be prompted to select a server to install and execute the servlet. Choose the GlassFish server.
4.	At this point the IDE will launch a web browser view that will execute the servlet by simply referencing it as http://localhost:8084/Servlet_Examples/SimpleServlet.
C. Creating the Robot Radio Control Web Client 
In this portion of the lab you will transform the RadioControl.java file into an applet that can be executed on a web browser to send “POST” commands to a servlet to drive a motor on the NXT robot.
1.	Create a new Java project in the GlassFish IDE and create a new class called RadioControlApplet. Convert this class into an Applet by extending the Applet class.
2.	Copy and paste the code for the RadioControl class into the RadioControlApplet class.
3.	There will many errors but many of these can be removed by adding the appropriate include files and deleting unnecessary code. For example:
a.	Add to the RadioControlApplet class that it implements the KeyListener and Runnable interfaces.
b.	Because RadioControlApplet cannot extend a Frame then you will have to define an instance variable of Frame type, lets call it myFrame.
c.	Because Applets do not support constructor methods, the constructor for RemoteControl should be renamed to the init() method, that is the equivalent to a constructor. All the references to this in the constructor should be renamed to myFrame. 
d.	Applets do not support main() methods instead the main method should be renamed to the start() method, that is executed whenever the applet is invoked. Also remove any references to direct NXT API calls since they do not apply to a remote client, therefore the only behavior that the start() method does is to launch a thread to handle communications with the server. The following code is an example of that:
		if (thread==null) {
			thread = new Thread(this);
		}
		thread.start();

The variable thread needs to be declared as an instance variable of the RadioControlApplet class because it needs to be set to null after the Applet closes, in the stop() method. For example:
public void stop(){
		thread=null;
	}

e.	Remove all other references to the NXT APIs, for example anything related to the NXTConn and MotorA variables.
f.	Define some static variables that will be used as commands for the backend servlet. The following ones are an example:
	private static final String MOTOR_A = "A";
	private static final String MOTOR_B = "B";
	private static final String MOTOR_C = "C";
	private static final int STOP = 0;
g.	Remove any other lingering errors before proceeding to adding the code to communicate with the Servlet.
4.	Test the applet to make certain it executes by running it as an applet in the IDE. The Applet should create a window and do nothing.
Communicating to a Servlet
Now you will add the servlet communication component to the Applet.
1.	Increase the height of the Frame to 450. We will add a text component in the Frame to help debug the Applet.
2.	Add a text component to the Frame by including these lines in the init() method:
		textArea = new TextArea();
		textArea.setFocusable(false);
		window.add(textArea);
3.	Define the location of the servlet (note that for testing purposes you will use the SimpleServlet that you defined in Part B:
	String location = "http://localhost:8084/Servlet_Examples/SimpleServlet";
4.	Define a method called sendMotorCommand(String motor, int direction) that you will use to send a POST request to the servlet specifying the motor ID and direction. For each of the command cases in the run() method you can include a call to this method with the corresponding arguments.
5.	In the sendMotorCommand method you need to compose the message that is to be sent with the POST command in a UTF-8 format. This the argument part of a URL message that is commonly seen in a browser when data from a form is sent to the server.
	message = "Motor=" + URLEncoder.encode(motor,"UTF-8") + "&" +"Cmd=" + URLEncoder.encode(Integer.toString(direction),"UTF-8");
6.	The code necessary to send a POST request to the servlet is:
	rcServlet = new URL( location );
	servletConnection = rcServlet.openConnection();
	servletConnection.setDoInput(true); 
	servletConnection.setDoOutput(true); 
	servletConnection.setUseCaches(false); 

	DataOutputStream dos;

	dos = new DataOutputStream (servletConnection.getOutputStream());
	dos.writeBytes(message); 
	dos.flush(); 
	dos.close();

	// the server responds 

	BufferedReader dis
	= new BufferedReader(new InputStreamReader(servletConnection.getInputStream()));

	String s,out=""; 

	while ((s = dis.readLine()) != null)
	{ 
		out += s+"\n";
	} 
	textArea.setText(out); 
	   dis.close();
	   
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

Note how this code simply prints out to the window in the Frame the information returned by the servlet. This is done simply to test the functionality of the Applet before moving forward.
7.	Execute the SimpleServlet. After it is running on the server execute the RadioControlApplet. The SimpleServlet will return the HTML code output by the servlet. The Applet should print this HTML text in the Frame. If you want to confirm that the POST request is actually executing comment out the processRequest call in the doGet method of SimpleServlet. You will notice that when you now execute SimpleServlet there is no output from the web browser in GlassFish because it sends a GET request rather than POST while there should be some output from the Applet. Make certain that this is working before proceeding on.

Handling the POST request arguments in the servlet

Handling the POST request arguments in the servlet is fairly straight forward. You need to only call the following method from the doPost method, request.getParameter(String argumentName)to get the value of an argument that was passed in the POST request. Add the extra line of code to the doPost method in order to get an output from the Frame that appears similar to that shown below.

 

You should be able to also press on the up and down arrow keys and the motor direction displayed in the text area will change appropriately.

D. The NXTRobotService Servlet
This servlet will be based on the NXTRobotService class that was developed for the RMI lab. This conversion is a lot simpler than the Applet since the server application does not require any major alterations.
1.	Create a New “Dynamic Web Project” name it Servlet Example
2.	In this project crate a New “Servlet” name it NXTRobotService. This servlet unlike the Web Servlet you created in section B does not have any default code in the doGet and doPost methods.
3.	Include into this project the pccomm.jar file from the Lejos API that you have been using for the other labs and add to the Servlet code the import lejos.nxt.*; and import lejos.pc.comm.*; lines.
4.	Since you will have to interpret the same commands from the RobotControlApplet then copy and paste the static declarations of the commands:
	private static final int STOP = 0;
	private static final int DIRECTION_FORWARDS = 1;
	private static final int DIRECTION_BACKWARDS = 2;
	private static final String MOTOR_A = "A";
	private static final String MOTOR_B = "B";
	private static final String MOTOR_C = "C";
5.	Add the NXT specific code that you can copy from the original NXTRobotService:
	static NXTComm NXTConn;
6.	As in the original NXTRobotService add to the NXTRobotService servlet contructor the NXT connection initialization. 
		try {
			NXTConn = NXTCommFactory.createNXTComm(NXTCommFactory.USB);
		}
		catch(NXTCommException e) {
			System.out.println("Error - creating NXT connection");
		}
7.	Take all the rest of the methods in the original NXTRobotService (forward, stop, backward) and simply copy and paste them as methods for the NXTRobotService servlet.
8.	In the doPost method you simply need to read the arguments sent in the POST message, see the SimpleServer example, and act on these by calling the appropriate robot control method (forward, stop, backward). I would also recommend that you return some acknowledgement message like an “<ACK>” text message after processing the request.
9.	At this point the NXTRobotService is ready to be tested. Unfortunately if you try to launch the NXTRobotService servlet you will get a Java Class Not Found - NXTCommException error from the GlassFish server. It is necessary to copy and paste the pccomm.jar file from the folder that you installed the NXT JAR files into to DriveLetter:\GlassFish-Tools-Bundle-For-Eclipse-1.2\glassfishv3\glassfish\lib. This will remove this error.
10.	After the NXTRobotService servlet has installed without errors, you can modify the RadioControlApplet so that it references the NXTRobotService servlet rather than the SimpleServlet servlet (location = "http://localhost:8084/Lab_4_Servlet/NXTRobotService")


