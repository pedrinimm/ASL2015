package database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;


import entities.Message;
import entities.Queue;
import server.DatabaseConnectorServer;

public class CreateNewMessage {
		//This string contains the name of the store procedure in the database
		public static final String callableFunction = "{call insert_new_message(?,?,?,?,?,?)}";
		
//		for testing the databse connection
		public static Connection connection_1 = null;
		public static DatabaseConnectorServer connectingServer;
		
		public synchronized static String execute_query(Connection con, Message newMessage, String queueID){
			CallableStatement callFunction = null;
			
			try{
				
				//checking if the connection that is returning is not closed
				if(!con.isClosed()){
					System.out.println("conencted!!");
					
					//prepare callable function
					callFunction =con.prepareCall(callableFunction);
					//setting the parameter for the callable function
					callFunction.setString(1, newMessage.message);
					callFunction.setString(2, newMessage.sender);
					callFunction.setString(3, newMessage.reciever);
					callFunction.setString(4, newMessage.messageID.toString());
					callFunction.setString(5, newMessage.timestamp.toString());
					callFunction.setString(6, queueID);
					
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
						System.out.println("Call function closed");
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
//		create new message
		Message newMessage=new Message("sender_1");
		String queueID="";
		queueID = database.GetQueue.execute_query(connection_1, "general");
		if(!queueID.equals("")){
			database.CreateNewMessage.execute_query(connection_1, newMessage, queueID);
		}else{
//			create general queue
			Queue newQueue=new Queue("general");
			database.CreateNewQueue.execute_query(connection_1, newQueue);
//			get the new queueid of the general queue
			queueID = database.GetQueue.execute_query(connection_1, "general");
//			after creating a new queue then insert message into the queue
			database.CreateNewMessage.execute_query(connection_1, newMessage, queueID);
		}

	}

}
