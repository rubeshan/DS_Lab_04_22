import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class RadioControlApplet extends Applet implements Runnable, KeyListener {

	/*
	 * This program is a simple program that will drive a motor when the user
	 * presses a arrow key form in the keyboard.
	 */

	private static final long serialVersionUID = 1L;
	public static final int FRAME_HEIGHT = 450;
	public static final int FRAME_WIDTH = 300;
	public static final int DELAY_MS = 100;
	public static final int COMMAND_NONE = 1;
	public static final int COMMAND_FORWARDS = 2;
	public static final int COMMAND_BACKWARDS = 3;
	private static final int DIRECTION_FORWARDS = 1;
	private static final int DIRECTION_BACKWARDS = 2;
	private static Thread thread;
	private static TextArea textArea;
	private static final String MOTOR_A = "A";
	
	String location = "http://localhost:8080/SimpleServlet/NXTRobotService";
	private int command;
	private Frame myFrame= new Frame();
	private int direction;
	
	
	/*
	 * Constructor.
	 */
	public void init() {
		
		myFrame.setBounds(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
		myFrame.addKeyListener(this);
		myFrame.addKeyListener(this);
		myFrame.setVisible(true);
		
		command = COMMAND_NONE;
		setDirection(DIRECTION_FORWARDS);
		textArea = new TextArea();
		textArea.setFocusable(false);
		myFrame.add(textArea);
		
		
	}

	
	public void run() {
		
		while (true) {
			switch (command) {
			case COMMAND_NONE:
				myFrame.setTitle("None");
				sendMotorCommand(MOTOR_A,COMMAND_NONE);
				break;
			case COMMAND_FORWARDS:
				myFrame.setTitle("Forwards");
				setDirection(DIRECTION_FORWARDS);
				sendMotorCommand(MOTOR_A,COMMAND_FORWARDS);
				break;
			case COMMAND_BACKWARDS:
				myFrame.setTitle("Backwards");
				setDirection(DIRECTION_BACKWARDS);
				sendMotorCommand(MOTOR_A,COMMAND_BACKWARDS);
				break;
			default:
				System.out.println("unknown command " + command);
				System.exit(1);
			}

			
			try {
				Thread.sleep(DELAY_MS);
			} catch (Exception e){
				System.out.println(e);
				System.exit(1);
			}
			
		}
	}
	
	
	
	/*
	 * Window closing event.
	 */
	protected void processWindowEvent(WindowEvent e) {
		super.processEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			System.exit(0);
		}
	}

	/*
	 * Main.
	 */
	public void start(){
			run();
        
		if (thread==null) {
			thread = new Thread();
		}
		thread.start();
	}
	
	public void stop(){
		thread=null;
	}
	
	public void sendMotorCommand(String motor, int direction){
        
	try {
		String	message = "Motor=" + URLEncoder.encode(motor,"UTF-8") + "&" +"Cmd=" + URLEncoder.encode(Integer.toString(direction),"UTF-8");
	 
	
	URL rcServlet = new URL(location);
	URLConnection  servletConnection = rcServlet.openConnection();
	servletConnection.setDoInput(true); 
	servletConnection.setDoOutput(true); 
	servletConnection.setUseCaches(false); 

	DataOutputStream dos;

	dos = new DataOutputStream (servletConnection.getOutputStream());
	dos.writeBytes(message); 
	dos.flush(); 
	dos.close();

	// the server responds 

	BufferedReader dis	= new BufferedReader(new InputStreamReader(servletConnection.getInputStream()));

	String s,out=""; 

	while ((s = dis.readLine()) != null)	{ 
		out += s+"\n";
	} 
	textArea.setText(out); 
	   dis.close();
	   
	}catch (IOException e) {

		e.printStackTrace();
	}

		
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getDirection() {
		return direction;
	}

	public void keyPressed(KeyEvent e) {
		int kc = e.getKeyCode();

		switch (kc) {
		case java.awt.event.KeyEvent.VK_UP:
			command = COMMAND_FORWARDS;
			break;
		case java.awt.event.KeyEvent.VK_DOWN:
			command = COMMAND_BACKWARDS;
			break;
		default:
			command = COMMAND_NONE;
			break;
		}
	}

	public void keyReleased(KeyEvent e) {
		command = COMMAND_NONE;
	}

	public void keyTyped(KeyEvent e) { /* do nothing */
	}

}
