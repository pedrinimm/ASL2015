package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.util.logging.Logger;

import entities.Message;
import entities.Protocol;
import entities.Queue;
import entities.User;
import logger.LoggingSet;

public class ClientHandler implements Runnable{

	
	protected Socket clientSocket = null;
    protected String serverText   = null;
    ObjectInputStream input;
    ObjectOutputStream output;
    int idL;
    //variable for the connection to the dtaase 
    Connection connection_1 = null;
    DatabaseConnectorServer connectingServer;
    

      
    
	public ClientHandler(Socket clientSocket, String serverText, Connection con,int idC){
		this.clientSocket = clientSocket;
        this.serverText   = serverText;
        this.connection_1=con;
        this.idL=idC;
	}
	 @Override
	public void run(){
		  //--for measuring reasons 
		 LoggingSet l_measure=new LoggingSet(ClientHandler.class.getName()+"-tracing-"+this.idL+"-");
		
		 Logger log=l_measure.getLogger();
		 
		 
		  	//---end
		 log.info("System_Running\t"+System.currentTimeMillis());
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
	            			log.info("Request_Read_Message\t"+clientOption+"\t"+objectTransit.counter+"\t"+System.currentTimeMillis());
//	            			read the message
	            			log.info("Request_Read_Message\t"+"db\t"+clientOption+"\t"+objectTransit.counter+"\t"+System.currentTimeMillis());
	            			Message newMessage_y = database.GetMessage.execute_query(connection_1,objectTransit.userName);
	           	         
//	            			delete the message after reading
	            			database.DeleteMessage.execute_query(connection_1, newMessage_y.messageID.toString());	
	            			log.info("Respond_Read_Message\t"+"db\t"+clientOption+"\t"+objectTransit.counter+"\t"+System.currentTimeMillis());
//	            			return the message
	            			objectTransit.protocolNum=clientOption;
	            			objectTransit.counter=counter;
	            			objectTransit.newMessage=newMessage_y;
	            			
	            			output.reset();
	            			output.writeObject(objectTransit);
	            			output.flush();
	            			log.info("Respond_Read_Message\t"+clientOption+"\t"+objectTransit.counter+"\t"+System.currentTimeMillis());
	            			break;
	            		case 1:
	            			log.info("Request_Read_Message_for_me\t"+clientOption+"\t"+objectTransit.counter+"\t"+System.currentTimeMillis());
//	            			reading special message for me
	            			log.info("Request_Read_Message_for_me\t"+"db\t"+clientOption+"\t"+objectTransit.counter+"\t"+System.currentTimeMillis());
	            			Message newMessage_1 = database.GetOwnMessage.execute_query(connection_1, objectTransit.userName);
//	            			delete the message after reading
	            			database.DeleteMessage.execute_query(connection_1, newMessage_1.messageID.toString());
	            			log.info("Respond_Read_Message\t"+"db\t"+clientOption+"\t"+objectTransit.counter+"\t"+System.currentTimeMillis());
//	            			return the message
	            			objectTransit.protocolNum=clientOption;
	            			objectTransit.counter=counter;
	            			objectTransit.newMessage=newMessage_1;
	            			
	            			output.reset();
	            			output.writeObject(objectTransit);
	            			output.flush();
	            			log.info("Respond_Read_Message\t"+clientOption+"\t"+objectTransit.counter+"\t"+System.currentTimeMillis());
	            			break;
	            		case 2:
	            			log.info("Request_Read_Message_sent_by\t"+clientOption+"\t"+objectTransit.counter+"\t"+System.currentTimeMillis());
//	            			looking for a message from
	            			log.info("Request_Read_Message_sent_by\t"+"db\t"+clientOption+"\t"+objectTransit.counter+"\t"+System.currentTimeMillis());
	            			Message newMessage_x = database.GetMessageFrom.execute_query(connection_1,objectTransit.userName,
	            					objectTransit.newMessage.sender);
	         
//	            			delete the message after reading
	            			database.DeleteMessage.execute_query(connection_1, newMessage_x.messageID.toString());
	            			log.info("Respond_Read_Message_sent_by\t"+"db\t"+clientOption+"\t"+objectTransit.counter+"\t"+System.currentTimeMillis());
	            			
//	            			return the message
	            			objectTransit.protocolNum=clientOption;
	            			objectTransit.counter=counter;
	            			objectTransit.newMessage=newMessage_x;
	            			
	            			output.reset();
	            			output.writeObject(objectTransit);
	            			output.flush();
	            			log.info("Respond_Read_Message_sent_by\t"+clientOption+"\t"+objectTransit.counter+"\t"+System.currentTimeMillis());
	            			break;
	            		case 3:
	            			log.info("Request_Send_New_Message\t"+clientOption+"\t"+objectTransit.counter+"\t"+System.currentTimeMillis());
//	            			create a message

//	            			Steps for creating a new message
//	            			read the message from the client
	            			Message newMessage=new Message("username");
	            			newMessage = objectTransit.newMessage;
//	            			Check if general queue Exists
	            			log.info("Request_Send_New_Message\t"+"db\t"+clientOption+"\t"+objectTransit.counter+"\t"+System.currentTimeMillis());
	            			String queueID= database.GetQueue.execute_query(connection_1, "general");
	            			if(!queueID.equals("")){
	            				
	            				database.CreateNewMessage.execute_query(connection_1, newMessage, queueID);
	            				log.info("Respond_Send_New_Message\t"+"db\t"+clientOption+"\t"+objectTransit.counter+"\t"+System.currentTimeMillis());
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
	            				log.info("Respond_Send_New_Message\t"+"db\t"+clientOption+"\t"+objectTransit.counter+"\t"+System.currentTimeMillis());
	            				objectTransit.protocolNum=clientOption;
		            			objectTransit.counter=counter;
		            			objectTransit.message="Message created";
		            			
	            				output.reset();
	            				output.writeObject(objectTransit);
	            				output.flush();
	            			}
	            			log.info("Respond_Send_New_Message\t"+clientOption+"\t"+objectTransit.counter+"\t"+System.currentTimeMillis());
	            			break;
	            		case 4:
	            			log.info("Request_Create_Queue\t"+clientOption+"\t"+objectTransit.counter+"\t"+System.currentTimeMillis());
//	            			create a new queue
	            			
//	            			Check if general queue Exists
	            			log.info("Request_Create_Queue\t"+"db\t"+clientOption+"\t"+objectTransit.counter+"\t"+System.currentTimeMillis());
	            			String queueID_3= database.GetQueue.execute_query(connection_1, objectTransit.newQueue.name);
	            			if(!queueID_3.equals("")){
	            				log.info("Respond_Create_Queue\t"+"db\t"+clientOption+"\t"+objectTransit.counter+"\t"+System.currentTimeMillis());
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
	            				log.info("Respond_Create_Queue\t"+"db\t"+clientOption+"\t"+objectTransit.counter+"\t"+System.currentTimeMillis());
	            				objectTransit.protocolNum=clientOption;
		            			objectTransit.counter=counter;
		            			objectTransit.message="Queue Created";
		            			
		            			
	            				output.reset();
	            				output.writeObject(objectTransit);
	            				output.flush();
	            			}
	            			log.info("Respond_Create_Queue\t"+clientOption+"\t"+objectTransit.counter+"\t"+System.currentTimeMillis());
	            			break;
	            		case 5:
	            			log.info("Request_Create_Messge_to\t"+clientOption+"\t"+objectTransit.counter+"\t"+System.currentTimeMillis());
//	            			create a new message for a specific receiver
	            			
//	            			Steps for creating a new message
//	            			read the message from the client
	            			Message newMessage_2=new Message("username");
	            			newMessage_2 = objectTransit.newMessage;
//	            			initialize queue of the object transit ---is going nulll 
//	            			objectTransit.newQueue=new Queue("newQueue");
	            			
//	            			Check if general queue Exists
	            			log.info("Request_Create_Messge_to\t"+"db\t"+clientOption+"\t"+objectTransit.counter+"\t"+System.currentTimeMillis());
	            			String queueID_4= database.GetQueue.execute_query(connection_1, objectTransit.newQueue.name);
	            			if(!queueID_4.equals("")){
	            				database.CreateNewMessage.execute_query(connection_1, newMessage_2, queueID_4);
	            				log.info("Respond_Create_Messge_to\t"+"db\t"+clientOption+"\t"+objectTransit.counter+"\t"+System.currentTimeMillis());
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
	            				log.info("Respond_Create_Messge_to\t"+"db\t"+clientOption+"\t"+objectTransit.counter+"\t"+System.currentTimeMillis());
	            				objectTransit.protocolNum=clientOption;
		            			objectTransit.counter=counter;
		            			objectTransit.message="Message created";
	            				
	            				output.reset();
	            				output.writeObject(objectTransit);
	            				output.flush();
	            			}
	            			log.info("Respond_Create_Messge_to\t"+clientOption+"\t"+objectTransit.counter+"\t"+System.currentTimeMillis());
	            			break;
	            		case 6:
	            			log.info("Request_Delete_Queue\t"+clientOption+"\t"+objectTransit.counter+"\t"+System.currentTimeMillis());
//	            			delete queue
//	            			Check if general queue Exists
	            			log.info("Request_Delete_Queue\t"+"db\t"+clientOption+"\t"+objectTransit.counter+"\t"+System.currentTimeMillis());
	            			String queueID_2= database.GetQueue.execute_query(connection_1,objectTransit.newQueue.name);
	            			if(!queueID_2.equals("")){
//	            				delete all messages in that queue first
	            				database.DeleteMessagesInQueue.execute_query(connection_1, queueID_2);
//	            				then delete queue
	            				database.DeleteQueue.execute_query(connection_1, queueID_2);
	            				log.info("Request_Delete_Queue\t"+"db\t"+clientOption+"\t"+objectTransit.counter+"\t"+System.currentTimeMillis());
	            				objectTransit.protocolNum=clientOption;
		            			objectTransit.counter=counter;
		            			objectTransit.message="Queue deleted";
		            			
		            			
	            				output.reset();
	            				output.writeObject(objectTransit);
	            				output.flush();
	            			}else{//if doesn't exists create general queue
	            				log.info("Request_Delete_Queue\t"+"db\t"+clientOption+"\t"+objectTransit.counter+"\t"+System.currentTimeMillis());
	            				objectTransit.protocolNum=clientOption;
		            			objectTransit.counter=counter;
		            			objectTransit.message="Queue doesn't exist";
	            				
	            				output.reset();
	            				output.writeObject(objectTransit);
	            				output.flush();
	            			}
	            			log.info("Request_Delete_Queue\t"+clientOption+"\t"+objectTransit.counter+"\t"+System.currentTimeMillis());
	            			break;
	            		case 99:
	            			log.info("Initialize_session\t"+clientOption+"\t"+objectTransit.counter+"\t"+System.currentTimeMillis());
//	            			This means this is the first message from the client so we have to check
//	            			if the client already exists or if it's a new one
	            			
//	            			Step 1 check user in database
	            			log.info("Initialize_session\t"+"db\t"+clientOption+"\t"+objectTransit.counter+"t"+System.currentTimeMillis());
	            			String userID = database.GetUser.execute_query(connection_1, objectTransit.newUser.name);
	            			if(!userID.equals("")){
	            				log.info("Session_Initialized\t"+"db\t"+clientOption+"\t"+objectTransit.counter+"\t"+System.currentTimeMillis());
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
	            				log.info("Session_Initialized\t"+"db\t"+clientOption+"\t"+objectTransit.counter+"\t"+System.currentTimeMillis());
	            				objectTransit.protocolNum=clientOption;
		            			objectTransit.counter=counter;
		            			objectTransit.message="User created";
	            				
	            				output.reset();
	            				output.writeObject(objectTransit);
	            				output.flush();
	            			}
	            			log.info("Session_Initialized\t"+clientOption+"\t"+objectTransit.counter+"\t"+System.currentTimeMillis());
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
