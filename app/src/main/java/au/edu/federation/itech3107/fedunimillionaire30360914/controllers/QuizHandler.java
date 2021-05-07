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
    private static final int MAX = 11;
    private List<Question> mQuizQuestions;
    private QuestionBank mQuestionBank;
    private Integer mCurrentNumber = 0;


    public QuizHandler(QuestionBank questionBank) {
        this.mQuestionBank = questionBank;
        loadQuestions();
    }


    public void loadQuestions() {
        mQuizQuestions = mQuestionBank.getQuizQuestions();
    }

    public Integer getCurrentNumber() {
        return mCurrentNumber + 1;
    }

    public Integer getQuestionsLeft() {
        return MAX - (mCurrentNumber + 1);
    }

    public Question startFrom(int number) {
        mCurrentNumber = Math.max(number - 1, 0);
        return getQuestionAt(mCurrentNumber);
    }

    public Question nextQuestion() {
        return getQuestionAt(++mCurrentNumber);
    }

    public Question currentQuestion() {
        return getQuestionAt(mCurrentNumber);
    }

    private Question getQuestionAt(int index) {
        if (index < mQuizQuestions.size()) {
            return mQuizQuestions.get(index);
        }
        return null;
    }

    public Integer getSafeMoneyValue() {
        return QUESTION_VALUE_SAFE_MONEY_LIST.get(mCurrentNumber)[1];
    }

    public Integer getQuestionValue() {
        return QUESTION_VALUE_SAFE_MONEY_LIST.get(mCurrentNumber)[0];
    }

    public Question switchQuestion() {
        Log.d(LOG_TAG, "[QUIZ HANDLER] Current number: " + mCurrentNumber);
        Question currentQuestion = mQuizQuestions.get(mCurrentNumber);
        Log.d(LOG_TAG, "[QUIZ HANDLER] Current question: " + currentQuestion.toString());

        Difficulty difficulty = currentQuestion.getDifficulty();
        List<Question> questionList = mQuestionBank.getQuestions(difficulty);
        Random rand = new Random();
        while (true) {
            int randInt = rand.nextInt(questionList.size());
            Question newQuestion = questionList.get(randInt);
            if (!newQuestion.getTitle().equals(currentQuestion.getTitle())) {
                Log.d(LOG_TAG, "[QUIZ HANDLER] New question: " + newQuestion.toString());
                mQuizQuestions.remove((int) mCurrentNumber);
                mQuizQuestions.add(mCurrentNumber, newQuestion);
                return newQuestion;
            }
        }
    }
}
