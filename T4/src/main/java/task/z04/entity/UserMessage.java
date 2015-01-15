package task.z04.entity;

import java.util.Calendar;

public class UserMessage {

	public UserMessage(int id, int userId, Calendar creationDate, String text) {
		this.id = id;
		this.userId = userId;
		this.creationDate = creationDate;
		this.text = text;
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

	private int id;
	private int userId;
	private Calendar creationDate;
	private String text;
}
