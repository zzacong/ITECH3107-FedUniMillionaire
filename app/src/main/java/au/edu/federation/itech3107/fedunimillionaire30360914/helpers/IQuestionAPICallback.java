package au.edu.federation.itech3107.fedunimillionaire30360914.helpers;

import com.android.volley.VolleyError;

import java.util.List;

import au.edu.federation.itech3107.fedunimillionaire30360914.models.Question;

public interface IQuestionAPICallback {
    public void notifySuccess(Question.Difficulty difficulty, List<Question> questionList);
    public void notifyError(VolleyError error);
}
