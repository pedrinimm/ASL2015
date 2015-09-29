package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.Random;

import entities.Message;
import entities.Protocol;
import entities.Queue;
import entities.User;



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
		int randomNum=rand.nextInt(8);
		String messageServer="";
		
		//This is the protocol variable to figure out what to ask for to the client;
		User client = new User(this.username);
		Protocol transitObject= new Protocol(99,client,client.name);
		int counter=transitObject.counter;
//		
////		Initialize the streamers for the input and output
		try {
			
			this.output = new ObjectOutputStream(clientSocket.getOutputStream());
			transitObject.counter=counter;
			output.reset();
			output.writeObject(transitObject);
			output.flush();
			counter++;
			this.input  = new ObjectInputStream(clientSocket.getInputStream());
			
			transitObject=(Protocol) input.readObject();
			while(true){
				if(transitObject.counter+1==counter){
					break;
				}else{
					transitObject=(Protocol) input.readObject();
				}
			}
			messageServer=transitObject.message;
			
			System.out.println(messageServer);
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		while(! isStopped()){
			randomNum=rand.nextInt(8);
			//System.out.println("The option was "+randomNum);
			try {
//				object that will be use to handle the communication between client and server
				Message forServer=new Message(this.username);
				String replayFromServer="";
				Queue queueForServer=new Queue();
				queueForServer.name="general";
				
//				Options of services that the server provides

				while(randomNum!=7){
//					this manual assignmetn is for debuging reasons
//					randomNum=5;
//					end of debuging reason
					System.out.println("The option was "+randomNum);
					switch(randomNum){
						case 0:
//							read message
							transitObject.protocolNum=randomNum;
							transitObject.newMessage=forServer;
							transitObject.counter=counter;
							output.reset();
							output.writeObject(transitObject);
							output.flush();
							counter++;
							
//							condicion para sincronizar los objectos transferidos
							transitObject=(Protocol) input.readObject();
							while(true){
								if(transitObject.counter+1==counter){
									break;
								}else{
									transitObject=(Protocol) input.readObject();
								}
							}
							forServer= transitObject.newMessage;
							
							System.out.println(forServer.messageID);
							break;
						case 1:
//							reading message for me 
							transitObject.protocolNum=randomNum;
							transitObject.userName=this.username;
							transitObject.counter=counter;
							output.reset();
							output.writeObject(transitObject);
							output.flush();
							counter++;
							
//							condicion para sincronizar los objectos transferidos
							transitObject=(Protocol) input.readObject();
							while(true){
								if(transitObject.counter+1==counter){
									break;
								}else{
									transitObject=(Protocol) input.readObject();
								}
							}
							forServer= transitObject.newMessage;
							
							
							System.out.println(forServer.messageID);
							break;
						case 2:
//							looking for message sent by someone 
							transitObject.protocolNum=randomNum;
							transitObject.userName=this.username;
							forServer.sender="user_z";
							transitObject.newMessage=forServer;
							transitObject.counter=counter;
							output.reset();
							output.writeObject(transitObject);
							output.flush();
							counter++;
							
//							condicion para sincronizar los objectos transferidos
							transitObject=(Protocol) input.readObject();
							while(true){
								if(transitObject.counter+1==counter){
									break;
								}else{
									transitObject=(Protocol) input.readObject();
								}
							}
							forServer= transitObject.newMessage;
					
							
							System.out.println(forServer.messageID);
							break;
						case 3:
//							create a new message
							transitObject.protocolNum=randomNum;
							forServer=new Message("Time made "+new Timestamp(System.currentTimeMillis()),this.username);
							transitObject.newMessage=forServer;
							transitObject.counter=counter;
							output.reset();
							output.writeObject(transitObject);
							output.flush();
							counter++;
							
//							condicion para sincronizar los objectos transferidos
							transitObject=(Protocol) input.readObject();
							while(true){
								if(transitObject.counter+1==counter){
									break;
								}else{
									transitObject=(Protocol) input.readObject();
								}
							}
							replayFromServer=transitObject.message;
					
							
							
							
							System.out.println(replayFromServer);
							break;
						case 4:
//							create a new queue
							transitObject.protocolNum=randomNum;
							queueForServer.name="newName";
							transitObject.newQueue=queueForServer;
							transitObject.counter=counter;
							output.reset();
							output.writeObject(transitObject);
							output.flush();
							counter++;
							
//							condicion para sincronizar los objectos transferidos
							transitObject=(Protocol) input.readObject();
							while(true){
								if(transitObject.counter+1==counter){
									break;
								}else{
									transitObject=(Protocol) input.readObject();
								}
							}
							replayFromServer=transitObject.message;
							
							System.out.println(replayFromServer);
							break;
						case 5:
//							create a new message for a specific receiver
							transitObject.protocolNum=randomNum;
							forServer=new Message("Time made "+new Timestamp(System.currentTimeMillis()),this.username,"user_z");
							transitObject.newMessage=forServer;
							transitObject.counter=counter;
							output.reset();
							output.writeObject(transitObject);
							output.flush();
							counter++;
							
//							condicion para sincronizar los objectos transferidos
							transitObject=(Protocol) input.readObject();
							while(true){
								if(transitObject.counter+1==counter){
									break;
								}else{
									transitObject=(Protocol) input.readObject();
								}
							}
							replayFromServer=transitObject.message;
							
							System.out.println(replayFromServer);
							break;
						case 6:
//							delete queue
							transitObject.protocolNum=randomNum;
							queueForServer.name="newName";
							transitObject.newQueue=queueForServer;
							transitObject.counter=counter;
							output.reset();
							output.writeObject(transitObject);
							output.flush();
							counter++;
							
//							condicion para sincronizar los objectos transferidos
							transitObject=(Protocol) input.readObject();
							while(true){
								if(transitObject.counter+1==counter){
									break;
								}else{
									transitObject=(Protocol) input.readObject();
								}
							}
							replayFromServer=transitObject.message;
							
							System.out.println(replayFromServer);
							break;
					
					}
					randomNum=rand.nextInt(8);
					System.out.println("ACK option was "+randomNum);
				}
				
//				output.writeObject(randomNum);
//				messageServer=(String) input.readObject();
				replayFromServer="I am done!!";
				System.out.println(replayFromServer);
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
		String userName = "user_cool";

		
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
