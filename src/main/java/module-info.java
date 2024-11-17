module com.Server {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;


    opens com.Server to javafx.fxml;
    exports com.Server;
}