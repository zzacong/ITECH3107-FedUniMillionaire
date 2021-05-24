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

    private Drawable mArrowUpward, mArrowDownward;
    private TextView mTvMoneyHeader, mTvDatetimeHeader;
    private ScrollView mSvMain;
    private ImageView mImgTransparent;

    private List<Score> mScoreList;
    private ScoreListAdapter mScoreListAdapter;

    private boolean mMoneyOrder = true;
    private boolean mDatetimeOrder = true;
    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        RecyclerView mRvScores = findViewById(R.id.rvScores);
        mTvMoneyHeader = findViewById(R.id.tvMoneyHeader);
        mTvDatetimeHeader = findViewById(R.id.tvDatetimeHeader);
        mSvMain = findViewById(R.id.svMain);
        mImgTransparent = findViewById(R.id.imgTransparent);

        mArrowUpward = getDrawable(R.drawable.arrow_upward);
        mArrowDownward = getDrawable(R.drawable.arrow_downward);
        mArrowUpward.setTint(getColor(R.color.white));
        mArrowDownward.setTint(getColor(R.color.white));

        mScoreList = getAllRecordsFromDatabase(ScoreSQLiteOpenHelper.COLUMN_DATETIME, ScoreDataSource.ASC);
        mScoreListAdapter = new ScoreListAdapter(mScoreList);

        mRvScores.setLayoutManager(new LinearLayoutManager(this));
        mRvScores.setAdapter(mScoreListAdapter);

        // Set arrowUpward right icon
        mTvDatetimeHeader.setCompoundDrawablesWithIntrinsicBounds(null, null, mArrowUpward, null);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);

        // Don't scroll in ScrollView on touch events in map
        touchEventInMap();
    }


    //region ---------- Score table ----------
    // set onClickListener on the moneyHeader text view
    // to sort the scores by dollar value
    public void onSortMoney(View view) {
        mMoneyOrder = !mMoneyOrder;
        mTvDatetimeHeader.setCompoundDrawables(null, null, null, null);
        sorting((TextView) view, ScoreSQLiteOpenHelper.COLUMN_MONEY, mMoneyOrder);
    }

    // set onClickListener on the datetimeHeader text view
    // to sort the scores by date/time
    public void onSortDatetime(View view) {
        mDatetimeOrder = !mDatetimeOrder;
        mTvMoneyHeader.setCompoundDrawables(null, null, null, null);
        sorting((TextView) view, ScoreSQLiteOpenHelper.COLUMN_DATETIME, mDatetimeOrder);
    }

    // Method used to sort a column
    private void sorting(TextView textView, String column, boolean isAsc) {
        Drawable rightIcon = isAsc ? mArrowUpward : mArrowDownward;
        String order = isAsc ? ScoreDataSource.ASC : ScoreDataSource.DESC;

        Log.d(LOG_TAG, "[SORT] " + column + " " + order);

        textView.setCompoundDrawablesWithIntrinsicBounds(null, null, rightIcon, null);
        mScoreListAdapter.refresh(getAllRecordsFromDatabase(column, order));
    }

    public List<Score> getAllRecordsFromDatabase(String column, String order) {
        ScoreDataSource dataSource = new ScoreDataSource(this);
        dataSource.open();
        // Retrieve all scores in the database
        List<Score> scoreList = dataSource.retrieveAllScores(column, order);
        dataSource.close();
        return scoreList;
    }

    // Delete button is pressed
    public void onDeleteScores(View view) {
        List<Score> scoreList = mScoreListAdapter.getDataSet();
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
            mScoreList = new ArrayList<>(scoreList);
            mScoreListAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Scores successfully deleted!", Toast.LENGTH_SHORT).show();

            // Update map markers after scores are removed
            updateMapUI();
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
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        updateMapUI();
    }

    public void updateMapUI() {
        if (mMap == null) return;

        // Clear all existing markers first
        mMap.clear();

        // Add a marker for every score
        LatLng latLng = null;
        for (Score sc : mScoreList) {
            Log.d(LOG_TAG, String.format("[MAP] Marker (%f, %f)", sc.getLat(), sc.getLng()));
            latLng = new LatLng(sc.getLat(), sc.getLng());
            mMap.addMarker(new MarkerOptions().position(latLng).title(sc.getName()).snippet(sc.getMoney()));
        }
        if (latLng != null) {
            // Move camera to focus the last marker
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
        }
    }

    /**
     * Ignore touch event in Map (interfer with ScrollView)
     * reference: https://stackoverflow.com/a/17317176
     */
    @SuppressLint("ClickableViewAccessibility")
    private void touchEventInMap() {
        mImgTransparent.setOnTouchListener((v, event) -> {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    // Disallow ScrollView to intercept touch events.
                    mSvMain.requestDisallowInterceptTouchEvent(true);
                    // Disable touch on transparent view
                    return false;
                case MotionEvent.ACTION_UP:
                    // Allow ScrollView to intercept touch events.
                    mSvMain.requestDisallowInterceptTouchEvent(false);
                    return true;
                default:
                    return true;
            }
        });
    }
    //endregion
}
