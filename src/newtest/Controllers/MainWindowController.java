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
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import newtest.Classes.Alerts;
import newtest.Classes.Answer;
import newtest.Classes.DB;
import newtest.Classes.Item;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {
    @FXML
    Label lbStatus;
    @FXML
    MenuItem miAddSubject, miAddTopic, miAddQuestion;
    @FXML
    TreeView treeView;
    @FXML
    TableColumn<Answer, String> tcAnswer;
    @FXML
    TableColumn<Answer, Boolean> tcIsTrue;
    @FXML
    TableView tableView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (DB.getConnection()!= null)
            lbStatus.setText("Соединение с базой данных установлено");
        else
            lbStatus.setText("Ошибка соединения с базой данных");
        buildTree();
    }
// --------Нажатие кнопки Exit------------
    public void OnBtnExitHandle(ActionEvent actionEvent) {
        System.exit(0);
    }

    //-----Построение дерева----------
    private void buildTree(){
        ObservableList<Item> subjects = FXCollections.observableArrayList();
        ObservableList<Item> themes = FXCollections.observableArrayList();
        ObservableList<Item> questions = FXCollections.observableArrayList();

        try {
            ResultSet rst = DB.Select("subjects", null);
            TreeItem<Item> rootItem = new TreeItem<>();
            rootItem.setExpanded(true);
            while (rst.next()){
                subjects.add(new Item(0, rst.getInt("idSub"),
                        rst.getString("nameSub")));
            }
            for (Item subject : subjects) {
                TreeItem<Item> sub = new TreeItem<>(subject);
                rootItem.getChildren().add(sub);
                rst = DB.Select("topics", "idSub = " + sub.getValue().getIdOwn());
                themes.clear();
                while (rst.next()) {
                    themes.add(new Item(rst.getInt("idSub"),
                            rst.getInt("idTopic"), rst.getString("nameTopic")));
                }
                for (Item item : themes) {
                    TreeItem<Item> theme = new TreeItem<>(item);
                    sub.getChildren().add(theme);
                    rst = DB.Select("questions", "idTopic = " + theme.getValue().getIdOwn());
                    questions.clear();
                    while (rst.next()) {
                        questions.add(new Item(rst.getInt("idTopic"),
                                rst.getInt("idQuestion"), rst.getString("nameQuestion")));
                    }
                    for (Item question : questions) {
                        TreeItem<Item> quest = new TreeItem<>(question);
                        theme.getChildren().add(quest);
                    }
                }
            }
            treeView.setRoot(rootItem);
            treeView.setShowRoot(false);
        }
        catch (SQLException e){
            Alerts.Error(e.getMessage());
            return;
        }
    }

    public void AddSubjectHandle(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("../FXML/NewSubjectFrm.fxml"));
        stage.setTitle("Введите название нового предмета");
        stage.setResizable(false);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        buildTree();
    }

    public void AddTopicHandle(ActionEvent actionEvent) throws IOException{

        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("../FXML/NewTopicFrm.fxml"));
        stage.setTitle("Введите название новой темы");
        stage.setResizable(false);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.showAndWait();
        buildTree();
    }

    public void AddQuestionHandle(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("../FXML/NewQuestionFrm.fxml"));
        stage.setTitle("Введите вопрос");
        stage.setResizable(false);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        buildTree();
    }

    public void OnTreeViewEntered(MouseEvent mouseEvent) {
        MultipleSelectionModel<TreeItem<Item>> selection = treeView.getSelectionModel();
        selection.setSelectionMode(SelectionMode.SINGLE);
        try {
        int level = treeView.getTreeItemLevel(selection.getSelectedItem());
            switch (level){
                case 1:
                    NewTopicController.setSubjects(getSubjects());
                    String selectedSubject = selection.getSelectedItem().getValue().getName();
                    NewTopicController.setDefaultSubject(selectedSubject);
                    NewQuestionController.setSubjects(getSubjects());
                    int idSubject = DB.Select("subjects",
                            "nameSub = \""+selectedSubject+"\"").getInt("idSub");
                    NewQuestionController.setTopics(getTopics(idSubject));
                    NewQuestionController.setDefaultSubject(selection.getSelectedItem().getValue().getName());
                    break;
                case 2:
                    NewTopicController.setSubjects(getSubjects());
                    NewQuestionController.setSubjects(getSubjects());
                    String selectedTopic = selection.getSelectedItem().getValue().getName();
                    int idSub = (DB.Select("topics",
                            "nameTopic = \""+selectedTopic+"\"")).getInt("idSub");
                    NewTopicController.setDefaultSubject(DB.Select("subjects",
                            "idSub = \""+idSub+"\"").getString("nameSub"));
                    NewQuestionController.setTopics(getTopics(idSub));
                    NewQuestionController.setDefaultTopic(selectedTopic);
                    break;
                case 3:
                    NewTopicController.setSubjects(getSubjects());
                    NewQuestionController.setSubjects(getSubjects());
                    //String selectedQuestion = selection.getSelectedItem().getValue().getName();
                    int idTopic = (DB.Select("questions",
                            "nameQuestion = \""+
                                    selection.getSelectedItem().getValue().getName()+"\"")).getInt("idTopic");
                    int idSubj = (DB.Select("topics", "idTopic = \""+
                            idTopic+"\"")).getInt("idSub");
                    NewQuestionController.setTopics(getTopics(idSubj));
                    NewQuestionController.setDefaultSubject(DB.Select("subjects",
                            "idSub = \""+idSubj+"\"").getString("nameSub"));
                    NewQuestionController.setDefaultTopic(DB.Select("topics",
                            "idTopic = \""+idTopic+"\"").getString("nameTopic"));
                    //---Заполняем таблицу ответов----------------------
                    ObservableList<Answer> answers = FXCollections.observableArrayList();
                    ResultSet rst = DB.Select("answers",
                            "idQuestion = \""+selection.getSelectedItem().getValue().getIdOwn()+"\"");
                    while (rst.next()){
                        Boolean isTrue;
                        if (rst.getInt("isCorrect")!=0)
                            isTrue = true;
                        else
                            isTrue = false;
                        answers.add(new Answer(rst.getInt("idAnswer"),
                                rst.getInt("idQuestion"),
                                rst.getString("nameAnswer"),
                                isTrue)); //Где-то здесь ошибка!!!!!!
                    }
                    tableView.setItems(answers);
                    break;
            }
        } catch (SQLException e){
            Alerts.Error(e.getMessage());
        }
    }
    public static ObservableList<Item> getSubjects(){
        ObservableList subs = FXCollections.observableArrayList();
        try {
            ResultSet rst = DB.Select("subjects", null);
            while (rst.next()) {
                subs.add(new Item(0, rst.getInt("idSub"), rst.getString("nameSub")));
            }
        } catch (SQLException e){
            Alerts.Error(e.getMessage());
            return null;
        }
        return subs;
    }
    public static ObservableList<Item> getTopics(int idSub){
        ObservableList tops = FXCollections.observableArrayList();
        try {
            ResultSet rst = DB.Select("topics", "idSub = \""+ idSub+"\"");
            while (rst.next()){
                tops.add(new Item (rst.getInt("idSub"),
                        rst.getInt("idTopic"),
                        rst.getString("nameTopic")));
            }
        }catch (SQLException e){
            Alerts.Error(e.getMessage());
            return null;
        }
        return tops;
    }
}
