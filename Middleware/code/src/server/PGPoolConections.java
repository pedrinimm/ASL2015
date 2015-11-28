package server;

import java.sql.Connection;
import java.util.Vector;

public class PGPoolConections {

	Vector connectionPool = new Vector();
	int MAX_POOL_SIZE = 2;
	
	public PGPoolConections(int maxSize) {
		MAX_POOL_SIZE=maxSize;
		initialize();
	}
	private void initialize()
	{

		initializePool();
	}
	private void initializePool()
	{
		while(!poolIsFull())
		{
			System.out.println("Connection Pool is NOT full. Proceeding with adding new connections");
			//Adding new connection instance until the pool is full
			connectionPool.addElement(createNewConnection());
		}
		System.out.println("Connection Pool is full.");
	}
	private synchronized boolean poolIsFull()
	{
//		final int MAX_POOL_SIZE = 2;

		//Check if the pool size
		if(connectionPool.size() < MAX_POOL_SIZE)
		{
			return false;
		}

		return true;
	}
	private Connection createNewConnection()
	{
		Connection connection = null;

//		try
//		{
//			Class.forName("com.mysql.jdbc.Driver");
//			connection = DriverManager.getConnection(databaseUrl, userName, password);
//			System.out.println("Connection: "+connection);
//		}
//		catch(SQLException sqle)
//		{
//			System.err.println("SQLException: "+sqle);
//			return null;
//		}
//		catch(ClassNotFoundException cnfe)
//		{
//			System.err.println("ClassNotFoundException: "+cnfe);
//			return null;
//		}

		return connection;
	}
	public synchronized Connection getConnectionFromPool()
	{
		Connection connection = null;

		//Check if there is a connection available. There are times when all the connections in the pool may be used up
		if(connectionPool.size() > 0)
		{
			connection = (Connection) connectionPool.firstElement();
			connectionPool.removeElementAt(0);
		}
		//Giving away the connection from the connection pool
		return connection;
	}

	public synchronized void returnConnectionToPool(Connection connection)
	{
		//Adding the connection from the client back to the connection pool
		connectionPool.addElement(connection);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
