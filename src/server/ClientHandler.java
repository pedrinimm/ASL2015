package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable{

	
	protected Socket clientSocket = null;
    protected String serverText   = null;
    
	public ClientHandler(Socket clientSocket, String serverText){
		this.clientSocket = clientSocket;
        this.serverText   = serverText;
	}
	 public void run(){
		 try {
	            ObjectInputStream input  = new ObjectInputStream(clientSocket.getInputStream());
	            int clientOption = (Integer) input.readObject();
	            ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
	            output.writeObject("Copy That");
	            long time = System.currentTimeMillis();
	            while(clientOption!=7){
	            	clientOption = (Integer) input.readObject();
	            	output.writeObject("Copy That");
	            	time = System.currentTimeMillis();
	            	System.out.println("Request processed: " + time);
	            }
	            
	        } catch (IOException | ClassNotFoundException e) {
	            //report exception somewhere.
	            e.printStackTrace();
	        }
		 System.out.println("Connection from client accepted "+clientSocket.getLocalAddress().getHostAddress());
	 }
	
}
