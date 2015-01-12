package task1;

import java.text.MessageFormat;
import java.util.Scanner;

public class Qapplication {

	public static final String QUIT_CMD = "quit";

	public static void main(String[] args) {
		System.out.println(" *****Your data***** ");
		Scanner scanner = new Scanner(System.in);

		String value = null;

		while (true) {
			try {
				System.out.print("Enter a :");
				value = scanner.next();
				float a = Float.parseFloat(value);
				
				System.out.print("Enter b :");
				value = scanner.next();
				float b = Float.parseFloat(value);
				
				System.out.print("Enter c :");
				value = scanner.next();
				float c = Float.parseFloat(value);

				Resolver qer = new Resolver(a, b, c);

				System.out.print(
					MessageFormat.format(
						"Equation {0}x^2 {3}{1}x {4}{2} = 0\n",
						String.valueOf(a), String.valueOf(b), String.valueOf(c), (b >= 1) ? "+ " : "", (c >= 0) ? "+ " : ""
					)
				);

				Result(qer.resolve());
			} catch (NumberFormatException e) {
				if (QUIT_CMD.equalsIgnoreCase(value)) {
					scanner.close();
					return;
				} else
					System.out.println(value + " is not a number");
			}
		}
	}

	private static void Result(Root<Double, Double> result) {
		if (result == null)
			System.out.print("There is no any roots: D<0 \n\n");
		else if (result.first.doubleValue() == result.second.doubleValue()) {
			System.out.print("The root : x = " + result.second.doubleValue() + ";\n\n");
		} else {
			System.out.print("The roots : x1 = " + result.first.doubleValue() + "; x2 = " + result.second.doubleValue() + ";\n\n");
		}
	}
}
