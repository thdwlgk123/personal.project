import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class Receiver7 extends Thread
{
	Socket socket;
	BufferedReader in=null;
	
	public Receiver7(Socket socket)
	{
		this.socket=socket;
		try {
			in=new BufferedReader(new InputStreamReader(
									this.socket.getInputStream()));
		}catch(Exception e)
		{
			System.out.println("예외 : "+e);
		}
	}
	
	public void run() {
		while(in!=null)
		{
			try {
				System.out.println(in.readLine());
			}catch(java.net.SocketException ne) {
				break;
			}catch(Exception e) {
				System.out.println("예외: "+e);
			}
		}
		try {
			in.close();
		}catch(Exception e) {
			System.out.println("예외3:"+e);
		}
	}

}
