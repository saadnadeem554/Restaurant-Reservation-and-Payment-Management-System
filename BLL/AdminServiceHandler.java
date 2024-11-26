package BLL;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import DAL.*;

public class AdminServiceHandler extends Handler {
	public AdminServiceHandler()
	{
		super();
	}
	public void addTable(int capacity) 
	{
        if (capacity <= 0) {
            System.out.println("Invalid table capacity. Must be greater than 0.");
            return;
        }

        // Use the DatabaseHandler instance to add the table
        db.addTable(capacity);
    }
	// get all tables
	public ArrayList<TableDTO> getAllTables() {
		ArrayList<TableDTO> tables = db.getTables();
		return tables;
	}
	
	public ArrayList<CustomerDTO> getAllCustomers() {
		ArrayList<CustomerDTO> customers = db.getCustomer();
		return customers;
	}
	
	public void deleteTable(int id)
	{
		// if table reserved, cannot delete
		if (getTableStatus(id) == false) {
			System.out.println("Table is reserved and cannot be deleted.");
			return;
		}
		db.deleteTable(id);
	}

	public void updateTable(int id, int capacity, Boolean status) {
		db.updateTable(id, capacity, status);
	}
	public ArrayList<ReservationDTO> ViewAllReservations() {
		ArrayList<ReservationDTO> reservations = db.getReservations();
		if (reservations.isEmpty() || reservations == null) {
			//System.out.println("No reservations found.");
			return null;
		}
		return reservations;
	}
	// get all feedback
	public ArrayList<FeedbackDTO> getAllFeedback() {
		ArrayList<FeedbackDTO> feedback = db.getFeedback();
		return feedback;
	}
	// verify cash payment
	public Boolean verifyCashPayment(int reservationID) 
	{
		ReservationDTO res = getReservation(reservationID);
		CashPayment payment = getCashPayment(reservationID);
		if (payment == null) {
			System.out.println("Payment not found");
			return false;
		}
		db.updatePayment(payment.getPaymentID(), res.getReservationID() , payment.getAmount(), res.getReservationDateTime(), "Cash", true);
		db.deleteReservation(res.getReservationID());
		freeTable(res.getTableID());
		//add reservation to log
		db.insertLog(res.getReservationDateTime());
		return true;
	}

	public ArrayList<CashPayment> getAllCashPayments() {
		ArrayList<CashPayment> cashPayments = db.getCashPayments();
		if (cashPayments.isEmpty() || cashPayments == null) {
			System.out.println("No cash payments found.");
			return null;
		}
		return cashPayments;
	}
	//generate report on reservations made each month
	public Map<String, Integer> generateReport() {
	    ArrayList<String> reservations = db.getLog();
		if (reservations.isEmpty() || reservations == null) {
			//System.out.println("NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN.");
			return null;
		}
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

	    // Initialize a map to store counts for each of the past 6 months
	    Map<String, Integer> monthCounts = new HashMap<>();
	    LocalDate today = LocalDate.now();

	    // Get the past 6 months
	    for (int i = 0; i < 6; i++) {
	        LocalDate month = today.minusMonths(i);
	        String monthKey = month.getMonth() + " " + month.getYear();
	        monthCounts.put(monthKey, 0);
	    }

	    // Count reservations for each month
	    for (String res : reservations) {
	        // Parse the reservation time to LocalDateTime
	        LocalDateTime resDateTime = LocalDateTime.parse(res, formatter);
	        LocalDate resDate = resDateTime.toLocalDate(); // Get only the date part
	        String monthKey = resDate.getMonth() + " " + resDate.getYear();

	        if (monthCounts.containsKey(monthKey)) {
	            monthCounts.put(monthKey, monthCounts.get(monthKey) + 1);
	        }
	    }

	    // Print the report
	    System.out.println("Reservation Report (Past 6 Months):");
	    for (Map.Entry<String, Integer> entry : monthCounts.entrySet()) {
	        System.out.println(entry.getKey() + ": " + entry.getValue() + " reservations");
	    }
	    return monthCounts;
	}
	//gererate report on feedback
	public ArrayList<Integer> generateFeedbackReport() {
		ArrayList<FeedbackDTO> feedback = db.getFeedback();
		if (feedback.isEmpty() || feedback == null) {
			System.out.println("No feedback found.");
			return null;
		}
		int total = feedback.size();
		int positive = 0;
		int negative = 0;
		for (FeedbackDTO f : feedback) {
			if (f.getRating() >= 2) {
				positive++;
			} else {
				negative++;
			}
		}
		// return the data
		ArrayList<Integer> report = new ArrayList<>();
		report.add(total);
		report.add(positive);
		report.add(negative);
		return report;
		
	}
	
