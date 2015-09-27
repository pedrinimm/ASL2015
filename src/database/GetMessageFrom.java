package database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.UUID;

import entities.Message;

public class GetMessageFrom {
	//This string contains the name of the store procedure in the database
			public static final String callableFunction = "{call get_message_from(?,?)}";
			
			public synchronized static Message execute_query(Connection con, String receiver, String sender){
				CallableStatement callFunction = null;
				Message newMessage=new Message();
				try{
					
					//checking if the connection that is returning is not closed
					if(!con.isClosed()){
						System.out.println("conencted!!");
						
						//prepare callable function
						callFunction =con.prepareCall(callableFunction);
						
						//setting the parameter for the callable function
						callFunction.setString(1, receiver);
						callFunction.setString(2, sender);
						
						callFunction.execute();
						ResultSet result= callFunction.getResultSet();
						result.next();
						System.out.println(result.getString(1));
						newMessage.message=result.getString(1);
						newMessage.sender=result.getString(2);
						newMessage.reciever=result.getString(3);
						newMessage.messageID=UUID.fromString(result.getString(4));
						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
					    Date parsedDate = (Date) dateFormat.parse(result.getString(5));
						newMessage.timestamp= new Timestamp(parsedDate.getTime());
						
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

			}
		}
