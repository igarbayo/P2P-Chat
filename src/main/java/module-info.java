module com.Server {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;
    requires java.desktop;

    // Permite la reflexión para el paquete com.Server
    opens com.Server to javafx.fxml, java.rmi;

    // Exporta com.Server para uso general
    exports com.Server;

    // Agrega estas líneas para manejar com.Client
    opens com.Client to javafx.fxml, java.rmi; // Permite reflexión en com.Client
    exports com.Client;             // Exporta com.Client para otros módulos


}
