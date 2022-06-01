import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;

public class Heartbeat extends Thread {
	
	Instant lastUpdate;
	Socket s;
	
	InputStream is;
	
	public Heartbeat(Socket s)
	{
		this.s = s;
		lastUpdate = Instant.now();
		
	}
	
	public void run() 
	{
		
		try {
			is = s.getInputStream();
		} catch (IOException e1) {
			this.interrupt();
			return; 
		} 
		while(true)
		{
			  byte[] lenBytes = new byte[4];
		       try {
				is.read(lenBytes, 0, 4);
				
		        int len = (((lenBytes[3] & 0xff) << 24) | ((lenBytes[2] & 0xff) << 16) |
		                  ((lenBytes[1] & 0xff) << 8) | (lenBytes[0] & 0xff));
		        byte[] receivedBytes = new byte[len];
		        is.read(receivedBytes, 0, len);
		        String received = new String(receivedBytes, 0, len);
		        
		        lastUpdate = Instant.now();  
		       } catch (IOException e) {
		    	   this.interrupt();
		       }

		}
		
	}
	
	public boolean isActive()
	{
		Duration timeElapsed = Duration.between(lastUpdate, Instant.now());
		
		return timeElapsed.toMillis() < 1000; 
	}
	
}
