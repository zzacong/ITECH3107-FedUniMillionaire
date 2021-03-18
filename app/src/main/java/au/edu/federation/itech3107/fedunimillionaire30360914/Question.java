package au.edu.federation.itech3107.fedunimillionaire30360914;

import java.util.ArrayList;

public class Question {

    private String title;
    private int answer;
    private ArrayList<String> choices = new ArrayList<String>();

    public Question(String title) {
        this.title = title;
    }

    public Question(String title, ArrayList<String> choices, int answer) {
        this(title);
        this.choices = choices;
        this.answer = answer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
        this.choices.add(a);
        this.choices.add(b);
        this.choices.add(c);
        this.choices.add(d);
    }

    public boolean attempt(int choice) {
        return choice == answer;
    }

}
