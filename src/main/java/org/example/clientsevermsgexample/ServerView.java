package org.example.clientsevermsgexample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ServerView implements Initializable {
    @FXML
    private AnchorPane ap_main;

    @FXML
    private Button button_send;

    @FXML
    private ScrollPane sp_main;

    @FXML
    private TextField tf_message;

    @FXML
    private VBox vbox_messages;

    private ServerSocket serverSocket;

    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            serverSocket = new ServerSocket(12345);
            Thread thread = new Thread(() -> {
                while (!serverSocket.isClosed()) {
                    try {
                        socket = serverSocket.accept();
                         oos = new ObjectOutputStream(socket.getOutputStream());
                         oos.flush();
                         ois = new ObjectInputStream(socket.getInputStream());

                        while (socket.isConnected()) {
                            Object object = ois.readObject();
                            if (object instanceof String) {
                                String message = (String) object;
                                Label label = new Label("Client: " + message);
                                label.setAlignment(Pos.BASELINE_LEFT);
                                Platform.runLater(() -> {
                                    vbox_messages.getChildren().add(label);
                                });
                            }
                        }
                    }
                    catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void sendMessage(ActionEvent event) throws IOException {
        if(socket.isConnected()) {
            oos.writeObject(tf_message.getText());
            oos.flush();
            HBox hBox = new HBox();
            Label label = new Label(tf_message.getText());
            hBox.setAlignment(Pos.CENTER_RIGHT);
            hBox.getChildren().add(label);
            vbox_messages.getChildren().add(hBox);
        }
    }
}
