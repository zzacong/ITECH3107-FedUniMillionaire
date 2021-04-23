package au.edu.federation.itech3107.fedunimillionaire30360914.helpers;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import au.edu.federation.itech3107.fedunimillionaire30360914.models.Question;
import au.edu.federation.itech3107.fedunimillionaire30360914.models.Question.Difficulty;

import static au.edu.federation.itech3107.fedunimillionaire30360914.helpers.QuestionOpenHelper.EASY_QUESTIONS_FILENAME;
import static au.edu.federation.itech3107.fedunimillionaire30360914.helpers.QuestionOpenHelper.HARD_QUESTIONS_FILENAME;
import static au.edu.federation.itech3107.fedunimillionaire30360914.helpers.QuestionOpenHelper.MEDIUM_QUESTIONS_FILENAME;

public class QuestionBank {

    public static final Map<Integer, int[]> QUESTION_VALUE_SAFE_MONEY_LIST = new HashMap<Integer, int[]>() {
        {
            put(0, new int[]{0, 0});
            put(1, new int[]{1000, 1000});
            put(2, new int[]{2000, 1000});
            put(3, new int[]{4000, 1000});
            put(4, new int[]{8000, 1000});
            put(5, new int[]{16000, 1000});
            put(6, new int[]{32000, 32000});
            put(7, new int[]{64000, 32000});
            put(8, new int[]{125000, 32000});
            put(9, new int[]{250000, 32000});
            put(10, new int[]{500000, 32000});
            put(11, new int[]{1000000, 1000000});
        }
    };

    private static final String LOG_TAG = QuestionBank.class.getSimpleName();

    private QuestionOpenHelper questionOpenHelper;
    private ArrayList<Question> easyQuestions;
    private ArrayList<Question> mediumQuestions;
    private ArrayList<Question> hardQuestions;


    public QuestionBank(Context context) {
        this.questionOpenHelper = new QuestionOpenHelper(context);
        this.easyQuestions = getEasyQuestions();
        this.mediumQuestions = getMediumQuestions();
        this.hardQuestions = getHardQuestions();
    }

    public ArrayList<Question> getQuestions() {
        ArrayList<Question> questionList = new ArrayList<>();

        int easyNumber = 5;
        int mediumNumber = 4;
        int hardNumber = 2;

        for (int i : randomArrayInt(easyQuestions.size(), easyNumber)) {
            questionList.add(easyQuestions.get(i));
        }

        for (int i : randomArrayInt(mediumQuestions.size(), mediumNumber)) {
            questionList.add(mediumQuestions.get(i));
        }

        for (int i : randomArrayInt(hardQuestions.size(), hardNumber)) {
            questionList.add(hardQuestions.get(i));
        }

        return questionList;
    }

    public ArrayList<Question> getEasyQuestions() {
        return questionOpenHelper.readQuestionsFromTextFile(EASY_QUESTIONS_FILENAME);
    }

    public ArrayList<Question> getMediumQuestions() {
        return questionOpenHelper.readQuestionsFromTextFile(MEDIUM_QUESTIONS_FILENAME);
    }

    public ArrayList<Question> getHardQuestions() {
        return questionOpenHelper.readQuestionsFromTextFile(HARD_QUESTIONS_FILENAME);
    }

    private Integer[] randomArrayInt(int max, int size) {
        if (max < size) throw new IllegalArgumentException("Size must be greater than max.");

        ArrayList<Integer> list = new ArrayList<>();
        Random rand = new Random();

        do {
            int i = rand.nextInt(max);
            if (list.contains(i)) continue;
            list.add(i);
        } while (list.size() < size);

        Integer[] arr = new Integer[list.size()];
        return list.toArray(arr);
    }
}
