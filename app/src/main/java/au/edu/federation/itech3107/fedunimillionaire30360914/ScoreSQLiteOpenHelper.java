package au.edu.federation.itech3107.fedunimillionaire30360914;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class ScoreSQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = ScoreSQLiteOpenHelper.class.getSimpleName();

    // Declare database name and version number
    private static final String DATABASE_NAME = "score.db";
    private static final int DATABASE_VERSION = 1;

    // Define the "score" table
    public static final String TABLE_PEOPLE = "score";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_MONEY = "money";
    public static final String COLUMN_DATETIME = "datetime";

    // SQL for table creation
    private static final String DATABASE_CREATE = "CREATE TABLE "
            + TABLE_PEOPLE + "("
            + COLUMN_ID + " integer PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NAME + " text NOT NULL, "
            + COLUMN_MONEY + " integer NOT NULL, "
            + COLUMN_DATETIME + " text NOT NULL "
            + ");";


    public ScoreSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Our overridden onCreate() method will create the database
    // using our DATABASE_CREATE SQLite statement.
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
        Log.d(LOG_TAG, "[DATABASE] Created database");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the table before creating a new one
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PEOPLE);

        // Recreate the database
        onCreate(db);

        Log.d(LOG_TAG, "[DATABASE] Upgraded database");
    }
}