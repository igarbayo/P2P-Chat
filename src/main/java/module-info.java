module com.p2p {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.p2p to javafx.fxml;
    exports com.p2p;
}