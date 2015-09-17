package server;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

import org.postgresql.ds.PGPoolingDataSource;

public class DatabaseConnectorServer {
	
	//pool of connections for the database
	private final PGPoolingDataSource connectionPool;
	
	//constructor of the class
	public DatabaseConnectorServer(){
		
		connectionPool= new PGPoolingDataSource();
		
	}
	
	//method for setting up the connection to the database
	public void setupDatabaseConnectionPool(String username,
            String password, String server, String database, int maxConnections) {
        		connectionPool.setUser(username);
        		connectionPool.setPassword(password);
        		connectionPool.setServerName(server);
        		connectionPool.setDatabaseName(database);
        		connectionPool.setMaxConnections(maxConnections);
    }
	
	//method for requesting a new connection to the database
	public Connection getDatabaseConnection(){
		Connection con;
		try {
			con = connectionPool.getConnection();
			//with auto-commit on false no instruction will be applied until I set commit
			//manually
			con.setAutoCommit(false);
	        return con;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	//method to close the connection with the database
	public void closeConnection(){
		connectionPool.close();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int ConnectionCapacity;
		DatabaseConnectorServer connectingServer;
		Connection connection_1;
		
		connectingServer=new DatabaseConnectorServer();
		connectingServer.setupDatabaseConnectionPool("postgres", "squirrel","localhost", "messaging", 100);
		
		
		try{
			connection_1=connectingServer.getDatabaseConnection();
			//cheing if the connection that is retunring is not closed
			if(!connection_1.isClosed()){
				System.out.println("conencted!!");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
