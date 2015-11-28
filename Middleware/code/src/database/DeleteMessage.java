package database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;


import server.DatabaseConnectorServer;

public class DeleteMessage {
	//This string contains the name of the store procedure in the database
	public static final String callableFunction = "{call delete_message(?)}";

//	for testing the databse connection
	public static Connection connection_1 = null;
	public static DatabaseConnectorServer connectingServer;
	
	public synchronized static String execute_query(Connection con,  String message_id){
		
		CallableStatement callFunction =null;
		
		try{
			
			//checking if the connection that is returning is not closed
			if(!con.isClosed()){
//				System.out.println("conencted!!");
				
				callFunction = con.prepareCall(callableFunction);
				
				//setting the parameter for the callable function
				callFunction.setString(1, message_id);
				
				callFunction.execute();
				ResultSet result= callFunction.getResultSet();
				result.next();
				System.out.println(result.getString(1));
				return result.getString(1);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			//close the database connection
			if(callFunction!=null){
				try {
					callFunction.close();
//					System.out.println("Call function closed");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		connectingServer=new DatabaseConnectorServer();
		connectingServer.setupDatabaseConnectionPool("postgres", "squirrel","localhost", "messaging", 100,9000);
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
		
		
//		get message from db
		
		String returning = database.DeleteMessage.execute_query(connection_1, "247ca59e-68a9-4241-9eff-3e210ea06408");
		System.out.println(returning);

	}

}