package au.edu.federation.itech3107.fedunimillionaire30360914.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import au.edu.federation.itech3107.fedunimillionaire30360914.R;
import au.edu.federation.itech3107.fedunimillionaire30360914.controllers.QuestionListAdapter;
import au.edu.federation.itech3107.fedunimillionaire30360914.helpers.QuestionBank;
import au.edu.federation.itech3107.fedunimillionaire30360914.helpers.ShakeDetector;
import au.edu.federation.itech3107.fedunimillionaire30360914.models.Question;
import au.edu.federation.itech3107.fedunimillionaire30360914.models.Question.Difficulty;

import static au.edu.federation.itech3107.fedunimillionaire30360914.models.Question.Difficulty.easy;
import static au.edu.federation.itech3107.fedunimillionaire30360914.models.Question.Difficulty.hard;
import static au.edu.federation.itech3107.fedunimillionaire30360914.models.Question.Difficulty.medium;
import static au.edu.federation.itech3107.fedunimillionaire30360914.utils.MyString.capitalise;

/**
 * reference: https://developer.android.com/guide/topics/sensors/sensors_overview#sensors-monitor
 * <p>
 * Sensor Implementation (Accelerometer sensor):
 * Shake the device while using the add new question form,
 * this will trigger a AlertDialog to confirm if you want to clear the form inputs
 */
public class QuestionActivity extends AppCompatActivity implements ShakeDetector.OnShakeListener {

    private static final String LOG_TAG = QuestionActivity.class.getSimpleName();

    private View mClNewQuestionForm, mClQuestionList;
    private TextView mTvQuestionFormError;
    private EditText mEtQuestionTitle, mEtCorrectAnswer, mEtWrongAnswer1,
            mEtWrongAnswer2, mEtWrongAnswer3;
    private Spinner mSpDifficulty;
    private Button mBtnNewQuestion, mBtnShowEasy, mBtnShowMedium, mBtnShowHard;

    private QuestionBank mQuestionBank;
    private QuestionListAdapter mQuestionListAdapter;
    private Difficulty difficulty = easy;

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private ShakeDetector mShakeDetector;

    private boolean mShowForm = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        RecyclerView rvQuestionList = findViewById(R.id.rvQuestionList);
        mClNewQuestionForm = findViewById(R.id.clNewQuestionForm);
        mClQuestionList = findViewById(R.id.clQuestionList);
        mTvQuestionFormError = findViewById(R.id.tvQuestionFormError);
        mEtQuestionTitle = findViewById(R.id.etQuestionTitle);
        mEtCorrectAnswer = findViewById(R.id.etCorrectAnswer);
        mEtWrongAnswer1 = findViewById(R.id.etWrongAnswer1);
        mEtWrongAnswer2 = findViewById(R.id.etWrongAnswer2);
        mEtWrongAnswer3 = findViewById(R.id.etWrongAnswer3);
        mBtnNewQuestion = findViewById(R.id.btnNewQuestion);
        mBtnShowEasy = findViewById(R.id.btnShowEasy);
        mBtnShowMedium = findViewById(R.id.btnShowMedium);
        mBtnShowHard = findViewById(R.id.btnShowHard);
        mSpDifficulty = findViewById(R.id.spDifficulty);

        mQuestionBank = new QuestionBank(this);
        mQuestionBank.loadQuestionsFromFiles();
        mQuestionListAdapter = new QuestionListAdapter(mQuestionBank.getQuestions(easy));

        rvQuestionList.setLayoutManager(new LinearLayoutManager(this));
        rvQuestionList.setAdapter(mQuestionListAdapter);

        mBtnNewQuestion.setOnClickListener(v -> {
            triggerView();
        });

