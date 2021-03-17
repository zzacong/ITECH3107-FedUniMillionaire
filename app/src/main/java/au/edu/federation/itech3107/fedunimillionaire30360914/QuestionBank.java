package au.edu.federation.itech3107.fedunimillionaire30360914;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QuestionBank {

  private static QuestionBank instance = new QuestionBank();
  private ArrayList<Question> questions;

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

    questions.add(q1);
    questions.add(q2);
    questions.add(q3);
    questions.add(q4);
    questions.add(q5);
    questions.add(q6);
    questions.add(q7);
    questions.add(q8);
    questions.add(q9);
    questions.add(q10);
    questions.add(q11);
  }

  public static ArrayList<Question> getQuestions() {
    return instance.questions;
  }
}
