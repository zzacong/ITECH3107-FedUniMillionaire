package au.edu.federation.itech3107.fedunimillionaire30360914;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import au.edu.federation.itech3107.fedunimillionaire30360914.controllers.ScoreListAdapter;
import au.edu.federation.itech3107.fedunimillionaire30360914.helpers.ScoreDataSource;
import au.edu.federation.itech3107.fedunimillionaire30360914.models.Score;

public class ScoresActivity extends AppCompatActivity {

    private static final String LOG_TAG = ScoresActivity.class.getSimpleName();
    private List<Score> scoreList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        RecyclerView rvScores = findViewById(R.id.rvScores);

//        insertDefaultRecords();
        scoreList = getAllRecordsFromDatabase();

        Log.d(LOG_TAG, "" + scoreList.size());
        rvScores.setLayoutManager(new LinearLayoutManager(this));
        rvScores.setAdapter(new ScoreListAdapter(scoreList));
    }


    public List<Score> getAllRecordsFromDatabase() {
        ScoreDataSource dataSource = new ScoreDataSource(this);
        dataSource.open();
        // Retrieve all scores in the database
        List<Score> scoreList = dataSource.retrieveAllScores();
        dataSource.close();
        return scoreList;
    }

    // Method to clear the database and insert a few placeholder records
    private void insertDefaultRecords() {
        ScoreDataSource dataSource = new ScoreDataSource(this);
        dataSource.open();

        // Clear the table, then add some default records
        dataSource.deleteAllScores();
        scoreList.clear();

        // Add a few scores to the list
        scoreList.add(new Score("Al", 1000, "2021/04/02"));
        scoreList.add(new Score("Bob", 32000, "2021/04/03"));
        scoreList.add(new Score("Carol", 1000, "2021/04/04"));

        // Now add all the people in the list to the database
        for (Score sc : scoreList) {
            dataSource.insert(sc.getName(), sc.getMoney(), sc.getDatetime());
        }

        dataSource.close();
    }
}