package task2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public class ExtendedPrintStream extends PrintStream {

	public ExtendedPrintStream(File arg0, String arg1) throws FileNotFoundException, UnsupportedEncodingException {
		super(arg0, arg1);
	}
	public ExtendedPrintStream(File arg0) throws FileNotFoundException {
		super(arg0);
	}
	public ExtendedPrintStream(OutputStream arg0, boolean arg1, String arg2) throws UnsupportedEncodingException {
		super(arg0, arg1, arg2);
	}
	public ExtendedPrintStream(OutputStream arg0, boolean arg1) {
		super(arg0, arg1);
	}
	public ExtendedPrintStream(OutputStream arg0) {
		super(arg0);
	}
	public ExtendedPrintStream(String arg0, String arg1) throws FileNotFoundException, UnsupportedEncodingException {
		super(arg0, arg1);
	}
	public ExtendedPrintStream(String arg0) throws FileNotFoundException {
		super(arg0);
	}

	public void print(Map<? extends Object, ? extends Object> map) {
		String mapRepresentation = MapUtil.toString(map);
		super.print(mapRepresentation);
	}
}