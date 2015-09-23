package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;

import entities.Message;
import entities.Queue;

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
		 try {
			 
//			 	read users username pendant
	            input  = new ObjectInputStream(clientSocket.getInputStream());
	            int clientOption = (Integer) input.readObject();
	            output = new ObjectOutputStream(clientSocket.getOutputStream());
	            output.writeObject("Copy That");
	            long time = System.currentTimeMillis();
	            while(clientOption!=7){
	            	clientOption = (Integer) input.readObject();
	            	switch(clientOption){
	            		case 0:
//	            			for reading message
	            			break;
	            		case 1:
//	            			reading special message for me
	            			break;
	            		case 2:
//	            			looking for a message from
	            			break;
	            		case 3:
//	            			create a message

//	            			Steps for creating a new message
//	            			read the message from the client
	            			Message newMessage=new Message("username");
	            			newMessage = (Message) input.readObject();
//	            			Check if general queue Exists
	            			String queueID= database.GetQueue.execute_query(connection_1, "general");
	            			if(!queueID.equals("")){
	            				database.CreateNewMessage.execute_query(connection_1, newMessage, queueID);
	            			}else{//if doesn't exists create general queue
//	            				create general queue
	            				Queue newQueue=new Queue("general");
	            				database.CreateNewQueue.execute_query(connection_1, newQueue);
//	            				get the new queueid of the general queue
	            				queueID = database.GetQueue.execute_query(connection_1, "general");
//	            				after creating a new queue then insert message into the queue
	            				database.CreateNewMessage.execute_query(connection_1, newMessage, queueID);
	            			}
	            			break;
	            		case 4:
//	            			create a new queue
	            			break;
	            		case 5:
//	            			create a new message for a specific receiver
	            			break;
	            		case 6:
//	            			delete queue
	            			break;
	            			
	            		
	            	}
	            	output.writeObject("Copy That");
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
