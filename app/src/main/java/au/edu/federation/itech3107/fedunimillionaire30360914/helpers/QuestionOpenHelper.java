package au.edu.federation.itech3107.fedunimillionaire30360914.helpers;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import au.edu.federation.itech3107.fedunimillionaire30360914.models.Question;

public class QuestionOpenHelper {

    public static final String EASY_QUESTIONS_FILENAME = "questions-easy.txt";
    public static final String MEDIUM_QUESTIONS_FILENAME = "questions-medium.txt";
    public static final String HARD_QUESTIONS_FILENAME = "questions-hard.txt";

    private static final String LOG_TAG = QuestionOpenHelper.class.getSimpleName();

    private Context context;

    public QuestionOpenHelper(Context context) {
        this.context = context;
    }


    public List<Question> readQuestionsFromFile(String fileName) {
        List<Question> questionList = new ArrayList<>();
        try {
            JSONArray questionsArr = getQuestionAsJSONArray(fileName);
            Question question;

            // For every JSON object in the JSON array, create a Question object and add to questions List
            for (int i = 0; i < questionsArr.length(); i++) {
                question = new Question();
                JSONObject questionObj = questionsArr.getJSONObject(i);

                // Get a random int between 0-3, use this to reference the index of correct answer later
                int answerIndex = new Random().nextInt(4);

                // Convert the incorrect answers JSON array into List<String>
                JSONArray incorrectAnswer = questionObj.getJSONArray("incorrect_answers");
                List<String> choices = new ArrayList<>();
                for (int j = 0; j < incorrectAnswer.length(); j++) {
                    choices.add(incorrectAnswer.getString(j));
                }

                // Add the correct answer at a random position to the choices List
                choices.add(answerIndex, questionObj.getString("correct_answer"));

                // Populate the question properties, then add it to the questions List
                question.setTitle(questionObj.getString("question"));
                question.setChoices(choices);
                question.setAnswer(answerIndex);
                question.setDifficulty(Question.Difficulty.valueOf(questionObj.getString("difficulty")));

                questionList.add(question);
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return questionList;
    }
    private JSONArray getQuestionAsJSONArray(String fileName) throws JSONException, IOException {
        InputStream inputStream = null;
        try {
            // Try opening question file from internal storage
            inputStream = context.openFileInput(fileName);
        } catch (FileNotFoundException e) {
            // If file is not found from internal storage,
            // then load it from assets folder
            Log.d(LOG_TAG, "[INTERNAL FILE NOT FOUND] Loading " + fileName + " from assets folder");
            inputStream = context.getAssets().open(fileName);
        }

        String fullText = "";
        String line = null;

        // Use BufferedReader to read text
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        // Read each line and append it to the fullText
        while ((line = bufferedReader.readLine()) != null) {
            fullText += line;
        }
        bufferedReader.close();

        // Parse the fullText into a JSON object, then get the questions JSON array
        JSONObject json = new JSONObject(fullText);
        return json.getJSONArray("questions");
    }

}