        // Sensors Implementation
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // Use the accelerometer.
        if ((mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)) != null) {
            Log.d(LOG_TAG, "[SENSOR] Sensor found!");
            Toast.makeText(this, "Sensor found!", Toast.LENGTH_SHORT).show();
            mShakeDetector = new ShakeDetector(this);
        } else {
            // Sorry, there are no accelerometers on your device.
            Log.d(LOG_TAG, "[SENSOR] No sensor found!");
            Toast.makeText(this, "No sensor found!", Toast.LENGTH_SHORT).show();
        }
    }


    //region ---------- Listing questions ----------
    public void triggerView() {
        mShowForm = !mShowForm;
        mBtnNewQuestion.setText(mShowForm ? R.string.hide : R.string.new_);
        mClNewQuestionForm.setVisibility(mShowForm ? View.VISIBLE : View.GONE);
        mClQuestionList.setVisibility(mShowForm ? View.GONE : View.VISIBLE);

        // Register sensor when the form is opened,
        // unregister when form is closed
        if (mShowForm) registerSensor();
        else unregisterSensor();
    }

    public void showQuestions(View view) {
        mBtnShowEasy.setEnabled(true);
        mBtnShowMedium.setEnabled(true);
        mBtnShowHard.setEnabled(true);

        view.setEnabled(false);
        switch (view.getId()) {
            case R.id.btnShowEasy:
                difficulty = Difficulty.easy;
                mQuestionListAdapter.refresh(mQuestionBank.getQuestions(easy));
                break;
            case R.id.btnShowMedium:
                difficulty = Difficulty.medium;
                mQuestionListAdapter.refresh(mQuestionBank.getQuestions(medium));
                break;
            case R.id.btnShowHard:
                difficulty = Difficulty.hard;
                mQuestionListAdapter.refresh(mQuestionBank.getQuestions(hard));
                break;
        }
        Log.d(LOG_TAG, "[SHOW QUESTIONS] " + difficulty);
    }
    //endregion

    //region ---------- New question ----------
    public void addQuestion(View view) {
        if (validateForm()) {
            // Form passes validation, now extract values from form fields
            // Instantiate a Question object to store the new question
            Question newQuestion = new Question();

            // Determine the type (difficulty) of new question
            Difficulty difficulty = Difficulty.valueOf(mSpDifficulty.getSelectedItem().toString());
            newQuestion.setTitle(mEtQuestionTitle.getText().toString());
            newQuestion.setDifficulty(difficulty);
            newQuestion.setAnswer(0);
            newQuestion.setChoices(mEtCorrectAnswer.getText().toString(), mEtWrongAnswer1.getText().toString(), mEtWrongAnswer2.getText().toString(), mEtWrongAnswer3.getText().toString());

            if (mQuestionBank.addQuestions(newQuestion)) {
                // New question successfully added to file, show a success message
                Toast.makeText(this, "New question successfully added!", Toast.LENGTH_SHORT).show();

                // Reset the form after submission
                clearForm();

                // Hide the form and show questions
                triggerView();

                // Update the RecyclerView only if we are viewing the same question type (difficulty)
                if (this.difficulty == difficulty) mQuestionListAdapter.addItem(newQuestion);
            } else {
                // Failed to add question to file, show a fail message
                Toast.makeText(this, "Fail to add new question.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean validateForm() {
        return isValidTextField(mEtQuestionTitle) && isValidTextField(mEtCorrectAnswer) && isValidTextField(mEtWrongAnswer1) && isValidTextField(mEtWrongAnswer2) && isValidTextField(mEtWrongAnswer3);
    }

    public boolean isValidTextField(EditText editText) {
        String string = editText.getText().toString();
        if (string == null || string.isEmpty()) {
            Log.d(LOG_TAG, "[INVALID] " + editText.getHint() + " is empty");
            String errorMessage = capitalise(String.valueOf(editText.getHint())) + " is empty";
            mTvQuestionFormError.setText(errorMessage);
            return false;
        }
        mTvQuestionFormError.setText("");
        return true;
    }

    public void clearForm() {
        mEtQuestionTitle.setText("");
        mEtCorrectAnswer.setText("");
        mEtWrongAnswer1.setText("");
        mEtWrongAnswer2.setText("");
        mEtWrongAnswer3.setText("");
        mSpDifficulty.setSelection(0);
    }
    //endregion

    //region ---------- Delete questions ----------
    // Delete button is pressed
    public void onDeleteQuestions(View view) {
        List<Question> questionList = mQuestionListAdapter.getDataSet();
        List<Question> questionToDelete = new ArrayList<>();

        // For every question, check if the checkbox is selected
        for (Question q : questionList) {
            if (q.isChecked()) questionToDelete.add(q);
        }
        if (!questionToDelete.isEmpty()) {
            // There are questions to delete
            // Remove all selected questions
            questionList.removeAll(questionToDelete);

            // Override the filtered question list back its file
            if (mQuestionBank.writeQuestions(questionList, difficulty)) {
                mQuestionListAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Questions successfully deleted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Questions delete failed.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // No questions to delete
            Toast.makeText(this, "No questions selected.", Toast.LENGTH_SHORT).show();
        }
    }
    //endregion

    //region ---------- Sensors ----------
    @Override
    public void onShake() {
        Toast.makeText(this, "Shaky shaky!", Toast.LENGTH_SHORT).show();
        if (mShowForm) confirmClearForm();
    }

    public void confirmClearForm() {
        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_clear_form_title)
                .setMessage(R.string.dialog_clear_form_message)
                .setPositiveButton(R.string.confirm, (dialog, id) -> {
                    Toast.makeText(this, "Form cleared!", Toast.LENGTH_SHORT).show();
                    clearForm();
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                })
                .show();
    }

    public void registerSensor() {
        if (mSensor != null && mShakeDetector != null) {
            mSensorManager.registerListener(mShakeDetector, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void unregisterSensor() {
        if (mSensor != null && mShakeDetector != null) {
            mSensorManager.unregisterListener(mShakeDetector);
        }
    }
    //endregion

    //region ---------- Override ----------
    @Override
    protected void onResume() {
        super.onResume();
        if (mShowForm) registerSensor();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterSensor();
    }
    //endregion
}
