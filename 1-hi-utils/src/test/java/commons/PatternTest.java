package commons;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class PatternTest {
	//	@Test
	//	public void testNotHave(){
	//		String reg="^(?!.*(不合谐)).*$";//用到了前瞻   
	//        System.out.println("不管信不信,反正现在很不合谐".matches(reg));//false不通过   
	//        System.out.println("不管信不信,反正现在非常合谐".matches(reg));//true通过   
	//        System.out.println("不合谐在某国是普遍存在的".matches(reg));//false不通过   
	//	}
	
	//@Test
	//	public void testNotHave(){
	//		Pattern p=Pattern.compile("(?!.*(\\s{1,}class\\s{1,})).*$");
	//		System.out.println(p.matcher("public class User").find());
	//		System.out.println(p.matcher("User user").find());
	//		System.out.println(p.matcher("myclass User").find()); 
	//	}
	
	@Test
	public void testNotHave() {
		String s;
		
		//String reg="^((?<!class).)*User(.(?!class))*$";//用到了前瞻   
		//String reg="^(?! (class)|(.*\\s+class)|\\s*class).*$";//用到了前瞻   
		//String reg="^((?! (class)|(.*\\s+class)|\\s*class).*(\\s+)User(\\W*).*)|(^\\s*User\\s+)|(^.*\\W+User\\W+).*$";//用到了前瞻   
		String reg = "^((?!((class|\\s+class|.*\\s+class)\\s+))((.*(\\s+|\\W+)))User(\\s+|\\W+)|(User(\\s+|\\W+))).*$";//用到了前瞻   
		Matcher m;
		Pattern p = Pattern.compile(reg);
		
		s = "class User";
		m = p.matcher(s);
		System.out.println(m.find() + "          " + s);
		
		s = "  class User";
		m = p.matcher(s);
		System.out.println(m.find() + "          " + s);		
		
		s = "public class User";
		m = p.matcher(s);
		System.out.println(m.find() + "          " + s);
		
		s = "public class User {";
		m = p.matcher(s);
		System.out.println(m.find() + "          " + s);
		
		s = "public class User impliment {";
		m = p.matcher(s);
		System.out.println(m.find() + "          " + s);
		
		s = "AUser user";
		m = p.matcher(s);
		System.out.println(m.find() + "          " + s);
		
		s = "AUser u";
		m = p.matcher(s);
		System.out.println(m.find() + "          " + s);
		
		s = "Userr user";
		m = p.matcher(s);
		System.out.println(m.find() + "          " + s);
		
		s = "abc.getUser(String id)";
		m = p.matcher(s);
		System.out.println(m.find() + "          " + s);
		
		s = "User user";
		m = p.matcher(s);
		System.out.println(m.find() + "          " + s);
		
		s = "User u";
		m = p.matcher(s);
		System.out.println(m.find() + "          " + s);		
		
		s = "  User user";
		m = p.matcher(s);
		System.out.println(m.find() + "          " + s);		
		
		
		s = "Abc myclass ,User u";
		m = p.matcher(s);
		System.out.println(m.find() + "          " + s);
		
		s = "Abc myclassabc ,User u";
		m = p.matcher(s);
		System.out.println(m.find() + "          " + s);
		
		s = "public List<User> getAllUser()";
		m = p.matcher(s);
		System.out.println(m.find() + "          " + s);
		
		s = "return (User)query.uniqueResult()";
		m = p.matcher(s);
		System.out.println(m.find() + "          " + s);
		
		//		System.out.println("xcalendar ".matches("((.*calendar.*)(?!.*simple.*))"));
		//		System.out.println("simple ".matches("((.*calendar.*)(?!.*simple.*))"));
		//		System.out.println("calendar simple ".matches("((.*calendar.*)(?!.*simple.*))"));
		//		System.out.println("simple calendar".matches("((.*calendar.*)(?!.*simple.*))"));
		
	}
	
}
