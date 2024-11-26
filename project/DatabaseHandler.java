package project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.Timestamp;

public class DatabaseHandler {
    private Connection connection;
    // constructor
	public DatabaseHandler() {
		connect();
		createTablesIfNotExist(connection);
	}
    public void connect() {
        try {
        	  String url = "jdbc:sqlserver://DESKTOP-M12NPKF\\SQLEXPRESS:1433;databaseName=RestaurantManagementSystem;integratedSecurity=true;encrypt=true;trustServerCertificate=true;applicationIntent=ReadWrite;instanceName=SQLEXPRESS;";            connection = DriverManager.getConnection(url);
            System.out.println("Connected to Database");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // table creation
    public static void createTablesIfNotExist(Connection connection) {
        try (Statement statement = connection.createStatement()) {

            // Create Customer Table
            statement.execute("""
                IF NOT EXISTS (
                    SELECT * FROM sysobjects WHERE name='Customer' AND xtype='U'
                )
                CREATE TABLE Customer (
                    CustomerID INT IDENTITY(1,1) PRIMARY KEY,
                    Name VARCHAR(100) NOT NULL,
                    Email VARCHAR(255) UNIQUE NOT NULL,
                    PhoneNumber VARCHAR(15) UNIQUE NOT NULL
                )
            """);

            // Create RestaurantTable Table
            statement.execute("""
                IF NOT EXISTS (
                    SELECT * FROM sysobjects WHERE name='RestaurantTable' AND xtype='U'
                )
                CREATE TABLE RestaurantTable (
                    TableID INT IDENTITY(1,1) PRIMARY KEY,
                    Capacity INT NOT NULL,
                    Status BIT DEFAULT 1 -- '1' represents Available
                )
            """);

            // Create Reservation Table
            statement.execute("""
                IF NOT EXISTS (
                    SELECT * FROM sysobjects WHERE name='Reservations' AND xtype='U'
                )
                CREATE TABLE Reservations (
                    ReservationID INT IDENTITY(1,1) PRIMARY KEY,
                    CustomerID INT NOT NULL,
                    TableID INT,
                    ReservationTime DATETIME NOT NULL,
                    ConfirmationSent BIT DEFAULT 0, -- '0' represents FALSE
                    FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID) ON DELETE CASCADE,
                    FOREIGN KEY (TableID) REFERENCES RestaurantTable(TableID) ON DELETE SET NULL
                )
            """);

            // Create Feedback Table with decimal rating
            statement.execute("""
                IF NOT EXISTS (
                    SELECT * FROM sysobjects WHERE name='Feedback' AND xtype='U'
                )
                CREATE TABLE Feedback (
                    FeedbackID INT IDENTITY(1,1) PRIMARY KEY,
                    Description TEXT,
                    Rating DECIMAL(3, 1) CHECK (Rating >= 0 AND Rating <= 10)
                )
            """);

            // Create Payment Table
            statement.execute("""
                IF NOT EXISTS (
                    SELECT * FROM sysobjects WHERE name='Payments' AND xtype='U'
                )
                CREATE TABLE Payments (
                    PaymentID INT IDENTITY(1,1) PRIMARY KEY,
                    ReservationID INT,
                    Amount DECIMAL(10, 2) NOT NULL,
                    PaymentType VARCHAR(10) CHECK (PaymentType IN ('Cash', 'Online')),
                    PaymentStatus BIT DEFAULT 0, -- '0' represents FALSE,
                    PaymentDate DATETIME DEFAULT GETDATE(),
                    FOREIGN KEY (ReservationID) REFERENCES Reservations(ReservationID) ON DELETE SET NULL,
                )
            """);

            // Create SMSLog Table
            statement.execute("""
                IF NOT EXISTS (
                    SELECT * FROM sysobjects WHERE name='SMSLog' AND xtype='U'
                )
                CREATE TABLE SMSLog (
                    LogID INT IDENTITY(1,1) PRIMARY KEY,
                    PhoneNumber VARCHAR(15) NOT NULL,
                    Message TEXT NOT NULL,
                    SentTime DATETIME DEFAULT GETDATE(),
                    Status BIT DEFAULT 1 -- '1' represents TRUE
                )
            """);
            
            //CREATE Log table
			statement.execute("""
					    IF NOT EXISTS (
					        SELECT * FROM sysobjects WHERE name='Log' AND xtype='U'
					    )
					    CREATE TABLE Log (
					        LogID INT IDENTITY(1,1) PRIMARY KEY,
					        LogTime DATETIME DEFAULT GETDATE()
					    )
					""");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // method to add table to the database
	public void addTable(int capacity) {
		String query = "INSERT INTO RestaurantTable (Capacity,Status) VALUES (?,1)";
		try {
			var statement = connection.prepareStatement(query);
			statement.setInt(1, capacity);
			statement.executeUpdate();
			System.out.println("Table added successfully");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	// method to delete table from the database
	public void deleteTable(int tableId) {
		String query = "DELETE FROM RestaurantTable WHERE TableId = ?";
		try {
			var statement = connection.prepareStatement(query);
			statement.setInt(1, tableId);
			statement.executeUpdate();
			System.out.println("Table deleted successfully");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	// method to insert customer into the database
	public void insertCustomer(String name, String phone, String email) {
		String query = "INSERT INTO Customer (Name, PhoneNumber, Email) VALUES (?, ?, ?)";
		try {
			var statement = connection.prepareStatement(query);
			statement.setString(1, name);
			statement.setString(2, phone);
			statement.setString(3, email);
			statement.executeUpdate();
			System.out.println("Customer added successfully");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	// method to delete customer from the database
	public void deleteCustomer(int customerId) {
		String query = "DELETE FROM Customer WHERE CustomerId = ?";
		try {
			var statement = connection.prepareStatement(query);
			statement.setInt(1, customerId);
			statement.executeUpdate();
			System.out.println("Customer deleted successfully");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	// method to insert reservation into the database
	public void insertReservation(int tableId, int customerId, String date, String time) {
	    String query = "INSERT INTO Reservations (TableID, CustomerID, ReservationTime) VALUES (?, ?, ?)";
	    try (PreparedStatement statement = connection.prepareStatement(query)) {
	        // Combine date and time
	        String dateTimeString = date + " " + time; // Example: "2024-11-22 18:30:00"
	        Timestamp reservationTimestamp = Timestamp.valueOf(dateTimeString);

	        // Set query parameters
	        statement.setInt(1, tableId);
	        statement.setInt(2, customerId);
	        statement.setTimestamp(3, reservationTimestamp);

	        // Execute the query
	        statement.executeUpdate();
	        System.out.println("Reservation added successfully");
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } catch (IllegalArgumentException e) {
	        System.out.println("Invalid date or time format. Please use 'yyyy-MM-dd' for date and 'HH:mm:ss' for time.");
	    }
	}
	// method to delete reservation from the database
	public void deleteReservation(int reservationId) {
		String query = "DELETE FROM Reservations WHERE ReservationId = ?";
		try {
			var statement = connection.prepareStatement(query);
			statement.setInt(1, reservationId);
			statement.executeUpdate();
			System.out.println("Reservation deleted successfully");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	// method to insert feedback into the database
	public void insertFeedback(String description, double rating){
		String query = "INSERT INTO Feedback (Description, Rating) VALUES ( ?, ?)";
		try {
			var statement = connection.prepareStatement(query);
			statement.setString(1, description);
			statement.setDouble(2, rating);
			statement.executeUpdate();
			System.out.println("Feedback added successfully");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	// method to delete feedback from the database
	public void deleteFeedback(int feedbackId) {
		String query = "DELETE FROM Feedback WHERE FeedbackId = ?";
		try {
			var statement = connection.prepareStatement(query);
			statement.setInt(1, feedbackId);
			statement.executeUpdate();
			System.out.println("Feedback deleted successfully");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// method to insert payment into the database
	public void insertPayment(int ReservationID, double amount, String date, String type) {
		String query = "INSERT INTO Payments (ReservationID, Amount, PaymentDate, PaymentType) VALUES (?, ?, ?, ?)";
		try {
			var statement = connection.prepareStatement(query);
			statement.setInt(1, ReservationID);
			statement.setDouble(2, amount);
			statement.setString(3, date);
			statement.setString(4, type);
			statement.executeUpdate();
			System.out.println("Payment added successfully");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	// method to delete payment from the database
	public void deletePayment(int paymentId) {
		String query = "DELETE FROM Payments WHERE PaymentId = ?";
		try {
			var statement = connection.prepareStatement(query);
			statement.setInt(1, paymentId);
			statement.executeUpdate();
			System.out.println("Payment deleted successfully");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
    
    public void disconnect() {
        try {
            if (connection != null) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //update functions
    // update table
	public void updateTable(int id, int capacity, boolean status) 
	{
		String query = "UPDATE RestaurantTable SET Capacity = ?, Status = ? WHERE TableID = ?";
        try {
            var statement = connection.prepareStatement(query);
            statement.setInt(1, capacity);
            statement.setBoolean(2, status);
            statement.setInt(3, id);
            statement.executeUpdate();
            System.out.println("Table updated successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
	//update reservation
	public void updateReservation(int id, int tableId, int customerId, String date, String time) {
		String query = "UPDATE Reservations SET TableID = ?, CustomerID = ?, ReservationTime = ? WHERE ReservationID = ?";
		try {
			var statement = connection.prepareStatement(query);
			statement.setInt(1, tableId);
			statement.setInt(2, customerId);
			statement.setString(3, date + " " + time);
			statement.setInt(4, id);
			statement.executeUpdate();
			System.out.println("Reservation updated successfully");
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	//update payment
	public void updatePayment(int id, int ReservationID, double amount, String date, String type, boolean status) {
		String query = "UPDATE Payments SET ReservationID = ?, Amount = ?, PaymentType = ?, PaymentStatus = ?, PaymentDate = ? WHERE PaymentID = ?";
		try {
			var statement = connection.prepareStatement(query);
			statement.setInt(1, ReservationID);
			statement.setDouble(2, amount);
			statement.setString(3, type);
			statement.setBoolean(4, status);
			statement.setString(5, date);
			statement.setInt(6, id);
			statement.executeUpdate();
			System.out.println("Payment updated successfully");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	//update customer
	public void updateCustomer(int id, String name, String email, String phone) {
		String query = "UPDATE Customer SET Name = ?, Email = ?, PhoneNumber = ? WHERE CustomerID = ?";
		try {
			var statement = connection.prepareStatement(query);
			statement.setString(1, name);
			statement.setString(2, email);
			statement.setString(3, phone);
			statement.setInt(4, id);
			statement.executeUpdate();
			System.out.println("Customer updated successfully");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//selection functions for all tables
	public ArrayList<CustomerDTO> getCustomer() {
		String query = "SELECT * FROM Customer";
		try {
			var statement = connection.prepareStatement(query);
			statement.executeQuery();
			ResultSet resultSet = statement.getResultSet();
			//create list and return
			ArrayList<CustomerDTO> customers = new ArrayList<CustomerDTO>();
			while (resultSet.next()) 
			{
				customers.add(new CustomerDTO(resultSet.getString("Name"),resultSet.getInt("CustomerID"),resultSet.getString("Email"), resultSet.getString("PhoneNumber")));
			}
			System.out.println("Customer selected successfully");
			return customers;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	// get tables
	public ArrayList<TableDTO> getTables() {
		String query = "SELECT * FROM RestaurantTable";
		try {
			var statement = connection.prepareStatement(query);
			statement.executeQuery();
			ResultSet resultSet = statement.getResultSet();
			// create list and return
			ArrayList<TableDTO> tables = new ArrayList<TableDTO>();
			while (resultSet.next()) {
				tables.add(new TableDTO(resultSet.getInt("TableID"), resultSet.getInt("Capacity"),
						resultSet.getBoolean("Status")));
			}
			return tables;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	// get reservations
	public ArrayList<ReservationDTO> getReservations() {
		String query = "SELECT * FROM Reservations";
		try {
			var statement = connection.prepareStatement(query);
			statement.executeQuery();
			ResultSet resultSet = statement.getResultSet();
			// create list and return
			ArrayList<ReservationDTO> reservations = new ArrayList<ReservationDTO>();
			while (resultSet.next()) {
				reservations.add(new ReservationDTO(resultSet.getInt("ReservationID"), resultSet.getInt("CustomerID"),
						resultSet.getInt("TableID"), resultSet.getString("ReservationTime"),
						resultSet.getBoolean("ConfirmationSent")));
			}
			return reservations;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	// get feedback
	public ArrayList<FeedbackDTO> getFeedback() {
		String query = "SELECT * FROM Feedback";
		try {
			var statement = connection.prepareStatement(query);
			statement.executeQuery();
			ResultSet resultSet = statement.getResultSet();
			// create list and return
			ArrayList<FeedbackDTO> feedbacks = new ArrayList<FeedbackDTO>();
			while (resultSet.next()) {
				feedbacks.add(new FeedbackDTO(resultSet.getInt("FeedbackID"), resultSet.getString("Description"), resultSet.getInt("Rating")));
			}
			System.out.println("Feedback selected successfully");
			return feedbacks;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	// get cash payments
	public ArrayList<CashPayment> getCashPayments() {
		String query = "SELECT * FROM Payments WHERE PaymentType = 'Cash'";
		try {
			var statement = connection.prepareStatement(query);
			statement.executeQuery();
			ResultSet resultSet = statement.getResultSet();
			// create list and return
			ArrayList<CashPayment> payments = new ArrayList<CashPayment>();
			while (resultSet.next()) {
				payments.add(new CashPayment(resultSet.getInt("PaymentID"),resultSet.getBoolean("PaymentStatus"), resultSet.getInt("ReservationID"),
						resultSet.getDouble("Amount"), resultSet.getString("PaymentDate")));
			}
			System.out.println("Cash Payments selected successfully");
			return payments;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	// get online payments
	public ArrayList<OnlinePayment> getOnlinePayments() {
		String query = "SELECT * FROM Payments WHERE PaymentType = 'Online'";
		try {
			var statement = connection.prepareStatement(query);
			statement.executeQuery();
			ResultSet resultSet = statement.getResultSet();
			// create list and return
			ArrayList<OnlinePayment> payments = new ArrayList<OnlinePayment>();
			while (resultSet.next()) {
				payments.add(new OnlinePayment(resultSet.getInt("PaymentID"),resultSet.getBoolean("PaymentStatus"), resultSet.getInt("ReservationID"),
						resultSet.getDouble("Amount"), resultSet.getString("PaymentDate")));
			}
			System.out.println("Cash Payments selected successfully");
			return payments;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	//delete from LOG
	public void deleteLog(int logId) {
		String query = "DELETE FROM Log WHERE LogID = ?";
		try {
			var statement = connection.prepareStatement(query);
			statement.setInt(1, logId);
			statement.executeUpdate();
			System.out.println("Log deleted successfully");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	//insert into LOG
	public void insertLog(String logTime) {
		String query = "INSERT INTO Log (LogTime) VALUES (?)";
		try {
			var statement = connection.prepareStatement(query);
			statement.setString(1, logTime);
			statement.executeUpdate();
			System.out.println("Log added successfully");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	// get log
		public ArrayList<String> getLog() {
			String query = "SELECT * FROM Log";
			try {
				var statement = connection.prepareStatement(query);
				statement.executeQuery();
				ResultSet resultSet = statement.getResultSet();
				// create list and return
				ArrayList<String> logs = new ArrayList<String>();
				while (resultSet.next()) {
					logs.add(resultSet.getString("LogTime"));
				}
				System.out.println("Log selected successfully");
				return logs;
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return null;
		}
		
	// update Reservation confirmation
	public void updateReservationConfirmation(int reservationId, boolean confirmation) {
		String query = "UPDATE Reservations SET ConfirmationSent = ? WHERE ReservationID = ?";
		try {
			var statement = connection.prepareStatement(query);
			statement.setBoolean(1, confirmation);
			statement.setInt(2, reservationId);
			statement.executeUpdate();
			System.out.println("Reservation confirmation updated successfully");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
