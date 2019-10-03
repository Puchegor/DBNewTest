package newtest.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import newtest.Classes.Alerts;
import newtest.Classes.DB;
import newtest.Classes.Item;
import newtest.Classes.Task;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class TaskController implements Initializable {
    @FXML
    Button btnSave, btnCancel;
    @FXML
    ComboBox cbSubject, cbTopic;
    @FXML
    TextArea taTask;
    @FXML
    TextField tfAnswer;
    @FXML
    ListView lvTasks;
    private ObservableList<Item> subjects = FXCollections.observableArrayList();
    private ObservableList<Item> topics = FXCollections.observableArrayList();

    public void onSubjectChanged(ActionEvent actionEvent) {
        setCbTopic(((Item)cbSubject.getValue()).getIdOwn());
        setLvTasks(((Item)cbTopic.getValue()).getIdOwn());
    }

    public void onTopicChanged(ActionEvent actionEvent) {
        setLvTasks(((Item)cbTopic.getValue()).getIdOwn());
    }

    public void onSaveHandle(ActionEvent actionEvent) {
        if (taTask.getText().isEmpty()) {
            Alerts.Error("Необходимо ввести текст задачи!");
            return;
        }
        if (tfAnswer.getText().isEmpty()){
            Alerts.Error("Необходимо ввести правильный ответ!");
            return;
        }
    }

    public void onCancelHandle(ActionEvent actionEvent) {
        Stage stage = (Stage)btnCancel.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ResultSet rst = DB.Select("subjects", null);

        try{
            while(rst.next()){
                subjects.add(new Item(0,rst.getInt("idSub"), "subjects",rst.getString("nameSub")));
            }
            cbSubject.setItems(subjects);
            cbSubject.setValue(cbSubject.getItems().get(0));
            setCbTopic(((Item)cbSubject.getValue()).getIdOwn());
            setLvTasks(((Item)cbTopic.getValue()).getIdOwn());
        }catch (SQLException e){
            Alerts.Error(e.getMessage());
        }
    }

    private void setCbTopic (int idTopic){
        topics.clear();
        ResultSet rst = DB.Select("topics", "idSub = \""+idTopic+"\"");
        try {
            while(rst.next())
                topics.add(new Item(rst.getInt("idSub"), rst.getInt("idTopic"),
                        "topics", rst.getString("nameTopic")));
            cbTopic.setItems(topics);
            cbTopic.setValue(cbTopic.getItems().get(0));
        }catch (SQLException e){
            Alerts.Error(e.getMessage());
        }
    }
    private void setLvTasks(int idTopic){
        ObservableList<Task> tasks = FXCollections.observableArrayList();
        ResultSet rst = DB.Select("tasks", " idTopic = "+idTopic);
        try{
            while (rst.next())
                tasks.add(new Task(rst.getInt("idTopic"), rst.getInt("idtask"),
                        rst.getString("nameTask"), rst.getString("answer")));
            lvTasks.setItems(tasks);
        }catch (SQLException e){
            Alerts.Error(e.getMessage());
        }
    }
}
