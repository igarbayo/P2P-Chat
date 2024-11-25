package com.Client;

import com.Server.Server;
import com.Server.ServerInterface;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

public class InicioController extends AbstractVentana {

    // Atributos
    private String IP;
    private int puerto;

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public int getPuerto() {
        return puerto;
    }

    public void setPuerto(int puerto) {
        this.puerto = puerto;
    }

    // Elementos gráficos
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Text errorText;
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField passwordTextField;

    // Métodos de acciones

    @FXML
    public void onBotonLogin(ActionEvent event) {
        String username=usernameTextField.getText();
        String password=passwordTextField.getText();
        //Aquí iria comprobacion de contraseña y usuario validos
        //-
        //-
        if (username.isEmpty()||password.isEmpty()) {
            mostrarError("Por favor, cubra ambos campos");
            return;
        }

        try{
            Client client = new Client();
            ClientInfo info = new ClientInfo(username, password);
            client.setInfo(info);

            if(this.getServer().existeCliente(info)){
                // Cargamos la informacion del usuario desde el servidor
                // Verificamos la contraseña
                // Quitñe uso de cargarDatos, daba problemas (no sé muy bien por qué)
                if ((info = this.getServer().obtenerClienteInfo(username)) != null) {
                    if (password.equals(info.getContrasena())) {
                        client.setInfo(info);
                        client.getInfo().setOnline(true);
                        this.getServer().actualizarClienteInfo(client.getInfo());

                        // Registro RMI
                        client.registrarCliente(IP, puerto);
                        this.getServer().anadirClienteEnLinea(info);

                        // Loader
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PrincipalCliente-view.fxml"));
                        Scene scene = new Scene(fxmlLoader.load());

                        // Carga el stage
                        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                        stage.setScene(scene);
                        stage.show();

                        // Pasa la instancia del servidor y del cliente
                        PrincipalController controller = fxmlLoader.getController();
                        controller.setServer(this.getServer());
                        client.setIP(IP);
                        client.setPuerto(puerto);
                        this.setClient(client);
                        controller.setClient(client);
                        fxmlLoader.setController(controller);
                    } else {
                        mostrarError("Usuario o contraseña incorrectos");
                    }
                } else {
                    mostrarError("Usuario o contraseña incorrectos");
                }
            }else{
                mostrarError("Usuario o contraseña incorrectos");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }


    @FXML
    public void onBotonRegistrar(ActionEvent event) {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();

        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            mostrarError("Por favor, complete ambos campos correctamente");
            return;
        }

        try {
            ServerInterface server = this.getServer();
            if (server == null) {
                mostrarError("Error: No hay conexión con el servidor");
                return;
            }

            // Verificar si el usuario ya existe
            if (server.existeCliente(username.trim())) {
                mostrarError("Este nombre de usuario ya existe");
                return;
            }

            // Crear la información del cliente
            ClientInfo info = new ClientInfo(username.trim(), password);

            // Registrar la información del cliente en el servidor
            server.anadirCliente(info);

            // Crear el cliente local después del registro exitoso
            Client client = new Client();
            client.setInfo(info);

            // Configurar el estado online y registro RMI
            //if (IP == null || puerto == 0) {
            //    mostrarError("Error: IP o puerto no configurados");
            //    return;
            //}

            // Registrar el cliente en RMI
            client.registrarCliente(IP, puerto);

            // Actualizar estado online
            info.setOnline(true);
            server.actualizarClienteInfo(info);

            // Añadir a la lista de clientes en línea
            server.anadirClienteEnLinea(info);


            // Cargar la ventana principal
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PrincipalCliente-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            PrincipalController controller = fxmlLoader.getController();
            controller.setServer(server);
            client.setIP(IP);
            client.setPuerto(puerto);

            this.setClient(client);
            controller.setClient(client);

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (RemoteException e) {
            mostrarError("Error de conexión con el servidor: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            mostrarError("Error al cargar la interfaz: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            mostrarError("Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
    /*@FXML
    public void onBotonRegistrar(ActionEvent event) {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();


        // 1. Validación inicial de campos
        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            mostrarError("Por favor, complete ambos campos correctamente");
            return;
        }

        try {
            // 2. Verificación del servidor
            if (this.getServer() == null) {
                mostrarError("Error: No hay conexión con el servidor");
                return;
            }

            // 3. Creación y validación del cliente
            Client client;
            try {
                client = new Client();
                ClientInfo info = new ClientInfo(username.trim(), password);
                client.setInfo(info);


                // Verificación de existencia previa
                if (this.getServer().existeCliente(username.trim())) {
                    mostrarError("Este nombre de usuario ya existe");
                    return;
                }
            } catch (RemoteException e) {
                mostrarError("Error al crear el cliente: " + e.getMessage());
                e.printStackTrace();
                return;
            }

            // 4. Registro en el servidor
            try {
                this.getServer().anadirCliente(client);
            } catch (IllegalArgumentException e) {
                mostrarError("Error de validación: " + e.getMessage());
                return;
            } catch (RemoteException e) {
                mostrarError("Error de conexión con el servidor: " + e.getMessage());
                e.printStackTrace();
                return;
            }

            // 5. Actualización del estado online
            try {
                client.getInfo().setOnline(true);
                this.getServer().actualizarClienteInfo(client.getInfo());
            } catch (RemoteException e) {
                mostrarError("Error al actualizar el estado del cliente: " + e.getMessage());
                e.printStackTrace();
                return;
            }

            // 6. Registro RMI
            try {
                if (IP == null || puerto == 0) {
                    mostrarError("Error: IP o puerto no configurados");
                    return;
                }
                client.registrarCliente(IP, puerto);
                this.getServer().anadirClienteEnLinea(client);
            } catch (RemoteException e) {
                mostrarError("Error en el registro RMI: " + e.getMessage());
                e.printStackTrace();
                return;
            }

            // 7. Carga de la interfaz principal
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PrincipalCliente-view.fxml"));
                Scene scene = new Scene(fxmlLoader.load());

                // Configuración del controlador
                PrincipalController controller = fxmlLoader.getController();
                controller.setServer(this.getServer());
                this.setClient(client);
                controller.setClient(client);

                // Transición a la ventana principal
                Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                mostrarError("Error al cargar la interfaz principal: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (Exception e) {
            mostrarError("Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }*/


    /*@FXML
    public void onBotonRegistrar(ActionEvent event) {
        String username=usernameTextField.getText();
        String password=passwordTextField.getText();
        //Aquí iria comprobacion de contraseña y usuario validos
        //-
        //-
        if (username.isEmpty()||password.isEmpty()) {
            mostrarError("Por favor, cubra ambos campos");
            return;
        }


        try {
            Client client = new Client();
            ClientInfo info = new ClientInfo(username, password);
            client.setInfo(info);

            if(this.getServer().existeCliente(info)){
                mostrarError("Este usuario ya existe");
                return;
            }else{

                this.getServer().anadirCliente(client);
                client.getInfo().setOnline(true);
                this.getServer().actualizarClienteInfo(client.getInfo());

                // Registro RMI
                client.registrarCliente(IP, puerto);
                this.getServer().anadirClienteEnLinea(client);

                // Cargar el archivo FXML
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PrincipalCliente-view.fxml"));
                Scene scene = new Scene(fxmlLoader.load());

                // Carga el stage
                Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();

                // Pasa la instancia del servidor
                PrincipalController controller = fxmlLoader.getController();
                controller.setServer(this.getServer());
                this.setClient(client);
                controller.setClient(client);
                fxmlLoader.setController(controller);

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }*/

    @FXML
    private void mostrarError(String error) {
        errorText.setText(error);
        if(!errorText.isVisible()) {
            errorText.setVisible(true);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resources) {
        Platform.runLater(() -> {
            errorText.setVisible(false);
        });
    }

}
