package au.edu.federation.itech3107.fedunimillionaire30360914;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionBank {

    private static final QuestionBank INSTANCE = new QuestionBank();
    private final ArrayList<Question> questions;

    private static final List<Integer> SAFE_MONEY_LIST = Arrays.asList(0, 1000, 32000, 1000000);

    private static final Map<Integer, Integer> QUESTION_VALUE_LIST = new HashMap<Integer, Integer>() {
        {
            put(1, 1000);
            put(2, 2000);
            put(3, 4000);
            put(4, 8000);
            put(5, 16000);
            put(6, 32000);
            put(7, 64000);
            put(8, 125000);
            put(9, 250000);
            put(10, 500000);
            put(11, 1000000);
        }
    };

    private QuestionBank() {
        Question q1 = new Question("A sousaphone is also known as what?");
        q1.setChoices("Tuba", "Trumpet", "Banjo", "Harmonica");
        q1.setAnswer(0);

        Question q2 = new Question("Which of the following is not a Roman numeral?");
        q2.setChoices("M", "L", "G", "D");
        q2.setAnswer(2);

        Question q3 = new Question("If you celebrate something bi-annually, how often do you celebrate it?");
        q3.setChoices("Every two months", "Every two years", "Every three months", "Every three years");
        q3.setAnswer(1);

        Question q4 = new Question(
                "The four fundamental forces in physics are strong, electromagnetic, gravitational, and what?");
        q4.setChoices("Weak", "Frictional", "Magnetic", "Normal");
        q4.setAnswer(0);

        Question q5 = new Question("Which of these African countries is located south of the equator?");
        q5.setChoices("Chad", "Mali", "Angola", "Cameroon");
        q5.setAnswer(2);

        Question q6 = new Question("A popular expression goes \"A bird in the hand is worth two in the\" what?");
        q6.setChoices("Tree", "Bush", "Window", "Pot");
        q6.setAnswer(1);

        Question q7 = new Question("Which insect inspired the term 'computer bug'?");
        q7.setChoices("Cockroach", "Moth", "Fly", "Beetle");
        q7.setAnswer(1);

        Question q8 = new Question("Which part of a chicken is commonly called the 'drumstick'?");
        q8.setChoices("Leg", "Wing", "Thigh", "Breast");
        q8.setAnswer(0);

        Question q9 = new Question("Which man does NOT have a chemical element named after him?");
        q9.setChoices("Isaac Newton", "Enrico Fermi", "Albert Einstein", "Niels Bohr");
        q9.setAnswer(0);

        Question q10 = new Question("What is the Celsius equivalent of 77 degrees Fahrenheit?");
        q10.setChoices("15", "20", "25", "30");
        q10.setAnswer(2);

        Question q11 = new Question("What sort of animal is Walt Disney's Dumbo?");
        q11.setChoices("Deer", "Rabbit", "Elephant", "Donkey");
        q11.setAnswer(2);

        this.questions = new ArrayList<Question>();
        this.questions.add(q1);
        this.questions.add(q2);
        this.questions.add(q3);
        this.questions.add(q4);
        this.questions.add(q5);
        this.questions.add(q6);
        this.questions.add(q7);
        this.questions.add(q8);
        this.questions.add(q9);
        this.questions.add(q10);
        this.questions.add(q11);
    }

    public static QuestionBank getInstance() {
        return INSTANCE;
    }

    public ArrayList<Question> getQuestions() {
        return this.questions;
    }

    public int getQuestionValue(int key) {
        return QUESTION_VALUE_LIST.get(key);
    }

    public int getSafeMoneyValue(int index) {
        return SAFE_MONEY_LIST.get(index);
    }
}
