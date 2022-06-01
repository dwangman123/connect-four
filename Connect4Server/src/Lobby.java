import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.time.Duration;
import java.time.Instant;

public class Lobby extends Thread {

	Socket red;
	Socket yellow;
	
	Board board;
	
	BoardCell currentTurn;
	
	Instant lastTurn;
	
	InputStream redIS;
	InputStream yellowIS;
	OutputStream redOS;
	OutputStream yellowOS;
	
	ClientRead redRead;
	ClientRead yellowRead;
	
	String redName = "";
	String yellowName = "";
	
	public Lobby(Socket red, Socket yellow)
	{
		this.red = red;
		this.yellow = yellow;
		try {
			red.setSoTimeout(500);
		} catch (SocketException e1) {
		}
		try {
			yellow.setSoTimeout(500);
		} catch (SocketException e1) {
		}
		board = new Board(7, 6); 
		currentTurn = BoardCell.RED; 
		lastTurn = Instant.now();
		
		try
		{
			redIS = this.red.getInputStream();
			yellowIS = this.yellow.getInputStream();
			redOS = this.red.getOutputStream();
			yellowOS = this.yellow.getOutputStream();
		}
		catch(IOException e)
		{
			
		}
		
		redRead = new ClientRead(redIS);
		yellowRead = new ClientRead(yellowIS); 
		
	}  
	
	boolean isAlphaNumeric(String username)
	{
		for(int i = 0; i < username.length(); i++) 
		{
			if(username.charAt(i) >= 'a' && username.charAt(i) <= 'z' || username.charAt(i) >= 'A' && username.charAt(i) <= 'Z' || username.charAt(i) >= '0' && username.charAt(i) <= '9')
			{
				
			}
			else
			{
				return false;
			}
		}
		return true;
	}

