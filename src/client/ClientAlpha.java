package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;



public class ClientAlpha implements Runnable{
	
//	This is for sending and receiving the object using the sockets 
	private ObjectInputStream input;		
	private ObjectOutputStream output;		
	protected int    clientPort   = 10033;
    protected Socket clientSocket = null;
	
	
	
//	These are the parameter for each client for connecting 
	private String server, username;
	private int port;
//	This is for stop the running of my client
	protected boolean      isStopped    = false;
	
	
	public ClientAlpha(String server, int port, String username) {
		//log_mes.setUseParentHandlers(false);
		this.server = server;
		this.port = port;
		this.username = username;
	}
	
	public void run(){
//		Try to connect to the server
		try {
			clientSocket= new Socket(this.server,this.port);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String msg = "Connection accepted " + clientSocket.getInetAddress() + ":" + clientSocket.getPort()+" User: "+username ;
		System.out.println(msg);
		
//		Pick a random option		
		Random rand= new Random();
		int randomNum=rand.nextInt(9);
		String messageServer="";
//		
////		Initialize the streamers for the input and output
		try {
			
			this.output = new ObjectOutputStream(clientSocket.getOutputStream());
			output.writeObject(randomNum);
			this.input  = new ObjectInputStream(clientSocket.getInputStream());
			messageServer=(String) input.readObject();
			System.out.println(messageServer);
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		while(! isStopped()){
			randomNum=rand.nextInt(9);
			System.out.println("The option was "+randomNum);
			try {
				output.writeObject(randomNum);
				messageServer=(String) input.readObject();
				System.out.println(messageServer);
			} catch (IOException | ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(randomNum==7){
				stop();
			}
		}
		disconnect();
	}
	
	private void disconnect() {
		try { 
			if(input != null) input.close();
		}
		catch(Exception e) {
			System.out.println("Exception closing inbut buffer : " + e);
		}
		try {
			if(output != null) output.close();
		}
		catch(Exception e) {
			System.out.println("Exception closing output buffer : " + e);
		}
        try{
			if(clientSocket != null) clientSocket.close();
		}
		catch(Exception e) {
			System.out.println("Exception closing socket : " + e);
		}
		
	}
	
	private synchronized boolean isStopped() {
		return this.isStopped;
    }
	
	public synchronized void stop(){
        this.isStopped = true;
    }
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int portNumber = 9000;
		String serverAddress = "localhost";
		String userName = "user_z";

		
//		This switch is used to determine how to initialize the client
//		based on the number of arguments 
		switch(args.length) {
			case 3:
				serverAddress = args[2];
				portNumber= Integer.parseInt(args[1]);
				userName=args[0];
				break;
			case 2:
				try {
					portNumber = Integer.parseInt(args[1]);
				}
				catch(Exception e) {
					System.out.println("Invalid port number.");
					System.out.println("Usage is: > java Client [username] [portNumber] [serverAddress]");
					return;
				}
				break;
			case 1: 
				userName = args[0];
				break;
			case 0:
				break;
			default:
				System.out.println("Usage is: > java Client [username] [portNumber] {serverAddress]");
			return;
		}
		ClientAlpha client = new ClientAlpha(serverAddress, portNumber, userName);
		new Thread(client).start();
		//System.out.println("Stopping Clients");
		//client.stop();
	}

}
