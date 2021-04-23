package au.edu.federation.itech3107.fedunimillionaire30360914.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import au.edu.federation.itech3107.fedunimillionaire30360914.R;

public class EndgameActivity extends AppCompatActivity {

    private TextView tvWinLose, tvDollar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endgame);
        tvWinLose = findViewById(R.id.tvWinLose);
        tvDollar = findViewById(R.id.tvDollar);

        Intent intent = getIntent();
        boolean win = intent.getBooleanExtra(GameActivity.EXTRA_RESULT, true);
        String dollar = intent.getStringExtra(GameActivity.EXTRA_DOLLAR);
        String message = intent.getStringExtra(GameActivity.EXTRA_MESSAGE);

        if (message != null) {
            // If custom message is provided, then use it to display on EndGame screen
            tvWinLose.setText(message);
        } else {
            // Else, show end game message based on quiz result (win or lose?)
            tvWinLose.setText(win ? R.string.win_message : R.string.lose_message);
        }
        tvDollar.setText("$ " + dollar);
    }

    // Called when user press the MainMenu button, return to MainActivity
    public void gotoMainMenu(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}