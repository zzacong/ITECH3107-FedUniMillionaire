package au.edu.federation.itech3107.fedunimillionaire30360914.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import au.edu.federation.itech3107.fedunimillionaire30360914.models.Score;

/**
 * How to order SQLite database in desc/asc | Referenced from https://stackoverflow.com/questions/8948435/how-do-i-order-my-sqlite-database-in-descending-order-for-an-android-app
 */
public class ScoreDataSource {

    public static final String ASC = " ASC";
    public static final String DESC = " DESC";

    private static final String LOG_TAG = ScoreDataSource.class.getSimpleName();

    private SQLiteDatabase mDatabase;
    private ScoreSQLiteOpenHelper mDbHelper;

    // Declare a String array containing all the column names so we can easily
    // query the people table for all fields
    private static final String[] allColumns = {
            ScoreSQLiteOpenHelper.COLUMN_ID,
            ScoreSQLiteOpenHelper.COLUMN_NAME,
            ScoreSQLiteOpenHelper.COLUMN_MONEY,
            ScoreSQLiteOpenHelper.COLUMN_DATETIME,
            ScoreSQLiteOpenHelper.COLUMN_LAT,
            ScoreSQLiteOpenHelper.COLUMN_LNG
    };


    public ScoreDataSource(Context context) {
        // When we create a PersonDataSource, instantiate our dbHelper to
        // create/open the database
        mDbHelper = new ScoreSQLiteOpenHelper(context);
    }


    // Method to open the database for reading and writing
    public void open() {
        // The getWritableDatabase() method will throw an SQLException
        // if it fails, which we'll catch and alert the user about.
        try {
            mDatabase = mDbHelper.getWritableDatabase();
        } catch (SQLException sqle) {
            // Couldn't open the database? Log an error.
            Log.e(LOG_TAG, "[DATABASE] Could not open database: " + sqle.getMessage());
        }
    }

    // Method to close the database
    public void close() {
        mDatabase.close();
    }

    // Method to insert a record into the database
    public void insert(String name, int money, String datetime, double lat, double lng) {
        // Create a ContentValues object and populate the row
        ContentValues values = new ContentValues();
        values.put(ScoreSQLiteOpenHelper.COLUMN_NAME, name);
        values.put(ScoreSQLiteOpenHelper.COLUMN_MONEY, money);
        values.put(ScoreSQLiteOpenHelper.COLUMN_DATETIME, datetime);
        values.put(ScoreSQLiteOpenHelper.COLUMN_LAT, lat);
        values.put(ScoreSQLiteOpenHelper.COLUMN_LNG, lng);

        // insert it into the database
        long insertId = mDatabase.insert(ScoreSQLiteOpenHelper.TABLE_SCORE, null, values);

        Log.d(LOG_TAG, "[DATABASE] Inserted score " + insertId + " into the database");
    }


    // Method to delete a person
    public void delete(Score sc) {
        // Extract the id from the person object
        Long id = sc.getId();
        mDatabase.delete(ScoreSQLiteOpenHelper.TABLE_SCORE, ScoreSQLiteOpenHelper.COLUMN_ID + " = ?", new String[]{id.toString()});
        Log.d(LOG_TAG, "[SCORE] Deleted record with id: " + id);
    }


    // Method to delete all rows from the score table
    public void deleteAllScores() {
        mDatabase.delete(ScoreSQLiteOpenHelper.TABLE_SCORE, null, null);
        mDatabase.execSQL("UPDATE `sqlite_sequence` SET `seq` = 0 WHERE `name` = '" + ScoreSQLiteOpenHelper.TABLE_SCORE + "';");
    }


    // Extract a Score object from the Cursor and return it.
    private Score cursorToScore(Cursor cursor) {
        Score sc = new Score(
                cursor.getString(1), // name field
                cursor.getInt(2), // money field
                cursor.getString(3), // datetime field
                cursor.getDouble(4), // lat field
                cursor.getDouble(5) // lng field
        );

        // Set the id of the score (which was retrieved in field 0 of the record)
        sc.setId(cursor.getLong(0));

        return sc;
    }

    // Method to return a list of score with OrderBy function
    public List<Score> retrieveAllScores(String column, String order) {
        // Create a new ArrayList of People objects
        List<Score> scoreList = new ArrayList<>();

        // Form the orderBy clause
        String orderBy = null;
        if (column != null && order != null && !column.isEmpty() && !order.isEmpty()) {
            orderBy = column + " " + order;
            Log.d(LOG_TAG, "[ORDER BY] : " + orderBy);
        }

        // Query the database for all columns
        Cursor cursor = mDatabase.query(ScoreSQLiteOpenHelper.TABLE_SCORE, allColumns, null, null, null, null, orderBy);

        // Move to the first row
        cursor.moveToFirst();

        // extract every row as a Score object and add to score List
        while (!cursor.isAfterLast()) {
            Score score = cursorToScore(cursor);
            scoreList.add(score);
            cursor.moveToNext();
        }

        // Close the cursor after retrieving all rows
        cursor.close();

        return scoreList;
    }

}
