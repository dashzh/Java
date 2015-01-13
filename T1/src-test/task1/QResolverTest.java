package task1;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import task1.Resolver;
import task1.Root;

public class QResolverTest extends Assert {

	// Delta - для тестирования чисел с плавающей запятой 
	public static final float DELTA = 0.00001f; 
	
	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(QResolverTest.class);
	    for (Failure failure : result.getFailures()) {
	      System.out.println(failure.toString());
	    }
	}

	@Test
	public void testConstructorAndGetters()
    {
        Resolver q = new Resolver(2.0f, 3.0f, 4.0f);
        
        assertEquals(2.0f, q.getA(), DELTA);
        assertEquals(3.0f, q.getB(), DELTA);
        assertEquals(4.0f, q.getC(), DELTA);
    }

	@Test
    public void testNumRealRoots()
    {
		Resolver q = new Resolver(1.0f, 5.0f, -14.0f);
		Root<Double, Double> qr = q.resolve();
		double qrx1 = qr.first.doubleValue();
		double qrx2 = qr.second.doubleValue();
        assertTrue(qrx1 != qrx2);
		
        Resolver q2 = new Resolver(1.0f, 6.0f, 9.0f);
		Root<Double, Double> q2r = q2.resolve();
		double qr2x1 = q2r.first.doubleValue();
		double qr2x2 = q2r.second.doubleValue();
        assertTrue(qr2x1 == qr2x2);
        
        Resolver q3 = new Resolver(1.0f, 5.0f, 13.0f);
        Root<Double, Double> q3r = q3.resolve();
        assertNull(q3r);
    }
	
	@Test
    public void testCalcDiscriminant()
    {
        Resolver q = new Resolver(1.0f, 5.0f, -14.0f);
        assertEquals((25 - (4 * (-14))), q.Discriminant(), DELTA);
    }
    
	@Test
    public void testCalcDiscriminant1()
    {
        Resolver q2 = new Resolver(1.0f, 6.0f, 9.0f);
        assertEquals((36.0 - (4.0 * 9.0)), q2.Discriminant(), DELTA);
    }
    
	@Test
    public void testCalcDiscriminant2()
    {
        Resolver q3 = new Resolver(1.0f, 5.0f, 13.0f);
        assertEquals((25.0 - (4.0 * 13.0)), q3.Discriminant(), DELTA);
    }
}
