package newtest.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import newtest.Classes.Question;

import java.net.URL;
import java.util.Collections;
import java.util.ResourceBundle;

public class TestViewController implements Initializable {
    @FXML
    Button btnCancel, btnSave, btnPrint;
    @FXML
    WebView webView;

    private static ObservableList<Question> test = FXCollections.observableArrayList();
    private static int numQuestions;
    private static int numVariants;

//-------------SETTERS----------------------------------------
    public static void setNumQuestions(int numQuestions) {
        TestViewController.numQuestions = numQuestions;
    }

    public static void setNumVariants(int numVariants) {
        TestViewController.numVariants = numVariants;
    }

    public static void setTest(ObservableList<Question> rawTest) {
        test = rawTest;
    }
//-------------BUTTONS---------------------------------------
    public void onBtnCancelHandle(ActionEvent actionEvent) {
        Stage stage = (Stage)btnCancel.getScene().getWindow();
        stage.close();
    }

    public void onBtnSaveHandle(ActionEvent actionEvent) {
    }

    public void onBtnPrintHandle(ActionEvent actionEvent) {
    }
//-------------INITIALIZE------------------------------------
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        WebEngine engine = webView.getEngine();
        engine.loadContent(makeTest(test));
    }

    private String makeTest(ObservableList<Question> rawTest){
        String key = "";
        String printableTest = "<!doctype html><html lang-\"ru-BY\"><head><meta charset=\"UTF-8\">" +
                "<style> ol {list-style-type: none;}"+
                "li::before {margin-right: 5px;}"+
                "li:nth-child(1)::before { content: 'а)'; }"+
                "li:nth-child(2)::before { content: 'б)'; }"+
                "li:nth-child(3)::before { content: 'в)'; }"+
                "li:nth-child(4)::before { content: 'г)'; }"+
                "li:nth-child(5)::before { content: 'д)'; }"+
                "li:nth-child(6)::before { content: 'е)'; }"+
                "</style></head><body>";
        for (int i = 0; i < numVariants; i++){
            int v = i+1;
            printableTest += "<p>Вариант № "+ v +"</p>";
            key += "<p>Вариант № "+v+"</p>";
            for (int k = 0; k < numQuestions; k++){
                int nq = k+1;
                key +="<p>"+nq;
                printableTest += "<p>"+nq+". "+rawTest.get(k).getQuestion()+"</p><ol>";
                for (int a = 0; a < rawTest.get(k).getAnswers().size(); a++){
                    int correct;
                    if (rawTest.get(k).getAnswers().get(a).isIsTrue())
                        correct = a;
                    printableTest += "<li>"+rawTest.get(k).getAnswers().get(a).getAnswer()+"</li>";
                }
                printableTest += "</ol>";
            }
            Collections.shuffle(test);
        }
        printableTest = printableTest + "<p>Ответы</p>" + key+"</body></html>";
        return printableTest;
    }
}
