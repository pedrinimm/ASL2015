package client;

import java.util.Random;

public enum PossibleQueues {
	general	(0),
	alpha	(1),
	beta	(2),
	gamma	(3),
	delta	(4);

	
	private final int queueCode;
	
	private PossibleQueues(int queueC){
		this.queueCode=queueC;
		
	}
	public static PossibleQueues getRandomQueue(){
		Random random = new Random();
		return values()[random.nextInt(values().length)];
	}
}
