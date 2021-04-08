import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class myPractice
{
	static {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		}
		catch(ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
	}
	
	
	public static void main(String[] args)
	{
		Scanner sc=new Scanner(System.in);
		Connection con=null;
		PreparedStatement pstmt=null;

		
		try {
			con= DriverManager.getConnection(
						"jdbc:oracle:thin:@localhost:1521:xe",
						"scott","tiger");	
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		String rname=null;
		String pwd=null;
		
		try {
			System.out.println("방 이름을 입력하세요.");
			rname=sc.nextLine();
			System.out.println("비밀번호를 입력하세요.");
			pwd=sc.nextLine();
						
			pstmt=con.prepareStatement("update room set roomname=?, password=? where "
					+ "roomnum=(select min(roomnum) from room where roomnum!=0 and roomname=' ')");
			
			pstmt.setString(1, rname);
			pstmt.setString(2, pwd);
			
			pstmt.executeUpdate();
			
			System.out.println("방이름 ["+rname+"]"+"비밀번호 ["+pwd+"] : 방이 생성되었습니다.");
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try {
			
			if(pstmt!=null) pstmt.close();
			
			if(con!=null) con.close();
		}catch(SQLException sqle) {
				
		}
		
		
	}
}
