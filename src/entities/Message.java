package entities;

import java.sql.Timestamp;
import java.util.UUID;

public class Message {
	public String message;
	public Timestamp timestamp;
	public String sender;
	public String reciever;
	public UUID messageID;
	
	public Message(String message,String sender,String reciever){
		this.message=message;
		this.sender=sender;
		this.reciever=reciever;
		this.messageID=UUID.randomUUID();
		long time = System.currentTimeMillis();
		this.timestamp=new Timestamp(time);
	}
	public Message(String message,String sender){
		this.message=message;
		this.sender=sender;
		this.reciever="";
		this.messageID=UUID.randomUUID();
		long time = System.currentTimeMillis();
		this.timestamp=new Timestamp(time);
	}
	public Message(){
		this.message="";
		this.sender="";
		this.reciever="";
		long time = System.currentTimeMillis();
		this.timestamp=new Timestamp(time);
		this.messageID=UUID.randomUUID();
	}
	public String getReciever(){
		return this.reciever;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
