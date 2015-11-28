package entities;

public class Protocol implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String userName;
	public int protocolNum;
	public Message newMessage;
	public Queue newQueue;
	public User newUser;
	public int counter=0;
	public String message="";
	
	public Protocol(int protocol, String userName1){
		this.userName=userName1;
		this.protocolNum = protocol;
		this.newMessage = null;
		this.newQueue = null;
		this.newUser = null;
	}
	public Protocol(int protocol, Message message,  String userName1){
		this.userName=userName1;
		this.protocolNum = protocol;
		this.newMessage = message;
		this.newQueue = null;
		this.newUser = null;
	}
	public Protocol(int protocol, Message message, Queue queue,String userName1){
		this.userName=userName1;
		this.protocolNum = protocol;
		this.newMessage = message;
		this.newQueue = queue;
		this.newUser = null;
	}
	public Protocol(int protocol, Queue queue,String userName1){
		this.userName=userName1;
		this.protocolNum = protocol;
		this.newMessage = null;
		this.newQueue = queue;
		this.newUser = null;
	}
	public Protocol(int protocol, User user,String userName1){
		this.userName=userName1;
		this.protocolNum = protocol;
		this.newUser = user;
		this.newMessage = null;
		this.newQueue = new Queue();
	}

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
