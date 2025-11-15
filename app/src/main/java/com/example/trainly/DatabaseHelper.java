package com.example.trainly;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "trainly.db";
    public static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // USERS TABLE
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "email TEXT UNIQUE, " +
                "password TEXT, " +
                "age INTEGER, " +
                "height REAL, " +
                "weight REAL, " +
                "role TEXT)");

        // WORKOUT PLANS TABLE
        db.execSQL("CREATE TABLE workout_plans (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "description TEXT, " +
                "trainer_id INTEGER," +
                "FOREIGN KEY(trainer_id) REFERENCES users(id))");

        // EXERCISES TABLE
        db.execSQL("CREATE TABLE exercises (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "plan_id INTEGER, " +
                "name TEXT, " +
                "sets INTEGER, " +
                "reps INTEGER, " +
                "notes TEXT," +
                "FOREIGN KEY(plan_id) REFERENCES workout_plans(id))");

        // ASSIGNED WORKOUTS TABLE
        db.execSQL("CREATE TABLE assigned_workouts (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "trainee_id INTEGER, " +
                "plan_id INTEGER, " +
                "date_assigned TEXT, " +
                "is_completed INTEGER DEFAULT 0," +
                "FOREIGN KEY(trainee_id) REFERENCES users(id)," +
                "FOREIGN KEY(plan_id) REFERENCES workout_plans(id))");

        // WORKOUT HISTORY TABLE
        db.execSQL("CREATE TABLE workout_history (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "trainee_id INTEGER, " +
                "plan_id INTEGER, " +
                "date TEXT, " +
                "calories INTEGER, " +
                "status TEXT," +
                "FOREIGN KEY(trainee_id) REFERENCES users(id)," +
                "FOREIGN KEY(plan_id) REFERENCES workout_plans(id))");

        // MEAL TRACKING TABLE
        db.execSQL("CREATE TABLE meals (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "trainee_id INTEGER, " +
                "meal_name TEXT, " +
                "calories INTEGER, " +
                "date TEXT," +
                "FOREIGN KEY(trainee_id) REFERENCES users(id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS meals");
        db.execSQL("DROP TABLE IF EXISTS workout_history");
        db.execSQL("DROP TABLE IF EXISTS assigned_workouts");
        db.execSQL("DROP TABLE IF EXISTS exercises");
        db.execSQL("DROP TABLE IF EXISTS workout_plans");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    // ====== CREATE USER (SIGNUP) ======s
    public boolean createUser(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if email exists
        Cursor check = db.rawQuery("SELECT id FROM users WHERE email=?", new String[]{email});
        if (check.moveToFirst()) {
            check.close();
            return false; // Email already exists
        }
        check.close();

        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("email", email);
        cv.put("password", password);
        cv.put("role", "client");  // default user role

        long result = db.insert("users", null, cv);
        return result != -1;
    }

    // ====== LOGIN USER ======
    public boolean loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT id FROM users WHERE email=? AND password=?",
                new String[]{email, password}
        );

        boolean success = c.moveToFirst();
        c.close();
        return success;
    }

    // Get user role after login
    public String getUserRole(String email) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT role FROM users WHERE email=?",
                new String[]{email}
        );

        if (c.moveToFirst()) {
            String role = c.getString(0);
            c.close();
            return role;
        }
        c.close();
        return null;
    }
}
