package au.edu.federation.itech3107.fedunimillionaire30360914.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import au.edu.federation.itech3107.fedunimillionaire30360914.R;

import static au.edu.federation.itech3107.fedunimillionaire30360914.utils.MyString.capitalise;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_HOT_MODE = "au.edu.federation.itech3107.fedunimillionaire.extra.HOT_MODE";
    public static final String EXTRA_PLAYER_NAME = "au.edu.federation.itech3107.fedunimillionaire.extra.PLAYER_NAME";

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private EditText mEtPlayerName;
    private TextView mTvErrorMessage;

    private String mPlayerName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEtPlayerName = findViewById(R.id.etPlayerName);
        mTvErrorMessage = findViewById(R.id.tvErrorMessage);
    }


    // Called when user press play button, open the GameActivity
    // in normal / hot seat mode
    public void onStartGame(View view) {
        // Start the game if player has entered a name
        if (validPlayerName()) {
            Log.d(LOG_TAG, "[START QUIZ]");
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra(EXTRA_PLAYER_NAME, mPlayerName);

            // If player choose to start in hot seat mode
            if (view.getId() == R.id.btnStartHotMode) {
                Log.d(LOG_TAG, "[START QUIZ] HOT SEAT MODE");
                intent.putExtra(EXTRA_HOT_MODE, true);
            }
            // Start GameActivity
            startActivity(intent);
        }
    }

    // Called when user press view scores button, open the ScoreActivity
    public void onViewScores(View view) {
        Log.d(LOG_TAG, "[VIEW SCORES]");
        Intent intent = new Intent(this, ScoresActivity.class);
        startActivity(intent);
    }

    // Called when user press edit question button, open the QuestionActivity
    public void onEditQuestions(View view) {
        Log.d(LOG_TAG, "[EDIT QUESTIONS]");
        Intent intent = new Intent(this, QuestionActivity.class);
        startActivity(intent);
    }

    // Validation method
    public boolean validPlayerName() {
        String name = mEtPlayerName.getText().toString();
        boolean isValid = true;

        if (name == null || name.isEmpty()) {
            isValid = false;
            Log.d(LOG_TAG, "[INVALID] Player name is empty");
            mTvErrorMessage.setText(R.string.error_player_name);
        } else {
            mPlayerName = capitalise(name);
            mTvErrorMessage.setText("");
        }
        return isValid;
    }
}