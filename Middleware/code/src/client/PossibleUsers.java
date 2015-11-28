package client;

import java.util.Random;

public enum PossibleUsers {
	Zero	(0),
	Angel	(1),
	Beto	(2),
	Carlos	(3),
	Daniel	(4),
	Estefania (5),
	Flora	(6),
	Gabi	(7),
	Hector	(8),
	Ivan	(9);
	
	private final int userCode;
	
	private PossibleUsers(int userC){
		this.userCode=userC;
		
	}
	public static PossibleUsers getRandomUser(){
		Random random = new Random();
		return values()[random.nextInt(values().length)];
	}
}
