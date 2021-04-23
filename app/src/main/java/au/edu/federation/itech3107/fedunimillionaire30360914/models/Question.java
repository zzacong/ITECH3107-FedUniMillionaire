package au.edu.federation.itech3107.fedunimillionaire30360914.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static au.edu.federation.itech3107.fedunimillionaire30360914.utils.MyString.capitalise;

public class Question {

    private static final String LOG_TAG = Question.class.getSimpleName();

    public enum Difficulty {
        easy, medium, hard;
    }

    private Difficulty difficulty;
    private List<String> choices = new ArrayList<>();

    private String title;
    private int answer;
    public boolean isChecked;

    public Question() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = capitalise(title);
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    public List<String> getChoices() {
        return choices;
    }

    public void setChoices(String a, String b, String c, String d) {
        this.choices.clear();
        this.choices.add(capitalise(a));
        this.choices.add(capitalise(b));
        this.choices.add(capitalise(c));
        this.choices.add(capitalise(d));
    }

    public void setChoices(List<String> choices) {
        this.choices.clear();
        for (String str : choices) {
            this.choices.add(capitalise(str));
        }
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public boolean attempt(int choice) {
        return choice == answer;
    }



}
