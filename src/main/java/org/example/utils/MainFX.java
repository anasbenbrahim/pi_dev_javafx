package org.example.utils;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import org.example.services.Flux_service;
import org.example.services.Ollama;


public class MainFX extends Application {


    @Override
    public void start(Stage stage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("/image_generator.fxml"));
        Scene scene=new Scene(root);

        stage.setScene(scene);
        stage.setWidth(800);
        stage.setHeight(600);
        stage.setTitle("Ajouter Equipement");
        stage.show();
        //Flux_service ai=new Flux_service();
        //ai.generator();
        //Ollama ollama=new Ollama("llama3.1","who are you");
        //ollama.generate();


    }


    public static void main(String[] args) {
        launch(args);
    }
}