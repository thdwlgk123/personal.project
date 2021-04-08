import java.net.Socket;
import java.util.Scanner;

public class MultiClient7
{

	public static void main(String[] args)
	{
	
		Scanner sc=new Scanner(System.in);
		
		try {
			String ServerIP="localhost";
			if(args.length>0)
				ServerIP=args[0];
			Socket socket=new Socket(ServerIP,9999);
			System.out.println("서버와 연결이 되었습니다...");
			String name="";
			Thread receiver=new Receiver7(socket);
			receiver.start();
			
			Thread sender=new Sender7(socket, name);
			sender.start();
	
		}catch(Exception e) {
			System.out.println("예외[MultiClient class]: "+e);
		}
	}

}
