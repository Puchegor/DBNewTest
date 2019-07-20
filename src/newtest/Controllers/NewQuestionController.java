package newtest.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import newtest.Classes.Alerts;
import newtest.Classes.DB;
import newtest.Classes.Item;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class NewQuestionController implements Initializable {
    @FXML
    public TextArea taQuestion;
    @FXML
    Button btnSave, btnCancel;
    @FXML
    ComboBox cbSubject, cbTopic;

    private static String defaultSubject, defaultTopic;
    private static ObservableList <Item> subjects = FXCollections.observableArrayList();
    private static ObservableList<Item> topics = FXCollections.observableArrayList();

    public static void setDefaultSubject (String subject){
        defaultSubject = subject;
    }

    public static void setDefaultTopic (String topic){
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
        if (subjects.size()!=0) {
            cbSubject.setItems(subjects);
            cbSubject.setValue(defaultSubject);
            cbTopic.setItems(topics);
            cbTopic.setValue(defaultTopic);
        } else {
            subjects = MainWindowController.getSubjects();
            cbSubject.setItems(subjects);
            cbSubject.setValue(subjects.get(0));
            topics = MainWindowController.getTopics(subjects.get(0).getIdOwn());
            cbTopic.setItems(topics);
            cbTopic.setValue(topics.get(0));
        }
    }

    private void TopicRefresh (int idSub){

    }

    public void CbSubjectHandle(ActionEvent actionEvent) {
    }

    public void CbThemeHandle(ActionEvent actionEvent) {
    }

    public void OnBtnSaveHandle(ActionEvent actionEvent) {
        if (taQuestion.getText() != null){
            try {
                int idTopic = (DB.Select("topics", "nameTopic = \"" + cbTopic.getValue() + "\"")).getInt("idTopic");
                DB.Insert("questions", "nameQuestion, idTopic",   taQuestion.getText() +  "\", \"" + idTopic);// Здесь ошибка! Исправить!!!!!
            } catch (SQLException e){
                Alerts.Error(e.getMessage());
            }
        }
    }

    public void OnCancelHandle(ActionEvent actionEvent) {
        Stage stage = (Stage)btnCancel.getScene().getWindow();
        stage.close();
    }
}
