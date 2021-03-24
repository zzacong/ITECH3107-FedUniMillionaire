package au.edu.federation.itech3107.fedunimillionaire30360914;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity {

    private static final String LOG_TAG = GameActivity.class.getSimpleName();
    public static final String EXTRA_RESULT = "au.edu.federation.itech3107.fedunimillionaire.extra.RESULT";
    public static final String EXTRA_DOLLAR = "au.edu.federation.itech3107.fedunimillionaire.extra.DOLLAR";
    public static final String OUTSTATE_QUESTION_NO = "au.edu.federation.itech3107.fedunimillionaire.outstate.question_no";

    TextView tvDollarValue, tvSafeMoney, tvQuestionsLeft, tvQuestionNumber, tvQuestionTitle;
    RadioGroup radGroup;
    RadioButton radA, radB, radC, radD;
    Button btnSubmit;

    QuestionAdapter questionAdapter;
    Question question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Log.d(LOG_TAG, "[ON_CREATE]");

        // Link variables to screen components
        tvDollarValue = findViewById(R.id.tvDollarValue);
        tvSafeMoney = findViewById(R.id.tvSafeMoney);
        tvQuestionsLeft = findViewById(R.id.tvQuestionsLeft);
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);
        tvQuestionTitle = findViewById(R.id.tvQuestionTitle);
        radGroup = findViewById(R.id.radGroup);
        radA = findViewById(R.id.radA);
        radB = findViewById(R.id.radB);
        radC = findViewById(R.id.radC);
        radD = findViewById(R.id.radD);
        btnSubmit = findViewById(R.id.btnSubmit);

        // Instantiate a new QuestionAdapter to manage the quiz questions
        questionAdapter = new QuestionAdapter();
        this.question = questionAdapter.startFrom(0);
        nextQuestion();
    }

    // Called when users press submit button
    public void handleSubmit(View view) {
        int selectedRadId = radGroup.getCheckedRadioButtonId();
        // Check if player has selected an answer
        if (selectedRadId != -1) {
            int selectedIndex = radGroup.indexOfChild(findViewById(selectedRadId));
            if (question.attempt(selectedIndex)) {
                Log.d(LOG_TAG, "[CORRECT]");
                this.question = questionAdapter.nextQuestion();
                nextQuestion();
            } else {
                Log.d(LOG_TAG, "[WRONG] Current question: " + questionAdapter.getCurrentNumber());
                endGame(false);
            }
        } else {
            Log.d(LOG_TAG, "[INVALID] No answer selected");
        }
    }

    // Display current question dollar value, safe money amount and show the questions and choices
    private void nextQuestion() {
        if (question != null) {
            radGroup.clearCheck();
            tvQuestionTitle.setText(question.getTitle());
            radA.setText(question.getChoices().get(0));
            radB.setText(question.getChoices().get(1));
            radC.setText(question.getChoices().get(2));
            radD.setText(question.getChoices().get(3));

            Integer currentNumber = questionAdapter.getCurrentNumber();
            tvDollarValue.setText(questionAdapter.getQuestionValue().toString());
            tvSafeMoney.setText(questionAdapter.getSafeMoneyValue().toString());
            tvQuestionNumber.setText(currentNumber.toString());
            tvQuestionsLeft.setText(questionAdapter.getQuestionsLeft().toString());
        } else {
            // If there's no more questions, disable the submit button and ends the game
            Log.d(LOG_TAG, "[DONE] No more questions");
            btnSubmit.setEnabled(false);
            endGame(true);
        }
    }

    // Open the EndgameActivity with a boolean as the win/lose result
    private void endGame(boolean result) {
        Log.d(LOG_TAG, "[END QUIZ]");
        Intent intent = new Intent(this, EndgameActivity.class);
        intent.putExtra(EXTRA_RESULT, result);
        intent.putExtra(EXTRA_DOLLAR, questionAdapter.getSafeMoneyValue().toString());
        startActivity(intent);
        finish();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (questionAdapter.getCurrentNumber() > 1) {
            Log.d(LOG_TAG, "[SAVING STATE] Current question: " + questionAdapter.getCurrentNumber());
            outState.putInt(OUTSTATE_QUESTION_NO, questionAdapter.getCurrentNumber());
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int number = savedInstanceState.getInt(OUTSTATE_QUESTION_NO);
        Log.d(LOG_TAG, "[RESTORING STATE] Current question: " + number);
        this.question = questionAdapter.startFrom(number);
        nextQuestion();
    }

}