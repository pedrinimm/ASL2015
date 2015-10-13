package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import logger.LoggingSet;



public class Middleware implements Runnable{

	protected int          serverPort   = 8090;
    protected ServerSocket serverSocket = null;
    protected boolean      isStopped    = false;
    protected Thread       runningThread= null;
    protected ExecutorService threadPool;
    
//	Logger
//	private static final Logger log=LogManager.getRootLogger();
//	public static LoggingSet lg=new LoggingSet(Middleware.class.getName());
//	public static final Logger logger=LoggingSet.getLogger();
	//--for measuring reasons 
	public static LoggingSet l_measure=new LoggingSet(Middleware.class.getName()+"-tracing-");
	public static final Logger log=l_measure.getLogger();
	//---end
    
//    variables to get connection to the database
    DatabaseConnectorServer connectingServer;
	Connection connection_1 = null;
	
//	for timing control
//	private static long durationTime=30;
	
    
    public Middleware(int port){
    	this.serverPort = port;
    	connectingServer=new DatabaseConnectorServer();
		connectingServer.setupDatabaseConnectionPool("postgres", "squirrel","localhost", "messaging", 100, 9000);
		threadPool = Executors.newFixedThreadPool(1);
    }
    public Middleware(String dbServer,String dbName,String dbUser,String dbPassword,int noConnections,int port){
    	this.serverPort = port+1;
//    	this.durationTime=timing;
    	connectingServer=new DatabaseConnectorServer();
		connectingServer.setupDatabaseConnectionPool(dbUser, dbPassword,dbServer, dbName, noConnections, port);
		threadPool = Executors.newFixedThreadPool(noConnections);
    }
    @Override
	public void run(){
    	//Initialize running time variable 
//    	long endExecution = System.currentTimeMillis() + durationTime*1000;
//    	log.info("System_Running\t"+System.currentTimeMillis());
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }
        openDatabaseConnection();
        openServerSocket();
        int counter=0;
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
            this.threadPool.execute(new ClientHandler(clientSocket,"Thread Pooled Server",connection_1,counter++));
//            System.out.println("Number of clients: ");
//            if(System.currentTimeMillis()>endExecution){
//            	System.out.println("Time to shutdown");
//            	this.stop();
//            }
            
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
		String dbServer="";
		String dbUser="";
		String dbPassword="";
		String dbName="";
		int noConnections=0;
		int port=0;
//		long time=5;
		
		Middleware server;
		
//		This switch is used to determine how to initialize the client
//		based on the number of arguments 
		switch(args.length) {
			case 6:
				dbServer=args[0];
				dbName=args[1];
				dbUser=args[2];
				dbPassword=args[3];
				noConnections=Integer.parseInt(args[4]);
				port=Integer.parseInt(args[5]);
//				time=Long.parseLong(args[6]);
				server = new Middleware(dbServer,dbName,dbUser,dbPassword,noConnections,port);
				new Thread(server).start();
				break;
			case 1:
				port=Integer.parseInt(args[0]);
				server = new Middleware(port);
				new Thread(server).start();
				break;
		}
		
		

//		try {
//		    Thread.sleep(20 * 1000);
//		} catch (InterruptedException e) {
//		    e.printStackTrace();
//		}
		//System.out.println("Stopping Server");
		//server.stop();

	}

}
