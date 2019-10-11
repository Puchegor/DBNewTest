package newtest.Controllers;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import newtest.Classes.Alerts;
import newtest.Classes.Question;
import newtest.Classes.RawTest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TestViewController implements Initializable {
    @FXML
    Button btnCancel, btnSave, btnPrint;
    @FXML
    WebView webView;

    //private static ObservableList<Question> test = FXCollections.observableArrayList();
    private static int numQuestions;
    private static int numVariants;
    private String printableTest;

//-------------SETTERS----------------------------------------
    public static void setNumQuestions(int numQuestions) {
        TestViewController.numQuestions = numQuestions;
    }

    public static void setNumVariants(int numVariants) {
        TestViewController.numVariants = numVariants;
    }

    /*public static void setTest(ObservableList<Question> rawTest) {
        test = rawTest;
    }*/
//-------------BUTTONS---------------------------------------
    public void onBtnCancelHandle(ActionEvent actionEvent) {
        Stage stage = (Stage)btnCancel.getScene().getWindow();
        stage.close();
    }

    public void onBtnSaveHandle(ActionEvent actionEvent) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Выберите имя файла");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Файлы HTML", "*.html");
        chooser.getExtensionFilters().add(filter);
        File testFile = chooser.showSaveDialog(btnSave.getScene().getWindow());
        if (testFile != null){
            try (FileWriter writer = new FileWriter(testFile, false)){
                writer.write(printableTest);
                writer.flush();
            }catch (IOException e){
                Alerts.Error(e.getMessage());
            }
        }
    }

    public void onBtnPrintHandle(ActionEvent actionEvent) {
    }
//-------------INITIALIZE------------------------------------
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        WebEngine engine = webView.getEngine();
        engine.loadContent(makeTest(RawTest.getQuestions()));
    }

    private String makeTest(ObservableList<Question> rawTest){
        String key = "";
        printableTest = "<!doctype html><html lang-\"ru-BY\"><head><meta charset=\"UTF-8\">" +
                "<style> ol {list-style-type: none;}"+
                "li::before {margin-right: 5px;}"+
                "li:nth-child(1)::before { content: 'а)'; }"+
                "li:nth-child(2)::before { content: 'б)'; }"+
                "li:nth-child(3)::before { content: 'в)'; }"+
                "li:nth-child(4)::before { content: 'г)'; }"+
                "li:nth-child(5)::before { content: 'д)'; }"+
                "li:nth-child(6)::before { content: 'е)'; }"+
                "</style>" +
                "<style>.column{" +
                "column-count: 2; column-gap: 30px;}" +
                "</style></head><body><div class=\"column\">";
        for (int i = 0; i < numVariants; i++){
            int v = i+1;
            printableTest += "<p>Вариант № "+ v +"</p>";
            key += "<p>Вариант № "+v+"</p>";
            for (int k = 0; k < numQuestions; k++){
                int nq = k+1;
                key +="<p>"+nq;
                printableTest += "<p>"+nq+". "+rawTest.get(k).getQuestion()+"</p><ol>";
                int correct = -1;
                for (int a = 0; a < rawTest.get(k).getAnswers().size(); a++){
                    if (rawTest.get(k).getAnswers().get(a).isIsTrue())
                        correct = a;
                    printableTest += "<li>"+rawTest.get(k).getAnswers().get(a).getAnswer()+"</li>";
                }
                switch (correct){
                    case 0:
                        key += "a</p>";
                        break;
                    case 1:
                        key += "б</p>";
                        break;
                    case 2:
                        key += "в</p>";
                        break;
                    case 3:
                        key += "г</p>";
                        break;
                    case 4:
                        key += "д</p>";
                        break;
                    default:
                        key += "</p>";
                        break;
                }
                printableTest += "</ol>";
            }
            RawTest.shuffle();
        }
        printableTest = printableTest + "<p>Ответы</p>" + key;
        printableTest += "<p>ЗАДАЧИ</p>";
        for (int i = 0; i < RawTest.getTasks().size(); i++){
            printableTest += "<p>"+RawTest.getTasks().get(i).getNameTask()+"</p>";
            printableTest += "<p>"+ RawTest.getTasks().get(i).getAnswer()+"</p>";
        }
        printableTest += "</div></body></html>";

        return printableTest;
    }
}
