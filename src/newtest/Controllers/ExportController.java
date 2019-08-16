package newtest.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import newtest.Classes.Alerts;
import newtest.Classes.DB;
import newtest.Classes.Item;

import java.io.File;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ExportController implements Initializable {
    @FXML
    Button btnCancel, btnExport;
    @FXML
    RadioButton rbSubject, rbTopic;
    @FXML
    ListView<Item> listView;
    int i = 0;

    public void OnBtnExport() {
        ResultSet rst;
        MultipleSelectionModel<Item> selection = listView.getSelectionModel();
        if (selection.isEmpty()){
            Alerts.Warning("Не выбран элемент базы для экспорта",
                    "Пожалуйста, выберите предмет или тему которую необхлдимо экспортровать");
            return;
        }
        FileChooser fc = new FileChooser();
        fc.setTitle("Выберите имя файла для сохранения");
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Файлы баз данных",
                "*.db", "*.sqlite");
        fc.getExtensionFilters().add(extensionFilter);
        File file = fc.showSaveDialog(btnExport.getScene().getWindow());
        if (file == null)
            return;
        switch (i) {
            case 1:
                int subjectID = selection.getSelectedItem().getIdOwn();
                DB.Query("ATTACH DATABASE \'"+file.getPath()+"\' AS otherdb; ");
                DB.Query("CREATE TABLE otherdb.subjects (idSub INTEGER PRIMARY KEY AUTOINCREMENT, nameSub TEXT); ");
                DB.Query("CREATE TABLE otherdb.topics (idTopic INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "idSub INTEGER NOT NULL, nameTopic TEXT(50), " +
                        "FOREIGN KEY (idSub) REFERENCES subjects (idSub)); ");
                DB.Query("CREATE TABLE otherdb.questions (idQuestion INTEGER PRIMARY KEY AUTOINCREMENT, idTopic INTEGER NOT NULL, " +
                        "nameQuestion TEXT, correctAnswer TEXT, FOREIGN KEY (idTopic) REFERENCES topics(idTopic)); ");
                DB.Query("CREATE TABLE otherdb.answers (idAnswer INTEGER PRIMARY KEY AUTOINCREMENT, idQuestion INTEGER NOT NULL, " +
                    "nameAnswer TEXT, isCorrect INTEGER, FOREIGN KEY (idQuestion) REFERENCES questions (idQuestion)); ");
                DB.Query("INSERT INTO otherdb.subjects SELECT * FROM subjects WHERE idSub = \""+
                     subjectID+"\"");
                DB.Query("INSERT INTO otherdb.topics SELECT * FROM topics WHERE idSub = \""+
                     subjectID+"\"");
                DB.Query("INSERT INTO otherdb.questions " +
                        "SELECT questions.idQuestion, questions.idTopic, questions.nameQuestion, questions.correctAnswer " +
                        "FROM questions, topics, subjects " +
                        "WHERE questions.idTopic=topics.idTopic " +
                        "AND topics.idSub = subjects.idSub " +
                        "AND subjects.idSub = \""+subjectID+"\"");
                DB.Query("INSERT INTO otherdb.answers "+
                        "SELECT answers.idAnswer, answers.idQuestion, answers.nameAnswer, answers.isCorrect " +
                        "FROM answers, questions, topics, subjects " +
                        "WHERE answers.idQuestion = questions.idQuestion " +
                        "AND questions.idTopic = topics.idTopic " +
                        "AND topics.idSub = subjects.idSub " +
                        "AND subjects.idSub = \""+subjectID+"\"");
                break;
            case 2:
                int topicID = selection.getSelectedItem().getIdOwn();
                DB.Query("ATTACH DATABASE \'"+file.getPath()+"\' AS otherdb; ");
                DB.Query("CREATE TABLE otherdb.subjects (idSub INTEGER PRIMARY KEY AUTOINCREMENT, nameSub TEXT); ");
                DB.Query("CREATE TABLE otherdb.topics (idTopic INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "idSub INTEGER NOT NULL, nameTopic TEXT(50), " +
                        "FOREIGN KEY (idSub) REFERENCES subjects (idSub)); ");
                DB.Query("CREATE TABLE otherdb.questions (idQuestion INTEGER PRIMARY KEY AUTOINCREMENT, idTopic INTEGER NOT NULL, " +
                        "nameQuestion TEXT, correctAnswer TEXT, FOREIGN KEY (idTopic) REFERENCES topics(idTopic)); ");
                DB.Query("CREATE TABLE otherdb.answers (idAnswer INTEGER PRIMARY KEY AUTOINCREMENT, idQuestion INTEGER NOT NULL, " +
                        "nameAnswer TEXT, isCorrect INTEGER, FOREIGN KEY (idQuestion) REFERENCES questions (idQuestion)); ");
                DB.Query("INSERT INTO otherdb.topics SELECT * FROM topics WHERE idTopic = \""+
                        topicID+"\"");
                DB.Query("INSERT INTO otherdb.questions " +
                        "SELECT questions.idQuestion, questions.idTopic, questions.nameQuestion, questions.correctAnswer " +
                        "FROM questions, topics WHERE questions.idTopic=topics.idTopic AND topics.idTopic = \""+topicID+"\"");
                DB.Query("INSERT INTO otherdb.answers "+
                        "SELECT answers.idAnswer, answers.idQuestion, answers.nameAnswer, answers.isCorrect " +
                        "FROM answers, questions, topics WHERE answers.idQuestion = questions.idQuestion " +
                        "AND questions.idTopic = topics.idTopic AND topics.idTopic = \""+topicID+"\"");

                break;
            default:
                break;
        }
        Alerts.Success("Экспорт базы данных успешно завершен");
        Stage stage = (Stage) rbSubject.getScene().getWindow();
        stage.close();
    }

    public void onBtnCancel(ActionEvent actionEvent) {
        Stage stage = (Stage)btnCancel.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ToggleGroup toggleGroup = new ToggleGroup();
        rbSubject.setToggleGroup(toggleGroup);
        rbTopic.setToggleGroup(toggleGroup);
        toggleGroup.selectToggle(rbSubject);
        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            ObservableList<Item> lv = FXCollections.observableArrayList();
            ResultSet rst;
            try {
                if (rbSubject.isArmed()) {
                    rst = DB.Select("subjects", null);
                    while (rst.next())
                        lv.add(new Item(0, rst.getInt("idSub"),
                                "subjects", rst.getString("nameSub")));
                    i=1;
                }
                if (rbTopic.isArmed()) {
                    rst = DB.Select("topics", null);
                    while (rst.next())
                        lv.add(new Item(rst.getInt("idSub"), rst.getInt("idTopic"),
                                "topics", rst.getString("nameTopic")));
                    i=2;
                }
                listView.setItems(lv);
            } catch (SQLException | NullPointerException e) {
                Alerts.Error(e.getMessage());
            }
        });
    }
}
