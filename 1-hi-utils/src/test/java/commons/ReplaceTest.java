package commons;

import org.junit.Test;

import com.helpinput.core.Utils;

public class ReplaceTest {
	

	
	@Test
	public void test1() {
		//String s = "public User getUser(String id) {";
		//String s = "User u=\"sl fr User wh\"+\"User.id=\\\"11\\\"\"";
String s="{\n"+
"    String hql = \"from User u where u.id=?\";\n"+
"    Query query = sessionFactory.getCurrentSession().createQuery(hql);\n"+
"    query.setString(0, id);\n"+
"    return (User) query.uniqueResult();\n"+
"}\n";	
		String o = "User";
		String n = "java.lang.Object";
		System.out.println(s + "  " + s.length());
		
		StringBuilder sb = new StringBuilder(s.length() + 100);
		Utils.replaceWholeWord(sb, 0, s, o, n, '"');
		System.out.println(sb.toString());
	}
	
}
