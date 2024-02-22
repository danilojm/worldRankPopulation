module com.assignment1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens sql;
    opens com.assignment1 to javafx.fxml;
    exports com.assignment1;
}
