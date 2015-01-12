package task1_0;

import java.util.Random;

/**
 * ������� ��� �������� �������� Person
 */
public class People {

	/**
	 * ���������� ����� ��������� ������ Person.
	 * @return ����� ������ Person.
	 */
	public Person generate() {
		// ���������� �������� ������� ����������� ������ Java ��� �������� ���������� ��������� ����� �
		// ����� ��� ������������� ���������
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
	 * ���������� ����� ��������� ������ Person � ���������� �����������.
	 * 
	 * @param firstName ���;
	 * @param lastName �������;
	 * @param age ������� � �������� �� {@link task.z01.Person#MIN_AGE} �� {@link task.z01.Person#MAX_AGE}. 
	 * @return ����� ������ Person ��� null � ������ ����� ������� ������ �����������.
	 */
	public Person generate(String firstName, String lastName, int age) {
		try {
			return new Person(firstName, lastName, age);
		} catch (WrongAgeValueException e) {
			return null;			
		}
	}

	// ����������� ��� ��� ��������� �������� Person �� ��������� 
	private static final String[] firstNames = new String[] {
		"Billy", "Eugene", "Alex", "Anton", "Andrew", "Martan", "Jack", "Emil", "Tom", "Keith", "Sergey",
	};
	// ����������� ������� ��� ��������� �������� Person �� ���������	
	private static final String[] lastNames = new String[] {
		"Smirnov", "Ivanov", "Furcad", "Kozlov", "Shipulin", "Svendsen", "Lapshin", "Legkov", "Garanichev", "Loginov",
	};
}