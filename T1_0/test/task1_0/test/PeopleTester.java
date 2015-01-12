package task1_0.test;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import task1_0.People;
import task1_0.Person;

public class PeopleTester extends Assert {

	// ћетод дл€ запуска тестов в пакетном режиме
	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(PeopleTester.class);
	    for (Failure failure : result.getFailures()) {
	      System.out.println(failure.toString());
	    }
	}

	@Test
	public void generateWithoutArgsTest() {
		for (int i = 0; i < 100; i++) {
			Person person = personFabric.generate();
			assertNotNull("Person object has been generated incorrectly : person age is out of range", person);
			System.out.println(person);
		}
	}

	@Test
	public void generateWithArgsTest() {
		// ѕровер€ем корректность работы метода при выходе за границы диапазона значений возраста :
		Person person = personFabric.generate("Jack", "Johnson", Person.MIN_AGE - 1);
		assertNull("Age restriction durring creating the Person object doesn't work correctly : person age less than min", person);
		person = personFabric.generate("Alex", "Stewart", Person.MAX_AGE + 1);
		assertNull("Age restriction durring creating the Person object doesn't work correctly : person age more than max", person);

		// ѕолное покрытие данными :
		for (int i = Person.MIN_AGE; i <= Person.MAX_AGE; i++) {
			person = personFabric.generate("Jack", "Johnson", i);
			assertNotNull("Unable to create person object with the age of " + i, person);
		}
	}

	private static final People personFabric = new People();
}