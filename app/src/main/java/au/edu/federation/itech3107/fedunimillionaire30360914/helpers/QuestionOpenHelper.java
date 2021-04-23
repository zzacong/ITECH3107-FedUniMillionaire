package au.edu.federation.itech3107.fedunimillionaire30360914.helpers;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
                question.setDifficulty(Question.Difficulty.valueOf(questionObj.getString("difficulty")));

                questionList.add(question);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return questionList;
    }
}
