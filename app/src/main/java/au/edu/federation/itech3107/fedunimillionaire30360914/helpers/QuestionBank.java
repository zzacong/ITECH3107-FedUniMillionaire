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
    private static final String EASY_QUESTIONS_FILENAME = "questions-easy.txt";
    private static final String MEDIUM_QUESTIONS_FILENAME = "questions-medium.txt";
    private static final String HARD_QUESTIONS_FILENAME = "questions-hard.txt";

    private Context context;
    private ArrayList<Question> easyQuestions;
    private ArrayList<Question> mediumQuestions;
    private ArrayList<Question> hardQuestions;


    public QuestionBank(Context context) {
        this.context = context;
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
        return readQuestionsFromTextFile(EASY_QUESTIONS_FILENAME);
    }

    public ArrayList<Question> getMediumQuestions() {
        return readQuestionsFromTextFile(MEDIUM_QUESTIONS_FILENAME);
    }

    public ArrayList<Question> getHardQuestions() {
        return readQuestionsFromTextFile(HARD_QUESTIONS_FILENAME);
    }

    public ArrayList<Question> readQuestionsFromTextFile(String fileName) {

        ArrayList<Question> questionList = new ArrayList<>();
        String fullText = "";
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.getAssets().open(fileName)))) {
            String line = null;
            // Read each line and append it to the fullText
            while ((line = bufferedReader.readLine()) != null) {
                fullText += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            // Parse the fullText into a JSON object, then get the questions JSON array
            JSONObject json = new JSONObject(fullText);
            JSONArray questionsArr = json.getJSONArray("questions");
            Question question;

            // For every JSON object in the JSON array, create a Question object and add to questions ArrayList
            for (int i = 0; i < questionsArr.length(); i++) {
                question = new Question();
                JSONObject questionObj = questionsArr.getJSONObject(i);

                // Get a random int between 0-3, use this to reference the index of correct answer later
                int answerIndex = new Random().nextInt(4);

                // Convert the incorrect answers JSON array into ArrayList<String>
                JSONArray incorrectAnswer = questionObj.getJSONArray("incorrect_answers");
                ArrayList<String> choices = new ArrayList<>();
                for (int j = 0; j < incorrectAnswer.length(); j++) {
                    choices.add(incorrectAnswer.getString(j));
                }

                // Add the correct answer at a random position to the choices ArrayList
                choices.add(answerIndex, questionObj.getString("correct_answer"));

                // Populate the question properties, then add it to the questions ArrayList
                question.setTitle(questionObj.getString("question"));
                question.setChoices(choices);
                question.setAnswer(answerIndex);
                question.setDifficulty(Difficulty.valueOf(questionObj.getString("difficulty")));

                questionList.add(question);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return questionList;
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
