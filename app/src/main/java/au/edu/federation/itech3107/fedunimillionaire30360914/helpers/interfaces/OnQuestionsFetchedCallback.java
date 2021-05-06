package au.edu.federation.itech3107.fedunimillionaire30360914.helpers.interfaces;

import com.android.volley.VolleyError;

import java.util.List;

import au.edu.federation.itech3107.fedunimillionaire30360914.models.Question;

public interface OnQuestionsFetchedCallback {
    void onSuccess(Question.Difficulty difficulty, List<Question> questionList);
    void onError(VolleyError error);
}
