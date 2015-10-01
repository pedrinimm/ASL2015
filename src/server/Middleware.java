package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Middleware implements Runnable{

	protected int          serverPort   = 8090;
    protected ServerSocket serverSocket = null;
    protected boolean      isStopped    = false;
    protected Thread       runningThread= null;
    protected ExecutorService threadPool = Executors.newFixedThreadPool(10);
    
//    variables to get connection to the database
    DatabaseConnectorServer connectingServer;
	Connection connection_1 = null;
	
    
    public Middleware(int port){
    	this.serverPort = port;
    	connectingServer=new DatabaseConnectorServer();
		connectingServer.setupDatabaseConnectionPool("postgres", "squirrel","localhost", "messaging", 100);
    }
    @Override
	public void run(){
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }
        openDatabaseConnection();
        openServerSocket();
        while(! isStopped()){
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if(isStopped()) {
                    System.out.println("Server Stopped.") ;
                    break;
                }
                throw new RuntimeException(
                    "Error accepting client connection", e);
            }
            this.threadPool.execute(new ClientHandler(clientSocket,"Thread Pooled Server",connection_1));
//            System.out.println("Number of clients: ");
            
        }
        this.threadPool.shutdown();
        System.out.println("Server Stopped.") ;
    }

    
    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop(){
        this.isStopped = true;
      //close the database connection
		if(connection_1!=null){
			try {
				connection_1.close();
				System.out.println("Connection closed");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }
    private void openDatabaseConnection(){
    	try{
			connection_1=connectingServer.getDatabaseConnection();
			//checking if the connection that is returning is not closed
			if(!connection_1.isClosed()){
				System.out.println("conencted to database!!");
				//the parameter in the print if the call of the method
				//inside that class that calls the store procedure
//				System.out.println(database.GetUser.execute_query(connection_1,"user_1"));
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port "+this.serverPort+" ", e);
        }
    }

    
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Middleware server = new Middleware(9000);
		new Thread(server).start();

//		try {
//		    Thread.sleep(20 * 1000);
//		} catch (InterruptedException e) {
//		    e.printStackTrace();
//		}
		//System.out.println("Stopping Server");
		//server.stop();

	}

}
