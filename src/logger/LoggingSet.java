package logger;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;
import java.util.logging.Logger;




public class LoggingSet {
	static private FileHandler fileText;
	static private SimpleFormatter formatterTxt;
	static Logger logger;
	
	public LoggingSet(String className){
		setup(className);
	}
	
	static public void setup(String className){
		//global logger configure
		logger =Logger.getLogger(className);
		
		Handler[] handlers = logger.getHandlers();
		for(Handler handler : handlers) {
			logger.removeHandler(handler);
		}
		
		try {
			
			fileText = new FileHandler(className+".%u.%g.log",true);
			formatterTxt = new SimpleFormatter();
			
			fileText.setFormatter(formatterTxt);
			logger.addHandler(fileText);
		} catch (SecurityException | IOException e) {
			// TODO Auto-generated catch block
			logger.log(Level.SEVERE,null,e);
			e.printStackTrace();
		}
		
	}
	static public Logger getLogger(){
		return logger;
	}
}