	public ArrayList<Double> generatePaymentReport() {
	    ArrayList<CashPayment> cp = db.getCashPayments(); // Assume these return all CashPayments
	    ArrayList<OnlinePayment> op = db.getOnlinePayments(); // Assume these return all OnlinePayments
	    ArrayList<CashPayment> confirmedCashPayments = new ArrayList<>();
	    ArrayList<OnlinePayment> confirmedOnlinePayments = new ArrayList<>();

	    // Filter for confirmed payments
	    for (CashPayment payment : cp) {
	        if (payment.getStatus()) { // Assuming a getter `isPaymentStatus()`
	            confirmedCashPayments.add(payment);
	        }
	    }
	    for (OnlinePayment payment : op) {
	        if (payment.getStatus()) { // Assuming a getter `isPaymentStatus()`
	            confirmedOnlinePayments.add(payment);
	        }
	    }

	    // Calculate totals
	    int totalConfirmedPayments = 0;
	    totalConfirmedPayments = confirmedCashPayments.size() + confirmedOnlinePayments.size();
	    int confirmedCashCount = 0;
	    confirmedCashCount= confirmedCashPayments.size();
	    int confirmedOnlineCount =0;
	    confirmedOnlineCount= confirmedOnlinePayments.size();

	    // Calculate revenue over past 6 months
	    LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6);
	    double cashRevenue = 0.0;
	    double onlineRevenue = 0.0;

	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

	    for (CashPayment payment : confirmedCashPayments) {
	        LocalDateTime paymentDate = LocalDateTime.parse(payment.getDate(), formatter); // Assuming `getPaymentDate` returns a date string
	        if (paymentDate.toLocalDate().isAfter(sixMonthsAgo)) {
	            cashRevenue += payment.getAmount(); // Assuming `getAmount()` returns the payment amount
	        }
	    }
	    for (OnlinePayment payment : confirmedOnlinePayments) {
	        LocalDateTime paymentDate = LocalDateTime.parse(payment.getDate(), formatter); // Assuming `getPaymentDate` returns a date string
	        if (paymentDate.toLocalDate().isAfter(sixMonthsAgo)) {
	            onlineRevenue += payment.getAmount(); // Assuming `getAmount()` returns the payment amount
	        }
	    }

	    double totalRevenue = cashRevenue + onlineRevenue;

	    // Print the report
	    System.out.println("Payment Report:");
	    System.out.println("Total confirmed payments: " + totalConfirmedPayments);
	    System.out.println("Confirmed cash payments: " + confirmedCashCount);
	    System.out.println("Confirmed online payments: " + confirmedOnlineCount);
	    System.out.printf("Revenue generated in past 6 months: \n Cash: %.2f\n Online: %.2f\n Total: %.2f\n", 
	        cashRevenue, onlineRevenue, totalRevenue);
	    ArrayList<Double> report = new ArrayList<>();
	    report.add(totalRevenue);
	    report.add(cashRevenue);
	    report.add(onlineRevenue);
	    report.add((double) totalConfirmedPayments);
	    report.add((double) confirmedCashCount);
	    report.add((double) confirmedOnlineCount);
	    return report;
	}
	

}

	

