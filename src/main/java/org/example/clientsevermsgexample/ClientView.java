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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientView implements Initializable {

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

    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            socket = new Socket("localhost", 12345);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            oos.flush();
            Thread thread = new Thread(() -> {
                while(socket.isConnected()) {
                    try {
                        Object object = ois.readObject();
                        if(object instanceof String) {
                            String message = (String) object;
                            Label label = new Label("Server: " + message);
                            label.setAlignment(Pos.BASELINE_LEFT);
                            Platform.runLater(() -> {
                                vbox_messages.getChildren().add(label);
                            });
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            thread.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    void sendMessage(ActionEvent event) {
        try {
            if (!tf_message.getText().isEmpty()) {
                oos.writeObject(tf_message.getText());
                oos.flush();
                HBox hBox = new HBox();
                Label label = new Label(tf_message.getText());
                hBox.setAlignment(Pos.CENTER_RIGHT);
                hBox.getChildren().add(label);
                vbox_messages.getChildren().add(hBox);
                tf_message.clear();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
