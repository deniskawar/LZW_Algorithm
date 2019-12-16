package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override

    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("LZW Algorithm");
        primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("main.fxml")), 563, 408));
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}




