/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

 package com.assignment1;

 import javafx.application.Application;
 import javafx.fxml.FXMLLoader;
 import javafx.scene.Parent;
 import javafx.scene.Scene;
 import javafx.stage.Stage;
 
 /**
  *
  * @author spangsberg
  */
 public class App extends Application {
     
     @Override
     public void start(Stage stage) throws Exception {
         Parent root = FXMLLoader.load(App.class.getResource("PieChartView.fxml"));
         
         root.setStyle("-fx-padding: 20px;");

         Scene scene = new Scene(root);

         stage.setFullScreen(true);
         
         stage.setScene(scene);
         stage.show();
     }
 
     /**
      * @param args the command line arguments
      */
     public static void main(String[] args) {
         launch(args);
     }
     
 }