package au.edu.federation.itech3107.fedunimillionaire30360914.activities;

import android.animation.Animator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import au.edu.federation.itech3107.fedunimillionaire30360914.R;
import au.edu.federation.itech3107.fedunimillionaire30360914.controllers.QuizHandler;
import au.edu.federation.itech3107.fedunimillionaire30360914.helpers.QuestionBank;
import au.edu.federation.itech3107.fedunimillionaire30360914.helpers.ScoreDataSource;
import au.edu.federation.itech3107.fedunimillionaire30360914.models.Question;
import au.edu.federation.itech3107.fedunimillionaire30360914.models.Question.Difficulty;

import static au.edu.federation.itech3107.fedunimillionaire30360914.activities.MainActivity.EXTRA_HOT_MODE;
import static au.edu.federation.itech3107.fedunimillionaire30360914.activities.MainActivity.EXTRA_PLAYER_NAME;

/**
 * Datetime formatting to String | Referenced from https://www.javatpoint.com/java-simpledateformat
 * Get current datetime | Referenced from https://stackoverflow.com/questions/5369682/how-to-get-current-time-and-date-in-android
 */
public class GameActivity extends AppCompatActivity implements QuestionBank.OnReadyListener {

    public static final String EXTRA_RESULT = "au.edu.federation.itech3107.fedunimillionaire.extra.RESULT";
    public static final String EXTRA_MESSAGE = "au.edu.federation.itech3107.fedunimillionaire.extra.MESSAGE";
    public static final String EXTRA_DOLLAR = "au.edu.federation.itech3107.fedunimillionaire.extra.DOLLAR";
    public static final String OUTSTATE_QUESTION_NO = "au.edu.federation.itech3107.fedunimillionaire.outstate.question_no";
    public static final String DATETIME_FORMAT = "dd/MM/yyyy HH:mm";

    private static final String LOG_TAG = GameActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final LatLng BALLARAT = new LatLng(-37.5622, 143.8503);

    private final long HOT_MODE_TIME = 15000L;
    private final long ONE_SECOND = 1000L;
    private final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(DATETIME_FORMAT);

    private CardView mCvLifelines, mCvPercents;
    private TextView mTvDollarValue, mTvSafeMoney, mTvDifficulty, mTvQuestionsLeft, mTvQuestionNumber, mTvQuestionTitle, mTvTimer;
    private RadioGroup mRadGroup;
    private Button mBtnSubmit;
    private FloatingActionButton mFabHelp;
    private List<TextView> mTvPercentList = new ArrayList<>();

    private QuizHandler mQuizHandler;
    private Handler mHandler;
    private Timer mTimer;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;

    private String mPlayerName;
    private boolean mIsHotMode;
    private boolean mCanUseLifeline[] = {true, true, true};
    private boolean mLocationPermissionGranted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Log.d(LOG_TAG, "[ON_CREATE]");

        // Link variables to screen components
        mCvLifelines = findViewById(R.id.cvLifelines);
        mCvPercents = findViewById(R.id.cvPercents);

        mTvDollarValue = findViewById(R.id.tvDollarValue);
        mTvSafeMoney = findViewById(R.id.tvSafeMoney);
        mTvDifficulty = findViewById(R.id.tvDfficulty);
        mTvQuestionsLeft = findViewById(R.id.tvQuestionsLeft);
        mTvQuestionNumber = findViewById(R.id.tvQuestionNumber);
        mTvQuestionTitle = findViewById(R.id.tvQuestionTitle);
        mTvTimer = findViewById(R.id.tvTimer);

        mTvPercentList.add(findViewById(R.id.tvPercentA));
        mTvPercentList.add(findViewById(R.id.tvPercentB));
        mTvPercentList.add(findViewById(R.id.tvPercentC));
        mTvPercentList.add(findViewById(R.id.tvPercentD));

        mRadGroup = findViewById(R.id.radGroup);

        mBtnSubmit = findViewById(R.id.btnSubmit);
        mFabHelp = findViewById(R.id.fabHelp);

        // Get intent extra passed from MainActivity
        Intent intent = getIntent();
        mIsHotMode = intent.getBooleanExtra(EXTRA_HOT_MODE, false);
        mPlayerName = intent.getStringExtra(EXTRA_PLAYER_NAME);

        // Determine whether to start Hot Seat Mode
        if (mIsHotMode) {
            // Instantiate a handler to handle hot seat count down task
            mHandler = new Handler();
        }

