package au.edu.federation.itech3107.fedunimillionaire30360914.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import au.edu.federation.itech3107.fedunimillionaire30360914.R;
import au.edu.federation.itech3107.fedunimillionaire30360914.controllers.ScoreListAdapter;
import au.edu.federation.itech3107.fedunimillionaire30360914.helpers.ScoreDataSource;
import au.edu.federation.itech3107.fedunimillionaire30360914.helpers.ScoreSQLiteOpenHelper;
import au.edu.federation.itech3107.fedunimillionaire30360914.models.Score;

/**
 * Add icon at side of TextView | Referenced from https://stackoverflow.com/questions/25279715/android-how-to-add-icon-at-the-left-side-of-the-textview
 */
public class ScoresActivity extends AppCompatActivity {

    private static final String LOG_TAG = ScoresActivity.class.getSimpleName();

    private Drawable arrowUpward, arrowDownward;
    private TextView tvMoneyHeader, tvDatetimeHeader;
    private RecyclerView rvScores;

    private ScoreListAdapter scoreListAdapter;

    private boolean moneyOrder = true;
    private boolean datetimeOrder = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        tvMoneyHeader = findViewById(R.id.tvMoneyHeader);
        tvDatetimeHeader = findViewById(R.id.tvDatetimeHeader);
        rvScores = findViewById(R.id.rvScores);

        arrowUpward = getDrawable(R.drawable.arrow_upward);
        arrowDownward = getDrawable(R.drawable.arrow_downward);

        scoreListAdapter = new ScoreListAdapter(getAllRecordsFromDatabase(ScoreSQLiteOpenHelper.COLUMN_DATETIME, ScoreDataSource.ASC));

        rvScores.setLayoutManager(new LinearLayoutManager(this));
        rvScores.setAdapter(scoreListAdapter);

        tvDatetimeHeader.setCompoundDrawablesWithIntrinsicBounds(null, null, arrowUpward, null);


        // set onClickListener on the moneyHeader text view
        // to sort the scores by dollar value
        tvMoneyHeader.setOnClickListener(v -> {
            moneyOrder = !moneyOrder;
            tvDatetimeHeader.setCompoundDrawables(null, null, null, null);
            sorting((TextView) v, ScoreSQLiteOpenHelper.COLUMN_MONEY, moneyOrder);
        });

        // set onClickListener on the datetimeHeader text view
        // to sort the scores by date/time
        tvDatetimeHeader.setOnClickListener(v -> {
            datetimeOrder = !datetimeOrder;
            tvMoneyHeader.setCompoundDrawables(null, null, null, null);
            sorting((TextView) v, ScoreSQLiteOpenHelper.COLUMN_DATETIME, datetimeOrder);
        });
    }

    // Method used to sort a column
    private void sorting(TextView textView, String column, boolean isAsc) {
        Drawable rightIcon = isAsc ? arrowUpward : arrowDownward;
        String order = isAsc ? ScoreDataSource.ASC : ScoreDataSource.DESC;

        Log.d(LOG_TAG, "[SORT] " + column + " " + order);

        textView.setCompoundDrawablesWithIntrinsicBounds(null, null, rightIcon, null);
        scoreListAdapter.refresh(getAllRecordsFromDatabase(column, order));
    }

    public List<Score> getAllRecordsFromDatabase(String column, String order) {
        ScoreDataSource dataSource = new ScoreDataSource(this);
        dataSource.open();
        // Retrieve all scores in the database
        List<Score> scoreList = dataSource.retrieveAllScores(column, order);
        dataSource.close();
        return scoreList;
    }

    public void deleteScores(View view) {
        List<Score> scoreList = scoreListAdapter.getDataSet();
        List<Score> scoresToDelete = new ArrayList<>();

        // For every score, check if it is checked for delete
        for (Score sc : scoreList) {
            if (sc.isChecked()) {
                scoresToDelete.add(sc);
                // Delete the score record from database if checked
                deleteFromDatabase(sc);
            }
        }
        if (!scoresToDelete.isEmpty()) {
            // There are scores to delete
            // Remove all selected scores
            scoreList.removeAll(scoresToDelete);
            scoreListAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Scores successfully deleted!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No scores selected", Toast.LENGTH_SHORT).show();
        }

    }

    public void deleteFromDatabase(Score score) {
        ScoreDataSource dataSource = new ScoreDataSource(this);
        dataSource.open();
        dataSource.delete(score);
        dataSource.close();
    }
}