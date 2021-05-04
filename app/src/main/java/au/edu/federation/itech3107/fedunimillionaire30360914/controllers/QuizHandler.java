package au.edu.federation.itech3107.fedunimillionaire30360914.controllers;

import android.util.Log;

import java.util.List;
import java.util.Random;

import au.edu.federation.itech3107.fedunimillionaire30360914.helpers.QuestionBank;
import au.edu.federation.itech3107.fedunimillionaire30360914.models.Question;
import au.edu.federation.itech3107.fedunimillionaire30360914.models.Question.Difficulty;

import static au.edu.federation.itech3107.fedunimillionaire30360914.helpers.QuestionBank.QUESTION_VALUE_SAFE_MONEY_LIST;

public class QuizHandler {

    private static final String LOG_TAG = QuizHandler.class.getSimpleName();
    private final int MAX = 11;
    private List<Question> quizQuestions;
    private QuestionBank questionBank;
    private Integer currentNumber = 0;


    public QuizHandler(QuestionBank questionBank) {
        this.questionBank = questionBank;
//        this.questionList = questionBank.getQuizQuestions();
    }

    public void loadQuestions() {
        quizQuestions = questionBank.getQuizQuestions();
    }

    public Integer getCurrentNumber() {
        return currentNumber + 1;
    }

    public Integer getQuestionsLeft() {
        return MAX - (currentNumber + 1);
    }

    public Question startFrom(int number) {
        currentNumber = Math.max(number - 1, 0);
        return getQuestionAt(currentNumber);
    }

    public Question nextQuestion() {
        return getQuestionAt(++currentNumber);
    }

    public Question currentQuestion() {
        return getQuestionAt(currentNumber);
    }

    private Question getQuestionAt(int index) {
        if (index < quizQuestions.size()) {
            return quizQuestions.get(index);
        }
        return null;
    }

    public Integer getSafeMoneyValue() {
        return QUESTION_VALUE_SAFE_MONEY_LIST.get(currentNumber)[1];
    }

    public Integer getQuestionValue() {
        return QUESTION_VALUE_SAFE_MONEY_LIST.get(currentNumber)[0];
    }

    public Question switchQuestion() {
        Log.d(LOG_TAG, "[QUIZ HANDLER] Current number: " + currentNumber);
        Question currentQuestion = quizQuestions.get(currentNumber);
        Log.d(LOG_TAG, "[QUIZ HANDLER] Current question: " + currentQuestion.getTitle());

        Difficulty difficulty = currentQuestion.getDifficulty();
        List<Question> questionList = questionBank.getQuestions(difficulty);
        Random rand = new Random();
        while (true) {
            int randInt = rand.nextInt(questionList.size());
            Question newQuestion = questionList.get(randInt);
            if (!newQuestion.getTitle().equals(currentQuestion.getTitle())) {
                Log.d(LOG_TAG, "[QUIZ HANDLER] New question: " + newQuestion.getTitle());
                questionList.remove(currentNumber);
                questionList.add(currentNumber, newQuestion);
                return newQuestion;
            }
        }
    }
}
