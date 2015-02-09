package simararora.puautologin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Simar Arora on 2/4/2015.
 *
 */
public class UserDatabase {
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_PASSWORD = "password";

    private static final String DATABASE_NAME = "userDatabase";
    private static final String TABLE_NAME = "userTable";

    private static final int DATABASE_VERSION = 1;

    private final Context context;
    private UserDatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public UserDatabase(Context context) {
        this.context = context;
    }

    private class UserDatabaseHelper extends SQLiteOpenHelper {

        public UserDatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME + "(" + KEY_USER_NAME + " TEXT NOT NULL," + KEY_PASSWORD + " TEXT NOT NULL," + "PRIMARY KEY(" + KEY_USER_NAME + "));");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS" + DATABASE_NAME);
            onCreate(db);
        }
    }

    public UserDatabase open() {
        databaseHelper = new UserDatabaseHelper(context);
        database = databaseHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        databaseHelper.close();
    }

    public void addUser(String userName, String password) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_USER_NAME, userName);
        contentValues.put(KEY_PASSWORD, password);
        database.insert(TABLE_NAME, null, contentValues);
    }

    public void deleteUser(String userName) {
        userName = "'" + userName + "'";
        database.delete(TABLE_NAME, KEY_USER_NAME + " = " + userName, null);
    }

    public void editUser(String oldUSerName, String newUserName, String newPassword) {
        deleteUser(oldUSerName);
        addUser(newUserName, newPassword);
    }

    public String getPasswordFromUserName(String userName) {
        String[] columns = {KEY_USER_NAME, KEY_PASSWORD};
        Cursor cursor = database.query(TABLE_NAME, columns, null, null, null, null, null);
        int indexOfUserName, indexOfPassword;
        indexOfUserName = cursor.getColumnIndex(KEY_USER_NAME);
        indexOfPassword = cursor.getColumnIndex(KEY_PASSWORD);
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
           if(cursor.getString(indexOfUserName).equals(userName))
               return cursor.getString(indexOfPassword);
        }
        return null;
    }

    public ArrayList<String> getAllUsers() {
        String[] columns = {KEY_USER_NAME};
        Cursor cursor = database.query(TABLE_NAME, columns, null, null, null, null, null);
        int indexOfUserName = cursor.getColumnIndex(KEY_USER_NAME);
        ArrayList<String> users = new ArrayList<>();
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            users.add(cursor.getString(indexOfUserName));
        }
        return users;
    }
}
