package au.edu.federation.itech3107.fedunimillionaire30360914;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class EndgameActivity extends AppCompatActivity {

    TextView tvWinLose, tvDollar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endgame);
        tvWinLose = findViewById(R.id.tvWinLose);
        tvDollar = findViewById(R.id.tvDollar);

        Intent intent = getIntent();
        boolean win = intent.getBooleanExtra(GameActivity.EXTRA_RESULT, true);
        String dollar = intent.getStringExtra(GameActivity.EXTRA_DOLLAR);

        tvWinLose.setText(win ? R.string.win_message : R.string.lose_message);
        tvDollar.setText("$ " + dollar);
    }

    public void gotoMainMenu(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}