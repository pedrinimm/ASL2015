package client;

public class Testing {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		for (int i = 0; i < 10; i++) {
            System.out.println("Names " +PossibleUsers.getRandomUser());
        }
		for (int i = 0; i < 10; i++) {
            System.out.println("Queues " +PossibleQueues.getRandomQueue());
        }

	}

}
