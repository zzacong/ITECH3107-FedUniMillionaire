package au.edu.federation.itech3107.fedunimillionaire30360914;

import java.util.ArrayList;

public class Question {

    public enum Difficulty {
        easy, medium, hard;
    }

    private String title;
    private int answer;
    private Difficulty difficulty;
    private ArrayList<String> choices = new ArrayList<>();

    public Question() {}

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

    public ArrayList<String> getChoices() {
        return choices;
    }

    public void setChoices(String a, String b, String c, String d) {
        this.choices.clear();
        this.choices.add(capitalise(a));
        this.choices.add(capitalise(b));
        this.choices.add(capitalise(c));
        this.choices.add(capitalise(d));
    }

    public void setChoices(ArrayList<String> choices) {
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

    private String capitalise(String text) {
        if (text != null && !text.isEmpty()) {
            return text.substring(0, 1).toUpperCase() + text.substring(1);
        }
        return text;
    }

}
