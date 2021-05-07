package au.edu.federation.itech3107.fedunimillionaire30360914.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import au.edu.federation.itech3107.fedunimillionaire30360914.R;

public class EndgameActivity extends AppCompatActivity {

    private TextView mTvWinLose, mTvDollar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endgame);
        mTvWinLose = findViewById(R.id.tvWinLose);
        mTvDollar = findViewById(R.id.tvDollar);

        Intent intent = getIntent();
        boolean win = intent.getBooleanExtra(GameActivity.EXTRA_RESULT, true);
        String dollar = intent.getStringExtra(GameActivity.EXTRA_DOLLAR);
        String message = intent.getStringExtra(GameActivity.EXTRA_MESSAGE);

        if (message != null) {
            // If custom message is provided, then use it to display on EndGame screen
            mTvWinLose.setText(message);
        } else {
            // Else, show end game message based on quiz result (win or lose?)
            mTvWinLose.setText(win ? R.string.win_message : R.string.lose_message);
        }
        mTvDollar.setText("$ " + dollar);
    }

    // Called when user press the MainMenu button, return to MainActivity
    public void gotoMainMenu(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}