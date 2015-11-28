package entities;

import java.util.UUID;

public class User implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String name;
	public UUID userId;

		
		
		public User(){
			this.name="";
			this.userId=UUID.randomUUID();
		}
		public User(String userName){
			this.name=userName;
			this.userId=UUID.randomUUID();

		}
		public User(String userName,String userID){
			this.name=userName;
			this.userId=UUID.fromString(userID);

		}
		
		public String getName(){
			return this.name;
		}

	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
