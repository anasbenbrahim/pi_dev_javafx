package org.example.utils;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import org.example.services.Ollama;

public class MainFX extends Application {


    @Override
    public void start(Stage stage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("/front.fxml"));
        Scene scene=new Scene(root);

        stage.setScene(scene);
        stage.setWidth(800);
        stage.setHeight(600);
        stage.setTitle("Ajouter Equipement");

        //Ollama ollama=new Ollama("llama3.1","who are you");
        //ollama.generate();

        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}