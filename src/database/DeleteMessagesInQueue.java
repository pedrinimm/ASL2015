package database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeleteMessagesInQueue {
	//This string contains the name of the store procedure in the database
		public static final String callableFunction = "{call delete_messages_from_queue(?)}";
		
		
		public synchronized static String execute_query(Connection con, String queue_id){
			
			CallableStatement callFunction =null;
			
			try{
				
				//checking if the connection that is returning is not closed
				if(!con.isClosed()){
//					System.out.println("conencted!!");
					
					callFunction = con.prepareCall(callableFunction);
					
					//setting the parameter for the callable function
					callFunction.setString(1, queue_id);
					
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
//						System.out.println("Call function closed");
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
