package project;

import java.util.ArrayList;

public abstract class Handler {
	protected DatabaseHandler db;
	//constructor
	public Handler(DatabaseHandler db) {
		this.db = db;
	}
	//get status
	public Boolean getTableStatus(int id) {
		// get all tables from database and see if the table is reserved
		ArrayList<TableDTO> tables = db.getTables();
		for (TableDTO table : tables) {
            if (table.getTableID() == id) {
                return table.getStatus();
            }
		}
		return null;
	}
	//get table by id
		public TableDTO getTable(int id) {
			// Use the DatabaseHandler instance to get the table
			ArrayList<TableDTO> arr = db.getTables();
			for (TableDTO t : arr) {
				if (t.getTableID() == id) {
					return t;
				}
			}
			return null;
		}
		//get customer by id
		public CustomerDTO getCustomer(int id) {
			// Use the DatabaseHandler instance to get the customer
			ArrayList<CustomerDTO> arr = db.getCustomer();
			for (CustomerDTO c : arr) {
				if (c.getId() == id) {
					return c;
				}
			}
			return null;
		}
		//get reservation by id
		public ReservationDTO getReservation(int id) {
			// Use the DatabaseHandler instance to get the reservation
			ArrayList<ReservationDTO> arr = db.getReservations();
			for (ReservationDTO r : arr) {
				if (r.getReservationID() == id) {
					return r;
				}
			}
			return null;
		}
		//cancel reservation
		public Boolean cancelReservation(int reservationID) {
			ReservationDTO res = getReservation(reservationID);
			if (res == null) {
				System.out.println("Reservation not found.");
				return false;
			}
			//check unpaid bill
			CashPayment payment = getCashPayment(res.getReservationID());
			OnlinePayment onlinePayment = getOnlinePayment(res.getReservationID());
			if (payment != null && payment.getStatus() == false) {
				System.out.println("Customer has an unpaid bill.");
				return false;
			}
			if (onlinePayment != null && onlinePayment.getStatus() == false) {
				System.out.println("Customer has an unpaid bill.");
				return false;
			}
			// Use the DatabaseHandler instance to cancel the reservation
			db.deleteReservation(reservationID);
			// Update the table status to available
			TableDTO table = getTable(res.getTableID());
			if (table == null) {
				System.out.println("Table not found.");
				return false;
			}
			freeTable(table.getTableID());
			return true;
		}
		// get payment by reservation id
		public  CashPayment getCashPayment(int ReservationID ) {
			// Use the DatabaseHandler instance to get the payment
			ArrayList<CashPayment> arr = db.getCashPayments();
			for (CashPayment p : arr) {
				if (p.getReservationID() == ReservationID && p.getStatus() == false) {
					return p;
				}
			}
			return null;
		}
		// get payment by reservation id
		public OnlinePayment getOnlinePayment(int ReservationID) {
			// Use the DatabaseHandler instance to get the payment
			ArrayList<OnlinePayment> arr = db.getOnlinePayments();
			for (OnlinePayment p : arr) {
				if (p.getReservationID() == ReservationID && p.getStatus() == false) {
					return p;
				}
			}
			return null;
		}
		// get cash payment by customerID
		public ArrayList<CashPayment> getCashPaymentByCustomerID(int CustomerID) {
			// Use the DatabaseHandler instance to get the payment
			ArrayList<CashPayment> arr = db.getCashPayments();
			ArrayList<ReservationDTO> arr2 = db.getReservations();
			for (CashPayment p : arr) {
				for (ReservationDTO r : arr2) {
					if (p.getReservationID() == r.getReservationID()) {
						if (r.getCustomerID() == CustomerID) {
							continue;
						}
						else
						{
							arr.remove(p);
						}
					}
				}
			}
			return arr;
		}
		// get online payment by cutomerID
		public ArrayList<OnlinePayment> getOnlinePaymentByCustomerID(int CustomerID) {
			// Use the DatabaseHandler instance to get the payment
			ArrayList<OnlinePayment> arr = db.getOnlinePayments();
			ArrayList<ReservationDTO> arr2 = db.getReservations();
			for (OnlinePayment p : arr) {
				for (ReservationDTO r : arr2) {
					if (p.getReservationID() == r.getReservationID()) {
						if (r.getCustomerID() == CustomerID) {
							continue;
						} else {
							arr.remove(p);
						}
					}
				}
			}
			return arr;
		}
		// check for unpaid bills
		public Boolean checkUnpaidBills(int CustomerID) {
			ArrayList<CashPayment> cashPayments = getCashPaymentByCustomerID(CustomerID);
			ArrayList<OnlinePayment> onlinePayments = getOnlinePaymentByCustomerID(CustomerID);
			for (CashPayment c : cashPayments) {
				if (c.getStatus() == false) {
					return false;
				}
			}
			for (OnlinePayment o : onlinePayments) {
				if (o.getStatus() == false) {
					return false;
				}
			}
			return true;
		}
		//get all availible tables
		public ArrayList<TableDTO> getAvailableTables() {
			ArrayList<TableDTO> tables = db.getTables();
			ArrayList<TableDTO> availableTables = new ArrayList<>();
			for (TableDTO t : tables) {
				if (t.getStatus() == true) {
					availableTables.add(t);
				}
			}
			return availableTables;
		}
		public ReservationDTO getReservationByPhoneNumber(String phoneNumber) {
		    ArrayList<ReservationDTO> reservations = db.getReservations();
		    ArrayList<CustomerDTO> customers = db.getCustomer();

		    for (ReservationDTO reservation : reservations) {
		        for (CustomerDTO customer : customers) {
		            // Check if the customer matches the reservation
		            if (customer.getId() == reservation.getCustomerID()) {
		                // If the phone number matches, return the reservation
		                if (customer.getPhoneNumber().equals(phoneNumber)) {
		                    return reservation;  // Return the first matching reservation
		                }
		            }
		        }
		    }
		    // Return null if no reservation is found
		    return null;
		}
		//free table
		public Boolean freeTable(int tableID) {
			ArrayList<TableDTO> tables = db.getTables();
			//get reservations
			ArrayList<ReservationDTO> reservations = db.getReservations();
			
			for (TableDTO t : tables) 
			{
				if (t.getTableID() == tableID) 
				{
					//check if any reservation has that table
					for (ReservationDTO r : reservations) 
					{
						if (r.getTableID() == tableID) 
						{
							//cannot set table to free if it has a reservation
							return false;
						}
					}
					//set table to free
					db.updateTable(tableID, t.getCapacity(), true);
					return true;
				}
			}
			return false;
		}
		//is table available
		public Boolean isTableAvailable(int tableID , String date, String time) 
		{
			TableDTO table = getTable(tableID);
			if (table == null) {
				System.out.println("Table not found.");
				return false;
			}
			//get reservations
			ArrayList<ReservationDTO> reservations = db.getReservations();
			for (ReservationDTO r : reservations) {
				//split reservation Date time
				String[] reservationDateTime = r.getReservationDateTime().split(" ");
				//check if table is reserved
				if (r.getTableID() == tableID && reservationDateTime[0].equals(date)
						&& reservationDateTime[1].equals(time)) {
					return false;
				}
			}
			return true;
		}
}
