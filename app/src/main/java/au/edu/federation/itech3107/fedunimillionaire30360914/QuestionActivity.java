package au.edu.federation.itech3107.fedunimillionaire30360914;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import au.edu.federation.itech3107.fedunimillionaire30360914.models.Question.Difficulty;

public class QuestionActivity extends AppCompatActivity {

    private static final String LOG_TAG = QuestionActivity.class.getSimpleName();

    private View clNewQuestionForm;
    private TextView tvQuestionFormError;

    private boolean showForm = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        clNewQuestionForm = findViewById(R.id.clNewQuestionForm);
        tvQuestionFormError = findViewById(R.id.tvQuestionFormError);
    }

    public void showForm(View view) {
        showForm = !showForm;
        clNewQuestionForm.setVisibility(showForm ? View.VISIBLE : View.GONE);
    }

    public void showQuestions(View view) {
        Difficulty difficulty = null;
        switch (view.getId()) {
            case R.id.btnShowEasy:
                difficulty = Difficulty.easy;
                break;
            case R.id.btnShowMedium:
                difficulty = Difficulty.medium;
                break;
            case R.id.btnShowHard:
                difficulty = Difficulty.hard;
                break;
        }
        Log.d(LOG_TAG, "[SHOW QUESTIONS] " + difficulty);
    }
}