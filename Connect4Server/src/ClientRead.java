import java.io.IOException;
import java.io.InputStream;

public class ClientRead extends Thread{
	InputStream is;
	private String value;
	
	public ClientRead(InputStream is)
	{
		this.is = is;
	}
	
	public void run() 
	{
		while(!interrupted())
		{
			byte[] lenBytes = new byte[4];
			try {
				is.read(lenBytes, 0, 4);
			} catch (IOException e) {
			} 
			int len = (((lenBytes[3] & 0xff) << 24) | ((lenBytes[2] & 0xff) << 16) |
	                  ((lenBytes[1] & 0xff) << 8) | (lenBytes[0] & 0xff));
	        byte[] receivedBytes = new byte[len];
	        try {
				is.read(receivedBytes, 0, len);
			} catch (IOException e) {
			} 
	        String received = new String(receivedBytes, 0, len);
	        if(received.length()>0)
	        {
	        	value = received; 
	        }
		}
	}
	
	public String getValue()
	{
		return value;
	}
	
	public void setValue(String val)
	{
		this.value = val; 
	}
}
