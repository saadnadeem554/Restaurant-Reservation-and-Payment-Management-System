module Proj {
	requires javafx.controls;
	requires java.sql;
	requires javafx.graphics;
	requires javafx.fxml;
	requires java.security.jgss;
	requires jdk.security.auth;
	requires com.microsoft.sqlserver.jdbc;
	requires com.twilio.sdk;


	opens project to javafx.graphics, javafx.fxml, javafx.base;
	opens UI to javafx.fxml, javafx.base;
}
