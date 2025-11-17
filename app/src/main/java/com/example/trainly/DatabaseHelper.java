package com.example.trainly;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

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

        // TRAINER REQUESTS TABLE
        db.execSQL("CREATE TABLE trainer_requests (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "trainee_id INTEGER, " +
                "trainer_id INTEGER, " +
                "status TEXT DEFAULT 'pending', " +
                "reason TEXT, " +
                "date_sent TEXT, " +
                "date_updated TEXT, " +
                "FOREIGN KEY(trainee_id) REFERENCES users(id), " +
                "FOREIGN KEY(trainer_id) REFERENCES users(id))");

        // TRAINER–TRAINEE RELATION TABLE
        db.execSQL("CREATE TABLE trainer_trainee (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "trainer_id INTEGER, " +
                "trainee_id INTEGER, " +
                "FOREIGN KEY(trainer_id) REFERENCES users(id), " +
                "FOREIGN KEY(trainee_id) REFERENCES users(id))");


        // DEFAULT TRAINER ACCOUNT
        db.execSQL("INSERT INTO users (name, email, password, age, height, weight, role) " +
                "VALUES ('Coach Mike', 'trainer@trainly.com', '123456', 30, 175, 75, 'trainer')");
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
    public boolean createUser(String name, String email, String password,
                              int age, double height, double weight) {

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor check = db.rawQuery("SELECT id FROM users WHERE email=?", new String[]{email});
        if (check.moveToFirst()) {
            check.close();
            return false;
        }
        check.close();

        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("email", email);
        cv.put("password", password);
        cv.put("age", age);
        cv.put("height", height);
        cv.put("weight", weight);
        cv.put("role", "client");

        return db.insert("users", null, cv) != -1;
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

    // Get user name after login
    public String getUserNameByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT name FROM users WHERE email=?", new String[]{email});
        if (c.moveToFirst()) {
            String name = c.getString(0);
            c.close();
            return name;
        }
        c.close();
        return null;
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

    // ====== GET PASSWORD BY EMAIL (FORGOT PASSWORD) ======
    public String getPasswordByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT password FROM users WHERE email=?",
                new String[]{email}
        );

        if (c.moveToFirst()) {
            String pass = c.getString(0);
            c.close();
            return pass;
        }

        c.close();
        return null;
    }

    // Trainee send request to Trainer
    public boolean sendTrainerRequest(int traineeId, int trainerId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if pending request already exists
        Cursor c = db.rawQuery(
                "SELECT id FROM trainer_requests WHERE trainee_id=? AND trainer_id=? AND status='pending'",
                new String[]{String.valueOf(traineeId), String.valueOf(trainerId)}
        );

        if (c.moveToFirst()) {
            c.close();
            return false; // Already sent
        }
        c.close();

        ContentValues cv = new ContentValues();
        cv.put("trainee_id", traineeId);
        cv.put("trainer_id", trainerId);
        cv.put("date_sent", String.valueOf(System.currentTimeMillis()));

        return db.insert("trainer_requests", null, cv) != -1;
    }

    // Trainer get request pending list
    public Cursor getPendingTrainerRequests(int trainerId) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT r.id, r.trainee_id, u.name, u.email, u.age, u.height, u.weight " +
                        "FROM trainer_requests r " +
                        "JOIN users u ON r.trainee_id = u.id " +
                        "WHERE r.trainer_id=? AND r.status='pending'",
                new String[]{String.valueOf(trainerId)}
        );
    }

    // Trainer ACCEPT trainee
    public boolean acceptTrainerRequest(int requestId, int trainerId, int traineeId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Update request status
        ContentValues cv = new ContentValues();
        cv.put("status", "accepted");
        cv.put("date_updated", String.valueOf(System.currentTimeMillis()));
        db.update("trainer_requests", cv, "id=?", new String[]{String.valueOf(requestId)});

        // Add to trainer_trainee relation
        ContentValues relation = new ContentValues();
        relation.put("trainer_id", trainerId);
        relation.put("trainee_id", traineeId);

        return db.insert("trainer_trainee", null, relation) != -1;
    }

    // Trainer REJECT trainee (ith reason)
    public void rejectTrainerRequest(int requestId, String reason) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("status", "rejected");
        cv.put("reason", reason);
        cv.put("date_updated", String.valueOf(System.currentTimeMillis()));

        db.update("trainer_requests", cv, "id=?", new String[]{String.valueOf(requestId)});
    }

    // Get trainee list trainer accepted
    public Cursor getTrainerTrainees(int trainerId) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT u.id, u.name, u.email, u.age, u.height, u.weight " +
                        "FROM trainer_trainee tt " +
                        "JOIN users u ON tt.trainee_id = u.id " +
                        "WHERE tt.trainer_id=?",
                new String[]{String.valueOf(trainerId)}
        );
    }

    // Trainee check request status
    public Cursor getTraineeRequests(int traineeId) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT r.id, u.name AS trainer_name, r.status, r.reason " +
                        "FROM trainer_requests r " +
                        "JOIN users u ON r.trainer_id = u.id " +
                        "WHERE r.trainee_id=?",
                new String[]{String.valueOf(traineeId)}
        );
    }

    // Check if trainee HAS trainer or NOT
    public boolean hasTrainer(int traineeId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT id FROM trainer_trainee WHERE trainee_id=?",
                new String[]{String.valueOf(traineeId)}
        );

        boolean exists = c.moveToFirst();
        c.close();
        return exists;
    }

    // Get current trainer of trainee
    public Cursor getCurrentTrainer(int traineeId) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT u.id, u.name, u.email FROM trainer_trainee tt " +
                        "JOIN users u ON tt.trainer_id = u.id " +
                        "WHERE tt.trainee_id=?",
                new String[]{String.valueOf(traineeId)}
        );
    }

    // ====== CREATE WORKOUT PLAN ======
    public long createWorkoutPlan(int trainerId, String title, String description) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("description", description);
        cv.put("trainer_id", trainerId);

        return db.insert("workout_plans", null, cv);
    }

    // ====== GET WORKOUT PLANS BY TRAINER ======
    public Cursor getWorkoutPlansByTrainer(int trainerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT id, title FROM workout_plans WHERE trainer_id=?",
                new String[]{String.valueOf(trainerId)}
        );
    }

    // ====== GET ALL CLIENTS (TRAINEES) ======
    public Cursor getAllClients() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT id, name, email FROM users WHERE role='client'",
                null
        );
    }

    // ====== ASSIGN WORKOUT TO TRAINEE ======
    public long assignWorkout(int traineeId, int planId, String dateAssigned) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("trainee_id", traineeId);
        cv.put("plan_id", planId);
        cv.put("date_assigned", dateAssigned);
        cv.put("is_completed", 0);

        return db.insert("assigned_workouts", null, cv);
    }

    // ====== GET TRAINEES OF A TRAINER (FOR PROGRESS / REMINDER) ======
    public Cursor getTraineesForTrainer(int trainerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT DISTINCT u.id, u.name, u.email " +
                        "FROM users u " +
                        "JOIN assigned_workouts aw ON aw.trainee_id = u.id " +
                        "JOIN workout_plans p ON aw.plan_id = p.id " +
                        "WHERE p.trainer_id = ?",
                new String[]{String.valueOf(trainerId)}
        );
    }

    // ====== GET PROGRESS STATS PER TRAINEE ======
    public Cursor getTraineeProgressForTrainer(int trainerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT u.id, u.name, " +
                        "COUNT(aw.id) AS total_assigned, " +
                        "SUM(CASE WHEN aw.is_completed = 1 THEN 1 ELSE 0 END) AS completed " +
                        "FROM users u " +
                        "JOIN assigned_workouts aw ON aw.trainee_id = u.id " +
                        "JOIN workout_plans p ON aw.plan_id = p.id " +
                        "WHERE p.trainer_id = ? " +
                        "GROUP BY u.id, u.name",
                new String[]{String.valueOf(trainerId)}
        );
    }

    // Get total number workouts finished
    public int getCompletedWorkouts(int traineeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT COUNT(*) FROM workout_history WHERE trainee_id=? AND status='completed'",
                new String[]{String.valueOf(traineeId)}
        );

        if (c.moveToFirst()) {
            int result = c.getInt(0);
            c.close();
            return result;
        }

        c.close();
        return 0;
    }

    // Get total calories burned
    public int getTotalCalories(int traineeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT SUM(calories) FROM workout_history WHERE trainee_id=?",
                new String[]{String.valueOf(traineeId)}
        );

        if (c.moveToFirst()) {
            int result = c.getInt(0);
            c.close();
            return result;
        }

        c.close();
        return 0;
    }

    // Get user weight
    public double getUserWeightByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT weight FROM users WHERE email=?",
                new String[]{email}
        );

        if (c.moveToFirst()) {
            double weight = c.getDouble(0);
            c.close();
            return weight;
        }

        c.close();
        return 0.0;
    }

    // Get userId from email
    public int getUserIdByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT id FROM users WHERE email=?",
                new String[]{email}
        );

        if (c.moveToFirst()) {
            int id = c.getInt(0);
            c.close();
            return id;
        }

        c.close();
        return -1;
    }

    // ====== GET ASSIGNED WORKOUTS FOR CLIENT ======
    public Cursor getAssignedWorkouts(int traineeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT aw.id, p.title, p.description, aw.is_completed " +
                        "FROM assigned_workouts aw " +
                        "JOIN workout_plans p ON aw.plan_id = p.id " +
                        "WHERE aw.trainee_id=?",
                new String[]{String.valueOf(traineeId)}
        );
    }

    // Get assign workout to client
    public int getAssignedWorkoutsCount(int traineeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT COUNT(*) FROM assigned_workouts WHERE trainee_id=?",
                new String[]{String.valueOf(traineeId)}
        );

        if (c.moveToFirst()) {
            int result = c.getInt(0);
            c.close();
            return result;
        }
        c.close();
        return 0;
    }

    // Insert client workout history
    public void insertWorkoutHistory(int traineeId, int planId, int calories, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("trainee_id", traineeId);
        cv.put("plan_id", planId);
        cv.put("date", String.valueOf(System.currentTimeMillis()));
        cv.put("calories", calories);
        cv.put("status", status);

        db.insert("workout_history", null, cv);
    }

    // Calculate weekly stats
    public Cursor getWeeklyHistory(int traineeId) {
        long sevenDaysAgo = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000);

        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT date, calories, status FROM workout_history " +
                        "WHERE trainee_id=? AND date >= ?",
                new String[]{ String.valueOf(traineeId), String.valueOf(sevenDaysAgo) }
        );
    }

    // ===== INSERT MEAL =====
    public boolean insertMeal(int traineeId, String mealName, int calories) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("trainee_id", traineeId);
        cv.put("meal_name", mealName);
        cv.put("calories", calories);
        cv.put("date", String.valueOf(System.currentTimeMillis()));

        return db.insert("meals", null, cv) != -1;
    }

    // ===== GET TODAY MEALS =====
    public Cursor getMealsToday(int traineeId) {
        long dayStart = System.currentTimeMillis() - (24L * 60 * 60 * 1000);

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT meal_name, calories FROM meals WHERE trainee_id=? AND date>=?",
                new String[]{String.valueOf(traineeId), String.valueOf(dayStart)}
        );
    }

    // ===== GET TODAY CALORIES TOTAL =====
    public int getTotalCaloriesToday(int traineeId) {
        long dayStart = System.currentTimeMillis() - (24L * 60 * 60 * 1000);

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT SUM(calories) FROM meals WHERE trainee_id=? AND date>=?",
                new String[]{String.valueOf(traineeId), String.valueOf(dayStart)}
        );

        if (c.moveToFirst()) {
            int total = c.getInt(0);
            c.close();
            return total;
        }

        c.close();
        return 0;
    }

    // ===== GET WEEKLY CALORIES (7 days) =====
    public ArrayList<Integer> getWeeklyCalories(int traineeId) {

        long now = System.currentTimeMillis();
        long sevenDaysAgo = now - (7L * 24 * 60 * 60 * 1000);

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT date, calories FROM meals WHERE trainee_id=? AND date>=?",
                new String[]{ String.valueOf(traineeId), String.valueOf(sevenDaysAgo) }
        );

        ArrayList<Integer> daily = new ArrayList<>();
        for (int i = 0; i < 7; i++) daily.add(0); // default 0 calories 7 days

        long oneDay = 24L * 60 * 60 * 1000;

        while (c.moveToNext()) {
            long mealTime = c.getLong(0);
            int cal = c.getInt(1);

            int dayIndex = (int) ((now - mealTime) / oneDay);

            if (dayIndex >= 0 && dayIndex < 7) {
                int idx = 6 - dayIndex; // đảo cho đúng thứ tự từ cũ → mới
                daily.set(idx, daily.get(idx) + cal);
            }
        }
        c.close();

        return daily;
    }

    // Get list workout base on date
    public Cursor getWorkoutsByDay(int traineeId, long startDay, long endDay) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT p.title, h.calories, h.status, h.date " +
                        "FROM workout_history h " +
                        "JOIN workout_plans p ON h.plan_id = p.id " +
                        "WHERE h.trainee_id=? AND h.date BETWEEN ? AND ?",
                new String[]{
                        String.valueOf(traineeId),
                        String.valueOf(startDay),
                        String.valueOf(endDay)
                }
        );
    }

    // Get trainer
    public Cursor getAllTrainers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT id, name, email FROM users WHERE role='trainer'",
                null
        );
    }

}
