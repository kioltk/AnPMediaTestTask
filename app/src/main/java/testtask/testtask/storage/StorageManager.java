package testtask.testtask.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import testtask.testtask.models.Picture;

import static testtask.testtask.TestTaskApplication.app;

/**
 * Created by Jesus Christ. Amen.
 */
public class StorageManager {
    // We have to store constants cause we on right side of power, right?
    private static final String DATABASE = "storage.db";
    private static final String PICTURES_TABLE = "pictures";
    /**
     *  Columns to get from table when we load pictures.
     * */
    private static final String[] PICTURES_TABLE_COLUMNS = {
            "id",
            "width",
            "height",
            "url",
            "orderIndex"
    };
    /**
     * Table creating query.
     * */
    private static final String PICTURES_TABLE_CREATE_QUERY =
            "CREATE TABLE " +
                    PICTURES_TABLE +
                    " ( " +
                    "id INTEGER PRIMARY KEY NOT NULL," +
                    "width INTEGER NOT NULL," +
                    "height INTEGER NOT NULL," +
                    "url TEXT NOT NULL," +
                    "orderIndex INTEGER DEFAULT 0" +
                    " ) ";
    /**
     * Also we need the lock object because we use the db from work threads so that can be
     * dangerous when we do in the same time a few operations.
     * */
    private static final Object LOCK = new Object();
    private final PicturesDatabaseConnector picturesDatabaseConnector;

    /**
     *  Creates an db and tables.
     *  However we could provide access from static context?
     * */
    public StorageManager() {
        Log.i("AGCY SPY SQL", "Initialization");
        picturesDatabaseConnector = new PicturesDatabaseConnector(app());
        picturesDatabaseConnector.getWritableDatabase();
        picturesDatabaseConnector.close();
    }

    /**
     * Do this in work thread, sometimes it takes a lot of time.
     * Clears db and write another pictures by their order in ArrayList.
     */
    public void clearAndStore(ArrayList<Picture> pictures) {
        synchronized (LOCK) {
            SQLiteDatabase database = picturesDatabaseConnector.getWritableDatabase();
            database.delete(PICTURES_TABLE, null, null);
            ContentValues pictureValues = new ContentValues();
            for (int i = 0; i < pictures.size(); i++) {
                Picture picture = pictures.get(i);
                pictureValues.put("id", picture.id);
                pictureValues.put("height", picture.height);
                pictureValues.put("width", picture.width);
                pictureValues.put("url", picture.url);
                pictureValues.put("orderIndex", i);

                database.insert(PICTURES_TABLE, null, pictureValues);
            }
            database.close();
            picturesDatabaseConnector.close();
        }
    }

    /**
     * Do this in work thread, sometimes it takes a lot of time.
     * Returns pictures from db.
     */
    public ArrayList<Picture> loadPictures() {
        ArrayList<Picture> pictures = new ArrayList<>();
        synchronized (LOCK) {
            SQLiteDatabase database = picturesDatabaseConnector.getWritableDatabase();
            final Cursor cursor = database.query(PICTURES_TABLE, PICTURES_TABLE_COLUMNS, null, null, null, null, "orderIndex");
            if (cursor.moveToFirst()) {
                final int idColumnIndex = cursor.getColumnIndex("id");
                final int widthColumnIndex = cursor.getColumnIndex("width");
                final int heightColumnIndex = cursor.getColumnIndex("height");
                final int urlColumnIndex = cursor.getColumnIndex("url");

                do {
                    pictures.add(new Picture() {{
                        id = cursor.getInt(idColumnIndex);
                        width = cursor.getInt(widthColumnIndex);
                        height = cursor.getInt(heightColumnIndex);
                        url = cursor.getString(urlColumnIndex);
                    }});
                } while (cursor.moveToNext());

            }
            cursor.close();
            database.close();
            picturesDatabaseConnector.close();
        }
        return pictures;
    }

    /**
     * Database connector manages creating and updating the databases and tables.
     */
    private static class PicturesDatabaseConnector extends SQLiteOpenHelper {

        public PicturesDatabaseConnector(Context context) {
            super(context, DATABASE, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase database) {
            database.execSQL(PICTURES_TABLE_CREATE_QUERY);
        }

        @Override
        public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        }

    }
}
