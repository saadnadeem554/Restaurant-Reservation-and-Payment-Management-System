package project;

//OnlinePayment Class
public class OnlinePayment extends Payment {
 public OnlinePayment(int paymentID, boolean status, int ReservationID, double amount, String date) {
     super(paymentID, status, ReservationID, amount, date);
     setType("Online");
 }

 public void setStatus() {
     // No implementation
 }
}
