package UI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import BLL.AdminServiceHandler;
import DAL.CashPayment;
import DAL.CustomerDTO;
import DAL.FeedbackDTO;
import DAL.ReservationDTO;
import DAL.TableDTO;

public class AdminController {
	
	public class TagValueRow {
	    private final SimpleStringProperty tag;
	    private final SimpleDoubleProperty value;

	    public TagValueRow(String tag, Double value) {
	        this.tag = new SimpleStringProperty(tag);
	        this.value = new SimpleDoubleProperty(value);
	    }
	    // Getters
		public String getTag() {
			return tag.get();
		}

		public Double getValue() {
			return value.get();
		}
	}
	public class ReservationDetails{
		private final SimpleIntegerProperty ReservationID;
		private final SimpleStringProperty customerName;
        private final SimpleStringProperty reservationTime;
        private final SimpleIntegerProperty tableCapacity;
        private final SimpleBooleanProperty confirmationSent;
        
        public ReservationDetails(int reservationID,String customerName, String reservationTime, int tableCapacity, Boolean confirmationSent) {
        	this.ReservationID = new SimpleIntegerProperty(reservationID);
            this.customerName = new SimpleStringProperty(customerName);
            this.reservationTime = new SimpleStringProperty(reservationTime);
            this.tableCapacity = new SimpleIntegerProperty(tableCapacity);
            this.confirmationSent = new SimpleBooleanProperty(confirmationSent);
        }
        // Getters
        public Integer getReservationID() {
            return ReservationID.get();
        }

        public String getCustomerName() {
            return customerName.get();
        }

        public Integer getTableCapacity() {
            return tableCapacity.get();
        }

        public String getReservationTime() {
            return reservationTime.get();
        }

        public Boolean isConfirmationSent() { // Boolean getters use "is"
            return confirmationSent.get();
        }
    }
	
	// Class to represent feedback comments
	public class Feedback {
	    private final SimpleIntegerProperty Rating;
	    private final SimpleStringProperty comment;

	    public Feedback(int Rating, String comment) {
	        this.Rating = new SimpleIntegerProperty(Rating);
	        this.comment = new SimpleStringProperty(comment);
	    }

	    public Integer getRating() {
	        return Rating.get();
	    }

	    public String getComment() {
	        return comment.get();
	    }
	}
	

	
    AdminServiceHandler ash;
    public AdminController() {
        // Default constructor for FXML
    	ash = new AdminServiceHandler();
    }

