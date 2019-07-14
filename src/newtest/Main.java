package newtest;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import newtest.Classes.DB;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        DB.setConnection();
        Parent root = FXMLLoader.load(getClass().getResource("FXML/MainWindow.fxml"));
        primaryStage.setTitle("DIZ Testing System");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
