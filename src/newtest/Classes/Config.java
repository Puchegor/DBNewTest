package newtest.Classes;

        import javafx.scene.control.Alert;
        import javafx.scene.control.ButtonType;
        import javafx.stage.FileChooser;
        import javafx.stage.Stage;

        import java.io.File;
        import java.io.FileInputStream;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.util.Optional;
        import java.util.Properties;

public class Config {
    public static final File config = new File("config.ini");
    public static Properties prop = new Properties();

    public static String ConfigRead() throws IOException {
        if (!config.exists())
            config.createNewFile();
        prop.load(new FileInputStream(config));
        prop.getProperty("DBFileName");
        if (prop.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Файл базы данных не найден");
            alert.setTitle("Внимание");
            alert.setContentText("Выберите создание нового файла базы данных или найдите файл на диске");
            ButtonType btnNew = new ButtonType("Новый");
            ButtonType btnSearch = new ButtonType("Найти");
            ButtonType btnCancel = new ButtonType("Отмена");
            alert.getButtonTypes().setAll(btnNew, btnSearch, btnCancel);
            Optional<ButtonType> choice = alert.showAndWait();
            if (choice.get() == btnNew){
                prop.setProperty("DBFileName","test.sq3");
                prop.store(new FileOutputStream(config), null);
            }
            if (choice.get()==btnSearch){
                FileChooser chooser = new FileChooser();
                chooser.setTitle("Выберите файл базы данных sqlite 3");
                File dbFile = chooser.showOpenDialog(new Stage());
                if (dbFile !=null){
                    prop.setProperty("DBFileName", dbFile.toString());
                    prop.store(new FileOutputStream(config), null);
                }
            }
            if (choice.get()==btnCancel){
                alert.close();
            }
        }
        return (prop.getProperty("DBFileName"));
    }
}

