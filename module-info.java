module Proj {
	requires javafx.controls;
	requires java.sql;
	requires javafx.graphics;
	requires javafx.fxml;
	requires java.security.jgss;
	requires jdk.security.auth;
	requires static java.sql;


	opens project to javafx.graphics, javafx.fxml, javafx.base;
	opens UI to javafx.fxml, javafx.base;
}
