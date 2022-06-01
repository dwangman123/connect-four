import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SignUp extends Thread
{
	ServerSocket ss;
	public void run()
	{
		try {
			ss = new ServerSocket(4345, 10);
		} catch (IOException e1) {
			return; 
		} 
		while(true)
		{
			try {
				Socket s = ss.accept();
				new CheckSignUp(s).start(); 
			}
			catch(IOException e)
			{
				
			}
		}
	}
}

class CheckSignUp extends Thread 
{
	Socket s;
	public CheckSignUp(Socket s)
	{
		this.s = s;
	}
	
	public void run()
	{
		try {
			InputStream is = s.getInputStream(); 
			OutputStream os = s.getOutputStream();
		 	byte[] lenBytes = new byte[4];
	        is.read(lenBytes, 0, 4);
	        int len = (((lenBytes[3] & 0xff) << 24) | ((lenBytes[2] & 0xff) << 16) |
	                  ((lenBytes[1] & 0xff) << 8) | (lenBytes[0] & 0xff));
	        byte[] receivedBytes = new byte[len];
	        is.read(receivedBytes, 0, len);
	        String username = new String(receivedBytes, 0, len);
	    	
	        is.read(lenBytes, 0, 4);
	        len = (((lenBytes[3] & 0xff) << 24) | ((lenBytes[2] & 0xff) << 16) |
	                  ((lenBytes[1] & 0xff) << 8) | (lenBytes[0] & 0xff));
	        receivedBytes = new byte[len];
	        is.read(receivedBytes, 0, len);
	        String password = new String(receivedBytes, 0, len); 
	        
	        boolean result = false;
	        try {
	        	result = CRUD.findUser(CRUD.collection, username);   
	        }
	        catch (IllegalStateException e)
	        {
	        	result = false; 
	        }
	        
	        // Sending
	        String toSend = "false";
	        
	        if(result == false && !username.equalsIgnoreCase("guest"))
	        {
	        	CRUD.addUser(CRUD.collection, username, password); 
	        	toSend = "true";
	        }
	        
	        byte[] toSendBytes = toSend.getBytes();
	        int toSendLen = toSendBytes.length;
	        byte[] toSendLenBytes = new byte[4];
	        toSendLenBytes[0] = (byte)(toSendLen & 0xff);
	        toSendLenBytes[1] = (byte)((toSendLen >> 8) & 0xff);
	        toSendLenBytes[2] = (byte)((toSendLen >> 16) & 0xff);
	        toSendLenBytes[3] = (byte)((toSendLen >> 24) & 0xff);
	        os.write(toSendLenBytes);
	        os.write(toSendBytes);
	        is.close();
	        os.close();
	        s.close();
		}
		catch(IOException e) 
		{
			
		}
	}
	
	
}