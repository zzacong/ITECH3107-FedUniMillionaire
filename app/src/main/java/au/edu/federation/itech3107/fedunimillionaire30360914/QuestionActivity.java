package au.edu.federation.itech3107.fedunimillionaire30360914;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import au.edu.federation.itech3107.fedunimillionaire30360914.controllers.QuestionListAdapter;
import au.edu.federation.itech3107.fedunimillionaire30360914.helpers.QuestionBank;
import au.edu.federation.itech3107.fedunimillionaire30360914.models.Question;
import au.edu.federation.itech3107.fedunimillionaire30360914.models.Question.Difficulty;

public class QuestionActivity extends AppCompatActivity {

    private static final String LOG_TAG = QuestionActivity.class.getSimpleName();

    private View clNewQuestionForm, clQuestionList;
    private RecyclerView rvQuestionList;
    private TextView tvQuestionFormError;
    private Button btnNewQuestion;

    QuestionBank questionBank;
    QuestionListAdapter questionListAdapter;

    private boolean showForm = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        clNewQuestionForm = findViewById(R.id.clNewQuestionForm);
        clQuestionList = findViewById(R.id.clQuestionList);
        tvQuestionFormError = findViewById(R.id.tvQuestionFormError);
        rvQuestionList = findViewById(R.id.rvQuestionList);
        btnNewQuestion = findViewById(R.id.btnNewQuestion);

        questionBank = new QuestionBank(this);
        questionListAdapter = new QuestionListAdapter(questionBank.getEasyQuestions());

        rvQuestionList.setLayoutManager(new LinearLayoutManager(this));
        rvQuestionList.setAdapter(questionListAdapter);
    }

    public void showForm(View view) {
        showForm = !showForm;
        btnNewQuestion.setText(showForm ? R.string.hide : R.string.new_);
        clNewQuestionForm.setVisibility(showForm ? View.VISIBLE : View.GONE);
        clQuestionList.setVisibility(showForm ? View.GONE : View.VISIBLE);
    }

    public void showQuestions(View view) {
        Difficulty difficulty = null;
        switch (view.getId()) {
            case R.id.btnShowEasy:
                difficulty = Difficulty.easy;
                questionListAdapter.refresh(questionBank.getEasyQuestions());
                break;
            case R.id.btnShowMedium:
                difficulty = Difficulty.medium;
                questionListAdapter.refresh(questionBank.getMediumQuestions());
                break;
            case R.id.btnShowHard:
                difficulty = Difficulty.hard;
                questionListAdapter.refresh(questionBank.getHardQuestions());
                break;
        }
        Log.d(LOG_TAG, "[SHOW QUESTIONS] " + difficulty);
    }
}