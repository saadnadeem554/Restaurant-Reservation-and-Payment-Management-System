module Proj {
	requires javafx.controls;
	requires java.sql;
	requires javafx.graphics;
	requires javafx.fxml;
	requires twilio;

	opens project to javafx.graphics, javafx.fxml, javafx.base;
	opens UI to javafx.fxml, javafx.base;
}
