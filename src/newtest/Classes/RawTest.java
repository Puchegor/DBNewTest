package newtest.Classes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Collections;

public class RawTest {
    private static ObservableList<Question>questions = FXCollections.observableArrayList();
    private static ObservableList<Task>tasks = FXCollections.observableArrayList();

    public static void setQuestions(ObservableList<Question> questions) {
        RawTest.questions = questions;
    }

    public static void setTasks(ObservableList<Task> tasks) {
        RawTest.tasks = tasks;
    }

    public static ObservableList<Question> getQuestions() {
        return questions;
    }

    public static ObservableList<Task> getTasks() {
        return tasks;
    }

    public static void shuffle(){
        Collections.shuffle(questions);
    }
}
