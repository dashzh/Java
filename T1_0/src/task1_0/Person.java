package task1_0;

import java.text.MessageFormat;

public class Person {

	// Константы для определения порогов минимального и максимального возраста
	public static final int MIN_AGE = 0;
	public static final int MAX_AGE = 120;

	public Person() {}

	public Person(String firstName, String lastName, int age) throws WrongAgeValueException {
		setAge(age);
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public int getAge() {
		return age;
	}
	public void setAge(int age) throws WrongAgeValueException {
		if (age < MIN_AGE || age > MAX_AGE)
			throw new WrongAgeValueException();
		this.age = age;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String toString() {
		return MessageFormat.format("{0} {1}, {2};", firstName, lastName, String.valueOf(age));
	}
	
	private int age;
	private String firstName;
	private String lastName;
}