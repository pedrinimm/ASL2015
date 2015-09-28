package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;

import entities.Message;
import entities.Protocol;
import entities.Queue;
import entities.User;

public class ClientHandler implements Runnable{

	
	protected Socket clientSocket = null;
    protected String serverText   = null;
    ObjectInputStream input;
    ObjectOutputStream output;
    
    //variable for the connection to the dtaase 
    Connection connection_1 = null;
    DatabaseConnectorServer connectingServer;
    
	public ClientHandler(Socket clientSocket, String serverText, Connection con){
		this.clientSocket = clientSocket;
        this.serverText   = serverText;
        this.connection_1=con;
	}
	 public void run(){
		 int counter=0;
		 try {
//			 	create the object protocol to understand what the client wants
			 	Protocol objectTransit=new Protocol(99,"");
//			 	read users username pendant
	            input  = new ObjectInputStream(clientSocket.getInputStream());
	            objectTransit = (Protocol) input.readObject();
	            counter=objectTransit.counter;
	            
	            int clientOption = objectTransit.protocolNum;
	            output = new ObjectOutputStream(clientSocket.getOutputStream());
	            //output.writeObject("Copy That");
	            long time = System.currentTimeMillis();
	           
	            while(clientOption!=7){
	            	System.out.println("Client option was "+clientOption);
	            	switch(clientOption){
	            		case 0:
//	            			read the message
	            			Message newMessage_y = database.GetMessage.execute_query(connection_1,objectTransit.userName);
	           	         
//	            			delete the message after reading
	            			database.DeleteMessage.execute_query(connection_1, newMessage_y.messageID.toString());	
	            			
//	            			return the message
	            			objectTransit.protocolNum=clientOption;
	            			objectTransit.counter=counter;
	            			objectTransit.newMessage=newMessage_y;
	            			
	            			output.reset();
	            			output.writeObject(objectTransit);
	            			output.flush();
	            			break;
	            		case 1:
//	            			reading special message for me
	            			Message newMessage_1 = database.GetOwnMessage.execute_query(connection_1, objectTransit.userName);
//	            			delete the message after reading
	            			database.DeleteMessage.execute_query(connection_1, newMessage_1.messageID.toString());
//	            			return the message
	            			objectTransit.protocolNum=clientOption;
	            			objectTransit.counter=counter;
	            			objectTransit.newMessage=newMessage_1;
	            			
	            			output.reset();
	            			output.writeObject(objectTransit);
	            			output.flush();
	            			
	            			break;
	            		case 2:
//	            			looking for a message from
	            			
	            			Message newMessage_x = database.GetMessageFrom.execute_query(connection_1,objectTransit.userName,
	            					objectTransit.newMessage.sender);
	         
//	            			delete the message after reading
	            			database.DeleteMessage.execute_query(connection_1, newMessage_x.messageID.toString());
	            			
	            			
//	            			return the message
	            			objectTransit.protocolNum=clientOption;
	            			objectTransit.counter=counter;
	            			objectTransit.newMessage=newMessage_x;
	            			
	            			output.reset();
	            			output.writeObject(objectTransit);
	            			output.flush();
	            			break;
	            		case 3:
//	            			create a message

//	            			Steps for creating a new message
//	            			read the message from the client
	            			Message newMessage=new Message("username");
	            			newMessage = objectTransit.newMessage;
//	            			Check if general queue Exists
	            			String queueID= database.GetQueue.execute_query(connection_1, "general");
	            			if(!queueID.equals("")){
	            				
	            				database.CreateNewMessage.execute_query(connection_1, newMessage, queueID);
	            				
	            				objectTransit.protocolNum=clientOption;
		            			objectTransit.counter=counter;
		            			objectTransit.message="Message created";
	            				
	            				output.reset();
	            				output.writeObject(objectTransit);
	            				output.flush();
	            			}else{//if doesn't exists create general queue
//	            				create general queue
	            				Queue newQueue=new Queue("general");
	            				database.CreateNewQueue.execute_query(connection_1, newQueue);
//	            				get the new queueid of the general queue
	            				queueID = database.GetQueue.execute_query(connection_1, "general");
//	            				after creating a new queue then insert message into the queue
	            				database.CreateNewMessage.execute_query(connection_1, newMessage, queueID);
	            				
	            				objectTransit.protocolNum=clientOption;
		            			objectTransit.counter=counter;
		            			objectTransit.message="Message created";
		            			
	            				output.reset();
	            				output.writeObject(objectTransit);
	            				output.flush();
	            			}
	            			break;
	            		case 4:
//	            			create a new queue
	            			
//	            			Check if general queue Exists
	            			String queueID_3= database.GetQueue.execute_query(connection_1, objectTransit.newQueue.name);
	            			if(!queueID_3.equals("")){
	            				objectTransit.protocolNum=clientOption;
		            			objectTransit.counter=counter;
		            			objectTransit.message="Queue Exist";
	            				
	            				
	            				output.reset();
	            				output.writeObject(objectTransit);
	            				output.flush();
	            			}else{//queue doesn't exist so must be created
//	            				create general queue
	            				Queue newQueue=new Queue(objectTransit.newQueue.name);
	            				database.CreateNewQueue.execute_query(connection_1, newQueue);
	            				
	            				objectTransit.protocolNum=clientOption;
		            			objectTransit.counter=counter;
		            			objectTransit.message="Queue Created";
		            			
		            			
	            				output.reset();
	            				output.writeObject(objectTransit);
	            				output.flush();
	            			}
	            			
	            			break;
	            		case 5:
//	            			create a new message for a specific receiver
	            			
//	            			Steps for creating a new message
//	            			read the message from the client
	            			Message newMessage_2=new Message("username");
	            			newMessage_2 = objectTransit.newMessage;
//	            			Check if general queue Exists
	            			String queueID_4= database.GetQueue.execute_query(connection_1, objectTransit.newQueue.name);
	            			if(!queueID_4.equals("")){
	            				database.CreateNewMessage.execute_query(connection_1, newMessage_2, queueID_4);
	            				
	            				objectTransit.protocolNum=clientOption;
		            			objectTransit.counter=counter;
		            			objectTransit.message="Message created";
	            				
	            				output.reset();
	            				output.writeObject(objectTransit);
	            				output.flush();
	            			}else{//if doesn't exists create general queue
//	            				create general queue
	            				Queue newQueue=new Queue(objectTransit.newQueue.name);
	            				database.CreateNewQueue.execute_query(connection_1, newQueue);
//	            				get the new queueid of the queue
	            				queueID_4 = database.GetQueue.execute_query(connection_1, objectTransit.newQueue.name);
//	            				after creating a new queue then insert message into the queue
	            				database.CreateNewMessage.execute_query(connection_1, newMessage_2, queueID_4);
	            				
	            				objectTransit.protocolNum=clientOption;
		            			objectTransit.counter=counter;
		            			objectTransit.message="Message created";
	            				
	            				output.reset();
	            				output.writeObject(objectTransit);
	            				output.flush();
	            			}
	            			break;
	            		case 6:
//	            			delete queue
//	            			Check if general queue Exists
	            			String queueID_2= database.GetQueue.execute_query(connection_1,objectTransit.newQueue.name);
	            			if(!queueID_2.equals("")){
//	            				delete all messages in that queue first
	            				database.DeleteMessagesInQueue.execute_query(connection_1, queueID_2);
//	            				then delete queue
	            				database.DeleteQueue.execute_query(connection_1, queueID_2);
	            				
	            				objectTransit.protocolNum=clientOption;
		            			objectTransit.counter=counter;
		            			objectTransit.message="Queue deleted";
		            			
		            			
	            				output.reset();
	            				output.writeObject(objectTransit);
	            				output.flush();
	            			}else{//if doesn't exists create general queue
	            				
	            				objectTransit.protocolNum=clientOption;
		            			objectTransit.counter=counter;
		            			objectTransit.message="Queue doesn't exist";
	            				
	            				output.reset();
	            				output.writeObject(objectTransit);
	            				output.flush();
	            			}
	            			break;
	            		case 99:
//	            			This means this is the first message from the client so we have to check
//	            			if the client already exists or if it's a new one
	            			
//	            			Step 1 check user in database
	            			String userID = database.GetUser.execute_query(connection_1, objectTransit.newUser.name);
	            			if(!userID.equals("")){
	            				System.out.println("this user already exist");
	            				
	            				objectTransit.protocolNum=clientOption;
		            			objectTransit.counter=counter;
		            			objectTransit.message="this user already exist";
	            				
	            				output.reset();
	            				output.writeObject(objectTransit);
	            				output.flush();
	            			}else{
	            				User newUser=objectTransit.newUser;
	            				database.CreateNewUser.execute_query(connection_1, newUser);
	            				
	            				objectTransit.protocolNum=clientOption;
		            			objectTransit.counter=counter;
		            			objectTransit.message="User created";
	            				
	            				output.reset();
	            				output.writeObject(objectTransit);
	            				output.flush();
	            			}
	            			break;
	            	}
	            	while(true){
	            		objectTransit = (Protocol) input.readObject();
	            		System.out.println("Counter "+counter+"  Protocol "+objectTransit.counter);
	            		if(counter!=objectTransit.counter){
	            			counter=objectTransit.counter;
	            			break;
	            		}
	            	}
	            	
        			clientOption = objectTransit.protocolNum;
	            	
	            	time = System.currentTimeMillis();
	            	System.out.println("Request processed: " + time);
	            }
	            
	        } catch (IOException | ClassNotFoundException e) {
	            //report exception somewhere.
	            e.printStackTrace();
	        }
		 System.out.println("Connection from client accepted "+clientSocket.getLocalAddress().getHostAddress());
		 close();
		 
	 }
	 private void close() {
			// try to close the connection
			try {
				if(output != null) output.close();
			}
			catch(Exception e) {}
			try {
				if(input != null) input.close();
			}
			catch(Exception e) {};
			try {
				if(clientSocket != null) clientSocket.close();
			}
			catch (Exception e) {}
		}
	
}
