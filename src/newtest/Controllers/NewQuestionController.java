package newtest.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import newtest.Classes.Alerts;
import newtest.Classes.DB;
import newtest.Classes.Item;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class NewQuestionController implements Initializable {
    @FXML
    Button btnSave, btnCancel;
    @FXML
    ComboBox cbSubject, cbTopic;
    private static String defaultSubject, defaultTopic;
    private static ObservableList <Item> subjects = FXCollections.observableArrayList();
    private static ObservableList<Item> topics = FXCollections.observableArrayList();

    public static void setCbSubject (String subject){
        defaultSubject = subject;
    }

    public static void setCbTopic (String topic){
        defaultTopic = topic;
    }

    public static void setSubjects (ObservableList<Item> subs){
        subjects = subs;
    }

    public static void setTopics (ObservableList<Item> tops){
        topics = tops;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbSubject.setItems(subjects);
        cbSubject.setValue(defaultSubject);
        cbTopic.setItems(topics);
    }

    private void TopicRefresh (int idSub){

    }

    public void CbSubjectHandle(ActionEvent actionEvent) {
    }

    public void CbThemeHandle(ActionEvent actionEvent) {
    }

    public void OnBtnSaveHandle(ActionEvent actionEvent) {
    }

    public void OnCancelHandle(ActionEvent actionEvent) {
        Stage stage = (Stage)btnCancel.getScene().getWindow();
        stage.close();
    }
}
