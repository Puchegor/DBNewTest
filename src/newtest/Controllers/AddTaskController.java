package newtest.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import newtest.Classes.*;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AddTaskController implements Initializable {
    @FXML
    Button btnCancel, btnOk;
    @FXML
    ListView listView;

    private static ObservableList<Item> topics = FXCollections.observableArrayList();
    private ObservableList<Task> tasks = FXCollections.observableArrayList();
    private ObservableList<Task> output = FXCollections.observableArrayList();

    public static void setTopic(ObservableList<Item> tops){
        topics = tops;
    }
    public void onCancelHandle(ActionEvent actionEvent) {
        Stage stage = (Stage)btnCancel.getScene().getWindow();
        stage.close();
    }

    public void onOkHandle(ActionEvent actionEvent) {
        //--------Добавляем задачи-------------------
        int z = listView.getSelectionModel().getSelectedItems().size();
        for (int i = 0; i < z; i++){
            output.add((Task) listView.getSelectionModel().getSelectedItems().get(i));
        }
        RawTest.setTasks(output);
        Alerts.Success("Добавлено "+z+" задач");
        Stage stage = (Stage)btnOk.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String sqlCondition = " ";
        for (int i = 0; i < topics.size(); i++) {
            sqlCondition += "idTopic = " + topics.get(i).getIdOwn() + " ";
            if (i < topics.size()-1)
                sqlCondition += "OR ";
        }
        ResultSet rst = DB.Select("tasks", sqlCondition);
        try{
            while(rst.next()){
                tasks.add(new Task(rst.getInt("idTopic"),
                        rst.getInt("idTask"),
                        rst.getString("nameTask"),
                        rst.getString("answer")));
                listView.setItems(tasks);
                listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            }
        }catch (SQLException e){
            Alerts.Error(e.getMessage());
        }
    }
}
