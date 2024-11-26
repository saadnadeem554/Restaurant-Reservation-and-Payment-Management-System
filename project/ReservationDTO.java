package project;

public class ReservationDTO {
	private int reservationID;
    private int customerID;
    private int tableID;
    private String reservationDateTime;
    private Boolean ConfirmationStatus;

    public ReservationDTO(int resID,int customerID, int tableID, String reservationDateTime,Boolean ConfirmationStatus) {
    	this.reservationID = resID;
    	this.ConfirmationStatus = ConfirmationStatus;
        this.customerID = customerID;
        this.tableID = tableID;
        this.reservationDateTime = reservationDateTime;
    }

    // Getters and Setters
    public int getReservationID() { return reservationID; }
    public void setReservationID(int reservationID) { this.reservationID = reservationID; }
    public Boolean getConfirmationStatus() { return ConfirmationStatus; }
    public void setConfirmationStatus(Boolean ConfirmationStatus) { this.ConfirmationStatus = ConfirmationStatus; }
    public int getCustomerID() { return customerID; }
    public void setCustomerID(int customerID) { this.customerID = customerID; }
    public int getTableID() { return tableID; }
    public void setTableID(int tableID) { this.tableID = tableID; }
    public String getReservationDateTime() { return reservationDateTime; }
    public void setReservationDateTime(String reservationDateTime) { this.reservationDateTime = reservationDateTime; }
}
