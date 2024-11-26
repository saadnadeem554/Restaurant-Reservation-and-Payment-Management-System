package DAL;

public class FeedbackDTO {
	private int id;
	private String feedback;
	private int rating;

	public FeedbackDTO(int id, String feedback, int rating) {
		this.id = id;
		this.feedback = feedback;
		this.rating = rating;
	}

	// Getters and Setters
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFeedback() {
		return feedback;
	}

	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

}