    public void displayAdminInterface() {
        // Prompt the user for a password using a dialog
        TextInputDialog passwordDialog = new TextInputDialog();
        passwordDialog.setTitle("Admin Login");
        passwordDialog.setHeaderText("Enter Admin Password");
        passwordDialog.setContentText("Password:");

        // Show the dialog and capture user input
        Optional<String> result = passwordDialog.showAndWait();

        // Verify the password
        result.ifPresentOrElse(password -> {
            if (isPasswordCorrect(password)) {
                // If the password is correct, load the admin interface
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminView.fxml"));

                    // Set a custom controller factory
                    loader.setControllerFactory(param -> new AdminController());

                    Parent root = loader.load();

                    // Set up the stage
                    Stage stage = new Stage();
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.setTitle("Admin");
                    stage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error loading the AdminView FXML.");
                }
            } else {
                // Show an error message if the password is incorrect
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Access Denied");
                alert.setHeaderText("Incorrect Password");
                alert.setContentText("Please try again.");
                alert.showAndWait();
            }
        }, () -> {
            // Handle case when the user cancels the dialog
            System.out.println("Admin login cancelled.");
        });
    }

    /**
     * Simulates password verification. Replace this with your own logic.
     */
    private boolean isPasswordCorrect(String password) {
        // Replace "admin123" with your desired password or logic for checking the password
        return "admin123".equals(password);
    }


   
    @FXML
    private Spinner<Integer> tableCapacitySpinner; // Spinner for table capacity
    @FXML
    private Spinner<Integer> tableCapacitySpinner1; // Spinner for table ID
    @FXML
    private ComboBox<String> tableDropdown; // ComboBox for available tables
    @FXML
    private ComboBox<String> tableDropdown1; // ComboBox for available tables
    @FXML
    private ComboBox<String> ReservationDropdown;
    @FXML
    private VBox CashPaymentReservations;
    
    @FXML private Label lblRes;
    @FXML private Label lblTable3;
    @FXML private Label lblName3;
    @FXML private Label lblPhone3;
    @FXML private Label lblAmount;

    @FXML
    private Button deleteButton; // Button to delete the selected table

    private ObservableList<String> availableTables; // List of available tables
    
    @FXML
    private BarChart<String, Number> reservationChart;

    @FXML
    private CategoryAxis monthAxis;

    @FXML
    private NumberAxis countAxis;
    
    @FXML
    private PieChart feedbackChart;
    @FXML
    private Tab feedbackTab;
    
    @FXML
    private TableView<Feedback> feedbackTable;
    @FXML
    private TableColumn<Feedback, Integer> RatingColumn;
    @FXML
    private TableColumn<Feedback, String> commentColumn;

    @FXML
    private TableView<TagValueRow> reportTable;
    @FXML
    private TableColumn<TagValueRow, String> tagColumn;
    @FXML
    private TableColumn<TagValueRow, Double> valueColumn;
    
    
    @FXML
    private TableView<ReservationDetails> reservationsTable;
    @FXML
    private TableColumn<ReservationDetails, Integer> reservationIDColumn;
    @FXML
    private TableColumn<ReservationDetails, String> customerNameColumn;
    @FXML
    private TableColumn<ReservationDetails, Integer> tableCapacityColumn;
    @FXML
    private TableColumn<ReservationDetails, String> reservationTimeColumn;
    @FXML
    private TableColumn<ReservationDetails, Boolean> confirmationSentColumn;



    @FXML
    public void initialize() {
        // Set a value factory to define the range and step for the Spinner
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1, 1);
        SpinnerValueFactory<Integer> valueFactory1 = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1, 1);
        
        tableCapacitySpinner.setValueFactory(valueFactory);
        tableCapacitySpinner1.setValueFactory(valueFactory1);
       

        filltabledropdown();
        
        
     
        
        
       
        CashPaymentReservations.setVisible(false);
       
    }
    
    @FXML
    private void HandleReportTabChanged() {
    	loadReportData();
    	// Configure the table columns
        RatingColumn.setCellValueFactory(new PropertyValueFactory<>("Rating"));
        commentColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));
        generateFeedbackReport(feedbackChart);
        
     // Set up columns
        System.out.println("-----------------TableView: " + reportTable); // Should not be null
        tagColumn.setCellValueFactory(new PropertyValueFactory<>("tag"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));

        // Load data into the table for payments report
        reportTable.setItems(getReportData());

    }
    
    @FXML
    private void HandleAllReservationsTabChanged() {
    	 reservationIDColumn.setCellValueFactory(new PropertyValueFactory<>("ReservationID"));
         customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("CustomerName"));
         tableCapacityColumn.setCellValueFactory(new PropertyValueFactory<>("TableCapacity"));
         reservationTimeColumn.setCellValueFactory(new PropertyValueFactory<>("ReservationTime"));
         confirmationSentColumn.setCellValueFactory(new PropertyValueFactory<>("ConfirmationSent"));
         
         // Load data into the table for all reservations
         reservationsTable.setItems(getReservationData());
    }
    
    @FXML
    private void handleGetDetailsButtonAction() {
    	String selectedReservation = ReservationDropdown.getValue(); // Get selected reservation
        if (selectedReservation != null) {
        	CashPaymentReservations.setVisible(true);
        	// get reservation details
        	ReservationDTO reservation = ash.getReservation(Integer.parseInt(selectedReservation));
			if (reservation == null) {
				System.out.println("Reservation not found!");
				return;
			}
			// get customer details
			CustomerDTO customer = ash.getCustomer(reservation.getCustomerID());
			if (customer == null) {
				System.out.println("Customer not found!");
				return;
			}
			// get table details
			TableDTO table = ash.getTable(reservation.getTableID());
			if (table == null) {
				System.out.println("Table not found!");
				return;
			}
			// get payment details
			CashPayment payment = ash.getCashPayment(reservation.getReservationID());
			lblRes.setText("Reservation ID: " + reservation.getReservationID());
			lblTable3.setText("Table ID: " + reservation.getTableID());
			lblName3.setText("Name: " + customer.getName());
			lblPhone3.setText("Phone: " + customer.getPhoneNumber());
			lblAmount.setText("Amount: " + payment.getAmount());
			
        	return;
        }
        else
        {
        	showAlert("Error", "Please select a reservation to view details.");
        	return;
        }
    }
    @FXML
	private void handleVerifyPaymentButtonAction() {
		String selectedReservation = ReservationDropdown.getValue(); // Get selected reservation
		if (selectedReservation != null) {
			// verify the payment
			if (ash.verifyCashPayment(Integer.parseInt(selectedReservation))) {
			showAlert("Success", "Payment verified successfully.");
			fillReservationDropdown();
			CashPaymentReservations.setVisible(false);
			return;
		} else {
			showAlert("Error", "Please select a reservation to verify payment.");
			return;
			}
		}
	}
    @FXML
    private void HandleVerifyPaymenttabchange() {
    	fillReservationDropdown();
    }

	private void fillReservationDropdown() {
    	ObservableList<String> reservations = FXCollections.observableArrayList();
    	// get all reservation ids from payment objects where status is false and method is cash
    	ArrayList<CashPayment> cashPayments = ash.getAllCashPayments();
    	if (cashPayments == null || cashPayments.isEmpty()) return;
    	        for (CashPayment payment : cashPayments) {
					if (payment.getStatus() == false) {
						reservations.add(String.valueOf(payment.getReservationID()));
					}
    	        }
    	 ReservationDropdown.setItems(reservations);
	}
    
	private ObservableList<ReservationDetails> getReservationData() {
		// Get the report from your method
		ArrayList<ReservationDTO> reservations = ash.ViewAllReservations();
		ArrayList<TableDTO> tables = ash.getAllTables();
		ArrayList<CustomerDTO> customers = ash.getAllCustomers();
		if (reservations == null || reservations.isEmpty()) {
            return null;
        }
		
		ObservableList<ReservationDetails> data = FXCollections.observableArrayList();
		for (ReservationDTO reservation : reservations) {
            // Find the customer name
            String customerName = "";
            for (CustomerDTO customer : customers) {
                if (customer.getId() == reservation.getCustomerID()) {
                    customerName = customer.getName();
                    break;
                }
            }
            // Find the table capacity
            int tableCapacity = 0;
            for (TableDTO table : tables) {
                if (table.getTableID() == reservation.getTableID()) {
                    tableCapacity = table.getCapacity();
                    break;
                }
            }
            data.add(new ReservationDetails(reservation.getReservationID(), customerName, reservation.getReservationDateTime(), tableCapacity, reservation.getConfirmationStatus()));
        }
		
		return data;	
	}
    
    private ObservableList<TagValueRow> getReportData() {
        // Get the report from your method
        ArrayList<Double> report = GeneratePaymentReport();

        // Define the tags
        String[] tags = {
            "Total Revenue",
            "Cash Revenue",
            "Online Revenue",
            "Total Confirmed Payments",
            "Confirmed Cash Payments",
            "Confirmed Online Payments"
        };

        // Combine tags and values into TagValueRow objects
        ObservableList<TagValueRow> data = FXCollections.observableArrayList();
        for (int i = 0; i < tags.length; i++) {
            data.add(new TagValueRow(tags[i], report.get(i)));
        }

        return data;
    }
    
    private void filltabledropdown() {
    	 availableTables = FXCollections.observableArrayList();
         // Get the list of available tables
         ArrayList<TableDTO> Tables = ViewAllTables();
         if (Tables == null || Tables.isEmpty()) 
         {
        	 tableDropdown.getItems().clear();
        	 return;
         }
 		for (TableDTO table : Tables) {
 			availableTables.add(String.valueOf(table.getTableID()) + " - " + table.getCapacity()); 		}
         // Bind the list to the ComboBox
 		 // clear the dropdown list
 		 
 		 tableDropdown.getItems().clear();
         tableDropdown.setItems(availableTables);
         availableTables = FXCollections.observableArrayList();
         // Get the list of available tables
         Tables = ViewAllTables();
         if (Tables == null || Tables.isEmpty()) 
         {
        	 tableDropdown.getItems().clear();
        	 return;
         }
 		for (TableDTO table : Tables) {
 			availableTables.add(String.valueOf(table.getTableID()) + " - " + table.getCapacity()); 		}
 		tableDropdown1.getItems().clear();
         tableDropdown1.setItems(availableTables);
    }

    // Example: Get the selected value from the Spinner
    @FXML
    private void handleAddTableAction() {
        int capacity = tableCapacitySpinner.getValue(); // Get the selected value
        System.out.println("Selected capacity: " + capacity);

        // Call a backend method to add a new table
        addTable(capacity);
    }
    @FXML
    private void HandleDeleteTableTabClick(){
    	System.out.println("Delete table tab clicked");
    	filltabledropdown();
    }
    @FXML
	private void HandleUpdateTableTabClick() {
		System.out.println("Update table tab clicked");
		filltabledropdown();
	}
    
    @FXML
    private void handleDeleteButtonAction() {
    	System.out.println("Delete button clicked");
    	String selectedTable = tableDropdown.getValue(); // Get selected table
        if (selectedTable != null) {
			selectedTable = selectedTable.split(" ")[0];
            // Remove the selected table
            System.out.println("Removing table: " + selectedTable);
            // Call backend logic to delete the table from the database
            deleteTable(Integer.parseInt(selectedTable));
            filltabledropdown();
        } else {
            System.out.println("No table selected!"); 
            // Display an error message window
            showAlert("Error", "Please select a table to delete.");
            }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
	private void handleUpdateButtonAction() {
		String selectedTable = tableDropdown1.getValue(); // Get selected table
		// select the table id from the string eg "16 - 8" will return 16
		
		if (selectedTable != null) {
			selectedTable = selectedTable.split(" ")[0];
			// Update the selected table
			System.out.println("Updating table: " + selectedTable);
			// Call backend logic to update the table in the database
			updateTable(Integer.parseInt(selectedTable), tableCapacitySpinner1.getValue(), true);
			filltabledropdown();
		} else {
			System.out.println("No table selected!");
			showAlert("Error", "Please select a table to update.");
		}
	}
    
    
    
    private void loadReportData() {
        // Generate the report data
        Map<String, Integer> reportData = GenerateReport();
		if (reportData == null) {
			return;
		}

        // Set categories (months) on the x-axis
        ObservableList<String> months = FXCollections.observableArrayList(reportData.keySet());
        monthAxis.setCategories(months);

        // Prepare the data series
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Reservations");

        for (Map.Entry<String, Integer> entry : reportData.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        
        // remove all existing data
        reservationChart.getData().clear();

        // Add the series to the chart
        reservationChart.getData().add(series);
    }
    
    private boolean isUpdating = false;

    public void generateFeedbackReport(PieChart feedbackChart) {
        if (isUpdating) return; // Prevent concurrent updates
        isUpdating = true;

        Platform.runLater(() -> {
            // Generate the feedback report
            ArrayList<Integer> feedbackData = GenerateFeedbackReport();
            List<Feedback> feedbackComments = fetchFeedbackComments(); // Fetch comments
            
            if (feedbackData != null && feedbackComments != null) {
                // Update the PieChart
                ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                    new PieChart.Data("Positive", feedbackData.get(1)),
                    new PieChart.Data("Negative", feedbackData.get(2))
                );

                feedbackChart.getData().clear();
                feedbackChart.setData(pieChartData);
                feedbackChart.setTitle("Feedback Ratio");

                // Update the TableView with comments
                ObservableList<Feedback> feedbackList = FXCollections.observableArrayList(feedbackComments);
                feedbackTable.setItems(feedbackList);
            }
            isUpdating = false; // Allow next update
        });
    }

    // Simulate fetching comments from the database or a data source
    private List<Feedback> fetchFeedbackComments() {
    	ArrayList<FeedbackDTO> feedback = ash.getAllFeedback();
		if (feedback == null || feedback.isEmpty()) {
			System.out.println("Feedback not found.");
			return null;
		}
        List<Feedback> feedbacks = new ArrayList<>();
		for (FeedbackDTO f : feedback) {
			feedbacks.add(new Feedback(f.getRating(), f.getFeedback()));
		}
               return feedbacks;
    }


   
    // call all ash methods here
	public void addTable(int capacity) {
		ash.addTable(capacity);
	}
	// get all tables
	public ArrayList<TableDTO> ViewAllTables() {
		ArrayList<TableDTO> tables = ash.getAvailableTables();
		if (tables == null || tables.isEmpty()) {
			System.out.println("Tables not found");
			return null;
		}
		return tables;
	}

	public void deleteTable(int id) {
		ash.deleteTable(id);
	}

	public void updateTable(int id, int capacity, Boolean status) {
		ash.updateTable(id, capacity, status);
	}

	public void verifyCashPayment(int reservationID) {
		ash.verifyCashPayment(reservationID);
	}

	public Map<String, Integer> GenerateReport() {
		Map<String, Integer> report = ash.generateReport();
		if (report == null || report.isEmpty()) {
			System.out.println("Report not found");
			return null;
		}
		return report;
	}

	public ArrayList<Integer> GenerateFeedbackReport() {
		ArrayList<Integer> r = ash.generateFeedbackReport();
		if (r == null) {
			System.out.println("Feedback not found");
			return null;
		}
		return r;
	}

	public ArrayList<Double> GeneratePaymentReport() {
		ArrayList<Double> r = ash.generatePaymentReport();
		return r;
	}
	

}
