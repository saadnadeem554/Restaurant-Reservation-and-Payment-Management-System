package UI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class HomeController {
	public interface ControlCallback {
        void onControlSelected(int control);
    }

    private ControlCallback callback;
    public void setCallback(ControlCallback callback) {
        this.callback = callback;
    }

    public void show(Stage stage, ControlCallback callback) {
        try {
            // Load FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/HomeView.fxml"));
            Parent root = loader.load();

            // Get the controller instance
            HomeController controller = loader.getController();

            // Set the callback in the controller
            controller.setCallback(callback);

            // Set up the stage
            Scene scene = new Scene(root, 900, 600); // Standard window size
            stage.setScene(scene);
            stage.setTitle("Home");

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading the HomeView FXML.");
        }
    }


 

	 // FXML annotations for the UI components
    @FXML
    private Label welcomeLabel;  // This is the 'welcomeLabel' from the FXML file

    @FXML
    private Button userButton;   // The 'User' button from the FXML file

    @FXML
    private Button adminButton;  // The 'Admin' button from the FXML file

    @FXML Accordion acc;
    
	@FXML 
	public void initialize() {
    	acc.setExpandedPane(acc.getPanes().get(0));
    }
    // Add event handler methods for the buttons
    @FXML
    private void handleUserButtonAction() {
        System.out.println("User button clicked");
        callback.onControlSelected(1);
        // Add logic to handle user button click
    }

    @FXML
    private void handleAdminButtonAction() {
        System.out.println("Admin button clicked");
        // Add logic to handle admin button click
        callback.onControlSelected(2);
    }
}

