package database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import entities.Protocol;
import entities.User;
import server.DatabaseConnectorServer;



public class CreateNewUser {
	//This string contains the name of the store procedure in the database
	public static final String callableFunction = "{call insert_new_user(?,?)}";

	
	//	for testing the databse connection
	public static Connection connection_1 = null;
	public static DatabaseConnectorServer connectingServer;
	
	public synchronized static String execute_query(Connection con, User newUser){
		CallableStatement callFunction = null;
		
		try{
			
			//checking if the connection that is returning is not closed
			if(!con.isClosed()){
//				System.out.println("conencted!!");
				
				//prepare callable function
				callFunction =con.prepareCall(callableFunction);
				//setting the parameter for the callable function
				callFunction.setString(1, newUser.name);
				callFunction.setString(2, newUser.userId.toString());

				
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
		User client = new User("user_z");
		Protocol objectTransit= new Protocol(99,client,client.name);
		
		connectingServer=new DatabaseConnectorServer();
		connectingServer.setupDatabaseConnectionPool("postgres", "squirrel","localhost", "messaging", 100);
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
		
		
//		Step 1 check user in database
		String userID = database.GetUser.execute_query(connection_1, objectTransit.newUser.name);
		
		if(!userID.equals("")){
			System.out.println("this user already exist");

		}else{
			User newUser=objectTransit.newUser;
			database.CreateNewUser.execute_query(connection_1, newUser);

		}
	}

}
