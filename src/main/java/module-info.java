module com.Server {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;
    requires java.desktop;
    requires org.bouncycastle.provider;
    requires spring.security.crypto;

    // Permite la reflexión para el paquete com.Server
    opens com.Server to javafx.fxml, java.rmi;

    // Exporta com.Server para uso general
    exports com.Server;

    // Agrega estas líneas para manejar com.Client
    opens com.Client to javafx.fxml, java.rmi; // Permite reflexión en com.Client
    exports com.Client;
    exports com.Client.gui;
    opens com.Client.gui to java.rmi, javafx.fxml;
    exports com.Client.security;
    opens com.Client.security to java.rmi, javafx.fxml;             // Exporta com.Client para otros módulos


}
