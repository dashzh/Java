package task1;

/** ����� ��� ����������� ��������� : aX*X + bX + c = 0, ��� a, b, c - ������������;
 */
public class Resolver {
	/**
	 * ������� ����� ���������� ��������� ������ ����.
	 * @param a ����������� ��� x*x, a != 0;
	 * @param b ����������� ��� x;
	 * @param c ��������� �����������;
	 */
	public Resolver(float a, float b, float c) {
		if (a == 0)
			throw new IllegalArgumentException("a equals zero");
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	public float getA() { return a; }

	public float getB() { return b; }

	public float getC() { return c; }

	/**
	 * ������ ���������� ��������� �������������� ������ ����������� ������.
	 * @return ������ � ���������� ������� ��������� ��� null � ������ ���������� ������.
	 */
	public Root<Double, Double> resolve() {
		if (!isResolved) {
			double discr = Discriminant();
		
			if (discr > 0) {
				double discrSqrt = Math.sqrt(discr);
				double x1 = (-b + discrSqrt) / (2d * a);
				double x2 = (-b - discrSqrt) / (2d * a);
				result = new Root<Double, Double>(x1, x2);
			} else if (discr == 0) {
				double x = - b / (2d * a);
				result = new Root<Double, Double>(x, x);
			} else
				result = null;
		}
			isResolved = true;
		
		return result;
	}

	/**
	 * ��������� ������������ ����������� ��������� ����������� �� ��������� ��������� �������������.
	 * @param a ����������� ��� x*x;
	 * @param b ����������� ��� x;
	 * @param c ��������� �����������;
	 * @return ������������ ����������� ��������� ����������� �� ��������� ��������� �������������.
	 */
	public double Discriminant() {
		return (double) b * b - 4d * a * c;
	}

	private float a;
	private float b;
	private float c;
	private Root<Double, Double> result;
	private boolean isResolved;
}