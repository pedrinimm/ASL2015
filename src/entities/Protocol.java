package entities;

public class Protocol {
	public int protocolNum;
	public Message newMessage;
	public Queue newQueue;
	public User newUser;
	
	public Protocol(int protocol){
		this.protocolNum = protocol;
		this.newMessage = null;
		this.newQueue = null;
		this.newUser = null;
	}
	public Protocol(int protocol, Message message){
		this.protocolNum = protocol;
		this.newMessage = message;
		this.newQueue = null;
		this.newUser = null;
	}
	public Protocol(int protocol, Queue queue){
		this.protocolNum = protocol;
		this.newMessage = null;
		this.newQueue = queue;
		this.newUser = null;
	}
	public Protocol(int protocol, User user){
		this.protocolNum = protocol;
		this.newUser = user;
		this.newMessage = null;
		this.newQueue = null;
	}

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
