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
import org.bouncycastle.util.encoders.Hex;

import java.io.*;
import java.net.URL;
import java.rmi.RemoteException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
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
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            mostrarError("Por favor, cubra ambos campos");
            return;
        }

        try {
            Client client = new Client();
            ClientInfo info = new ClientInfo(username, password);
            client.setInfo(info);

            if (this.getServer().existeCliente(client)) {
                // Leer la clave y el nonce desde el archivo de resources
                String userDir = "src/main/resources/com/Client/" + username + "-key-nonce.txt";
                File file = new File(userDir);

                if (!file.exists()) {
                    mostrarError("No se encontró el archivo de clave y nonce para el usuario");
                    return;
                }

                String keyHex = null;
                String nonceHex = null;

                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    keyHex = reader.readLine().split(":")[1].trim();
                    nonceHex = reader.readLine().split(":")[1].trim();
                }

                if (keyHex == null || nonceHex == null) {
                    mostrarError("Error al leer la clave y el nonce del archivo");
                    return;
                }

                // Convertir la clave y el nonce de hex a bytes
                byte[] key = Hex.decode(keyHex);
                byte[] nonce = Hex.decode(nonceHex);

                // Verificar la contraseña desencriptando la contraseña almacenada en el servidor
                if ((info = this.getServer().obtenerClienteInfo(username)) != null) {
                    String encryptedPassword = info.getContrasena();
                    String decryptedPassword = ChaChaDecryption.decryptPassword(encryptedPassword, key, nonce);

                    if (password.equals(decryptedPassword)) {
                        client.setInfo(info);
                        client.getInfo().setOnline(true);

                        this.getServer().actualizarClienteInfo(client);

                        // Registro RMI
                        client.registrarCliente(IP, puerto);
                        this.getServer().anadirClienteOnLine(client);

                        if (client.getAmigosOnLine() != null) {
                            System.out.println("Amigos en línea: " + client.getAmigosOnLine().keySet());
                        }
                        System.out.println("Amigos (todos): " + client.getInfo().getListaAmigos());

                        // Cargar la ventana principal
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PrincipalCliente-view.fxml"));
                        Scene scene = new Scene(fxmlLoader.load());

                        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                        stage.setScene(scene);
                        stage.show();

                        // Pasa la instancia del servidor y del cliente
                        PrincipalController controller = fxmlLoader.getController();
                        controller.setServer(this.getServer());
                        client.setIP(IP);
                        client.setPuerto(puerto);
                        client.setPrincipalController(controller);
                        this.setClient(client);

                        String con = "Tu amigo " + this.getClient().getInfo().getUsuario() + " se ha conectado";
                        System.out.println(con);
                        this.getClient().notificarClientes(this.getClient().getAmigosOnLine(), con);
                        for(ClientInterface amigo: this.getClient().getAmigosOnLine().values()){
                            this.getClient().notificarRecarga(amigo);
                        }
                        controller.setClient(client);
                        fxmlLoader.setController(controller);

                        // Debug
                        System.out.println(this.getClient().getInfo().getListaAmigos());
                    } else {
                        mostrarError("Usuario o contraseña incorrectos");
                    }
                } else {
                    mostrarError("Usuario o contraseña incorrectos");
                }
            } else {
                mostrarError("Usuario o contraseña incorrectos");
            }

        } catch (IOException e) {
            mostrarError("Error al leer el archivo de clave y nonce o al conectar con el servidor: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            mostrarError("Error al procesar la contraseña: " + e.getMessage());
            e.printStackTrace();
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

            // Generar clave y nonce
            SecureRandom secureRandom = new SecureRandom();
            byte[] key = new byte[32]; // Clave de 256 bits
            secureRandom.nextBytes(key);
            byte[] nonce = new byte[8]; // Nonce de 96 bits
            secureRandom.nextBytes(nonce);

            // Cifrar la contraseña
            String encryptedPassword = ChaChaEncryption.encryptPassword(password, key, nonce);

            // Guardar la clave y el nonce en un archivo en resources
            String userDir = "src/main/resources/com/Client/" + username + "-key-nonce.txt";
            File file = new File(userDir);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("Key: " + Hex.toHexString(key));
                writer.newLine();
                writer.write("Nonce: " + Hex.toHexString(nonce));
            }

            // Crear la información del cliente con la contraseña cifrada
            ClientInfo info = new ClientInfo(username.trim(), encryptedPassword);

            // Crear el cliente local después del registro exitoso
            Client client = new Client();
            client.setInfo(info);

            // Registrar el cliente en RMI
            client.registrarCliente(IP, puerto);
            this.getServer().anadirCliente(client);
            server.anadirClienteOnLine(client);

            // Actualizar estado online
            info.setOnline(true);

            server.actualizarClienteInfo(client);

            // Cargar la ventana principal
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PrincipalCliente-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            PrincipalController controller = fxmlLoader.getController();
            controller.setServer(server);
            client.setIP(IP);
            client.setPuerto(puerto);
            client.setPrincipalController(controller);
            this.setClient(client);
            controller.setClient(client);

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            mostrarError("Error al guardar la clave y el nonce: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            mostrarError("Error de conexión con el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }


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
