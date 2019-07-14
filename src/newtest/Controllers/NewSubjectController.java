package newtest.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import newtest.Classes.DB;

public class NewSubjectController {
    @FXML
    Button btnSave, btnCancel;
    @FXML
    TextField tfSubject;

    public void BtnSaveHandle(ActionEvent actionEvent) {
        if (!tfSubject.getText().isEmpty()){
            DB.Insert("subjects", "nameSub", tfSubject.getText());
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Внимание");
            alert.setHeaderText("Не введено название темы");
            alert.setContentText("Для продолжения работы введите название темы");
            alert.showAndWait();
        }
    }

    public void BtnCancelHandle(ActionEvent actionEvent) {
        Stage stage = (Stage)btnCancel.getScene().getWindow();
        stage.close();
    }
}
