package au.edu.federation.itech3107.fedunimillionaire30360914;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity {

    private static final String LOG_TAG = GameActivity.class.getSimpleName();

    TextView tvQuestionsLeft;
    TextView tvQuestionNumber;
    TextView tvQuestionTitle;
    RadioGroup radGroup;
    RadioButton radA;
    RadioButton radB;
    RadioButton radC;
    RadioButton radD;
    Button btnSubmit;

    QuestionAdapter questionAdapter;
    Question question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        tvQuestionsLeft = findViewById(R.id.tvQuestionsLeft);
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);
        tvQuestionTitle = findViewById(R.id.tvQuestionTitle);
        radGroup = findViewById(R.id.radGroup);
        radA = findViewById(R.id.radA);
        radB = findViewById(R.id.radB);
        radC = findViewById(R.id.radC);
        radD = findViewById(R.id.radD);
        btnSubmit = findViewById(R.id.btnSubmit);

        questionAdapter = new QuestionAdapter();
        question = questionAdapter.getCurrQuestion();

        tvQuestionNumber.setText(questionAdapter.getQuestionNumber().toString());
        tvQuestionsLeft.setText(questionAdapter.getQuestionsLeft().toString());
        tvQuestionTitle.setText(question.getTitle());
        radA.setText(question.getChoices().get(0));
        radB.setText(question.getChoices().get(1));
        radC.setText(question.getChoices().get(2));
        radD.setText(question.getChoices().get(3));
    }


    public void handleSubmit(View view) {
        int selectedRadId = radGroup.getCheckedRadioButtonId();
//        Log.d(LOG_TAG, "Selected: " + selectedRadId);

        // Check if player has selected an answer
        if (selectedRadId != -1) {
            int selectedIndex = radGroup.indexOfChild(findViewById(selectedRadId));
//            Log.d(LOG_TAG, "Index: " + selectedIndex);
            if (question.attempt(selectedIndex)) {
                Log.d(LOG_TAG, "[CORRECT]");
                nextQuestion();
            } else {
                Log.d(LOG_TAG, "[WRONG]");
            }
        } else {
            Log.d(LOG_TAG, "[INVALID] No answer selected");
        }
    }

    public void nextQuestion() {
        question = questionAdapter.nextQuestion();
        if (question != null) {
            radGroup.clearCheck();
            tvQuestionTitle.setText(question.getTitle());
            radA.setText(question.getChoices().get(0));
            radB.setText(question.getChoices().get(1));
            radC.setText(question.getChoices().get(2));
            radD.setText(question.getChoices().get(3));

            tvQuestionNumber.setText(questionAdapter.getQuestionNumber().toString());
            tvQuestionsLeft.setText(questionAdapter.getQuestionsLeft().toString());
        } else {
            Log.d(LOG_TAG, "[DONE] No more questions");
            btnSubmit.setEnabled(false);
        }
    }
}