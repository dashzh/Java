package task1_0;

import java.util.Random;

public class People {

	//Генерация новогоэкземпляра класса Person.
	 
	public Person generate() {
		Random random = new Random(System.nanoTime());
		String firstName = firstNames[random.nextInt(firstNames.length)];
		String lastName = lastNames[random.nextInt(lastNames.length)];
		int age = random.nextInt(Person.MAX_AGE + 1) + Person.MIN_AGE;
		try {
			return new Person(firstName, lastName, age);	
		} catch (WrongAgeValueException e) {
			e.printStackTrace();
			return null;
		}
	}

	
	 // Генерация нового экземпляра класса Person с указанными параметрами:
	// новый объект Person/null (некорректный возраст).
	public Person generate(String firstName, String lastName, int age) {
		try {
			return new Person(firstName, lastName, age);
		} catch (WrongAgeValueException e) {
			return null;			
		}
	}


	private static final String[] firstNames = new String[] {
		"Billy", "Eugene", "Alex", "Anton", "Andrew", "Martan", "Jack", "Emil", "Tom", "Keith", "Sergey",
	};
		
	private static final String[] lastNames = new String[] {
		"Smirnov", "Ivanov", "Furcad", "Kozlov", "Shipulin", "Svendsen", "Lapshin", "Legkov", "Garanichev", "Loginov",
	};
}
