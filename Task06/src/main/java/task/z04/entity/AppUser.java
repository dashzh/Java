package task.z04.entity;

import java.util.Calendar;

public class AppUser implements Comparable<AppUser> {

	public AppUser(int id, String name, String lastName, Calendar lastLogonDate) {
		this.id = id;
		this.name = name;
		this.lastName = lastName;
		this.lastLogonDate =  lastLogonDate;
	}

	public Calendar getLastLogonDate() {
		return lastLogonDate;
	}
	public String getName() {
		return name;
	}
	public String getLastName() {
		return lastName;
	}
	public int getId() {
		return id;
	}

	private final Calendar lastLogonDate;
	private final String name;
	private final String lastName;
	private final int id;

	@Override
	public int compareTo(AppUser other) {
		return lastLogonDate.compareTo(other.lastLogonDate);
	}
}
