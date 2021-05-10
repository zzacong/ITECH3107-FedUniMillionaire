package au.edu.federation.itech3107.fedunimillionaire30360914.helpers;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import au.edu.federation.itech3107.fedunimillionaire30360914.models.Question;
import au.edu.federation.itech3107.fedunimillionaire30360914.models.Question.Difficulty;

import static au.edu.federation.itech3107.fedunimillionaire30360914.utils.MyString.unescape;

public class QuestionAPIHelper {

    private static final String LOG_TAG = QuestionAPIHelper.class.getSimpleName();
    private static final String BASE_URL = "https://opentdb.com/api.php";
    private static final String QUERY_PARAMS = "?category=9&type=multiple"; // category=General Knowledge, type=multiple
    private static final String VOLLEY_REQUEST_TAG = "volley.request.tag";

    private OnFetchedListener mListener;
    private RequestQueue mQueue;

    public interface OnFetchedListener {
        void onSuccess(Question.Difficulty difficulty, List<Question> questionList);

        void onError(VolleyError error);
    }


    public QuestionAPIHelper(Context context, OnFetchedListener listener) {
        this.mListener = listener;
        this.mQueue = Volley.newRequestQueue(context);
    }


    public void fetchQuestions(Difficulty difficulty, int amount) {
        String url = buildUrl(difficulty, amount);
        Log.d(LOG_TAG, "[VOLLEY] Send request to " + url);
        // Request a string response from the provided URL
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    mListener.onSuccess(difficulty, toQuestionList(response));
                },
                error -> {
                    // Cancel all pending request
                    mQueue.cancelAll(VOLLEY_REQUEST_TAG);
                    mListener.onError(error);
                });
        jsonObjectRequest.setTag(VOLLEY_REQUEST_TAG);
        // Add the request to the RequestQueue.
        mQueue.add(jsonObjectRequest);
    }


    private String buildUrl(Difficulty difficulty, int amount) {
        StringBuilder sb = new StringBuilder(BASE_URL).append(QUERY_PARAMS);
        return sb.append("&difficulty=" + difficulty).append("&amount=" + amount).toString();
    }

    private List<Question> toQuestionList(JSONObject input) {
        List<Question> questionList = new ArrayList<>();
        try {
            JSONArray questionsArr = input.getJSONArray("results");

            Question question;
            for (int i = 0; i < questionsArr.length(); i++) {
                question = new Question();
                JSONObject questionJson = questionsArr.getJSONObject(i);

                // Get a random int between 0-3, use this to reference the index of correct answer later
                int answerIndex = new Random().nextInt(4);

                // Convert the incorrect answers JSON array into List<String>
                JSONArray incorrectAnswer = questionJson.getJSONArray("incorrect_answers");
                List<String> choices = new ArrayList<>();
                for (int j = 0; j < incorrectAnswer.length(); j++) {
                    choices.add(unescape(incorrectAnswer.getString(j)));
                }

                // Add the correct answer at a random position to the choices List
                choices.add(answerIndex, unescape(questionJson.getString("correct_answer")));

                // Populate the question properties, then add it to the questions List
                question.setTitle(unescape(questionJson.getString("question")));
                question.setChoices(choices);
                question.setAnswer(answerIndex);
                question.setDifficulty(Difficulty.valueOf(questionJson.getString("difficulty")));

                // Finally add the question to questionList
                questionList.add(question);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return questionList;
    }

}
