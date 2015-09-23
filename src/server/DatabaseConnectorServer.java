package server;

//import java.sql.CallableStatement;
import java.sql.Connection;
//import java.sql.ResultSet;
import java.sql.SQLException;


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
			con.setAutoCommit(true);
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
		
		
		DatabaseConnectorServer connectingServer;
		Connection connection_1 = null;
		
		connectingServer=new DatabaseConnectorServer();
		connectingServer.setupDatabaseConnectionPool("postgres", "squirrel","localhost", "messaging", 100);
	
		
		try{
			connection_1=connectingServer.getDatabaseConnection();
			//checking if the connection that is returning is not closed
			if(!connection_1.isClosed()){
				System.out.println("conencted to database!!");
				//the parameter in the print if the call of the method
				//inside that class that calls the store procedure
				System.out.println(database.GetUser.execute_query(connection_1,"user_1"));
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
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
		}
		
	}

}
