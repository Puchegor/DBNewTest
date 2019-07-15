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
import newtest.Classes.DB;
import newtest.Classes.Item;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {
    @FXML
    Label lbStatus;
    @FXML
    MenuItem miAddSubject, miAddTopic, miAddQuestion;
    @FXML
    TreeView treeView;

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
            for (Iterator<Item> is = subjects.iterator(); is.hasNext();){
                TreeItem<Item> sub = new TreeItem<>(is.next());
                rootItem.getChildren().add(sub);
                rst = DB.Select("topics", "idSub = "+sub.getValue().getIdOwn());
                themes.clear();
                while (rst.next()){
                    themes.add(new Item(rst.getInt("idSub"),
                            rst.getInt("idTopic"), rst.getString("nameTopic")));
                }
                for (Iterator<Item> it = themes.iterator(); it.hasNext();){
                    TreeItem<Item> theme = new TreeItem<>(it.next());
                    sub.getChildren().add(theme);
                    rst = DB.Select("questions", "idTopic = "+theme.getValue().getIdOwn());
                    questions.clear();
                    while (rst.next()){
                        questions.add(new Item(rst.getInt("idTopic"),
                                rst.getInt("idQuestion"), rst.getString("nameQuestion")));
                    }
                    for (Iterator<Item> iq = questions.iterator(); iq.hasNext();){
                        TreeItem<Item> quest = new TreeItem<>(iq.next());
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
                    ObservableList<Item> subs = FXCollections.observableArrayList();
                    ResultSet rst = DB.Select("subjects", null);
                    while (rst.next()){
                        subs.add(new Item(0, rst.getInt("idSub"), rst.getString("nameSub")));
                    }
                    NewTopicController.setSubjects(subs);
                    String selectedSubject = selection.getSelectedItem().getValue().getName();
                    NewTopicController.setDefaultSubject(selectedSubject);
                    NewQuestionController.setSubjects(subs);
                    int idSubject = DB.Select("subjects",
                            "nameSub = \""+selectedSubject+"\"").getInt("idSub");
                    rst = DB.Select("topics", "idSub = \""+idSubject+"\"" );
                    ObservableList<Item> tops = FXCollections.observableArrayList();
                    while (rst.next())
                        tops.add(new Item(rst.getInt("idSub"),
                                rst.getInt("idTopic"),
                                rst.getString("nameTopic")));
                    NewQuestionController.setTopics(tops);
                    NewQuestionController.setCbSubject(selection.getSelectedItem().getValue().getName());
                    break;
                case 2:
                    String subject = (DB.Select("subjects",
                            "idSub = " +selection.getSelectedItem().getValue().getIdParent()).getString("nameSub"));
                    NewTopicController.setDefaultSubject(subject);
                    NewQuestionController.setCbSubject(subject);
                    NewQuestionController.setCbTopic(selection.getSelectedItem().getValue().getName());
                    break;
                case 3:
                    break;
            }
        } catch (SQLException e){
            Alerts.Error(e.getMessage());
        }
    }
}
