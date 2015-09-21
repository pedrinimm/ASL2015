package entities;


import java.util.UUID;



public class Queue {
	public String name;
	public UUID queueId;
	
	public Queue(){
		this.name="";
		this.queueId=UUID.randomUUID();
	}
	public Queue(String queueName){
		this.name=queueName;
		this.queueId=UUID.randomUUID();

	}
	public Queue(String queueName,String queueID){
		this.name=queueName;
		this.queueId=UUID.fromString(queueID);

	}
	
	public String getName(){
		return this.name;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
