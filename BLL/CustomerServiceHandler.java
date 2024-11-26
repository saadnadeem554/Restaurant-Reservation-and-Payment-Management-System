package BLL;

import java.util.ArrayList;
import DAL.*;
import smsManagement.SmsService;

public class CustomerServiceHandler extends Handler
{
	public CustomerServiceHandler() {
		super();
	}

	public int addCustomer(String name, String phoneNumber, String emailAddress) {
		if (name == null || name.isEmpty()) {
            System.out.println("Invalid customer name. Must not be empty.");
            return -1;
        }
		// check if table already has customer with same phone or email
		ArrayList<CustomerDTO> arr = db.getCustomer();
		for(CustomerDTO c : arr)
		{
			//print phone and email
			System.out.println(c.getPhoneNumber() + " " + c.getEmailAddress());
			if(c.getPhoneNumber().equals(phoneNumber) || c.getEmailAddress().equals(emailAddress))
			{
				System.out.println("Customer already exists in DB");
				return c.getId();
			}
		}
		//print arrar size
		System.out.println(arr.size());
        // Use the DatabaseHandler instance to add the customer
        db.insertCustomer(name, phoneNumber, emailAddress);
        ArrayList<CustomerDTO> arr1 = db.getCustomer();
        return arr1.getLast().getId();
	}
	//add feedback
	public void addFeedback(String feedback, double rating) {
	    if (feedback == null || feedback.trim().isEmpty()) {
	        System.out.println("Invalid feedback. Must not be empty.");
	        return;
	    }

	    if (rating < 0 || rating > 10) {
	        System.out.println("Invalid rating. Must be between 0 and 10.");
	        return;
	    }

	    // Sanitize feedback
	    feedback = feedback.trim();

	    // Add feedback to the database
	    db.insertFeedback(feedback, rating);
	}
	//add reservation
	public Boolean addReservation(String name, String phoneNumber, String emailAddress, int tableID, String time, String date) {
	    int id = addCustomer(name, phoneNumber, emailAddress);
	    
	    // Check for due payments
	    if (!checkUnpaidBills(id)) {
	        System.out.println("Please pay the due amount before making a reservation.");
	        return false;
	    }

	    // Check if the customer already has a reservation
	    ArrayList<ReservationDTO> arr = db.getReservations();
	    for (ReservationDTO r : arr) {
	        if (r.getCustomerID() == id) {
	            System.out.println("Customer already has a reservation");
	            return false;
	        }
	    }

	    // Add the reservation to the database
	    System.out.println("Customer ID: " + id);
	    db.insertReservation(tableID, id, date, time);

	    // Update the table status to reserved
	    TableDTO table = getTable(tableID);
	    if (table == null) {
	        System.out.println("Table not found.");
	        return false;
	    }
	    db.updateTable(tableID, table.getCapacity(), false);

	    // Format the phone number
	    String formattedPhoneNumber = "+92" + phoneNumber.substring(1); // Replace first digit with +92

	    // Send SMS notification
	    try {
	        SmsService.sendSms(formattedPhoneNumber, "+19787189712", "Your reservation is confirmed!");
	    } catch (Exception e) {
	        System.out.println("Failed to send SMS: " + e.getMessage());
	        return false;
	    }

	    return true;
	}

	//paybill
	public Boolean payCardBill(int reservationID, double amount, String cardnum, String expiry, int cvv) {
		if (amount <= 0) {
			System.out.println("Invalid amount. Must be greater than 0.");
			return false;
		}
		ReservationDTO res = getReservation(reservationID);
		if (res == null) {
			System.out.println("Reservation not found.");
			return false;
		}
		
		// for card payment, insert unpaid bill and invoke payment gateway
		//check if payment with same reservationID exists
		OnlinePayment onlinePayment = getOnlinePayment(res.getReservationID());
		if (onlinePayment != null) {
			System.out.println("Bill already exists");
		}
		else
			db.insertPayment(res.getReservationID(), amount, res.getReservationDateTime(), "Online");
		// Payment gateway logic
		System.out.println("Payment gateway invoked for card payment.");
		// check for errors in cardnum, expiry and cvv
		if (!cardnum.matches("\\d{16}")) {
			System.out.println("Invalid card number. Must be 16 digits.");
			return false;
		}
		if (expiry.length() != 10) {
			System.out.println("Invalid expiry date. Must be in MM/YY format.");
			return false;
		}
		// Payment successful
		System.out.println("Payment successful.");
		OnlinePayment payment = getOnlinePayment(res.getReservationID());
		db.updatePayment(payment.getPaymentID(), res.getReservationID() , amount, res.getReservationDateTime(), "Online", true);
		db.deleteReservation(res.getReservationID());
		freeTable(res.getTableID());
		//add reservation to log
		db.insertLog(res.getReservationDateTime());
		return true;
	}
	public Boolean payCashBill(int reservationID, double amount) {
		if (amount <= 0) {
			System.out.println("Invalid amount. Must be greater than 0.");
			return false;
		}
		
		ReservationDTO res = getReservation(reservationID);
		if (res == null) {
			System.out.println("Reservation not found.");
			return false;
		}
		// check if payment with same reservationID exists
		CashPayment payment = getCashPayment(res.getReservationID());
		if (payment != null) {
			System.out.println("Bill already exists");
			return false;
		}
		db.insertPayment(res.getReservationID(), amount, res.getReservationDateTime(), "Cash");
		// Payment successful
		return true;
	}
	//
	public Boolean updateReservation(int reservationID, int tableID, int id, String time, String date) {
	    // Check if the new table is available
	    if (!isTableAvailable(tableID, date , time)) {
	        System.out.println("Table is not available at the selected time.");
	        return false;
	    }
	    // check if reservation has unpaid bill
		if (!checkUnpaidBills(id)) {
			System.out.println("Please pay the due amount before making a reservation.");
			return false;
		}

	    // Fetch current reservation details
	    ReservationDTO res = getReservation(reservationID);
	    if (res == null) {
	        System.out.println("Reservation not found.");
	        return false;
	    }

	    // Update reservation details
	    TableDTO table = getTable(tableID);
		if (table == null) {
			System.out.println("Table not found.");
			return false;
		}
		db.updateTable(tableID, table.getCapacity(), false);
	    db.updateReservation(reservationID, tableID, id, date, time);
	    // Update table statuses
	    freeTable(res.getTableID());

	    return true;
	}

	public ArrayList<TableDTO> getAvailableTables(String date, String time) {
		
		ArrayList<TableDTO> tables = db.getTables();
		ArrayList<TableDTO> availibleTables = new ArrayList<TableDTO>();
		for (TableDTO t : tables) {
			if (isTableAvailable(t.getTableID(), date, time)) {
				availibleTables.add(t);
			}
		}
		return availibleTables;
	}

}
