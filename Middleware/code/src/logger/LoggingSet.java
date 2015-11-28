package logger;

import java.io.IOException;
//import java.util.logging.FileHandler;
//import java.util.logging.Handler;
//import java.util.logging.Level;
//import java.util.logging.SimpleFormatter;
//import java.util.logging.Logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;




public class LoggingSet {
	private static Logger logger;
	
	public LoggingSet(String className){
		setup(className);
	}
	
	static public void setup(String className){
		logger = LogManager.getLogger(className);
		
		
	}
	public static Logger getLogger(){
		return logger;
	}
}
