package DAL;

//Payment Class
public abstract class Payment {
	//Attributes
	private int paymentID;
	private boolean status;
	private int ReservationID;
	private double amount;
	private String type;
	private String Date;
	//constructor
	public Payment(int paymentID, boolean status, int ReservationID, double amount, String date) {
		this.paymentID = paymentID;
		this.status = status;
		this.ReservationID = ReservationID;
		this.amount = amount;
		this.Date = date;
	}
	//Getters and Setters
	public String getDate() {
		return Date;
	}

	public void setDate(String date) {
        Date = date;
	}
	public int getPaymentID() { return paymentID; }

	public void setPaymentID(int paymentID) {
		this.paymentID = paymentID;
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public int getReservationID() {
		return ReservationID;
	}

	public void setReservationID(int ReservationID) {
		this.ReservationID = ReservationID;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	    
}
