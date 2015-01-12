package task1;

/** Общий вид квадратного уравнения : aX*X + bX + c = 0, где a, b, c - коэффициенты;
 */
public class Resolver {
	/**
	 * Создает новое квадратное уравнение общего вида.
	 * @param a коэффициент при x*x, a != 0;
	 * @param b коэффициент при x;
	 * @param c свободный коэффициент;
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
	 * Решает квадратное уравнение представленное данным экземпляром класса.
	 * @return кортеж с найденными корнями уравнения или null в случае отсутствия корней.
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
	 * Вычисляет дискриминант квадратного уравнения вычесленный на основании указанных коэффициентов.
	 * @param a коэффициент при x*x;
	 * @param b коэффициент при x;
	 * @param c свободный коэффициент;
	 * @return Дискриминант квадратного уравнения вычесленный на основании указанных коэффициентов.
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