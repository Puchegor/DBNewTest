package newtest.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import newtest.Classes.*;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

public class TestSetupController implements Initializable {
    @FXML
    Button btnOk, btnCancel, btnQMinus, btnQPlus, btnVMinus, btnVPlus;
    @FXML
    ComboBox<Item> cbSubjectsToTest;
    @FXML
    ListView lvTopicsToTest;
    @FXML
    TextField tfQuestions, tfVariants;

    public void onBtnCancelHandle() {
        Stage stage = (Stage)btnCancel.getScene().getWindow();
        stage.close();
    }

    public void onBtnOkHandle() {
        ObservableList<Question> test = FXCollections.observableArrayList();
        MultipleSelectionModel<Item> selection = lvTopicsToTest.getSelectionModel();
        selection.setSelectionMode(SelectionMode.MULTIPLE);
        ResultSet rst;
        for (int i = 0; i < selection.getSelectedItems().size(); i++){
            try {
                int idTopic = selection.getSelectedItems().get(i).getIdOwn();
                rst = DB.Select("questions", " idTopic = "+idTopic);
                ArrayList<Integer> ques = new ArrayList<>();
                while (rst.next()){
                    ques.add(rst.getInt("idQuestion"));
                }
                ObservableList<Answer> answers = FXCollections.observableArrayList();
                for (int q = 0; q < ques.size(); q++){
                    int idQuestion = ques.get(q);
                    rst = DB.Select("questions", " idQuestion = "+idQuestion);
                    String nameQuestion = rst.getString("nameQuestion");
                    rst = DB.Select("answers", " idQuestion = "+idQuestion);
                    while (rst.next()) {
                        boolean isCorrect = false;
                        if (rst.getInt("isCorrect") != 0) isCorrect = true;
                        answers.add(new Answer(rst.getInt("idAnswer"), idQuestion,
                                rst.getString("nameAnswer"), isCorrect));
                        Collections.shuffle(answers);
                    }
                    test.add(new Question(idQuestion, idTopic, nameQuestion, answers));
                    answers.clear();
                }
            }catch(SQLException e){
                Alerts.Error(e.getMessage());
            }
        }
        Collections.shuffle(test);
        
        /*Stage stage = new Stage();
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("../FXML/TestViewFrm.fxml"));
        }catch (IOException e){
            Alerts.Error(e.getMessage());
            return;
        }
        stage.setTitle("Тест");
        stage.setResizable(true);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();*/
    }

    @Override
    public void initialize(URL location, ResourceBundle resources){
        tfQuestions.setText("10");
        tfVariants.setText("3");
        tfQuestions.setEditable(false);
        tfVariants.setEditable(false);
        ObservableList<Item> subs = FXCollections.observableArrayList();
        lvTopicsToTest.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        try{
            ResultSet rst = DB.Select("subjects", null);
            while (rst.next()){
                subs.add(new Item(0, rst.getInt("idSub"),"subjects", rst.getString("nameSub") ));
            }
            cbSubjectsToTest.setItems(subs);
            cbSubjectsToTest.setValue(subs.get(0));
            fillListView(subs.get(0));
            lvTopicsToTest.setEditable(false);

        }catch (SQLException | NullPointerException e){
            Alerts.Error(e.getMessage());
        }
    }

    public void onCbSubjectsChangedHandle() {
        fillListView(cbSubjectsToTest.getValue());
    }

    private void fillListView(Item item){
        ObservableList<Item> topics = FXCollections.observableArrayList();
        ResultSet rst = DB.Select("topics", "idSub = "+item.getIdOwn());
        try{
            while(rst.next())
                topics.add(new Item(rst.getInt("idSub"), rst.getInt("idTopic"),
                        "topics", rst.getString("nameTopic")));
            lvTopicsToTest.setItems(topics);
        }catch (SQLException | NullPointerException e){
            Alerts.Error(e.getMessage());
        }
    }

    public void qMinusHandle(ActionEvent actionEvent) {
        int count = Integer.parseInt(tfQuestions.getText());
        if (count > 1)
            count--;
        else
            Alerts.Warning("Предупреждение", "Количество вопросов теста не может быть меньше 1!");
        tfQuestions.setText(String.valueOf(count));
    }

    public void vMinusHandle(ActionEvent actionEvent) {
        int count = Integer.parseInt(tfVariants.getText());
        if (count > 1)
            count--;
        else
            Alerts.Warning("Предупреждение", "Количество вариантов теста не может быть меньше 1!");
        tfVariants.setText(String.valueOf(count));
    }

    public void qPlusHandle(ActionEvent actionEvent) {
        int count = Integer.parseInt(tfQuestions.getText());
        count++;
        tfQuestions.setText(String.valueOf(count));
    }

    public void vPlusHandle(ActionEvent actionEvent){
        int count = Integer.parseInt(tfVariants.getText());
        count++;
        tfQuestions.setText(String.valueOf(count));
    }
}
