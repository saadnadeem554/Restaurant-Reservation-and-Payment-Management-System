package project;

import UI.AdminController;
import UI.CustomerController;
import UI.HomeController;
import javafx.application.Application;
import javafx.stage.Stage;
public class RestaurantSystemApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        HomeController homeController = new HomeController();

        homeController.show(primaryStage, control -> {
            if (control == 1) {
            	CustomerController customerController = new CustomerController();
            	customerController.displayCustomerInterface();                       

            } else {
                // Open admin view
                AdminController adminController = new AdminController();
                adminController.displayAdminInterface();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
