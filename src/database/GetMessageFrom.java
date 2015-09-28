package database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

import entities.Message;
import server.DatabaseConnectorServer;

public class GetMessageFrom {
	//This string contains the name of the store procedure in the database
			public static final String callableFunction = "{call get_message_from(?,?)}";
			
//			for testing the databse connection
			public static Connection connection_1 = null;
			public static DatabaseConnectorServer connectingServer;
			
			public synchronized static Message execute_query(Connection con, String receiver, String sender){
				CallableStatement callFunction = null;
				Message newMessage=new Message();
				try{
					
					//checking if the connection that is returning is not closed
					if(!con.isClosed()){
//						System.out.println("conencted!!");
						
						//prepare callable function
						callFunction =con.prepareCall(callableFunction);
						
						//setting the parameter for the callable function
						callFunction.setString(1, receiver);
						callFunction.setString(2, sender);
						
						callFunction.execute();
						ResultSet result= callFunction.getResultSet();
						result.next();
						if(result.getInt(7)==1){
							System.out.println("No message found!");
						}else{
							System.out.println(result.getString(1));
							newMessage.message=result.getString(1);
							newMessage.sender=result.getString(2);
							newMessage.reciever=result.getString(3);
							newMessage.messageID=UUID.fromString(result.getString(4));
							newMessage.timestamp= Timestamp.valueOf(result.getString(5));
						}
						
						
						
						return newMessage;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					//close the database connection
					if(callFunction!=null){
						try {
							callFunction.close();
//							System.out.println("Call function closed");
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
//						System.out.println(database.GetUser.execute_query(connection_1,"user_1"));
						
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
//				get message from db
				Message newMessage=new Message();
				newMessage = database.GetMessageFrom.execute_query(connection_1, "perritos","sender_2");
				System.out.println(newMessage.message);
			}
		}
