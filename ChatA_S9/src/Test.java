
public class Test
{

	public static void main(String[] args)
	{
		String s="hellohello my name is jiha";
		String h="my";
		int news=s.indexOf("my");
		String str=s.substring(0,news)+"**"
					+s.substring(news+h.length());
		System.out.println(str);

	}

}
