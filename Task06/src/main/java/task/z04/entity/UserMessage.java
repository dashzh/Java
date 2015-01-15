package task.z04.entity;

import java.util.Calendar;

public class UserMessage implements Comparable<UserMessage> {

	public UserMessage(int id, int userId, Calendar creationDate, String text) {
		this.id = id;
		this.userId = userId;
		this.creationDate = creationDate;
		this.text = text;
	}

	public AppUser getAuthor() {
		return author;
	}
	public void setAuthor(AppUser author) {
		this.author = author;
	}

	public int getId() {
		return id;
	}
	public int getUserId() {
		return userId;
	}
	public Calendar getCreationDate() {
		return creationDate;
	}
	public String getText() {
		return text;
	}

	@Override
	public int compareTo(UserMessage other) {
		return creationDate.compareTo(other.creationDate);
	}

	private int id;
	private int userId;
	private Calendar creationDate;
	private String text;
	private AppUser author;
}
