package task2.test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runners.Parameterized;

import task2.ExtendedPrintStream;
import task2.MapUtil;

@RunWith (Parameterized.class)
public class MainTest extends Assert {

	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(MainTest.class);
	    for (Failure failure : result.getFailures()) {
	      System.out.println(failure.toString());
	    }
	}

	public MainTest(PrintStream ps, ByteArrayOutputStream os) {
		this.ps = ps;
		this.os = os;
	}

	@Parameterized.Parameters
	public static List<Object[]> streams() {
		ByteArrayOutputStream b1 = new ByteArrayOutputStream();
		ByteArrayOutputStream b2 = new ByteArrayOutputStream();
		return Arrays.asList(
			new Object[][] {
				{new PrintStream(b1), b1},
				{new ExtendedPrintStream(b2), b2}
			}
		);
	}

	@Test
	public void testPrintMap() {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("Some key #1", new Object());
		m.put("Some key #2", new Object());
		m.put("Some key #3", new Object());

		if (ps instanceof ExtendedPrintStream) {
			((ExtendedPrintStream) ps).print(m);
			assertEquals(MapUtil.toString(m), os.toString());
		} else {
			ps.print(m);
			assertEquals(m.toString(), os.toString());
		}
	}
	
	@Test
	public void testPrintBoolean() {
		boolean b = true;
		ps.print(b);
		assertEquals(String.valueOf(b), os.toString());
	}
	
	@Test
	public void testPrintChar() {
		char c = 'r';
		ps.print(c);
		String content = os.toString();
		char[] buffer = new char[content.length()];
		content.getChars(0, content.length(), buffer, 0);
		assertEquals(1, buffer.length);
		assertEquals(c, buffer[0]);
	}
	
	@Test
	public void testPrintCharArray() {
		char[] chars = new char[]{'a','r','r','a','y'};
		ps.print(chars);
		String content = os.toString();
		char[] buffer = new char[content.length()];
		content.getChars(0, content.length(), buffer, 0);
		assertEquals(chars.length, buffer.length);
		assertEquals(new String(chars), new String(buffer));
		
	}
	@Test
	public void testPrintDouble() {
		double d = 123.775673821101;
		ps.print(d);
		assertEquals(String.valueOf(d), os.toString());
	}

	@Test
	public void testPrintFloat() {
		float f = 123.11103f;
		ps.print(f);
		assertEquals(String.valueOf(f), os.toString());
	}
	
	@Test
	public void testPrintInt() {
		int i = 555999;
		ps.print(i);
		assertEquals(String.valueOf(i), os.toString());
	}
	
	@Test
	public void testPrintLong() {
		long l = 11112999l;
		ps.print(l);
		assertEquals(String.valueOf(l), os.toString());
	}
	
	@Test
	public void testPrintObject() {
		Object o = new Object();
		ps.print(o);
		assertEquals(o.toString(), os.toString());
	}

	@Test
	public void testPrintString() {
		String msg = "Some test string";
		ps.print(msg);
		assertEquals(msg, os.toString());
	}

	@After
	public void reset() {
		os.reset();
	}

	private ByteArrayOutputStream os;
	private PrintStream ps;
}
