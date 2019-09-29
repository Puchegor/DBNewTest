package newtest.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class TestViewController {
    @FXML
    Button btnCancel, btnSave, btnPrint;
    @FXML
    WebView webView;

    public void onBtnCancelHandle(ActionEvent actionEvent) {
        Stage stage = (Stage)btnCancel.getScene().getWindow();
        stage.close();
    }

    public void onBtnSaveHandle(ActionEvent actionEvent) {
    }

    public void onBtnPrintHandle(ActionEvent actionEvent) {
    }
}
