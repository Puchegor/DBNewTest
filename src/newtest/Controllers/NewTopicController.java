package newtest.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import newtest.Classes.Alerts;
import newtest.Classes.DB;
import newtest.Classes.Item;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class NewTopicController implements Initializable {
    @FXML
    Button btnSave, btnCancel;
    @FXML
    TextField tfTopic;
    @FXML
    ComboBox cbSubject;
    private static String cbDefault;
    private static ObservableList <Item> subjects = FXCollections.observableArrayList();

    public static void setDefaultSubject(String subject){
        cbDefault = subject;
    }

    public static void setSubjects (ObservableList<Item> subs){
        subjects = subs;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (subjects.size()!=0) {
            cbSubject.setItems(subjects);
            cbSubject.setValue(cbDefault);
        } else {
            subjects = MainWindowController.getSubjects();
            cbSubject.setItems(subjects);
            cbSubject.setValue(subjects.get(0));
        }
    }
    public void OnCbHandle(ActionEvent actionEvent) {
    }

    public void OnBtnSaveHandle(ActionEvent actionEvent) {
        if (!tfTopic.getText().isEmpty()){
            try {
                if (cbSubject.getValue()!=null) {
                    int idSub = (DB.Select("subjects", "nameSub = \"" + cbSubject.getValue() + "\"")).getInt("idSub");
                    DB.Insert("topics", "nameTopic, idSub", tfTopic.getText() + "\" ,\"" + idSub);
                } else {
                    Alerts.Warning("Не указано название предмета",
                            "Для продолжения работы выберите название предмета");
                }
            } catch (SQLException e){
                Alerts.Error(e.getMessage());
            }
        } else {
            Alerts.Warning("Не введено название новой темы",
                    "Для продолжения работы введите название новой темы");
        }
    }

    public void OnBtnCancelHandle(ActionEvent actionEvent) {
        Stage stage = (Stage)btnCancel.getScene().getWindow();
        stage.close();
    }


}
