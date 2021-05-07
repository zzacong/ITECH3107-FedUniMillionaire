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

    private final long HOT_MODE_TIME = 15000L;
    private final long ONE_SECOND = 1000L;
    private final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(DATETIME_FORMAT);


    private CardView cvLifelines, cvPercents;
    private TextView tvDollarValue, tvSafeMoney, tvDifficulty, tvQuestionsLeft, tvQuestionNumber, tvQuestionTitle, tvTimer, tvPercentA, tvPercentB, tvPercentC, tvPercentD;
    private RadioGroup radGroup;
    private Button btnSubmit;
    private FloatingActionButton fabHelp;
    private List<TextView> tvPercentList = new ArrayList<>();

    private QuizHandler quizHandler;
    private Handler handler;
    private Timer timer;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastKnownLocation;
    private final LatLng ballarat = new LatLng(-37.5622, 143.8503);

    private String playerName;
    private boolean isHotMode;
    private boolean canUseLifeline[] = {true, true, true};
    private boolean locationPermissionGranted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Log.d(LOG_TAG, "[ON_CREATE]");

        // Link variables to screen components
        cvLifelines = findViewById(R.id.cvLifelines);
        cvPercents = findViewById(R.id.cvPercents);

        tvDollarValue = findViewById(R.id.tvDollarValue);
        tvSafeMoney = findViewById(R.id.tvSafeMoney);
        tvDifficulty = findViewById(R.id.tvDfficulty);
        tvQuestionsLeft = findViewById(R.id.tvQuestionsLeft);
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);
        tvQuestionTitle = findViewById(R.id.tvQuestionTitle);
        tvTimer = findViewById(R.id.tvTimer);

        tvPercentList.add(findViewById(R.id.tvPercentA));
        tvPercentList.add(findViewById(R.id.tvPercentB));
        tvPercentList.add(findViewById(R.id.tvPercentC));
        tvPercentList.add(findViewById(R.id.tvPercentD));

        radGroup = findViewById(R.id.radGroup);

        btnSubmit = findViewById(R.id.btnSubmit);
        fabHelp = findViewById(R.id.fabHelp);

        // Get intent extra passed from MainActivity
        Intent intent = getIntent();
        isHotMode = intent.getBooleanExtra(EXTRA_HOT_MODE, false);
        playerName = intent.getStringExtra(EXTRA_PLAYER_NAME);

        // Determine whether to start Hot Seat Mode
        if (isHotMode) {
            // Instantiate a handler to handle hot seat count down task
            handler = new Handler();
        }

        // Instantiate a new QuestionBank, and call loadQuestionsAsync(),
        // pass in 'this' as context & listener
        // to wait for QuestionBank to finish loading questions (from web API/files)
        // then the onQuestionsReady() method will be called
        new QuestionBank().loadQuestionsAsync(this, this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // Check location permission and get device location
        getLocationPermission();
    }

    //region ---------- Normal quiz methods ----------

    // Display current question dollar value, safe money amount and show the questions and choices
    private void updateQuestionView(Question question) {
        // Hide the percentages CardView if it is visible
        if (cvPercents.getVisibility() == View.VISIBLE) cvPercents.setVisibility(View.INVISIBLE);

        if (question != null) {
            radGroup.clearCheck();
            tvQuestionTitle.setText(question.getTitle());

            for (int i = 0; i < 4; i++) {
                RadioButton radButton = (RadioButton) radGroup.getChildAt(i);
                radButton.setEnabled(true);
                radButton.setText(question.getChoices().get(i));
            }

            tvDollarValue.setText(quizHandler.getQuestionValue().toString());
            tvSafeMoney.setText(quizHandler.getSafeMoneyValue().toString());
            tvDifficulty.setText(question.getDifficulty().toString());
            tvQuestionNumber.setText(quizHandler.getCurrentNumber().toString());
            tvQuestionsLeft.setText(quizHandler.getQuestionsLeft().toString());

            // Reset hot seat timer
            if (isHotMode) {
                startHotCounting();
                Integer sec = (int) HOT_MODE_TIME / 1000;
                tvTimer.setText(sec.toString());
            }

            btnSubmit.setEnabled(true);
        } else {
            // If there's no more questions, disable the submit button and ends the game
            Log.d(LOG_TAG, "[DONE] No more questions");
            btnSubmit.setEnabled(false);
            endGame(true);
        }
    }

    // Called when users press submit button
    public void onSubmit(View view) {
        int selectedRadId = radGroup.getCheckedRadioButtonId();
        // Check if player has selected an answer
        if (selectedRadId != -1) {
            // Cancel hot seat timer
            if (isHotMode) {
                cancelHotCounting();
            }
            int selectedIndex = radGroup.indexOfChild(findViewById(selectedRadId));
            // Check player has selected the correct answer;
            if (quizHandler.currentQuestion().attempt(selectedIndex)) {
                Log.d(LOG_TAG, "[CORRECT]");
                updateQuestionView(quizHandler.nextQuestion());
            } else {
                Log.d(LOG_TAG, "[WRONG] Current question: " + quizHandler.getCurrentNumber());
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
        intent.putExtra(EXTRA_DOLLAR, quizHandler.getSafeMoneyValue().toString());

        if (message != null)
            // Pass any optional message to replace the default endgame-message
            intent.putExtra(EXTRA_MESSAGE, message);

        startActivity(intent);
        finish();
    }

    //endregion

    //region ---------- Hot Seat ----------

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
        if (lastKnownLocation == null) {
            // If there is no last known location, use Ballarat as the default location
            Log.d(LOG_TAG, "[LOCATION] Insert default Ballarat");
            dataSource.insert(playerName, quizHandler.getSafeMoneyValue(), formattedDate, ballarat.latitude, ballarat.longitude);
        } else {
            Log.d(LOG_TAG, "[LOCATION] Insert location");
            dataSource.insert(playerName, quizHandler.getSafeMoneyValue(), formattedDate, lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        }
        dataSource.close();
    }

    //endregion

    //region ---------- Lifelines ----------

    // Called when user press the lifelines button on the bottom right of screen
    public void onCallLifeLines(View view) {
        view.setVisibility(View.INVISIBLE);
        circularRevealCard(cvLifelines);
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
        if (canUseLifeline[0]) {
            Random rand = new Random();
            int answer = quizHandler.currentQuestion().getAnswer();
            Log.d(LOG_TAG, "[50:50] answer is " + answer);
            int count = 0;
            for (int i = 0; i < radGroup.getChildCount(); i++) {
                while (true) {
                    int randInt = rand.nextInt(4);
                    // Make sure we don't eliminate the correct answer
                    if (randInt != answer) {
                        Log.d(LOG_TAG, "[50:50] Disable button: " + randInt);
                        RadioButton radButton = (RadioButton) radGroup.getChildAt(randInt);
                        // Make sure we don't repeat eliminating the same button
                        if (radButton.isEnabled()) {
                            radButton.setEnabled(false);
                            break;
                        }
                    }
                }
                if (++count >= 2) break;
                canUseLifeline[0] = false;
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
        if (canUseLifeline[1]) {
            // Show the percentages CardView
            cvPercents.setVisibility(View.VISIBLE);

            // Hide the lifelines buttons CardView
            cvLifelines.setVisibility(View.INVISIBLE);
            fabHelp.setVisibility(View.VISIBLE);

            Question question = quizHandler.currentQuestion();
            List<Integer> percentage = percentage(question.getDifficulty());

            Log.d(LOG_TAG, "Percentage size: " + percentage.size());
            for (int i = 0; i < tvPercentList.size(); i++) {
                if (i == question.getAnswer()) {
                    // The last element in integerList is the percentage for the correct answer
                    tvPercentList.get(i).setText(String.format("%s%%", percentage.get(percentage.size() - 1).toString()));
                } else {
                    tvPercentList.get(i).setText(String.format("%s%%", percentage.get(0).toString()));
                    percentage.remove(0);
                }
            }
            canUseLifeline[1] = false;
        }
    }

    public void handleSwitchQuestion() {
        if (canUseLifeline[2]) {
            updateQuestionView(quizHandler.switchQuestion());
            canUseLifeline[2] = false;
        }
    }

    //endregion

    //region ---------- Load questions ----------

    @Override
    public void onQuestionsReady(QuestionBank questionBank) {
        quizHandler = new QuizHandler(questionBank);
        updateQuestionView(quizHandler.startFrom(1));
    }

    //endregion

    //region ---------- Overridden methods ----------

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (quizHandler.getCurrentNumber() > 1) {
            Log.d(LOG_TAG, "[SAVING STATE] Current question: " + quizHandler.getCurrentNumber());
            outState.putInt(OUTSTATE_QUESTION_NO, quizHandler.getCurrentNumber());
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int number = savedInstanceState.getInt(OUTSTATE_QUESTION_NO);
        Log.d(LOG_TAG, "[RESTORING STATE] Current question: " + number);
        updateQuestionView(quizHandler.startFrom(number));
    }

    @Override
    protected void onStop() {
        Log.d(LOG_TAG, "[ON_STOP]");
        super.onStop();
        if (isHotMode)
            cancelHotCounting();
    }

    /**
     * reference: https://stackoverflow.com/a/32105890
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // Hide the lifelines modal when user touches outside the view
        Rect viewRect = new Rect();
        cvLifelines.getGlobalVisibleRect(viewRect);
        if (!viewRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
            cvLifelines.setVisibility(View.INVISIBLE);
            fabHelp.setVisibility(View.VISIBLE);
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
            locationPermissionGranted = true;
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
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
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
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        lastKnownLocation = task.getResult();
                        Log.d(LOG_TAG, String.format("[LOCATION] Current location is (%f, %f)", lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()));
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
            tvTimer.post(new Runnable() {
                @Override
                public void run() {
                    tvTimer.setText(secToDisplay.toString());
                }
            });
        }
    }
    //endregion

}