package newtest.Classes;

import javafx.scene.control.Alert;

public class Alerts {
    public static void Warning (String headerText, String contextText){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Внимание");
        alert.setHeaderText(headerText);
        alert.setContentText(contextText);
        alert.showAndWait();
    }
    public static void Error (String error){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Во время работы программы произошла ощибка");
        alert.setContentText(error);
        alert.showAndWait();
    }
}
