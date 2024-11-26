package project;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

public class CustomerController {

    private CustomerServiceHandler csh;
    private DatabaseHandler db;
    private int cancelReservationID;
    
    @FXML private TabPane tp1;
    @FXML private TabPane tp2;
    @FXML private TabPane tp3;
    @FXML private Tab tbCash;
    @FXML private Tab tbOnline;
    @FXML private AnchorPane checkoutPane;
    @FXML private TextField txtCheckoutPhone;
    @FXML private TextField txtCvv;
    @FXML private TextField txtName;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPhone;
    @FXML private TextField txtCancelReservationID;
    @FXML private TextField txtViewReservationID1;
    @FXML private TextField txtComments2;
    @FXML private TextField txtComments;
    @FXML private TextField txtCardNum;
    @FXML private TextField txtReservationID;
    @FXML private Label lblName2;
    @FXML private Label lblName3;
    @FXML private Label lblDate;
    @FXML private Label lblTime;
    @FXML private Label lblTable;
    @FXML private Label lblPhone2;
    @FXML private Label lblPhone3;
    @FXML private Label lblTable2;
    @FXML private Label lblDate2;
    @FXML private Label lblTime2;
    @FXML private Label lblTable3;
    @FXML private Label lblAmount;
    @FXML private Label lblCashPay;
    @FXML private Label lblCheck;
    @FXML private Label lblRes;
    @FXML private Slider slider1;
    @FXML private Slider slider2;
    @FXML private DatePicker dpDate;
    @FXML private DatePicker dpDate2;
    @FXML private DatePicker dpExpiry;
    @FXML private Button btnSubmit;
    @FXML private Button btnUpdate;
    @FXML private Button btnConfirm1;
    @FXML private Button btnConfirm2;
    @FXML private Button btnFeed1;
    @FXML private Button btnFeed2;
    @FXML private Button btnCancel;
    @FXML private ComboBox<String> tableComboBox;
    @FXML private ComboBox<String> comboTable2;
    @FXML private ComboBox<String> comboTime;
    @FXML private ComboBox<String> comboTime2;
    @FXML private VBox updateForm;
    @FXML private VBox cancelForm;
    @FXML private VBox viewResForm1;
    
    private double tempAmount;
    private int tempRes;

    public CustomerController() {
        
    }

	public CustomerController(DatabaseHandler db) {
		this.db = db;
		csh = new CustomerServiceHandler(db);
	}
    
