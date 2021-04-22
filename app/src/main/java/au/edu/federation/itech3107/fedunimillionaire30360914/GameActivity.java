package au.edu.federation.itech3107.fedunimillionaire30360914;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import au.edu.federation.itech3107.fedunimillionaire30360914.controllers.QuestionAdapter;
import au.edu.federation.itech3107.fedunimillionaire30360914.helpers.QuestionBank;
import au.edu.federation.itech3107.fedunimillionaire30360914.helpers.ScoreDataSource;
import au.edu.federation.itech3107.fedunimillionaire30360914.models.Question;
import au.edu.federation.itech3107.fedunimillionaire30360914.models.Score;

import static au.edu.federation.itech3107.fedunimillionaire30360914.MainActivity.EXTRA_HOT_MODE;
import static au.edu.federation.itech3107.fedunimillionaire30360914.MainActivity.EXTRA_PLAYER_NAME;


public class GameActivity extends AppCompatActivity {

    public static final String EXTRA_RESULT = "au.edu.federation.itech3107.fedunimillionaire.extra.RESULT";
    public static final String EXTRA_MESSAGE = "au.edu.federation.itech3107.fedunimillionaire.extra.MESSAGE";
    public static final String EXTRA_DOLLAR = "au.edu.federation.itech3107.fedunimillionaire.extra.DOLLAR";
    public static final String OUTSTATE_QUESTION_NO = "au.edu.federation.itech3107.fedunimillionaire.outstate.question_no";
    public static final String DATETIME_FORMAT = "dd/MM/yyyy HH:mm";

    private static final String LOG_TAG = GameActivity.class.getSimpleName();

    private final long HOT_MODE_TIME = 15000L;
    private final long ONE_SECOND = 1000L;
    private final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(DATETIME_FORMAT);


    private TextView tvDollarValue, tvSafeMoney, tvDifficulty, tvQuestionsLeft, tvQuestionNumber, tvQuestionTitle, tvTimer;
    private RadioGroup radGroup;
    private RadioButton radA, radB, radC, radD;
    private Button btnSubmit;

    private QuestionAdapter questionAdapter;
    private Question question;

    private Handler handler;
    private Timer timer;

    private boolean isHotMode;
    private String playerName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Log.d(LOG_TAG, "[ON_CREATE]");

        // Link variables to screen components
        tvDollarValue = findViewById(R.id.tvDollarValue);
        tvSafeMoney = findViewById(R.id.tvSafeMoney);
        tvDifficulty = findViewById(R.id.tvDfficulty);
        tvQuestionsLeft = findViewById(R.id.tvQuestionsLeft);
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);
        tvQuestionTitle = findViewById(R.id.tvQuestionTitle);
        tvTimer = findViewById(R.id.tvTimer);
        radGroup = findViewById(R.id.radGroup);
        radA = findViewById(R.id.radA);
        radB = findViewById(R.id.radB);
        radC = findViewById(R.id.radC);
        radD = findViewById(R.id.radD);
        btnSubmit = findViewById(R.id.btnSubmit);

        // Get intent extra passed from MainActivity
        Intent intent = getIntent();
        isHotMode = intent.getBooleanExtra(EXTRA_HOT_MODE, false);
        playerName = intent.getStringExtra(EXTRA_PLAYER_NAME);

        // Determine whether to start Hot Seat Mode
        if (isHotMode) {
            // Instantiate a handler to handle hot seat count down task
            handler = new Handler();
        }

        // Instantiate a new QuestionAdapter to manage the quiz questions
        questionAdapter = new QuestionAdapter(new QuestionBank(this));
        this.question = questionAdapter.startFrom(0);
        nextQuestion();
    }

    // Called when users press submit button
    public void handleSubmit(View view) {
        int selectedRadId = radGroup.getCheckedRadioButtonId();
        // Check if player has selected an answer
        if (selectedRadId != -1) {
            // Cancel hot seat timer
            if (isHotMode) {
                cancelHotCounting();
            }

            int selectedIndex = radGroup.indexOfChild(findViewById(selectedRadId));

            // Check player has selected the correct answer;
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
            tvDifficulty.setText(question.getDifficulty().toString());
            tvQuestionNumber.setText(currentNumber.toString());
            tvQuestionsLeft.setText(questionAdapter.getQuestionsLeft().toString());

            // Reset hot seat timer
            if (isHotMode) {
                startHotCounting();
                Integer sec = (int) HOT_MODE_TIME / 1000;
                tvTimer.setText(sec.toString());
            }
        } else {
            // If there's no more questions, disable the submit button and ends the game
            Log.d(LOG_TAG, "[DONE] No more questions");
            btnSubmit.setEnabled(false);
            endGame(true);
        }
    }

    // Open the EndgameActivity with a boolean as the win/lose result
    private void endGame(boolean result) {
        endGame(result, null);
    }

    private void endGame(boolean result, String message) {
        Log.d(LOG_TAG, "[END QUIZ]");

        insertScoreListing();

        Intent intent = new Intent(this, EndgameActivity.class);
        // Pass the result (win/lose)
        intent.putExtra(EXTRA_RESULT, result);
        // Pass the amount of money 'win'
        intent.putExtra(EXTRA_DOLLAR, questionAdapter.getSafeMoneyValue().toString());

        if (message != null)
            // Pass any optional message to replace the default endgame-message
            intent.putExtra(EXTRA_MESSAGE, message);

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

    @Override
    protected void onStop() {
        Log.d(LOG_TAG, "[ON_STOP]");
        super.onStop();
        if (isHotMode)
            cancelHotCounting();
    }

    public void insertScoreListing() {
        Date now = Calendar.getInstance().getTime();
        String formattedDate = dateTimeFormatter.format(now);

        ScoreDataSource dataSource = new ScoreDataSource(this);
        dataSource.open();
        // Add player's score to database
        dataSource.insert(playerName, questionAdapter.getSafeMoneyValue(), formattedDate);
        dataSource.close();
    }

    public void startHotCounting() {
        timer = new Timer();
        int initialSec = (int) HOT_MODE_TIME / 1000;

        timer.scheduleAtFixedRate(new DisplaySecondsTask(initialSec), 0, ONE_SECOND);
        // Post a job to handler to end the game after time's up
        handler.postDelayed(hotTask, HOT_MODE_TIME);

    }

    public void cancelHotCounting() {
        timer.cancel();
        handler.removeCallbacks(hotTask);
        Log.d(LOG_TAG, "[HOT MODE] Cancelled");
    }

    private Runnable hotTask = new Runnable() {
        @Override
        public void run() {
            Log.d(LOG_TAG, "[HOT MODE] Time's Up");
            endGame(false, "Time's Up!");
        }
    };

    private class DisplaySecondsTask extends TimerTask {
        int seconds;

        DisplaySecondsTask(int initialSec) {
            this.seconds = initialSec;
        }

        @Override
        public void run() {
            Integer secToDisplay = seconds--;
            // Show the seconds left to answer
            tvTimer.post(new Runnable() {
                @Override
                public void run() {
                    tvTimer.setText(secToDisplay.toString());
                }
            });
        }
    }

}