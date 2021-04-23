package au.edu.federation.itech3107.fedunimillionaire30360914.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import au.edu.federation.itech3107.fedunimillionaire30360914.R;
import au.edu.federation.itech3107.fedunimillionaire30360914.controllers.QuestionListAdapter;
import au.edu.federation.itech3107.fedunimillionaire30360914.helpers.QuestionBank;
import au.edu.federation.itech3107.fedunimillionaire30360914.models.Question;
import au.edu.federation.itech3107.fedunimillionaire30360914.models.Question.Difficulty;

import static au.edu.federation.itech3107.fedunimillionaire30360914.models.Question.Difficulty.easy;
import static au.edu.federation.itech3107.fedunimillionaire30360914.models.Question.Difficulty.hard;
import static au.edu.federation.itech3107.fedunimillionaire30360914.models.Question.Difficulty.medium;
import static au.edu.federation.itech3107.fedunimillionaire30360914.utils.MyString.capitalise;

public class QuestionActivity extends AppCompatActivity {

    private static final String LOG_TAG = QuestionActivity.class.getSimpleName();

    private View clNewQuestionForm, clQuestionList;
    private RecyclerView rvQuestionList;
    private TextView tvQuestionFormError;
    private EditText etQuestionTitle, etCorrectAnswer, etWrongAnswer1, etWrongAnswer2, etWrongAnswer3;
    private Spinner spDifficulty;
    private Button btnNewQuestion;

    QuestionBank questionBank;
    QuestionListAdapter questionListAdapter;
    Difficulty difficulty = easy;

    private boolean showForm = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        clNewQuestionForm = findViewById(R.id.clNewQuestionForm);
        clQuestionList = findViewById(R.id.clQuestionList);
        rvQuestionList = findViewById(R.id.rvQuestionList);
        tvQuestionFormError = findViewById(R.id.tvQuestionFormError);
        etQuestionTitle = findViewById(R.id.etQuestionTitle);
        etCorrectAnswer = findViewById(R.id.etCorrectAnswer);
        etWrongAnswer1 = findViewById(R.id.etWrongAnswer1);
        etWrongAnswer2 = findViewById(R.id.etWrongAnswer2);
        etWrongAnswer3 = findViewById(R.id.etWrongAnswer3);
        btnNewQuestion = findViewById(R.id.btnNewQuestion);
        spDifficulty = findViewById(R.id.spDifficulty);

        questionBank = new QuestionBank(this);
        questionListAdapter = new QuestionListAdapter(questionBank.getQuestions(easy));

        rvQuestionList.setLayoutManager(new LinearLayoutManager(this));
        rvQuestionList.setAdapter(questionListAdapter);

        btnNewQuestion.setOnClickListener(v -> {
            triggerView();
        });
    }

    public void triggerView() {
        showForm = !showForm;
        btnNewQuestion.setText(showForm ? R.string.hide : R.string.new_);
        clNewQuestionForm.setVisibility(showForm ? View.VISIBLE : View.GONE);
        clQuestionList.setVisibility(showForm ? View.GONE : View.VISIBLE);
    }

    public void showQuestions(View view) {
        switch (view.getId()) {
            case R.id.btnShowEasy:
                difficulty = Difficulty.easy;
                questionListAdapter.refresh(questionBank.getQuestions(easy));
                break;
            case R.id.btnShowMedium:
                difficulty = Difficulty.medium;
                questionListAdapter.refresh(questionBank.getQuestions(medium));
                break;
            case R.id.btnShowHard:
                difficulty = Difficulty.hard;
                questionListAdapter.refresh(questionBank.getQuestions(hard));
                break;
        }
        Log.d(LOG_TAG, "[SHOW QUESTIONS] " + difficulty);
    }

    public void addQuestion(View view) {
        if (validateForm()) {
            // Form passes validation, now extract values from form fields
            // Instantiate a Question object to store the new question
            Question newQuestion = new Question();

            // Determine the type (difficulty) of new question
            Difficulty difficulty = Difficulty.valueOf(spDifficulty.getSelectedItem().toString());
            newQuestion.setTitle(etQuestionTitle.getText().toString());
            newQuestion.setDifficulty(difficulty);
            newQuestion.setAnswer(0);
            newQuestion.setChoices(etCorrectAnswer.getText().toString(), etWrongAnswer1.getText().toString(), etWrongAnswer2.getText().toString(), etWrongAnswer3.getText().toString());

            if (questionBank.addQuestions(newQuestion)) {
                // New question successfully added to file, show a success message
                Toast.makeText(this, "New question successfully added!", Toast.LENGTH_SHORT).show();

                // Reset the form
                etQuestionTitle.setText("");
                etCorrectAnswer.setText("");
                etWrongAnswer1.setText("");
                etWrongAnswer2.setText("");
                etWrongAnswer3.setText("");
                spDifficulty.setSelection(0);

                // Hide the form and show questions
                triggerView();

                if (this.difficulty == difficulty)
                    // Update the RecyclerView only if we are viewing the same question type (difficulty)
                    questionListAdapter.addItem(newQuestion);
            } else {
                // Failed to add question to file, show a fail message
                Toast.makeText(this, "Fail to add new question.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean validateForm() {
        return isValidTextField(etQuestionTitle) && isValidTextField(etCorrectAnswer) && isValidTextField(etWrongAnswer1) && isValidTextField(etWrongAnswer2) && isValidTextField(etWrongAnswer3);
    }

    public boolean isValidTextField(EditText editText) {
        String string = editText.getText().toString();
        if (string == null || string.isEmpty()) {
            Log.d(LOG_TAG, "[INVALID] " + editText.getHint() + " is empty");
            String errorMessage = capitalise(String.valueOf(editText.getHint())) + " is empty";
            tvQuestionFormError.setText(errorMessage);
            return false;
        }
        tvQuestionFormError.setText("");
        return true;
    }
}
