package au.edu.federation.itech3107.fedunimillionaire30360914;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import au.edu.federation.itech3107.fedunimillionaire30360914.Question.Difficulty;

public class QuestionBank {

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

    private static final String TAG = QuestionBank.class.getSimpleName();
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

    public int getQuestionValue(int key) {
        return QUESTION_VALUE_LIST.get(key);
    }

    public int getSafeMoneyValue(int index) {
        return SAFE_MONEY_LIST.get(index);
    }

    public ArrayList<Question> getQuestions() {
        return null;
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
}