        // Instantiate a new QuestionBank, and call loadQuestionsAsync(),
        // pass in 'this' as context & listener
        // to wait for QuestionBank to finish loading questions (from web API/files)
        // then the onQuestionsReady() method will be called
        new QuestionBank().loadQuestionsAsync(this, this);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // Check location permission and get device location
        getLocationPermission();
    }


    //region ---------- Normal quiz methods ----------
    // Display current question dollar value, safe money amount and show the questions and choices
    private void updateQuestionView(Question question) {
        // Hide the percentages CardView if it is visible
        if (mCvPercents.getVisibility() == View.VISIBLE) mCvPercents.setVisibility(View.INVISIBLE);

        if (question != null) {
            mRadGroup.clearCheck();
            mTvQuestionTitle.setText(question.getTitle());

            for (int i = 0; i < 4; i++) {
                RadioButton radButton = (RadioButton) mRadGroup.getChildAt(i);
                radButton.setEnabled(true);
                radButton.setText(question.getChoices().get(i));
            }

            mTvDollarValue.setText(mQuizHandler.getQuestionValue().toString());
            mTvSafeMoney.setText(mQuizHandler.getSafeMoneyValue().toString());
            mTvDifficulty.setText(question.getDifficulty().toString());
            mTvQuestionNumber.setText(mQuizHandler.getCurrentNumber().toString());
            mTvQuestionsLeft.setText(mQuizHandler.getQuestionsLeft().toString());

            // Reset hot seat timer
            if (mIsHotMode) {
                startHotCounting();
                Integer sec = (int) HOT_MODE_TIME / 1000;
                mTvTimer.setText(sec.toString());
            }

            mBtnSubmit.setEnabled(true);
        } else {
            // If there's no more questions, disable the submit button and ends the game
            Log.d(LOG_TAG, "[DONE] No more questions");
            mBtnSubmit.setEnabled(false);
            endGame(true);
        }
    }

    // Called when users press submit button
    public void onSubmit(View view) {
        int selectedRadId = mRadGroup.getCheckedRadioButtonId();
        // Check if player has selected an answer
        if (selectedRadId != -1) {
            // Cancel hot seat timer
            if (mIsHotMode) {
                cancelHotCounting();
            }
            int selectedIndex = mRadGroup.indexOfChild(findViewById(selectedRadId));
            // Check player has selected the correct answer;
            if (mQuizHandler.currentQuestion().attempt(selectedIndex)) {
                Log.d(LOG_TAG, "[CORRECT]");
                updateQuestionView(mQuizHandler.nextQuestion());
            } else {
                Log.d(LOG_TAG, "[WRONG] Current question: " + mQuizHandler.getCurrentNumber());
                endGame(false);
            }
        } else {
            Log.d(LOG_TAG, "[INVALID] No answer selected");
            Toast.makeText(this, "Please select an answer.", Toast.LENGTH_SHORT).show();
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
        intent.putExtra(EXTRA_DOLLAR, mQuizHandler.getSafeMoneyValue().toString());

        if (message != null)
            // Pass any optional message to replace the default endgame-message
            intent.putExtra(EXTRA_MESSAGE, message);

        startActivity(intent);
        finish();
    }
    //endregion

    //region ---------- Hot Seat ----------
    public void startHotCounting() {
        mTimer = new Timer();
        int initialSec = (int) HOT_MODE_TIME / 1000;

        mTimer.scheduleAtFixedRate(new DisplaySecondsTask(initialSec), 0, ONE_SECOND);
        // Post a job to handler to end the game after time's up
        mHandler.postDelayed(hotTask, HOT_MODE_TIME);

    }

    public void cancelHotCounting() {
        mTimer.cancel();
        mHandler.removeCallbacks(hotTask);
        Log.d(LOG_TAG, "[HOT MODE] Cancelled");
    }

    private Runnable hotTask = () -> {
        Log.d(LOG_TAG, "[HOT MODE] Time's Up");
        endGame(false, "Time's Up!");
    };
    //endregion

    //region ---------- Database ----------
    public void insertScoreListing() {
        Date now = Calendar.getInstance().getTime();
        String formattedDate = dateTimeFormatter.format(now);

        ScoreDataSource dataSource = new ScoreDataSource(this);
        dataSource.open();
        // Add player's score to database
        if (mLastKnownLocation == null) {
            // If there is no last known location, use Ballarat as the default location
            Log.d(LOG_TAG, "[LOCATION] Insert default Ballarat");
            dataSource.insert(mPlayerName, mQuizHandler.getSafeMoneyValue(), formattedDate, BALLARAT.latitude, BALLARAT.longitude);
        } else {
            Log.d(LOG_TAG, "[LOCATION] Insert location");
            dataSource.insert(mPlayerName, mQuizHandler.getSafeMoneyValue(), formattedDate, mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
        }
        dataSource.close();
    }
    //endregion

    //region ---------- Lifelines ----------
    // Called when user press the lifelines button on the bottom right of screen
    public void onCallLifeLines(View view) {
        view.setVisibility(View.INVISIBLE);
        circularRevealCard(mCvLifelines);
    }

    /**
     * reference: https://stackoverflow.com/a/42114635
     */
    // Animator function
    private void circularRevealCard(View view) {
        float finalRadius = Math.max(view.getWidth(), view.getHeight());
        // create the animator which display the card view
        // in a circular motion that starts from right
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(view, view.getWidth(), 0, 0, finalRadius * 1.1f);
        circularReveal.setDuration(500);
        // make the view visible and start the animation
        view.setVisibility(View.VISIBLE);
        circularReveal.start();
    }

    // Called when one of the lifeline buttons is pressed
    public void onLifeLines(View view) {
        view.setEnabled(false);
        switch (view.getId()) {
            case R.id.btnFiftyfifty:
                Log.d(LOG_TAG, "[LIFELINES] 50:50");
                handleFiftyfifty();
                break;
            case R.id.btnAudience:
                Log.d(LOG_TAG, "[LIFELINES] Ask audience");
                handleAskAudience();
                break;
            case R.id.btnSwitch:
                Log.d(LOG_TAG, "[LIFELINES] Switch");
                handleSwitchQuestion();
                break;
        }
    }

    public void handleFiftyfifty() {
        if (mCanUseLifeline[0]) {
            Random rand = new Random();
            int answer = mQuizHandler.currentQuestion().getAnswer();
            Log.d(LOG_TAG, "[50:50] answer is " + answer);
            int count = 0;
            for (int i = 0; i < mRadGroup.getChildCount(); i++) {
                while (true) {
                    int randInt = rand.nextInt(4);
                    // Make sure we don't eliminate the correct answer
                    if (randInt != answer) {
                        Log.d(LOG_TAG, "[50:50] Disable button: " + randInt);
                        RadioButton radButton = (RadioButton) mRadGroup.getChildAt(randInt);
                        // Make sure we don't repeat eliminating the same button
                        if (radButton.isEnabled()) {
                            radButton.setEnabled(false);
                            break;
                        }
                    }
                }
                if (++count >= 2) break;
                mCanUseLifeline[0] = false;
            }
        }
    }

    // Method to generate random integer within a range
    public int genRandIntInRange(int min, int max) {
        Log.d(LOG_TAG, "min: " + min + " , max: " + max);
        Random rand = new Random();
        return rand.nextInt(max - min) + min;
    }

    // Method for calculating different percentage based on difficulty given
    public List<Integer> percentage(Difficulty difficulty) {
        List<Integer> list = new ArrayList<>();
        int sum = 0;

        switch (difficulty) {
            case easy:
                Log.d(LOG_TAG, "[ASK AUDIENCE] easy");
                for (int i = 0; i < 3; i++) {
                    int num = genRandIntInRange(1, 6);
                    sum += num;
                    list.add(num);
                }
                int lastInt = 100 - sum;
                list.add(lastInt);
                break;
            case medium:
                Log.d(LOG_TAG, "[ASK AUDIENCE] medium");
                int max = 0;
                for (int i = 0; i < 4; i++) {
                    int num;
                    if (i < 2) {
                        num = genRandIntInRange(40, 50);
                        // If the second random number is the same as the first, re-run the method
                        if (num == max) return percentage(difficulty);
                    } else {
                        int left = 100 - sum;
                        // On last iteration, take whatever's left until 100
                        if (i >= 3) num = 100 - sum;
                        else num = genRandIntInRange(1, left - 1);
                    }
                    sum += num;
                    // Put the largest number at the end of the list
                    if (num > max) {
                        max = num;
                        list.add(num);
                        continue;
                    }
                    list.add(0, num);
                }
                break;
            case hard:
                Log.d(LOG_TAG, "[ASK AUDIENCE] hard");
                for (int i = 0; i < 4; i++) {
                    int num;
                    if (i < 2) {
                        num = genRandIntInRange(30, 33);
                        list.add(num);
                    } else {
                        int left = 100 - sum;
                        // On last iteration, take whatever's left until 100
                        if (i >= 3) num = 100 - sum;
                        else num = genRandIntInRange(1, left - 1);
                        list.add(0, num);
                    }
                    sum += num;
                }
                break;
        }
        return list;
    }

    public void handleAskAudience() {
        if (mCanUseLifeline[1]) {
            // Show the percentages CardView
            mCvPercents.setVisibility(View.VISIBLE);

            // Hide the lifelines buttons CardView
            mCvLifelines.setVisibility(View.INVISIBLE);
            mFabHelp.setVisibility(View.VISIBLE);

            Question question = mQuizHandler.currentQuestion();
            List<Integer> percentage = percentage(question.getDifficulty());

            Log.d(LOG_TAG, "Percentage size: " + percentage.size());
            for (int i = 0; i < mTvPercentList.size(); i++) {
                if (i == question.getAnswer()) {
                    // The last element in integerList is the percentage for the correct answer
                    mTvPercentList.get(i).setText(String.format("%s%%", percentage.get(percentage.size() - 1).toString()));
                } else {
                    mTvPercentList.get(i).setText(String.format("%s%%", percentage.get(0).toString()));
                    percentage.remove(0);
                }
            }
            mCanUseLifeline[1] = false;
        }
    }

    public void handleSwitchQuestion() {
        if (mCanUseLifeline[2]) {
            updateQuestionView(mQuizHandler.switchQuestion());
            mCanUseLifeline[2] = false;
        }
    }
    //endregion

    //region ---------- Load questions ----------
    @Override
    public void onQuestionsReady(QuestionBank questionBank) {
        mQuizHandler = new QuizHandler(questionBank);
        updateQuestionView(mQuizHandler.startFrom(1));
    }
    //endregion

    //region ---------- Overridden methods ----------
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mQuizHandler.getCurrentNumber() > 1) {
            Log.d(LOG_TAG, "[SAVING STATE] Current question: " + mQuizHandler.getCurrentNumber());
            outState.putInt(OUTSTATE_QUESTION_NO, mQuizHandler.getCurrentNumber());
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int number = savedInstanceState.getInt(OUTSTATE_QUESTION_NO);
        Log.d(LOG_TAG, "[RESTORING STATE] Current question: " + number);
        updateQuestionView(mQuizHandler.startFrom(number));
    }

    @Override
    protected void onStop() {
        Log.d(LOG_TAG, "[ON_STOP]");
        super.onStop();
        if (mIsHotMode)
            cancelHotCounting();
    }

    /**
     * reference: https://stackoverflow.com/a/32105890
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // Hide the lifelines modal when user touches outside the view
        Rect viewRect = new Rect();
        mCvLifelines.getGlobalVisibleRect(viewRect);
        if (!viewRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
            mCvLifelines.setVisibility(View.INVISIBLE);
            mFabHelp.setVisibility(View.VISIBLE);
        }
        return super.dispatchTouchEvent(ev);
    }
    //endregion

    //region ---------- Location ----------
    /**
     * reference: https://developers.google.com/maps/documentation/android-sdk/current-place-tutorial
     */

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            getDeviceLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    getDeviceLocation();
                }
            }
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        mLastKnownLocation = task.getResult();
                        Log.d(LOG_TAG, String.format("[LOCATION] Current location is (%f, %f)", mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
                    } else {
                        Log.d(LOG_TAG, "[LOCATION] Current location is null");
                        Log.e(LOG_TAG, "[LOCATION] Exception: %s", task.getException());
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }
    //endregion

    //region ---------- Private classes ----------
    private class DisplaySecondsTask extends TimerTask {
        int seconds;

        DisplaySecondsTask(int initialSec) {
            this.seconds = initialSec;
        }

        @Override
        public void run() {
            Integer secToDisplay = seconds--;
            // Show the seconds left to answer
            mTvTimer.post(new Runnable() {
                @Override
                public void run() {
                    mTvTimer.setText(secToDisplay.toString());
                }
            });
        }
    }
    //endregion

}