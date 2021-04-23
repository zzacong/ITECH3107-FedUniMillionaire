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
    private boolean isChecked;

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

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public boolean attempt(int choice) {
        return choice == answer;
    }

    public JSONObject toJSONObj() {
        JSONObject json = new JSONObject();
        try {
            JSONArray arr = new JSONArray();
            for (int i = 0; i < choices.size(); i++) {
                if (i == answer) continue;
                arr.put(choices.get(i));
            }

            json.put("category", "General Knowledge");
            json.put("type", "multiple");
            json.put("difficulty", getDifficulty().toString());
            json.put("question", getTitle());
            json.put("correct_answer", choices.get(answer));
            json.put("incorrect_answers", arr);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(LOG_TAG, json.toString());
        return json;
    }

    @Override
    public String toString() {
        return "Question {" +
                "difficulty=" + difficulty +
                ", choices=" + choices +
                ", title='" + title + '\'' +
                ", answer=" + answer +
                ", isChecked=" + isChecked +
                '}';
    }
}
