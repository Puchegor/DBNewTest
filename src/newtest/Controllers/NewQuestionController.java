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

    public static void setCbSubject (String subject){
        defaultSubject = subject;
    }
    public static void setCbTopic (String topic){
        defaultTopic = topic;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList <String> subjects = FXCollections.observableArrayList();
        try {
            ResultSet rst = DB.Select("subjects", null);
            while (rst.next())
                subjects.add(rst.getString("nameSub"));
            cbSubject.setItems(subjects);
            cbSubject.setValue(defaultSubject);
            if (defaultSubject!=null){
                rst = DB.Select("subjects", "nameSub = \""+cbSubject.getValue()+"\"");
                TopicRefresh(rst.getInt("idSub"));
            }
        } catch (SQLException e){
            Alerts.Error(e.getMessage());
        }

    }

    private void TopicRefresh (int idSub){
        ObservableList<String> topics = FXCollections.observableArrayList();
        try{
            ResultSet rst = DB.Select("topics", "idSub = "+idSub);
            while (rst.next())
                topics.add(rst.getString("nameTopic"));
        } catch (Exception e){
            Alerts.Error(e.getMessage());
        }
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