	public void run()
	{
		String received = " ";
		String received2 = " "; 
		//while(!isAlphaNumeric(received) || received.length() == 0)
		//{	
			//send("name",redOS);
		// while(!isAlphaNumeric(received)|| received.length() == 0)
		// {
			 byte[] lenBytes = new byte[4];
        try {
			redIS.read(lenBytes, 0, 4); 
		} catch (IOException e1) {
		} 
        int len = (((lenBytes[3] & 0xff) << 24) | ((lenBytes[2] & 0xff) << 16) |
                  ((lenBytes[1] & 0xff) << 8) | (lenBytes[0] & 0xff));
        try {
			Thread.sleep(1);
		} catch (InterruptedException e2) {
		}
        byte[] receivedBytes = new byte[len];
        try {

			redIS.read(receivedBytes, 0, len);  
		} catch (IOException e) {
		}
        received = null;
        received = new String(receivedBytes, 0, len); 
        //if(!isAlphaNumeric(received)) 
        // {
        //	try {
		//		redIS.skip(1);
		//	} catch (IOException e) {
		//	}
       // }
		// }
		//} 
		//while(!isAlphaNumeric(received2) || received2.length() == 0)  
		//{
			//send("name",yellowOS); 
        //while(!isAlphaNumeric(received2)|| received2.length() == 0)
       // {
	        lenBytes = new byte[4]; 
	        try {
				yellowIS.read(lenBytes, 0, 4);
			} catch (IOException e1) {
			}
	        len = (((lenBytes[3] & 0xff) << 24) | ((lenBytes[2] & 0xff) << 16) |
	                  ((lenBytes[1] & 0xff) << 8) | (lenBytes[0] & 0xff));
	        try {
				Thread.sleep(1);
			} catch (InterruptedException e2) {
			}
	        receivedBytes = new byte[len]; 
	        try {
				yellowIS.read(receivedBytes, 0, len);
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
	        received2 = null;
	        received2 = new String(receivedBytes, 0, len);  
	    //    if(!isAlphaNumeric(received2))
	   //     {
	       // 	try {
			//		yellowIS.skip(1);
			//	} catch (IOException e) {
			//	}
	        //}
        //}
        
        redName = received;
        yellowName = received2;
        //System.out.println(redName);
        //System.out.println(yellowName);
		
        lastTurn = Instant.now();
        
        send("your turn", redOS);
        send("opponent turn", yellowOS);  
        try {
			red.setSoTimeout(100);
		} catch (SocketException e1) {
		}
		try {
			yellow.setSoTimeout(100); 
		} catch (SocketException e1) {
		}
        
		redRead.start();
		yellowRead.start();
		
		while(true) 
		{ 
			try {
				Thread.sleep(1);
			} catch (InterruptedException e1) {
			}
			if(currentTurn == BoardCell.RED)
			{

				/*byte[] lenBytes3 = new byte[4];
			    try {
					yellowIS.read(lenBytes3, 0, 4);
				} catch (IOException e) {
				} 
			    int len3 = (((lenBytes3[3] & 0xff) << 24) | ((lenBytes3[2] & 0xff) << 16) |
			               ((lenBytes3[1] & 0xff) << 8) | (lenBytes3[0] & 0xff));
			    byte[] receivedBytes3 = new byte[len3];
			    //System.out.println(len3); 
			    boolean found = false;
			    try {
					yellowIS.read(receivedBytes3, 0, len3);
					if(len3>0)
					{
						found = true;
					}
				} catch (IOException e) {
					//System.out.println("Exception");
				} 
			    if(found)
			    {*/
			    	try 
			    	{
			    		String received3 = redRead.getValue();
			    		//System.out.println(received3); 
			    	//String received3 = new String(receivedBytes3, 0, len3);
			    	//System.out.println(received3);
			    	/*if(received3.contains("drop"))
			    	{
			    		received3 = received3.substring("drop".length());
			    	}*/
			    	
				    	int res = Integer.parseInt(received3);
				    	if(board.dropPiece(res, currentTurn))
				    	{
				    		if(board.checkForWin() == currentTurn)
				    		{
				    			send("red " + res, redOS);
				    			send("red " + res, yellowOS); 
				    			Thread.sleep(1000);
				    			send("you win", redOS);
				    			send("you lose", yellowOS);
				    			win(currentTurn);
				    			break; 
				    		}
				    		else
				    		{
				    			send("red " + res, redOS);
				    			send("red " + res, yellowOS);
						        send("opponent turn", redOS); 
						        send("your turn", yellowOS);  
						    	lastTurn = Instant.now();
						    	currentTurn = BoardCell.YELLOW; 
						    	yellowRead.setValue(null);
				    		}
				    	}
				    	else
				    	{
				    		//System.out.println(new String(receivedBytes3, 0, len3));  
				    		send("invalid", redOS);
				    	}
			    	}
			    	catch(NumberFormatException e)
			    	{
			            send("your turn", redOS);
			            //send("opponent turn", yellowOS); 
			    		//System.out.println(new String(receivedBytes3, 0, len3));
			    		//("invalid", redOS); 
			    	}
			    	catch(Exception e)
			    	{

			            send("your turn", redOS);
			            //send("opponent turn", yellowOS); 
			    	}
			    // }
				Duration timeElapsed = Duration.between(lastTurn, Instant.now()); 
				if(currentTurn == BoardCell.RED && timeElapsed.toMillis() > 20000)
				{
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
					} 
					send("you lose", redOS);
	    			send("opponent timed out", yellowOS); 
	    			win(BoardCell.YELLOW);
	    			break; 
				}
			}
			else
			{
				/*byte[] lenBytes3 = new byte[4];
			    try {
					yellowIS.read(lenBytes3, 0, 4);
				} catch (IOException e) {
				} 
			    int len3 = (((lenBytes3[3] & 0xff) << 24) | ((lenBytes3[2] & 0xff) << 16) |
			               ((lenBytes3[1] & 0xff) << 8) | (lenBytes3[0] & 0xff));
			    byte[] receivedBytes3 = new byte[len3];
			    boolean found = false;
			    try {
					yellowIS.read(receivedBytes3, 0, len3);
					if(len3>0)
					{
						found = true;
					}
				} catch (IOException e) {
					//System.out.println("Exception");
				}
			    if(found) 
			    {*/
			    	try 
			    	{
			    		String received3 = yellowRead.getValue();
			    	//String received3 = new String(receivedBytes3, 0, len3);
			    	//System.out.println(received3); 
			    	if(received3.contains("drop"))
			    	{
			    		received3 = received3.substring("drop".length());
			    	}
			    	 
				    	int res = Integer.parseInt(received3);
				    	if(board.dropPiece(res, currentTurn))
				    	{
				    		if(board.checkForWin() == currentTurn)
				    		{
				    			send("yellow " + res, redOS);
				    			send("yellow " + res, yellowOS); 
				    			Thread.sleep(1000);
				    			send("you win", yellowOS);
				    			send("you lose", redOS);
				    			win(currentTurn);
				    			break; 
				    		}
				    		else
				    		{
				    			send("yellow " + res, redOS);
				    			send("yellow " + res, yellowOS); 
						        send("opponent turn", yellowOS); 
						        send("your turn", redOS);
						    	lastTurn = Instant.now();
						    	currentTurn = BoardCell.RED; 
						    	redRead.setValue(null); 
				    		}
				    	}
				    	else
				    	{
				    		send("invalid", yellowOS);
				    		//System.out.println(new String(receivedBytes3, 0, len3));
				    		//System.out.println("a");
				    	}
			    	}
			    	catch(NumberFormatException e)
			    	{

			            send("your turn", yellowOS);
			            //send("opponent turn", redOS); 
			    		//send("invalid", yellowOS); 
			    		//System.out.println(new String(receivedBytes3, 0, len3)); 
			    		//System.out.println("a"); 
			    	}
			    	catch(Exception e)
			    	{
			            send("your turn", yellowOS);
			    	}
			    //}
				Duration timeElapsed = Duration.between(lastTurn, Instant.now()); 
				if(currentTurn == BoardCell.YELLOW && timeElapsed.toMillis() > 20000)
				{
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
					}
					send("you lose", yellowOS);
	    			send("opponent timed out", redOS); 
	    			win(BoardCell.RED); 
	    			break; 
				}
			}
		}
	} 
	
	private void win(BoardCell winner)
	{
		//update database here
		if(!redName.equalsIgnoreCase("guest"))
		{
			if(CRUD.findUser(CRUD.collection, redName) && (CRUD.findUser(CRUD.collection, yellowName) || yellowName.equals("guest"))) 
			{		
				if(winner == BoardCell.RED)
				{
					CRUD.addWin(CRUD.collection, redName, yellowName);
					CRUD.updateWins(CRUD.collection, redName);
				}
				else
				{
					CRUD.addLoss(CRUD.collection, redName, yellowName);
					CRUD.updateLosses(CRUD.collection, redName); 
	
				}
			}
		}
		if(!yellowName.equalsIgnoreCase("guest"))
		{
			if(CRUD.findUser(CRUD.collection, yellowName) && (CRUD.findUser(CRUD.collection, redName) || redName.equals("guest")))
			{	
				if(winner == BoardCell.YELLOW) 
				{
					CRUD.addWin(CRUD.collection, yellowName, redName);
					CRUD.updateWins(CRUD.collection, yellowName);
				}
				else
				{
					CRUD.addLoss(CRUD.collection, yellowName, redName);
					CRUD.updateLosses(CRUD.collection, yellowName);
				}
			}
		}
	}
	
	private void send(String message, OutputStream os)
	{
		byte[] toSendBytes = message.getBytes();
        int toSendLen = toSendBytes.length;
        byte[] toSendLenBytes = new byte[4];
        toSendLenBytes[0] = (byte)(toSendLen & 0xff);
        toSendLenBytes[1] = (byte)((toSendLen >> 8) & 0xff);
        toSendLenBytes[2] = (byte)((toSendLen >> 16) & 0xff);
        toSendLenBytes[3] = (byte)((toSendLen >> 24) & 0xff);
        try
        {
	        os.write(toSendLenBytes);
	        os.write(toSendBytes);
        }
        catch(IOException e) 
        {
        	
        }
	}
	
	private void close()
	{
		try
		{
			redIS.close();
			redOS.close();
			yellowIS.close();
			yellowOS.close();
			red.close();
			yellow.close(); 
			redRead.interrupt();
			yellowRead.interrupt();
		}
		catch(IOException e)
		{
			
		}
	}
}
