package newtest.Controllers;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import newtest.Classes.*;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {
    @FXML
    Label lbStatus;
    @FXML
    MenuItem miAddSubject, miAddTopic, miAddQuestion, mniBeginTest;
    @FXML
    TreeView treeView;
    @FXML
    TableColumn<Answer, String> tcAnswer;
    @FXML
    TableColumn<Answer, Boolean> tcIsTrue;
    @FXML
    TableView<Answer> tableView;
    @FXML
    TextArea taNewAnswer;
    @FXML
    CheckBox cbIsTrue;
    @FXML
    Button btnAddAnswer, btnDelete;
    TreeItem<Item> selectedItem;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (DB.getConnection()!= null)
            lbStatus.setText("Соединение с базой данных установлено");
        else
            lbStatus.setText("Ошибка соединения с базой данных");
        buildTree(null);
        taNewAnswer.setWrapText(true);
        taNewAnswer.setDisable(true);
        btnAddAnswer.setDisable(true);
        cbIsTrue.setDisable(true);
    }
// --------Нажатие кнопки Exit------------
    public void OnBtnExitHandle() {
        System.exit(0);
    }

    //-----Построение дерева----------
    private void buildTree(TreeItem<Item> selItem){
        ObservableList<Item> subjects = FXCollections.observableArrayList();
        ObservableList<Item> themes = FXCollections.observableArrayList();
        ObservableList<Item> questions = FXCollections.observableArrayList();

        try {
            ResultSet rst = DB.Select("subjects", null);
            TreeItem<Item> rootItem = new TreeItem<>();
            rootItem.setExpanded(true);
            assert rst != null;
            while (rst.next()){
                subjects.add(new Item(0, rst.getInt("idSub"), "subjects",
                        rst.getString("nameSub")));
            }
            for (Item subject : subjects) {
                TreeItem<Item> sub = new TreeItem<>(subject);
                rootItem.getChildren().add(sub);
                rst = DB.Select("topics", "idSub = " + sub.getValue().getIdOwn());
                themes.clear();
                while (rst.next()) {
                    themes.add(new Item(rst.getInt("idSub"),
                            rst.getInt("idTopic"), "topics", rst.getString("nameTopic")));
                }
                for (Item item : themes) {
                    TreeItem<Item> theme = new TreeItem<>(item);
                    sub.getChildren().add(theme);
                    rst = DB.Select("questions", "idTopic = " + theme.getValue().getIdOwn());
                    questions.clear();
                    while (rst.next()) {
                        questions.add(new Item(rst.getInt("idTopic"),
                                rst.getInt("idQuestion"), "questions",
                                rst.getString("nameQuestion")));
                    }
                    for (Item question : questions) {
                        TreeItem<Item> quest = new TreeItem<>(question);
                        theme.getChildren().add(quest);
                    }
                }
            }
            treeView.setRoot(rootItem);
            treeView.setShowRoot(false);
            treeView.setEditable(true);
            treeView.setCellFactory((Callback<TreeView<Item>, TreeCell<Item>>) param -> new textFieldTreeCell());
            expandTreeView(selectedItem);
        }
        catch (SQLException e){
            Alerts.Error(e.getMessage());
            return;
        }
    }

    private void expandTreeView (TreeItem<Item> selectedItem){
        if (selectedItem != null && !selectedItem.isLeaf()){
            selectedItem.setExpanded(true);
            TreeItem<Item> parent = selectedItem.getParent();
            while(parent!=null){
                selectedItem.getParent().setExpanded(true);
                parent = parent.getParent();
            }
        }
        /*if (selectedItem != null){
            expandTreeView(selectedItem.getParent());
            if (!selectedItem.isLeaf())
                selectedItem.setExpanded(true);
        }*/
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
        buildTree(selectedItem);
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
        buildTree(selectedItem);
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
        buildTree(selectedItem);
    }

    public void OnTreeViewEntered(MouseEvent mouseEvent) {
        MultipleSelectionModel<TreeItem<Item>> selection = treeView.getSelectionModel();
        selection.setSelectionMode(SelectionMode.SINGLE);
        try {
        int level = treeView.getTreeItemLevel(selection.getSelectedItem());
        selectedItem = selection.getSelectedItem();
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
                    taNewAnswer.setDisable(true);
                    btnAddAnswer.setDisable(true);
                    cbIsTrue.setDisable(true);
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
                    taNewAnswer.setDisable(true);
                    btnAddAnswer.setDisable(true);
                    cbIsTrue.setDisable(true);
                    break;
                case 3:
                    taNewAnswer.setDisable(false);
                    btnAddAnswer.setDisable(false);
                    cbIsTrue.setDisable(false);
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
                        isTrue = rst.getInt("isCorrect") != 0;
                        answers.add(new Answer(rst.getInt("idAnswer"),
                                rst.getInt("idQuestion"),
                                rst.getString("nameAnswer"),
                                isTrue));
                    }
                    tableView.setItems(answers);
                    tableView.setEditable(true);
                    //Перенос строк в таблице (отключает редактирование) РАЗОБРАТЬСЯ!!!!
                    tcAnswer.setCellFactory(param -> new TableCell<Answer, String>(){
                        @Override
                        protected void updateItem(String item, boolean empty){
                            super.updateItem(item, empty);
                            if (item == null || empty){
                                setText(null);
                            } else {
                                Text text = new Text(item);
                                text.wrappingWidthProperty().bind(getTableColumn().widthProperty().subtract(35));
                                setGraphic(text);
                            }
                        }
                    });
                    //Редактирование данных в ячейке
                    tcAnswer.setCellValueFactory(new PropertyValueFactory<>("Answer"));
                    tcAnswer.setCellFactory(TextFieldTableCell.forTableColumn());
                    tcAnswer.setOnEditCommit(event -> {
                        event.getTableView().getItems().get(event.getTablePosition().getRow()).setAnswer(event.getNewValue());
                        DB.Update("answers",
                                "nameAnswer = \""+event.getNewValue()+"\"",
                                "idAnswer = "+ event.getTableView().getItems().get(event.getTablePosition(
                                ).getRow()).getIdAnswer());
                    });

                    tcIsTrue.setCellValueFactory(param -> {
                        Answer answer = param.getValue();
                        SimpleBooleanProperty boolProp = new SimpleBooleanProperty(answer.isIsTrue());
                        boolProp.addListener((observable, oldValue, newValue) -> {
                            answer.setIsTrue(newValue);
                            int isTrue;
                            if (newValue) isTrue = 1;
                            else isTrue = 0;
                            DB.Update("answers", "isCorrect = \""+isTrue+"\"",
                                    "idAnswer = \""+answer.getIdAnswer()+"\"");
                        });
                        return boolProp;
                    });
                    tcIsTrue.setCellFactory(param -> {
                        CheckBoxTableCell<Answer, Boolean> cell = new CheckBoxTableCell<>();
                        cell.setAlignment(Pos.CENTER);
                        return cell;
                    });
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
                subs.add(new Item(0, rst.getInt("idSub"), "subjects",
                        rst.getString("nameSub")));
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
                        rst.getInt("idTopic"), "topics",
                        rst.getString("nameTopic")));
            }
        }catch (SQLException e){
            Alerts.Error(e.getMessage());
            return null;
        }
        return tops;
    }

    public void onAddAnswerHandle(ActionEvent actionEvent) {
        MultipleSelectionModel<TreeItem<Item>> selection = treeView.getSelectionModel();
        int idAnswer = selection.getSelectedItem().getValue().getIdOwn();
        int isTrue;
        if (cbIsTrue.isSelected())
            isTrue = 1;
        else
            isTrue = 0;
        DB.Insert("Answers",
                "nameAnswer, idQuestion, isCorrect",
                taNewAnswer.getText()+"\", \""+idAnswer+"\" ,\""+isTrue);
        taNewAnswer.clear();
        cbIsTrue.setSelected(false);
    }

    public void OnDeleteHandle() {
        MultipleSelectionModel<TreeItem<Item>> selection = treeView.getSelectionModel();
        int level = treeView.getTreeItemLevel(selection.getSelectedItem());
        switch (level){
            case 1:
                if (!selection.getSelectedItem().getChildren().isEmpty())
                    Alerts.Warning("Данный предмет имеет темы",
                            "Для продолжения необходимо удалить темы");
                else
                    if (Alerts.Confirmation(selection.getSelectedItem().getValue().getName()))
                        DB.Delete("subjects",
                                "idSub",
                                selection.getSelectedItem().getValue().getIdOwn());
                break;
            case 2:
                if (!selection.getSelectedItem().getChildren().isEmpty())
                    Alerts.Warning("Данная тема имеет вопросы",
                            "Для продолжения необходимо удалить вопросы");
                else
                    if(Alerts.Confirmation(selection.getSelectedItem().getValue().getName()))
                        DB.Delete("topics", "idTopic", selection.getSelectedItem().getValue().getIdOwn());
                break;
            case 3:
                if(Alerts.Confirmation(selection.getSelectedItem().getValue().getName())){
                    int idQuestion = selection.getSelectedItem().getValue().getIdOwn();
                    DB.Delete("answers", "idQuestion", idQuestion);
                    DB.Delete("questions", "idQuestion", idQuestion);
                }
                break;

        }
        buildTree(selection.getSelectedItem().getParent());
    }

    public void OnMniExport(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("../FXML/Export.fxml"));
        stage.setTitle("Экспортировать базу данных");
        stage.setResizable(false);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    public void onMniBeginTestHandle(ActionEvent actionEvent) throws IOException{
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("../FXML/TestSetupFrm.fxml"));
        stage.setTitle("Настройки теста");
        stage.setResizable(true);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    public void onMniTasksHandle(ActionEvent actionEvent) throws IOException{
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("../FXML/TaskFrm.fxml"));
        stage.setTitle("Задачи");
        stage.setResizable(false);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }
}
