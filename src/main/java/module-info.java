module com.assignment1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.assignment1.controller to javafx.fxml;
    opens com.assignment1.model to javafx.fxml;
    opens com.assignment1 to javafx.fxml;
    exports com.assignment1;
}
