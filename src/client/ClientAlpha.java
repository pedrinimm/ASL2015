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
import logger.LoggingSet;



import java.util.logging.Logger;







public class ClientAlpha implements Runnable{
	
	
//	Logger
//	private static final Logger log=LogManager.getRootLogger();
	

//	public static LoggingSet lg=new LoggingSet(ClientAlpha.class.getName());
//	public static final Logger logger=LoggingSet.getLogger();

//	This is for sending and receiving the object using the sockets 
	private ObjectInputStream input;		
	private ObjectOutputStream output;		
	protected int    clientPort   = 10033;
    protected Socket clientSocket = null;
	
    //Messages
	private String message_1="Oder ich uns ich kind eia wort. Schatz kommst te bilder worden an servus um warmer. Sto weste sagte her unten blieb ich guter wuchs. Fruh sto orte hof nein noch. Immer wu davon blick zu komme ruhen mu. Sag nachmittag ich sauberlich hausdacher kuchenture mag dus. "+
    "Denken freute ige storen vom gehort wer tat. Hochstens da schranken mudigkeit im polemisch. Orte wach zu wand muhe scho ab. Lehrlingen pa zu drechslers freundlich handarbeit aneinander brotkugeln.";
	private String message_2="Es betrubtes pa dammerung um plaudernd.";
//	These are the parameter for each client for connecting 
	private String server, username;
	private int port;
//	This is for stop the running of my client
	protected boolean      isStopped    = false;
//	Variables for experiments
	private static long waitingTime=0;
	private static int operationToPerform=-1;
	private static long durationTime=0;
	
	
	
	public ClientAlpha(String server, int port, String username) {
		//log_mes.setUseParentHandlers(false);
		this.server = server;
		this.port = port;
		this.username = username;
	}
	