    public void attachDB(DatabaseHandler db) {
		this.db = db;
		csh = new CustomerServiceHandler(db);
    }
    
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title); // Ensure this line has a semicolon
        alert.setHeaderText(null); // Ensure this line has a semicolon
        alert.setContentText(message); // Ensure this line has a semicolon
        alert.showAndWait(); // Ensure this line has a semicolon
    }

    //populate time comboboxes like 12:00:00 13:00:00 ... 23:00:00
    public void populateTimeComboBox() {
        // Initialize a list to hold time strings
        List<String> timeOptions = new ArrayList<>();

        // Loop through hours from 12 to 23
        for (int hour = 12; hour <= 23; hour++) {
            // Format the hour into HH:00:00 format
            String time = String.format("%02d:00:00.0", hour);
            timeOptions.add(time);
        }

        // Add time options to both ComboBoxes
        comboTime.getItems().setAll(timeOptions);
        comboTime2.getItems().setAll(timeOptions);
    }
    //populate table combobox
    public void populateTableComboBox() {
    	//clear table combobox
    	tableComboBox.getItems().clear();
        ObservableList<String> availableTables = FXCollections.observableArrayList();
        String selectedTime = comboTime.getSelectionModel().getSelectedItem();
        if (selectedTime == null) {
			return;
		}
		if (dpDate.getValue() == null) {
			return;
		}
		String selectedDate = dpDate.getValue().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE);
		ArrayList<TableDTO> tables = csh.getAvailableTables(selectedDate, selectedTime);
		for (TableDTO table : tables) {
			availableTables.add(String.valueOf(table.getTableID()) + " - " + table.getCapacity());
		}
        tableComboBox.setItems(availableTables);
    }
  //populate table combobox2
    public void populateTableComboBox2() {
    	//clear table combobox
    	comboTable2.getItems().clear();
        ObservableList<String> availableTables = FXCollections.observableArrayList();
        String selectedTime = comboTime2.getSelectionModel().getSelectedItem();
		if (selectedTime == null) {
			return;
		}
		if (dpDate2.getValue() == null) {
			return;
		}
		String selectedDate = dpDate2.getValue().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE);
		ArrayList<TableDTO> tables = csh.getAvailableTables(selectedDate, selectedTime);
		for (TableDTO table : tables) {
			availableTables.add(String.valueOf(table.getTableID()) + " - " + table.getCapacity());
		}
        comboTable2.setItems(availableTables);
    }

    @FXML
    public void initialize() {
		updateForm.setVisible(false);
		cancelForm.setVisible(false);
		viewResForm1.setVisible(false);
		checkoutPane.setVisible(false);
		
		//event listener for tab selection
		tp1.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
	        clearUIElements();
	    });		
		tp2.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
			clearUIElements();
		});
		tp3.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
			clearUIElements2();
		});
		//event listener for time combobox selection
		comboTime.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
	        populateTableComboBox();
	    });
		comboTime2.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			populateTableComboBox2();
		});
		//event listener for textBox change
		txtPhone.textProperty().addListener((observable, oldValue, newValue) -> {
			populateTableComboBox();
		});
		txtEmail.textProperty().addListener((observable, oldValue, newValue) -> {
			populateTableComboBox();
		});
		//event listener for datepicker selection
		dpDate.valueProperty().addListener((observable, oldValue, newValue) -> {
			populateTableComboBox();
		});
		dpDate2.valueProperty().addListener((observable, oldValue, newValue) -> {
			populateTableComboBox2();
		});
		
		// Set the minimum date to the current date
	    dpDate.setValue(LocalDate.now()); // To ensure the default value is set to today
	    dpDate.setDayCellFactory(picker -> new DateCell() {
	        @Override
	        public void updateItem(LocalDate date, boolean empty) {
	            super.updateItem(date, empty);
	            setDisable(empty || date.isBefore(LocalDate.now())); // Disable past dates
	        }
	    });
	    dpDate2.setValue(LocalDate.now()); // To ensure the default value is set to today
		dpDate2.setDayCellFactory(picker -> new DateCell() {
			@Override
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				setDisable(empty || date.isBefore(LocalDate.now())); // Disable past dates
			}
		});
		dpExpiry.setValue(LocalDate.now()); // To ensure the default value is set to today
		dpExpiry.setDayCellFactory(picker -> new DateCell() {
			@Override
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				setDisable(empty || date.isBefore(LocalDate.now())); // Disable past dates
			}
		});
		
		populateTimeComboBox();
		
	}
    public void displayCustomerInterface(Stage stage) {
        // Use FXML or scene switching to load the customer interface
        // Example assuming CustomerHomeView is an FXML file
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CustomerHomeView.fxml"));
            
            loader.setControllerFactory(param -> new CustomerController(db));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Action handler methods based on UI actions
    @FXML
    private void handleReservationSubmission() {
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();

        if (dpDate.getValue() == null) {
            showAlert("Error", "Please select a valid date.");
            return;
        }

        if (!phone.matches("\\d{11}")) {
            showAlert("Error", "Phone number must be 11 digits.");
            return;
        }
        if (email.isEmpty()) {
			showAlert("Error", "Email cannot be empty.");
			return;
		}
		if (!email.matches("^(.+)@(.+)$")) {
			showAlert("Error", "Invalid email address.");
			return;
		}
		if(name.isEmpty())
		{
			showAlert("Error", "Name cannot be empty.");
			return;
		}
		if(phone.isEmpty())
		{
			showAlert("Error", "Phone cannot be empty.");
			return;
		}
        

        String date = dpDate.getValue().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE);
        String time = comboTime.getSelectionModel().getSelectedItem();
        String selectedTable = tableComboBox.getSelectionModel().getSelectedItem();
        if (selectedTable == null) {
            showAlert("Error", "Please select a table.");
            return;
        }

        int tableID = Integer.parseInt(selectedTable.split(" - ")[0]);
        if(csh.addReservation(name, phone, email, tableID, time, date))
        	showAlert("Success", "Reservation submitted for " + name);
		else
			showAlert("Error", "Error submitting reservation");
    }

    @FXML
    private void handleUpdateReservation() {
        String phone = txtReservationID.getText();

        if (!phone.matches("\\d{11}")) {
            showAlert("Error", "Phone number must be 11 digits.");
            return;
        }

        ReservationDTO reservation = csh.getReservationByPhoneNumber(phone);
        if (reservation == null) {
            showAlert("Error", "No reservation found for phone: " + phone);
            return;
        }

        if (dpDate2.getValue() == null) {
            showAlert("Error", "Please select a valid date.");
            return;
        }

        String date = dpDate2.getValue().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE);
        String time = comboTime2.getSelectionModel().getSelectedItem();
        String selectedTable = comboTable2.getSelectionModel().getSelectedItem();
        if (selectedTable == null) {
            showAlert("Error", "Please select a table.");
            return;
        }

        int tableID = Integer.parseInt(selectedTable.split(" - ")[0]);
        if(csh.updateReservation(reservation.getReservationID(), tableID, reservation.getCustomerID(), time, date))
        	showAlert("Success", "Reservation updated for customer ID: " + reservation.getCustomerID());
        else	
        	showAlert("Error", "Error updating reservation");
    }

    @FXML
    private void handleSearch() {
        String phone = txtReservationID.getText();

        if (!phone.matches("\\d{11}")) {
            showAlert("Error", "Phone number must be 11 digits.");
            return;
        }

        ReservationDTO reservation = csh.getReservationByPhoneNumber(phone);
        if (reservation != null) {
            updateForm.setVisible(true);
        } else {
            showAlert("Error", "No reservation found with phone: " + phone);
        }
    }

    @FXML
    private void handleSearch2() {
        String phone = txtCancelReservationID.getText();

        if (!phone.matches("\\d{11}")) {
            showAlert("Error", "Phone number must be 11 digits.");
            return;
        }

        ReservationDTO reservation = csh.getReservationByPhoneNumber(phone);
        if (reservation != null) {
            cancelForm.setVisible(true);
            String reservationDateTime = reservation.getReservationDateTime();
            if (reservationDateTime != null && reservationDateTime.contains(" ")) {
                String[] dateTimeParts = reservationDateTime.split(" ");
                lblDate.setText(dateTimeParts[0]);
                lblTime.setText(dateTimeParts[1]);
                lblTable.setText(String.valueOf(reservation.getTableID()));
                cancelReservationID = reservation.getReservationID();
            } else {
                showAlert("Error", "Invalid reservation date-time format.");
                lblDate.setText("N/A");
                lblTime.setText("N/A");
            }
        } else {
            showAlert("Error", "No reservation found with phone: " + phone);
        }
    }

    @FXML
    private void handleSearch3() {
        String phone = txtViewReservationID1.getText();

        if (!phone.matches("\\d{11}")) {
            showAlert("Error", "Phone number must be 11 digits.");
            return;
        }

        ReservationDTO reservation = csh.getReservationByPhoneNumber(phone);
        if (reservation != null) {
            viewResForm1.setVisible(true);
            CustomerDTO customer = csh.getCustomer(reservation.getCustomerID());
            String reservationDateTime = reservation.getReservationDateTime();
            if (reservationDateTime != null && reservationDateTime.contains(" ")) {
                String[] dateTimeParts = reservationDateTime.split(" ");
                lblName2.setText(lblName2.getText() + " " + customer.getName());
                lblPhone2.setText(lblPhone2.getText() + " " + customer.getPhoneNumber());
                lblTable2.setText(lblTable2.getText() + " " + reservation.getTableID());
                lblDate2.setText(lblDate2.getText() + " " + dateTimeParts[0]);
                lblTime2.setText(lblTime2.getText() + " " + dateTimeParts[1]);
            } else {
                showAlert("Error", "Invalid reservation date-time format.");
                lblDate2.setText("N/A");
                lblTime2.setText("N/A");
            }
        } else {
            showAlert("Error", "No reservation found with phone: " + phone);
        }
    }

    @FXML
    private void handleSearch4() {
        String phone = txtCheckoutPhone.getText();

        if (!phone.matches("\\d{11}")) {
            showAlert("Error", "Phone number must be 11 digits.");
            return;
        }

        ReservationDTO reservation = csh.getReservationByPhoneNumber(phone);
        if (reservation != null) {
            CashPayment cashPayment = csh.getCashPayment(reservation.getReservationID());
            OnlinePayment onlinePayment = csh.getOnlinePayment(reservation.getReservationID());
            if (cashPayment != null && !cashPayment.getStatus()) {
                showAlert("Error", "Pending cash payment.");
                return;
            }
            if (onlinePayment != null && !onlinePayment.getStatus()) {
                showAlert("Error", "Pending online payment.");
                return;
            }
            checkoutPane.setVisible(true);
            CustomerDTO customer = csh.getCustomer(reservation.getCustomerID());
            lblRes.setText("Reservation ID: " + reservation.getReservationID());
            lblTable3.setText("Table ID: " + reservation.getTableID());
            lblName3.setText("Name: " + customer.getName());
            lblPhone3.setText("Phone: " + customer.getPhoneNumber());
            double amount = Math.random() * 1000;
            //set amount to 2 decimal places
            amount = Math.round(amount * 100.0) / 100.0;
            lblAmount.setText("Amount: " + amount);
            this.tempAmount = amount;
            this.tempRes = reservation.getReservationID();
        } else {
            showAlert("Error", "No reservation found with phone: " + phone);
        }
    }

    @FXML
    private void handleCancelReservation() {
        if(csh.cancelReservation(cancelReservationID))
        {
			System.out.println("Reservation cancelled with ID: " + cancelReservationID);
			cancelForm.setVisible(false);
		} else {
			showAlert("Error", "Error cancelling reservation.");
        }
    }
    
    @FXML 
    private void handleFeedback() {
        String comments = txtComments.getText();
		if (comments.isEmpty()) {
			showAlert("Error", "Please provide feedback.");
			return;
		}
        btnFeed1.setVisible(false);
        csh.addFeedback(comments, slider1.getValue());
        showAlert("Feedback Submitted", "Thank you for your feedback!");
    }

    @FXML
    private void handleFeedback2() {
        String comments = txtComments2.getText();
        if (comments.isEmpty()) {
        	showAlert("Error", "Please provide feedback.");
 	   	}
        btnFeed2.setVisible(false);
        csh.addFeedback(comments, slider2.getValue());
        showAlert("Feedback Submitted", "Thank you for your feedback!");
    }

    @FXML
    private void handleOnlinePayment() {
        String cardnum = txtCardNum.getText();
        String expiry = dpExpiry.getValue().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE);
        String cvv = txtCvv.getText();

        // Validate card number
        if (!cardnum.matches("\\d{16}")) {
            showAlert("Invalid Input", "Invalid card number. Must be 16 digits.");
            return;
        }

        // Validate expiry date length
        if (expiry.length() != 10) {
            showAlert("Invalid Input", "Invalid expiry date. Must be in MM/YY format.");
            return;
        }

        // Validate CVV
        if (!cvv.matches("\\d{3}")) {
            showAlert("Invalid Input", "Invalid CVV. Must be 3 digits.");
            return;
        }

        // Proceed with the online payment
        if(csh.payCardBill(tempRes, tempAmount, cardnum, expiry, Integer.parseInt(cvv)))
        {
        	btnConfirm2.setVisible(false);
        	tbCash.setDisable(true);
        	showAlert("Payment Successful", "Thank you for your payment!");
        }
        else
        	showAlert("Error","Payment Failed");
    }

    @FXML
    private void handleCashPayment() {
        if (csh.payCashBill(tempRes, tempAmount)) {
            btnConfirm1.setVisible(false);
            lblCashPay.setText("Thank you for your payment");
            tbOnline.setDisable(true);
            showAlert("Payment Successful", "Thank you for your cash payment!");
        } else {
            lblCashPay.setText("Error in payment");
            showAlert("Payment Error", "There was an error processing your cash payment. Please try again.");
        }
    }

    
    @FXML
    private void clearUIElements2() {
        // Clear all text fields
        txtCheckoutPhone.clear();
        txtCvv.clear();
        txtName.clear();
        txtEmail.clear();
        txtPhone.clear();
        txtCancelReservationID.clear();
        txtViewReservationID1.clear();
        txtComments2.clear();
        txtComments.clear();
        txtCardNum.clear();
        txtReservationID.clear();

        // Clear labels
        lblName2.setText("Name:");
        
        lblDate.setText("");
        lblTime.setText("");
        lblTable.setText("");
        lblPhone2.setText("Phone:");
        
        lblTable2.setText("Table:");
        lblDate2.setText("Date:");
        lblTime2.setText("Time:");
        lblCashPay.setText("PROVIDE CASH TO WAITER");
        lblCheck.setVisible(false);

        // Reset sliders
        slider1.setValue(0);
        slider2.setValue(0);

        // Reset DatePickers
        dpDate.setValue(LocalDate.now());
        dpDate2.setValue(LocalDate.now());
        dpExpiry.setValue(LocalDate.now());
        // show buttons
        btnFeed1.setVisible(true);
        btnFeed2.setVisible(true);
        btnConfirm1.setVisible(true);
        btnConfirm2.setVisible(true);
        // Clear ComboBoxes
        tableComboBox.getSelectionModel().clearSelection();
        comboTable2.getSelectionModel().clearSelection();
        comboTime.getSelectionModel().clearSelection();
        comboTime2.getSelectionModel().clearSelection();
        //repopulate table combobox
        populateTimeComboBox();

        // Hide forms
        updateForm.setVisible(false);
        cancelForm.setVisible(false);
        viewResForm1.setVisible(false);
        
        // show tab
        tbCash.setDisable(false);
        tbOnline.setDisable(false);
        
    }
    @FXML
    private void clearUIElements()
    {
    	clearUIElements2();
    	lblRes.setText("Reservation ID:");
    	lblTable3.setText("Table Number:");
    	lblName3.setText("Name:");
    	lblPhone3.setText("Phone:");
        lblAmount.setText("TOTAL AMOUNT:");
    	checkoutPane.setVisible(false);
    }

}
