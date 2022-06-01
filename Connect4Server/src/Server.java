import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Server  {

	ServerSocket ss; 
	
	Queue<Heartbeat> line = new LinkedList<Heartbeat>(); 
	
	public Server()
	{	        
		try {
			ss = new ServerSocket(4343, 10); 
		} catch (IOException e1) {
			return; 
		} 

		while(true)
		{
			try {
				Socket s = ss.accept();
				//System.out.println(line.size());
				if(line.size()>0)
				{
					boolean added = false;
					
						if(line.peek().isActive())
						{
							new Lobby(line.peek().s, s).start();
							line.poll();
							added = true;
						}
						else
						{
							line.peek().is.close();
							line.peek().s.close();
							//System.out.println("Heatbeat fail");
							line.poll(); 
						}
					
					if(!added)
					{
						Heartbeat temp = new Heartbeat(s);
						line.add(temp);
						//System.out.println("Heatbeat created"); 
						temp.start(); 
					}
				}
				else
				{
					Heartbeat temp = new Heartbeat(s); 
					line.add(temp);
					//System.out.println("Heatbeat created"); 
					temp.start();
				}
				
			} catch (IOException e) {
			//System.out.println("error"); 	
			}
			
		}
	}
	
	public static void main(String[] args) 
	{
		CRUD.startDatabase(); 
		new Profile().start();
		new Login().start();
		new SignUp().start(); 
		new LeaderBoard().start(); 
		Server server = new Server();
	}
}

