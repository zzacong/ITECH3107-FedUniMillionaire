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

public class ScoreDataSource {

    private final static String TAG = ScoreDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private ScoreSQLiteOpenHelper dbHelper;

    // Declare a String array containing all the column names so we can easily
    // query the people table for all fields
    private static final String[] allColumns = {
            ScoreSQLiteOpenHelper.COLUMN_ID,
            ScoreSQLiteOpenHelper.COLUMN_NAME,
            ScoreSQLiteOpenHelper.COLUMN_MONEY,
            ScoreSQLiteOpenHelper.COLUMN_DATETIME
    };


    public ScoreDataSource(Context context) {
        // When we create a PersonDataSource, instantiate our dbHelper to
        // create/open the database
        dbHelper = new ScoreSQLiteOpenHelper(context);
    }


    // Method to open the database for reading and writing
    public void open() {
        // The getWritableDatabase() method will throw an SQLException
        // if it fails, which we'll catch and alert the user about.
        try {
            database = dbHelper.getWritableDatabase();
        } catch (SQLException sqle) {
            // Couldn't open the database? Log an error.
            Log.e(TAG, "[DATABASE] Could not open database: " + sqle.getMessage());
        }
    }


    // Method to close the database
    public void close() {
        database.close();
    }


    // Method to insert a record into the database
    public void insert(String name, int money, String datetime) {
        // Create a ContentValues object and populate the row
        ContentValues values = new ContentValues();

        values.put(ScoreSQLiteOpenHelper.COLUMN_NAME, name);
        values.put(ScoreSQLiteOpenHelper.COLUMN_MONEY, money);
        values.put(ScoreSQLiteOpenHelper.COLUMN_DATETIME, datetime);

        // insert it into the database
        long insertId = database.insert(ScoreSQLiteOpenHelper.TABLE_PEOPLE, null, values);

        Log.d(TAG, "Inserted person " + insertId + " into the database!");
    }


    // Method to delete a person
    public void delete(Score sc) {
        // Extract the id from the person object
        Long id = sc.getId();
        database.delete(ScoreSQLiteOpenHelper.TABLE_PEOPLE, ScoreSQLiteOpenHelper.COLUMN_ID + " = ?", new String[]{id.toString()});
        Log.d(TAG, "[SCORE] Deleted record with id: " + id);
    }


    // Method to delete all rows from the score table
    public void deleteAllScores() {
        database.delete(ScoreSQLiteOpenHelper.TABLE_PEOPLE, null, null);
    }


    // Extract a Score object from the Cursor and return it.
    private Score cursorToScore(Cursor cursor) {
        Score sc = new Score(
                cursor.getString(1), // name field
                Integer.parseInt(cursor.getString(2)), // money field
                cursor.getString(3) // datetime field
        );

        // Set the id of the score (which was retrieved in field 0 of the record)
        sc.setId(cursor.getLong(0));

        return sc;
    }


    // Method to return a list of people
    public List<Score> retrieveAllPeople() {
        // Create a new ArrayList of People objects
        List<Score> scoreList = new ArrayList<Score>();

        // Query the database for all columns
        Cursor cursor = database.query(ScoreSQLiteOpenHelper.TABLE_PEOPLE, allColumns, null, null, null, null, null);

        // Move to the very first record (i.e. 'row') in our result set...
        cursor.moveToFirst();

        // extract every row as a Score object and add to score ArrayList
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
