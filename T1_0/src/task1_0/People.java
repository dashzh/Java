package task1_0;

import java.util.Random;

/**
 * Фабрика для создания объектов Person
 */
public class People {

	/**
	 * Генерирует новый экземпляр класса Person.
	 * @return новый объект Person.
	 */
	public Person generate() {
		// Используем значение времени виртуальной машины Java для создания генератора случайных чисел с
		// целью его дифферентного поведения
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

	/**
	 * Генерирует новый экземпляр класса Person с указанными параметрами.
	 * 
	 * @param firstName Имя;
	 * @param lastName Фамилия;
	 * @param age Возраст в пределах от {@link task.z01.Person#MIN_AGE} до {@link task.z01.Person#MAX_AGE}. 
	 * @return новый объект Person или null в случае когда возраст указан некорректно.
	 */
	public Person generate(String firstName, String lastName, int age) {
		try {
			return new Person(firstName, lastName, age);
		} catch (WrongAgeValueException e) {
			return null;			
		}
	}

	// Репозиторий имён для генерации объектов Person по умолчанию 
	private static final String[] firstNames = new String[] {
		"Billy", "Eugene", "Alex", "Anton", "Andrew", "Martan", "Jack", "Emil", "Tom", "Keith", "Sergey",
	};
	// Репозиторий фамилий для генерации объектов Person по умолчанию	
	private static final String[] lastNames = new String[] {
		"Smirnov", "Ivanov", "Furcad", "Kozlov", "Shipulin", "Svendsen", "Lapshin", "Legkov", "Garanichev", "Loginov",
	};
}