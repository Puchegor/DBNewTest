package newtest.Classes;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

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
        alert.setHeaderText("Во время работы программы произошла ошибка");
        alert.setContentText(error);
        alert.showAndWait();
    }
    public static Boolean Confirmation (String confirmation){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение");
        alert.setHeaderText("Вы действительно хотите удалить \""+confirmation+"\"");
        alert.setContentText("Подтвердите");
        ButtonType btnYes = new ButtonType("Да");
        ButtonType btnNo = new ButtonType("Нет");
        alert.getButtonTypes().setAll(btnYes, btnNo);
        Optional<ButtonType> choise = alert.showAndWait();
        return choise.get() == btnYes;
    }
    public static void Success (String text){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Операция завершена");
        alert.setHeaderText("Операция завершена успешно");
        alert.setContentText(text);
        alert.showAndWait();
    }
}