	@Override
	public void run(){
		//--for measuring reasons 
		LoggingSet l_measure=new LoggingSet(ClientAlpha.class.getName()+"-tracing-"+this.username+"-");
		Logger log=l_measure.getLogger();
		//---end
		
//		Try to connect to the server
		try {
			log.info("Connection_Requested\t"+System.currentTimeMillis());
			clientSocket= new Socket(this.server,this.port);
			log.info("Connection_Accepted\t"+System.currentTimeMillis());
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
			log.info("Initialize_session\t"+transitObject.counter+"\t"+System.currentTimeMillis());
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
			log.info("Session_Initialized\t"+transitObject.counter+"\t"+System.currentTimeMillis());
			System.out.println(messageServer);
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Initialize running time variable 
		long endExecution = System.currentTimeMillis() + durationTime*1000;
		while(! isStopped()){
			if(operationToPerform==-1){
				randomNum=rand.nextInt(7);
			}else{
				randomNum=operationToPerform;
			}
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
							log.info("Request_Read_Message\t"+randomNum+"\t"+transitObject.counter+"\t"+System.currentTimeMillis());
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
							log.info("Respond_Read_Message\t"+randomNum+"\t"+transitObject.counter+"\t"+System.currentTimeMillis());
							System.out.println(forServer.messageID);
							break;
						case 1:
//							reading message for me 
							transitObject.protocolNum=randomNum;
							transitObject.userName=this.username;
							transitObject.counter=counter;
							output.reset();
							log.info("Request_Read_Message_for_me\t"+randomNum+"\t"+transitObject.counter+"\t"+System.currentTimeMillis());
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
							log.info("Respond_Read_Message_for_me\t"+randomNum+"\t"+transitObject.counter+"\t"+System.currentTimeMillis());
							
							System.out.println(forServer.messageID);
							break;
						case 2:
//							looking for message sent by someone 
							transitObject.protocolNum=randomNum;
							transitObject.userName=this.username;
							forServer.sender=PossibleUsers.getRandomUser().toString();
							transitObject.newMessage=forServer;
							transitObject.counter=counter;
							output.reset();
							log.info("Request_Read_Message_sent_by\t"+randomNum+"\t"+transitObject.counter+"\t"+System.currentTimeMillis());
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
							
							log.info("Respond_Read_Message_sent_by\t"+randomNum+"\t"+transitObject.counter+"\t"+System.currentTimeMillis());
							
							System.out.println(forServer.messageID);
							break;
						case 3:
//							create a new message
							transitObject.protocolNum=randomNum;
							forServer=new Message("Time made "+new Timestamp(System.currentTimeMillis()),this.username);
//							select which message to send
							if(rand.nextInt(2)==0){
								forServer.message=message_1;
							}else{
								forServer.message=message_2;
							}
							transitObject.newMessage=forServer;
							transitObject.counter=counter;
							output.reset();
							log.info("Request_Send_New_Message\t"+randomNum+"\t"+transitObject.counter+"\t"+System.currentTimeMillis());
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
					
							log.info("Respond_Send_New_Message\t"+randomNum+"\t"+transitObject.counter+"\t"+System.currentTimeMillis());
							
							
							System.out.println(replayFromServer);
							break;
						case 4:
//							create a new queue
							transitObject.protocolNum=randomNum;
							queueForServer.name=PossibleQueues.getRandomQueue().toString();
							transitObject.newQueue=queueForServer;
							transitObject.counter=counter;
							output.reset();
							log.info("Request_Create_Queue\t"+randomNum+"\t"+transitObject.counter+"\t"+System.currentTimeMillis());
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
							
							log.info("Respond_Create_Queue\t"+randomNum+"\t"+transitObject.counter+"\t"+System.currentTimeMillis());
							
							System.out.println(replayFromServer);
							break;
						case 5:
//							create a new message for a specific receiver
							transitObject.protocolNum=randomNum;
							forServer=new Message("Time made "+new Timestamp(System.currentTimeMillis()),this.username,PossibleUsers.getRandomUser().toString());
//							select which message to send
							if(rand.nextInt(2)==0){
								forServer.message=message_1;
							}else{
								forServer.message=message_2;
							}
							transitObject.newMessage=forServer;
							transitObject.counter=counter;
							transitObject.newQueue.name=PossibleQueues.getRandomQueue().toString();
							output.reset();
							log.info("Request_Create_Messge_to \t"+randomNum+"\t"+transitObject.counter+"\t"+System.currentTimeMillis());
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
							log.info("Respond_Create_Messge_to \t"+randomNum+"\t"+transitObject.counter+"\t"+System.currentTimeMillis());
							System.out.println(replayFromServer);
							break;
						case 6:
//							delete queue
							transitObject.protocolNum=randomNum;
							queueForServer.name="newName";
							transitObject.newQueue=queueForServer;
							transitObject.counter=counter;
							output.reset();
							log.info("Request_Delete_Queue\t"+randomNum+"\t"+transitObject.counter+"\t"+System.currentTimeMillis());
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
							log.info("Request_Delete_Queue\t"+randomNum+"\t"+transitObject.counter+"\t"+System.currentTimeMillis());
							System.out.println(replayFromServer);
							break;
					
					}
					if(operationToPerform==-1){
						randomNum=rand.nextInt(7);
						System.out.println("ACK option was "+randomNum);
					}else{
						System.out.println("ACK option was "+randomNum);
					}
//					Sleeping time for the next operation
					long start = System.currentTimeMillis();
					long end = start + waitingTime*1000; // 60 seconds * 1000 ms/sec
					while (System.currentTimeMillis() < end)
					{
					    // run
					}
					if(System.currentTimeMillis()>endExecution){
//						System.out.println("Cheking "+System.currentTimeMillis()+"stop at "+e);
//						send last message
						randomNum=7;
						transitObject.counter=counter;
						transitObject.protocolNum=randomNum;
						output.reset();
						output.writeObject(transitObject);
						output.flush();
						counter++;
					}
				}

//				try {
//					Thread.sleep(waitingTime*1000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
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
			case 6:
				waitingTime=Integer.parseInt(args[5]);
				operationToPerform=Integer.parseInt(args[4]);
				serverAddress = args[3];
				portNumber= Integer.parseInt(args[2]);
				userName=args[1];
				durationTime=Long.parseLong(args[0]);
				break;
			case 5:
	
				operationToPerform=Integer.parseInt(args[4]);
				serverAddress = args[3];
				portNumber= Integer.parseInt(args[2]);
				userName=args[1];
				durationTime=Long.parseLong(args[0]);
				break;
			case 4:

				serverAddress = args[3];
				portNumber= Integer.parseInt(args[2]);
				userName=args[1];
				durationTime=Long.parseLong(args[0]);
				break;
			case 3:

				portNumber= Integer.parseInt(args[2]);
				userName=args[1];
				durationTime=Long.parseLong(args[0]);
				break;
			case 2:
				try {
					portNumber = Integer.parseInt(args[1]);
				}
				catch(Exception e) {
					System.out.println("Invalid port number.");
					System.out.println("Usage is: > java Client [dauration] [username] [portNumber] [serverAddress] [operation] [waiting time in seconds]");
					return;
				}
				break;
			case 1: 
				userName = args[0];
				break;
			case 0:
				break;
			default:
				System.out.println("Usage is: > java Client [dauration] [username] [portNumber] [serverAddress] [operation] [waiting time in seconds]");
			return;
		}
		ClientAlpha client = new ClientAlpha(serverAddress, portNumber, userName);
		new Thread(client).start();
		//System.out.println("Stopping Clients");
		//client.stop();
	}

}
