package au.edu.federation.itech3107.fedunimillionaire30360914.helpers;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import au.edu.federation.itech3107.fedunimillionaire30360914.models.Question;
import au.edu.federation.itech3107.fedunimillionaire30360914.models.Question.Difficulty;

import static au.edu.federation.itech3107.fedunimillionaire30360914.helpers.QuestionOpenHelper.EASY_QUESTIONS_FILENAME;
import static au.edu.federation.itech3107.fedunimillionaire30360914.helpers.QuestionOpenHelper.HARD_QUESTIONS_FILENAME;
import static au.edu.federation.itech3107.fedunimillionaire30360914.helpers.QuestionOpenHelper.MEDIUM_QUESTIONS_FILENAME;
import static au.edu.federation.itech3107.fedunimillionaire30360914.models.Question.Difficulty.easy;
import static au.edu.federation.itech3107.fedunimillionaire30360914.models.Question.Difficulty.hard;
import static au.edu.federation.itech3107.fedunimillionaire30360914.models.Question.Difficulty.medium;

public class QuestionBank implements QuestionAPIHelper.OnFetchedListener {

    public static final Map<Integer, int[]> QUESTION_VALUE_SAFE_MONEY_LIST = new HashMap<Integer, int[]>() {
        {
            put(0, new int[]{1000, 0});
            put(1, new int[]{2000, 1000});
            put(2, new int[]{4000, 1000});
            put(3, new int[]{8000, 1000});
            put(4, new int[]{16000, 1000});
            put(5, new int[]{32000, 1000});
            put(6, new int[]{64000, 32000});
            put(7, new int[]{125000, 32000});
            put(8, new int[]{250000, 32000});
            put(9, new int[]{500000, 32000});
            put(10, new int[]{1000000, 32000});
            put(11, new int[]{1000000, 1000000});
        }
    };

    private static final String LOG_TAG = QuestionBank.class.getSimpleName();

    private Map<Difficulty, List<Question>> mQuestionsMap = new HashMap<>();

    private QuestionOpenHelper mQuestionOpenHelper;
    private QuestionAPIHelper mQuestionAPIHelper;
    private OnReadyListener mListener;

    /**
     * reference: https://stackoverflow.com/a/35629470
     */
    public interface OnReadyListener {
        void onQuestionsReady(QuestionBank questionBank);
    }


    public QuestionBank(Context context) {
        this.mQuestionOpenHelper = new QuestionOpenHelper(context);
        loadQuestionsFromFiles();
    }

    public QuestionBank() { }


    public List<Question> getQuestions(Difficulty difficulty) {
        return mQuestionsMap.get(difficulty);
    }

    public void loadQuestionsFromFiles() {
        Log.d(LOG_TAG, "[QUESTION BANK] Loading question from files");
        mQuestionsMap.put(easy, mQuestionOpenHelper.readQuestionsFromFile(EASY_QUESTIONS_FILENAME));
        mQuestionsMap.put(medium, mQuestionOpenHelper.readQuestionsFromFile(MEDIUM_QUESTIONS_FILENAME));
        mQuestionsMap.put(hard, mQuestionOpenHelper.readQuestionsFromFile(HARD_QUESTIONS_FILENAME));
    }

    public void loadQuestionsFromInternet() {
        Log.d(LOG_TAG, "[QUESTION BANK] Loading question from internet");
        mQuestionAPIHelper.fetchQuestions(easy, 10);
        mQuestionAPIHelper.fetchQuestions(medium, 10);
        mQuestionAPIHelper.fetchQuestions(hard, 10);
    }

    public void loadQuestionsAsync(Context context, OnReadyListener listener) {
        this.mListener = listener;
        if (new CheckNetwork(context).isNetworkConnected()) {
            this.mQuestionAPIHelper = new QuestionAPIHelper(context, this);
            loadQuestionsFromInternet();
        } else {
            this.mQuestionOpenHelper = new QuestionOpenHelper(context);
            loadQuestionsFromFiles();
            listener.onQuestionsReady(this);
        }
    }

    public List<Question> getQuizQuestions() {
        List<Question> questionList = new ArrayList<>();

        final int easyNumber = 5;
        final int mediumNumber = 4;
        final int hardNumber = 2;

        for (int i : randomArrayInt(mQuestionsMap.get(easy).size(), easyNumber)) {
            questionList.add(mQuestionsMap.get(easy).get(i));
        }
        for (int i : randomArrayInt(mQuestionsMap.get(medium).size(), mediumNumber)) {
            questionList.add(mQuestionsMap.get(medium).get(i));
        }
        for (int i : randomArrayInt(mQuestionsMap.get(hard).size(), hardNumber)) {
            questionList.add(mQuestionsMap.get(hard).get(i));
        }

        return questionList;
    }

    public boolean addQuestions(Question question) {
        String fileName = "";
        switch (question.getDifficulty()) {
            case easy:
                fileName = EASY_QUESTIONS_FILENAME;
                break;
            case medium:
                fileName = MEDIUM_QUESTIONS_FILENAME;
                break;
            case hard:
                fileName = HARD_QUESTIONS_FILENAME;
                break;
        }

        if (mQuestionOpenHelper.addQuestionToFile(fileName, question)) {
            // New question successfully added, now update memory of question list
            loadQuestionsFromFiles();
            return true;
        }
        return false;
    }

    public boolean writeQuestions(List<Question> questionsList, Difficulty difficulty) {
        String fileName = "";
        switch (difficulty) {
            case easy:
                fileName = EASY_QUESTIONS_FILENAME;
                break;
            case medium:
                fileName = MEDIUM_QUESTIONS_FILENAME;
                break;
            case hard:
                fileName = HARD_QUESTIONS_FILENAME;
                break;
        }

        if (mQuestionOpenHelper.writeQuestionsToFile(fileName, questionsList)) {
            // Questions overridden successfully, now update memory of question list
            loadQuestionsFromFiles();
            return true;
        }
        return false;
    }

    private Integer[] randomArrayInt(int max, int size) {
        if (max < size) throw new IllegalArgumentException("Size must be greater than max.");

        List<Integer> list = new ArrayList<>();
        Random rand = new Random();

        do {
            int i = rand.nextInt(max);
            if (list.contains(i)) continue;
            list.add(i);
        } while (list.size() < size);

        Integer[] arr = new Integer[list.size()];
        return list.toArray(arr);
    }


    /**
     * Overridden methods
     */

    @Override
    public void onSuccess(Difficulty difficulty, List<Question> questionList) {
        mQuestionsMap.put(difficulty, questionList);
        if (mQuestionsMap.size() >= 3)
            mListener.onQuestionsReady(this);
    }

    @Override
    public void onError(VolleyError error) {
        Log.d(LOG_TAG, "Volley JSON post" + "That didn't work!");
    }
}
