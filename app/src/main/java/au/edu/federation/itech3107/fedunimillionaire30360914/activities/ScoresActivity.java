package au.edu.federation.itech3107.fedunimillionaire30360914.activities;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
public class ScoresActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String LOG_TAG = ScoresActivity.class.getSimpleName();
    private static final int DEFAULT_ZOOM = 8;

    private Drawable arrowUpward, arrowDownward;
    private TextView tvMoneyHeader, tvDatetimeHeader;
    private RecyclerView rvScores;
    private ScrollView svMain;
    private ImageView imgTransparent;

    private List<Score> scoreList;
    private ScoreListAdapter scoreListAdapter;

    private boolean moneyOrder = true;
    private boolean datetimeOrder = true;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        tvMoneyHeader = findViewById(R.id.tvMoneyHeader);
        tvDatetimeHeader = findViewById(R.id.tvDatetimeHeader);
        rvScores = findViewById(R.id.rvScores);
        svMain = findViewById(R.id.svMain);
        imgTransparent = findViewById(R.id.imgTransparent);

        arrowUpward = getDrawable(R.drawable.arrow_upward);
        arrowDownward = getDrawable(R.drawable.arrow_downward);
        arrowUpward.setTint(getColor(R.color.white));
        arrowDownward.setTint(getColor(R.color.white));

        scoreList = getAllRecordsFromDatabase(ScoreSQLiteOpenHelper.COLUMN_DATETIME, ScoreDataSource.ASC);
        scoreListAdapter = new ScoreListAdapter(scoreList);

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

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);

        // Don't scroll in ScrollView on touch events in map
        touchEventInMap();
    }

    //region ---------- Score table ----------
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

    public void onDeleteScores(View view) {
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
    //endregion

    //region ---------- Map ----------
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);

        // Add a marker for every score
        LatLng latLng = null;
        for (Score sc : scoreList) {
            Log.d(LOG_TAG, String.format("[MAP] Marker (%f, %f)", sc.getLat(), sc.getLng()));
            latLng = new LatLng(sc.getLat(), sc.getLng());
            map.addMarker(new MarkerOptions().position(latLng).title(sc.getName()).snippet(sc.getMoney()));
        }
        if (latLng != null)
            // Move camera to the last marker
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void touchEventInMap() {
        /**
         * reference: https://stackoverflow.com/a/17317176
         */
        imgTransparent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        svMain.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;
                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        svMain.requestDisallowInterceptTouchEvent(false);
                        return true;
                    default:
                        return true;
                }
            }
        });
    }
    //endregion
}
