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

    public void OnBtnExport() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Выберите имя файла для сохранения");
        fc.showSaveDialog(btnExport.getScene().getWindow());
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
                }
                if (rbTopic.isArmed()) {
                    rst = DB.Select("topics", null);
                    while (rst.next())
                        lv.add(new Item(rst.getInt("idSub"), rst.getInt("idTopic"),
                                "topics", rst.getString("nameTopic")));
                }
                listView.setItems(lv);
            } catch (SQLException | NullPointerException e) {
                Alerts.Error(e.getMessage());
            }
        });
    }
}
