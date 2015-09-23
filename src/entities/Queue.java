package entities;


import java.sql.Timestamp;
import java.util.UUID;



public class Queue {
	public String name;
	public UUID queueId;
	public Timestamp timestamp;
	
	public Queue(){
		this.name="";
		this.queueId=UUID.randomUUID();
		long time = System.currentTimeMillis();
		this.timestamp=new Timestamp(time);
	}
	public Queue(String queueName){
		this.name=queueName;
		this.queueId=UUID.randomUUID();
		long time = System.currentTimeMillis();
		this.timestamp=new Timestamp(time);

	}
	public Queue(String queueName,String queueID){
		this.name=queueName;
		this.queueId=UUID.fromString(queueID);
		long time = System.currentTimeMillis();
		this.timestamp=new Timestamp(time);

	}
	
	public String getName(){
		return this.name;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
