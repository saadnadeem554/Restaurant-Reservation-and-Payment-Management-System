package project;

//CashPayment Class
public class CashPayment extends Payment {
 public CashPayment(int paymentID, boolean status, int ReservationID, double amount, String date) {
     super(paymentID, status, ReservationID,amount, date);
     setType("Cash");
 }

 public void setStatus() {
     // No implementation
 }
}
