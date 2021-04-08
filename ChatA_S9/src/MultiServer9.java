import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public class MultiServer9
{
	ServerSocket serverSocket=null;
	Socket socket=null;
	
	Map<String, PrintWriter> clientMap;
	// 교재 p.395 참조
	
	public MultiServer9(){
		clientMap =new HashMap<String, PrintWriter>();
		
		Collections.synchronizedMap(clientMap);
	}
	static {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		}
		catch(ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
	}
	
	Scanner sc=new Scanner(System.in);
	Connection con=null;
	StringBuffer sb=new StringBuffer();
	Statement stmt;
	PreparedStatement pstmt1;
	PreparedStatement pstmt2;
	PreparedStatement pstmt3;
	PreparedStatement pstmt4;
	PreparedStatement pstmt5;
	PreparedStatement pstmt6;
	PreparedStatement pstmt7;
	PreparedStatement pstmt8;
	PreparedStatement pstmt9;
	PreparedStatement pstmt10;
	PreparedStatement pstmt11;
	
	
	public void init()
	{
		try {
			serverSocket=new ServerSocket(9999);
			System.out.println("서버가 시작되었습니다.");
			
			while(true) {
				socket=serverSocket.accept();
				System.out.println(socket.getInetAddress()+":"+socket.getPort());
				
				Thread mst=new MultiServerT(socket);								
				mst.start();
				}
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {
				serverSocket.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}	
	}
	//대기실(0번)방에 있는 회원들에게만 메세지 전송
	public void sendZeroMsg(String name, String msg) {
		try {
			pstmt10=con.prepareStatement("select id from allmember "
					+ "where roomname='0' and id!=?");
			pstmt10.setString(1, name);
			ResultSet rs=pstmt10.executeQuery();
			
			while(rs.next()) {
				PrintWriter it_out=(PrintWriter)clientMap.get(rs.getString(1));	
				if(name.equals(""))
					it_out.println(msg);
				else
					it_out.println(name+"> "+msg);
			}
			rs.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	//모든 회원들에게 메세지 전송가능
	public void sendAllMsg(String msg, String name)
	{
		Iterator<String> it=clientMap.keySet().iterator();
		
		while(it.hasNext()) {
			try{
				PrintWriter it_out=(PrintWriter)clientMap.get(it.next());
				if(name.equals(""))
					it_out.println(msg);
				else if(name.equals(name))
				{
					
				}
				else
					it_out.println(name+">"+msg);
			}catch(Exception e) {
				System.out.println("예외: "+e);
			}
		}
	}
	
	public void sendSecreteMsg(PrintWriter out,BufferedReader in, String name, String s)
	{				
		String rcv=s.substring(4,(s.indexOf(" ",4)));
		PrintWriter it_out=(PrintWriter)clientMap.get(rcv);
		try {	
			it_out.println(name+">님의 귓속말 : "+s.substring(4+rcv.length()));
			while(true) 
			{								
				s=in.readLine();
				if(s.substring(s.length()-1).equals("/")) {
					it_out.println(name+">님의 귓속말 : "+s.substring(0,s.length()-1));
					break;
				}
				else{
					it_out.println(name+">님의 귓속말 : "+s);
				}				
			}
		}catch(Exception e) {
			e.printStackTrace();
		}								
	}	
	public void sendSecreteMsgO(PrintWriter out,BufferedReader in, String name, String s)
	{
		String rcv=s.substring(4,(s.indexOf(" ",4)));
		PrintWriter it_out=(PrintWriter)clientMap.get(rcv);
		it_out.println(name+">님의 귓속말"+s.substring(4+rcv.length(),s.length()-1));

	}
	public void LogMemList(PrintWriter out, String name) {
		
		try{
			sb.setLength(0);
			sb.append("select distinct(id) from allmember where id!="+"'"+name+"'");
			ResultSet rs=stmt.executeQuery(sb.toString());
			out.print("접속한 회원 : [ ");
			while(rs.next()) {
				out.print(rs.getString(1)+" ");
			}
			out.println(" ]");
			rs.close();
		}catch(Exception e) {
			e.printStackTrace();
		}		
	}
	public boolean checkLogMem(String hide)
	{
		ResultSet rs;
		try {
			sb.setLength(0);
			sb.append("select distinct(id) from allmember");
			rs=stmt.executeQuery(sb.toString());
			
			while(rs.next()) {
				if(hide.equals(rs.getString(1))) {
					rs.close();
					return true;
				}
			}
			rs.close();	
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	public void list(PrintWriter out)
	{
		Iterator<String> it=clientMap.keySet().iterator();
		String msg="사용자 리스트 [";
		while(it.hasNext()) {
			msg+=(String)it.next()+",";
		}
		msg=msg.substring(0,msg.length()-1)+"]";
		try {
			out.println(msg);
		}catch(Exception e) {
			
		}
	}
	
	public static void main(String[] args)
	{
		MultiServer9 ms=new MultiServer9();
		ms.init();
	}
	
	class MultiServerT extends Thread
	{
		Socket socket;
		PrintWriter out=null;
		BufferedReader in=null;
		String id="";
		
		public MultiServerT(Socket socket) {
			this.socket=socket;
			try {
				out=new PrintWriter(this.socket.getOutputStream(), true);
				in=new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			}catch(Exception e) {
				System.out.println("예외 : "+e);
			}
		}
		
		public void run() {
			connectDB();
			String s="";
			String name="";
			String iD="";
			
			try {
myExit:			while(true) {
					out.println("[메뉴를 선택하세요.]");
					out.println("1. 로그인");
					out.println("2. 회원가입");
					
					String choice=in.readLine();
					
					if(("1").equals(choice))
					{						
						out.println("로그인을 진행합니다. 아이디를 입력해주세요");
						iD=in.readLine();
						
						if(checkLogMem(iD))
						{
							out.println("이미 접속한 아이디입니다.");
						}
						else if(check(iD))
						{
							name=logIn(out, in, iD);
							break myExit;
						}
						else {
							out.println("없는 아이디입니다.");
						}						
					}						
					else if(("2").equals(choice))
					{						
						iD=signIn(out, in);
						name=logIn(out, in, iD);
						break;
					}
					else {
						out.println("잘못 입력하였습니다. 번호를 다시 입력하세요.");
					}
				}
		
			}catch(Exception e) {
				System.out.println("에러: "+e);
			}
						
			try {				
				
				System.out.println("["+name+"]님이 대기실에 입장하셨습니다.");
				sendZeroMsg(name,name+"님이 입장하셨습니다.");
				clientMap.put(name, out);
				
				String sql="insert into allmember values(?, 0, null)";
				pstmt1=con.prepareStatement(sql);
				pstmt1.setString(1, name);
				pstmt1.executeUpdate();
				
				System.out.println("현재 접속자 수는"+clientMap.size()+"명 입니다.");
				
				while(in!=null) {
					s=in.readLine();					
					s=checkPhbWord(s);
					
					if(s.equals("q")||s.equals("Q"))
					{
						break;
					}	
					System.out.println(name+">"+s);					
					
					if(s.equals("/list"))
						list(out);
					else if (s.length()>4 && (s.substring(0,3)).equals("/to")) {
						if(clientMap.get(s.substring(4,(s.indexOf(" ",4))))==null) {
							out.println("없는 아이디입니다.");
							break;
						}
						else {
							if(s.substring(s.length()-1).equals("/")) {
								sendSecreteMsgO(out,in, name, s);
							}
							else
								sendSecreteMsg(out,in, name, s);
						}																				
					}
					else if(s.equals("/방생성")) {
						createRoom(name, out, in);
						
					}
					else if(s.equals("/방입장")) {
						selectRL(out, in);
						enterRoom(name, out, in);
					}
					else if(s.equals("/전체방보기")) {
						roomList(out);
					}
					else if(s.equals("/공개방보기")) {
						publicRL(out);
					}
					else if(s.equals("/비공개방보기")) {
						secreteRL(out);
					}
					else if(s.equals("/회원탈퇴")) {
						dropId(out, in, name);
					}
					else if(s.equals("/방정보")) {
						checkroomInfo(out, name);
					}
					else if(s.equals("/방삭제")) {
						if(name.equals("manager"))
							deleteRoom(out,in);
						else
							out.println("권한이 없습니다.");
					}
					else {
						sendZeroMsg(name, s);
					}
				}	
				System.out.println("쓰레드 종료");				
			}catch(Exception e) {
				System.out.println("예외 : "+e);
			}finally {
				clientMap.remove(name);
				try {
					pstmt2=con.prepareStatement("delete allmember where id=? ");
					pstmt2.setString(1, name);
					pstmt2.executeUpdate();					
				}catch(SQLException e) {
					e.printStackTrace();
				}
				System.out.println(name+"님이 퇴장하셨습니다.");
				sendZeroMsg(name, name+"님이 퇴장하셨습니다.");
				System.out.println("현재 접속자 수는"+clientMap.size()+"명 입니다.");
				try {			
					in.close();
					out.close();
					
					disconnectDB();
					if(clientMap.size()==0 && con!=null)
						con.close();
					socket.close();
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			
		}
	}
	public void connectDB() {
		try {
			con= DriverManager.getConnection(
						"jdbc:oracle:thin:@localhost:1521:xe",
						"scott","tiger");
			stmt=con.createStatement();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void disconnectDB() {
		try {
			if(pstmt1!=null) pstmt1.close();
			if(pstmt2!=null) pstmt2.close();
			if(pstmt3!=null) pstmt3.close();
			if(pstmt4!=null) pstmt4.close();
			if(pstmt5!=null) pstmt5.close();
			if(pstmt6!=null) pstmt6.close();
			if(pstmt7!=null) pstmt7.close();
			if(pstmt8!=null) pstmt8.close();
			if(pstmt9!=null) pstmt9.close();
			if(stmt!=null) stmt.close();
		}catch(SQLException sqle) {}
	}
	

	public String logIn(PrintWriter out, BufferedReader in, String iD) throws Exception
	{
		pstmt3=con.prepareStatement("select * from data1 where id=?");
		pstmt3.setString(1, iD);
		ResultSet rs=pstmt3.executeQuery();
		
		if(rs.next()){
			out.println("id :"+rs.getString(1));
			out.println("name: "+rs.getString(2));
		
			while(true) {
				out.println("비밀번호를 입력해주세요.");
					
				String pwd=in.readLine();
				
				if (pwd.equals(rs.getString(3))) {
					out.println("서버에 접속합니다.");
					if(rs.getString(1).equals("manager"))
					{
						out.println("관리자모드로 접속하였습니다.");
						out.println("관리자 모드 : 방정보 조회 및 방 폭파가능");
					}
					break;
				}
				else {
					out.println("잘못된 비밀번호입니다. 다시 입력해주세요.");
				}	
			}	
			rs.close();
		}				
		return iD;	
	}
		
	public String signIn(PrintWriter out, BufferedReader in) throws IOException {
		out.println("회원가입을 진행합니다.");
		out.println("사용할 id를 입력하세요");
		String iD=in.readLine();
		
		while(check(iD)) {
			out.println("아이디가 중복됩니다. 다른 아이디를 입력해주세요.");
			iD=in.readLine();
		}
		
		out.println("이름(실명)을 입력하세요.");
		String name=in.readLine();
		out.println("비밀번호를 입력하세요.");
		String pwd=in.readLine();
			
		String sql2="insert into data1 values(?,?,?)";
		try {
			pstmt4=con.prepareStatement(sql2);
			pstmt4.setString(1, iD);
			pstmt4.setString(2, name);
			pstmt4.setString(3, pwd);
			pstmt4.executeUpdate();
			out.println("회원가입이 완료되었습니다.");
		}catch(Exception e) {
			System.out.println("입력 에러입니다.");
		}
		return iD;
		
	}
	
	public boolean check(String iD) {
		ResultSet rs;
		try {
			sb.setLength(0);
			sb.append("select id from data1");
			rs=stmt.executeQuery(sb.toString());
			
			while(rs.next()) {
				if(rs.getString(1).equals(iD)) {
					rs.close();
					return true;
				}
			}			
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	public void selectRL(PrintWriter out, BufferedReader in) {
		out.println("번호를 선택하세요.");
		try{
			while(true) {
				out.println("1.전체방 보기  2.공개방 보기  3.비공개방 보기");
				String num=in.readLine();
				if(num.equals("1"))
				{
					roomList(out);
					break;
				}
				else if(num.equals("2")) {
					publicRL(out);
					break;
				}				
				else if(num.equals("3")) {
					secreteRL(out);
					break;
				}
				else
					out.println("잘못 입력하였습니다. 다시 입력해주세요.");
			}	
		}catch(Exception e) {
			e.printStackTrace();
		}
			
	}
	public void createRoom(String name, PrintWriter out, BufferedReader in)
	{
		String rname="";
		String pwd="";
		String msg="";
		
		try {
			out.println("방 이름을 입력하세요.");
			rname=in.readLine();
			out.println("비밀번호를 입력하세요. 비밀번호를 설정하지 않는다면 enter를 눌러주세요.");
			pwd=in.readLine();
						
			pstmt5=con.prepareStatement("update room set roomname=?, password=?, nowmemnum=? where "
					+ "roomnum=(select min(roomnum) from room where roomnum!=0 and roomname is null)");
			
			pstmt5.setString(1, rname);
			pstmt5.setString(2, pwd);
			pstmt5.setInt(3, 1);
			pstmt5.executeUpdate();
			
			pstmt6=con.prepareStatement("update allmember set roomname=? where id=?");
			pstmt6.setString(1, rname);
			pstmt6.setString(2, name);
			pstmt6.executeUpdate();
			
			out.println("방이름 ["+rname+"]"+", 비밀번호 ["+pwd+"] : 방이 생성되었습니다.");			
			sb.setLength(0);
			sb.append("update allmember set roommanage=0 where id="+"'"+name+"'");
			stmt.executeUpdate(sb.toString());
			
			System.out.println("[ "+rname+"] 방이 생성되었습니다.");
			out.println("["+rname+" ] 방의 방장이 되었습니다.");
			
			while(msg != null) {
				msg=in.readLine();
				if(msg.equals("/quit"))
				{
					leaveRoom(rname, name,out);
					out.println("대기실로 들어왔습니다.");
					break;
				}
				else
					sendRoomMsg(msg, rname, name);
				}		
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void roomList(PrintWriter out) {
		try {
			sb.setLength(0);
			sb.append("select roomname from room where roomname is not null");
			ResultSet rs=stmt.executeQuery(sb.toString());
			out.print("전체 방 목록 : [");
			while(rs.next()) {
				out.print(rs.getString(1)+" ");
			}
			out.println("]");
			rs.close();
		}catch(Exception e) {
			e.printStackTrace();
		}		
	}
	public void publicRL(PrintWriter out) {
		try {
			sb.setLength(0);
			sb.append("select roomname from room where roomname is not null"
					+ " and password is null");
			ResultSet rs=stmt.executeQuery(sb.toString());
			out.print("공개방 목록 : [");
			while(rs.next()) {
				out.print(rs.getString(1)+" ");
			}
			out.println("]");
			rs.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void secreteRL(PrintWriter out) {
		try {
			sb.setLength(0);
			sb.append("select roomname from room where roomname is not null"
					+ " and password is not null");
			ResultSet rs=stmt.executeQuery(sb.toString());
			out.print("비공개방 목록 : [");
			while(rs.next()) {
				out.print(rs.getString(1)+" ");
			}
			out.println("]");
			rs.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void enterRoom(String name, PrintWriter out, BufferedReader in) {
		
		String rname="";
		String msg="";
		String pwd="";
		ResultSet rs;
		try{
	
			while(true) {
				out.println("입장할 방 이름을 선택해주세요.");
				rname=in.readLine();
			
				if(checkRoom(rname)) {
					pstmt7=con.prepareStatement("update allmember set roomname=? where id=?");
					pstmt7.setString(1, rname);
					pstmt7.setString(2, name);
					pstmt7.executeUpdate();
					break;
				}
				else
					out.println("존재하지 않는 방입니다.");			
			}
			
			while(true)
			{				
				sb.setLength(0);
				sb.append("select password from room where roomname="+"'"+rname+"'");
				rs=stmt.executeQuery(sb.toString());
				
				rs.next();
				if((rs.getString(1))==null)
				{
					out.println(rname+" 방에 입장하셨습니다.");
					break;
				}
				else {
					out.println("비밀번호를 입력하세요.");
					pwd=in.readLine();
					if(pwd.equals(rs.getString(1))) {
						out.println(rname+" 방에 입장하셨습니다.");
						break;
					}
					else {
						out.println(" 잘못된 비밀번호입니다.");
					}
				}				
			}
			rs.close();
			UpdateNowMemnum(rname);
			updateRmgNumP(rname, name);
			
			while(msg!=null){
				msg=in.readLine();
				if(msg.equals("/quit"))
				{
					leaveRoom(rname, name,out);
					out.println("대기실로 들어왔습니다.");
					break;
				}	
				else
					sendRoomMsg(msg,rname, name);
			}	
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendRoomMsg(String msg, String rname, String name) 
	{
		try {
			pstmt8=con.prepareStatement("select id from allmember where roomname=? and id is not null");
			pstmt8.setString(1, rname);
			ResultSet rs=pstmt8.executeQuery();
			
			while(rs.next()) {
				if(!rs.getString(1).equals(name)) {
					PrintWriter it_out=(PrintWriter)clientMap.get(rs.getString(1));	
					it_out.println(rname +" : "+name+"> "+msg);
				}				
			}
			rs.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	public void leaveRoom(String rname, String name,PrintWriter out)
	{
		try{
			pstmt11=con.prepareStatement("select roommanage from allmember where id=?");
			pstmt11.setString(1, name);
			ResultSet rs=pstmt11.executeQuery();
			rs.next();
			int rmn=rs.getInt(1);
			rs.close(); 	//퇴장 회원이 방장인지 확인
			
			updateRmgNumM(rname, name);	//탈퇴 회원보다 방 번호가 높은 회원은 방 번호를 1씩 줄임
			
			sb.setLength(0);
			sb.append("update allmember set roomname='0', roommanage=0 where id="+"'"+name+"'");
			stmt.executeUpdate(sb.toString()); //퇴장 회원을 대기실0번으로 입장
			
			sb.setLength(0);
			sb.append("update room set nowmemnum="
					+ "(nowmemnum-1) where roomname="+"'"+rname+"'");
			stmt.executeUpdate(sb.toString()); //회원들의 방 입장 번호를 하나씩 줄임
			
			if(rmn==0)
				setRoomBoss(rname);  //방입장번호가 0번이 되면 방장설정

			rs.close();
		}catch(Exception e) {
			e.printStackTrace();
		}		
	}	
	
	public boolean checkRoom(String rname) {
		try {

			sb.setLength(0);
			sb.append("select roomname from room where roomname is not null");
			ResultSet rs=stmt.executeQuery(sb.toString());
			
			while(rs.next()) {
				if((rs.getString(1)).equals(rname)) {
					rs.close();
					return true;
				}
			}			
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	public void UpdateNowMemnum(String rname) {
		
		try{

			sb.setLength(0);
			sb.append("update room set nowmemnum=(nowmemnum+1)"+" where"
					+ " roomname="+"'"+rname +"'");
			stmt.executeUpdate(sb.toString());
			
		}catch(SQLException e) {
			e.printStackTrace();
		}		
	}
	public String checkPhbWord(String s)
	{
		try {
			pstmt9=con.prepareStatement("select * from prohibitword");
			ResultSet rs=pstmt9.executeQuery();
			
			while(rs.next()) {
			
				while (s.indexOf(rs.getString(1))!=-1)
				{
					int num1=s.indexOf(rs.getString(1));
					s=s.substring(0,num1)+rs.getString(2)
							+s.substring(num1+rs.getString(1).length());
				}
			}
			rs.close();					
		}catch(Exception e) {
			e.printStackTrace();
		}
		return s;
		
	}
	public void deleteRoom(PrintWriter out,BufferedReader in) {
		out.println("삭제할 방을 입력하세요.");
		roomList(out);
	
		try{
			String rname=in.readLine();
			if(checkRoom(rname))
			{
				sb.setLength(0);
				sb.append("select nowmemnum from room where roomname="+"'"+rname+"'");
				ResultSet rs=stmt.executeQuery(sb.toString());//방에 인원이 0명이면 방 삭제를 하기 위한 과정
							
				rs.next();
				if(rs.getInt(1)==0)
				{
					sb.setLength(0);
					sb.append("update room set roomname=null, password=null where roomname="+
						"'"+rname+"'");
					stmt.executeUpdate(sb.toString());
					System.out.println("[ "+rname+" ] 방이 삭제되었습니다.");
					out.println("[ "+rname+" ] 방이 삭제되었습니다.");
				}
				else
				{
					out.println("방 인원을 모두 퇴장시키고 방을 삭제하시겠습니까? y/n");
					String asw=in.readLine();
					if(asw.equals("y")) {
						outRoomMem(out,rname);
					}
					else if(asw.equals("n")) {
						out.println("방 삭제를 취소합니다.");
					}
					else
					{
						out.println("잘못 입력하였습니다. 방 삭제를 다시 진행해주세요.");
					}
				}
			}
			else
				out.println("잘못입력하였습니다. 방삭제를 취소합니다.");			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	public void outRoomMem(PrintWriter out, String rname) {
		try {
			sb.setLength(0);
			sb.append("select id from allmember where roomname="+"'"+rname+"'");
			ResultSet rs=stmt.executeQuery(sb.toString());
			while(rs.next()) {
				PrintWriter it_out=(PrintWriter)clientMap.get(rs.getString(1));
				it_out.println("["+rname+"] 방이 삭제되어 강제퇴장 당하였습니다.");
				out.println(rs.getString(1)+"님이 대기실로 퇴장당하였습니다.");
				sb.setLength(0);
				sb.append("update allmember set roomname='0', roommanage=0 where id="+
				"'"+rs.getString(1)+"'");
				stmt.executeUpdate(sb.toString());				
			}
			rs.close();
			
			sb.setLength(0);
			sb.append("update room set roomname=null, password=null, roomboss=null,"
				+ " nowmemnum=0 where roomname="+
				"'"+rname+"'");
			stmt.executeUpdate(sb.toString());
			out.println("[ "+rname+" ] 방이 삭제되었습니다.");
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
	}
	public void updateRmgNumP(String rname, String name) {
		try {
					
			sb.setLength(0);
			sb.append("update allmember set roommanage=(select max(roommanage) from"
					+ " allmember where roomname="+"'"+rname+"')+1 where id="+"'"+name+"'");
			stmt.executeUpdate(sb.toString());
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	public void updateRmgNumM(String rname, String name) {
		try {			
			sb.setLength(0);
			sb.append("update allmember set roommanage=(roommanage-1) where roomname="+"'"+rname+"'"+
			" and roommanage>(select roommanage from allmember where id="+"'"+name+"')");
			stmt.executeUpdate(sb.toString());
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	public void setRoomBoss(String rname) {
		try {
			sb.setLength(0);
			sb.append("select id from allmember where roomname="+"'"+rname+"'"
					+" and roommanage='0'");
			ResultSet rs=stmt.executeQuery(sb.toString());
			
			if(rs.next()) {
				PrintWriter it_out=(PrintWriter)clientMap.get(rs.getString(1));
				it_out.println("[ "+rname+"] 방의 방장이 되셨습니다.");
			}
			
			rs.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
	}
	public void dropId(PrintWriter out, BufferedReader in, String name) {
		String asw="";
		try
		{
			while(true) {
				out.println("회원 탈퇴를 진행하시겠습니까? y/n");
				asw=in.readLine();
				if(asw.equals("y"))
				{
					out.println("hk");
					deleteId(out, in, name);
					break;
				}
				else if(asw.equals("n"))
				{
					out.println("회원탈퇴를 취소합니다.");
					break;
				}
				else
					out.println("잘못 입력하였습니다.");
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
	}
	public void deleteId(PrintWriter out, BufferedReader in,String id) {
		try {
			while(true) {
				out.println("탈퇴를 진행하기 위해 비밀번호를 입력해주세요.");
				String asw=in.readLine();
				pstmt3.setString(1, id);
				ResultSet rs=pstmt3.executeQuery();
				rs.next();
				if(asw.equals(rs.getString(3))) {
					sb.setLength(0);
					sb.append("delete data1 where id="+"'"+id+"'");
					stmt.executeUpdate(sb.toString());
					out.println("탈퇴가 완료되었습니다.q 를 입력하시면 종료됩니다.");
					break;
				}
				else
					out.println("잘못된 비밀번호입니다.");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void checkroomInfo(PrintWriter out, String name) {
		if(name.equals("manager")) {
			try {
				sb.setLength(0);
				sb.append("select roomname, password, nowmemnum from room where roomname is not null");
				ResultSet rs=stmt.executeQuery(sb.toString());
				
				while(rs.next()) {
					out.print("방이름 : "+rs.getString(1)+"		");
					out.print("비밀번호 : "+rs.getString(2)+"		");
					out.println("현재인원 : "+rs.getString(3));
				}
				rs.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		else
			out.println("접근 권한이 없습니다.");		
	}
	
}

