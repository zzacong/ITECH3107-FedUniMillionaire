package au.edu.federation.itech3107.fedunimillionaire30360914;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static final String EXTRA_HOT_MODE = "au.edu.federation.itech3107.fedunimillionaire.extra.HOT_MODE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // Called when user press start button, open the GameActivity
    public void startGame(View view) {
        Log.d(LOG_TAG, "[START QUIZ]");
        Intent intent = new Intent(this, GameActivity.class);
        if (view.getId() == R.id.btnStartHotMode) {
            Log.d(LOG_TAG, "[START QUIZ] HOT SEAT MODE");
            intent.putExtra(EXTRA_HOT_MODE, true);
        }
        startActivity(intent);
    }

}