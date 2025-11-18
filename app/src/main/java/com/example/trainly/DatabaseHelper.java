package com.example.trainly;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "trainly.db";
    public static final int DB_VERSION = 5;

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
                "initiated_by TEXT DEFAULT 'trainee', " +
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

        // NOTIFICATIONS TABLE
        db.execSQL("CREATE TABLE notifications (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +              // Who gonna receive the notification
                "message TEXT, " +
                "timestamp TEXT, " +
                "is_read INTEGER DEFAULT 0, " +
                "FOREIGN KEY(user_id) REFERENCES users(id))");


        // DEFAULT TRAINER ACCOUNT
        db.execSQL("INSERT INTO users (name, email, password, age, height, weight, role) " +
                "VALUES ('Coach Mike', 'trainer@trainly.com', '123456', 30, 175, 75, 'trainer')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Migration from version 4 to 5: Add 'initiated_by' column
        if (oldVersion < 5) {
            db.execSQL("ALTER TABLE trainer_requests ADD COLUMN initiated_by TEXT DEFAULT 'trainee'");
        }

        // For other upgrades, drop and recreate (development only)
        // In production, you should handle each version upgrade carefully
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

        // Get trainee name for notification message
        String traineeName = "A trainee";
        Cursor nameC = db.rawQuery(
                "SELECT name FROM users WHERE id=?",
                new String[]{String.valueOf(traineeId)}
        );
        if (nameC.moveToFirst()) {
            traineeName = nameC.getString(0);
        }
        nameC.close();

        ContentValues cv = new ContentValues();
        cv.put("trainee_id", traineeId);
        cv.put("trainer_id", trainerId);
        cv.put("initiated_by", "trainee");
        cv.put("date_sent", String.valueOf(System.currentTimeMillis()));

        long result = db.insert("trainer_requests", null, cv);

        if (result != -1) {
            // Send notification to trainer
            insertNotification(trainerId, traineeName + " has sent you a trainer request!");
        }

        return result != -1;
    }

    // Trainer send invitation to Trainee
    public boolean trainerInviteTrainee(int trainerId, int traineeId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if pending invitation already exists
        Cursor c = db.rawQuery(
                "SELECT id FROM trainer_requests WHERE trainee_id=? AND trainer_id=? AND status='pending' AND initiated_by='trainer'",
                new String[]{String.valueOf(traineeId), String.valueOf(trainerId)}
        );

        if (c.moveToFirst()) {
            c.close();
            return false; // Already sent invitation
        }
        c.close();

        ContentValues cv = new ContentValues();
        cv.put("trainee_id", traineeId);
        cv.put("trainer_id", trainerId);
        cv.put("initiated_by", "trainer");
        cv.put("date_sent", String.valueOf(System.currentTimeMillis()));

        long result = db.insert("trainer_requests", null, cv);

        if (result != -1) {
            // Send notification to trainee
            insertNotification(traineeId, "You have a new invitation from a trainer!");
        }

        return result != -1;
    }

    // Trainer get request pending list (trainee -> trainer)
    public Cursor getPendingTrainerRequests(int trainerId) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT r.id, r.trainee_id, u.name, u.email, u.age, u.height, u.weight " +
                        "FROM trainer_requests r " +
                        "JOIN users u ON r.trainee_id = u.id " +
                        "WHERE r.trainer_id=? AND r.status='pending' AND r.initiated_by='trainee'",
                new String[]{String.valueOf(trainerId)}
        );
    }

    // Trainee get pending invitations from trainers (trainer -> trainee)
    public Cursor getPendingTrainerInvitations(int traineeId) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT r.id, r.trainer_id, u.name, u.email " +
                        "FROM trainer_requests r " +
                        "JOIN users u ON r.trainer_id = u.id " +
                        "WHERE r.trainee_id=? AND r.status='pending' AND r.initiated_by='trainer'",
                new String[]{String.valueOf(traineeId)}
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
        insertNotification(traineeId, "Your trainer request has been accepted!");

        // Add to trainer_trainee relation
        ContentValues relation = new ContentValues();
        relation.put("trainer_id", trainerId);
        relation.put("trainee_id", traineeId);

        return db.insert("trainer_trainee", null, relation) != -1;
    }

    // Trainer REJECT trainee request (with reason)
    public void rejectTrainerRequest(int requestId, int traineeId, String reason) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("status", "rejected");
        cv.put("reason", reason);
        cv.put("date_updated", String.valueOf(System.currentTimeMillis()));
        insertNotification(traineeId, "Your trainer request was rejected: " + reason);

        db.update("trainer_requests", cv, "id=?", new String[]{String.valueOf(requestId)});
    }

    // Trainee ACCEPT trainer invitation
    public boolean acceptTrainerInvitation(int requestId, int trainerId, int traineeId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Update invitation status
        ContentValues cv = new ContentValues();
        cv.put("status", "accepted");
        cv.put("date_updated", String.valueOf(System.currentTimeMillis()));
        db.update("trainer_requests", cv, "id=?", new String[]{String.valueOf(requestId)});
        insertNotification(trainerId, "Your trainer invitation has been accepted!");

        // Add to trainer_trainee relation
        ContentValues relation = new ContentValues();
        relation.put("trainer_id", trainerId);
        relation.put("trainee_id", traineeId);

        return db.insert("trainer_trainee", null, relation) != -1;
    }

    // Trainee REJECT trainer invitation
    public void rejectTrainerInvitation(int requestId, int trainerId, String reason) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("status", "rejected");
        cv.put("reason", reason);
        cv.put("date_updated", String.valueOf(System.currentTimeMillis()));
        insertNotification(trainerId, "Your trainer invitation was declined: " + reason);

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
                "SELECT u.id AS trainer_id, " +
                        "u.name AS trainer_name, " +
                        "u.email AS trainer_email " +
                        "FROM trainer_trainee tt " +
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
                "SELECT aw.id, p.title, p.description, aw.is_completed, aw.plan_id " +
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
                "SELECT h.date, h.calories, h.status, p.title, " +
                        "GROUP_CONCAT(e.name, ', ') as exercise_names " +
                        "FROM workout_history h " +
                        "JOIN workout_plans p ON h.plan_id = p.id " +
                        "LEFT JOIN exercises e ON e.plan_id = p.id " +
                        "WHERE h.trainee_id=? AND h.date >= ? " +
                        "GROUP BY h.id, h.date, h.calories, h.status, p.title " +
                        "ORDER BY h.date DESC",
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

    // Unassign: trainee removes current trainer
    public boolean unassignTrainer(int traineeId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Get trainer first
        Cursor c = db.rawQuery(
                "SELECT trainer_id FROM trainer_trainee WHERE trainee_id=?",
                new String[]{String.valueOf(traineeId)}
        );

        if (!c.moveToFirst()) {
            c.close();
            return false; // no trainer to unassign
        }

        int trainerId = c.getInt(0);
        c.close();

        // 1. Delete trainer_trainee relation
        db.delete("trainer_trainee", "trainee_id=?", new String[]{String.valueOf(traineeId)});
        insertNotification(trainerId, "A trainee has unassigned from you.");

        // 2. Mark previous requests as cancelled
        ContentValues cv = new ContentValues();
        cv.put("status", "cancelled");
        cv.put("date_updated", String.valueOf(System.currentTimeMillis()));

        db.update("trainer_requests", cv,
                "trainee_id=? AND trainer_id=? AND status='accepted'",
                new String[]{
                        String.valueOf(traineeId),
                        String.valueOf(trainerId)
                });

        return true;
    }

    public void insertNotification(int userId, String message) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("user_id", userId);
        cv.put("message", message);
        cv.put("timestamp", String.valueOf(System.currentTimeMillis()));
        cv.put("is_read", 0);

        db.insert("notifications", null, cv);
    }

    public Cursor getNotifications(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT id, message, timestamp, is_read " +
                        "FROM notifications WHERE user_id=? ORDER BY timestamp DESC",
                new String[]{String.valueOf(userId)}
        );
    }

    // ====== GET UNREAD NOTIFICATION COUNT ======
    public int getUnreadNotificationCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT COUNT(*) FROM notifications WHERE user_id=? AND is_read=0",
                new String[]{String.valueOf(userId)}
        );

        if (c.moveToFirst()) {
            int count = c.getInt(0);
            c.close();
            return count;
        }
        c.close();
        return 0;
    }

    // ====== MARK NOTIFICATION AS READ ======
    public void markNotificationAsRead(int notificationId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("is_read", 1);
        db.update("notifications", cv, "id=?", new String[]{String.valueOf(notificationId)});
    }

    // ====== MARK ALL NOTIFICATIONS AS READ ======
    public void markAllNotificationsAsRead(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("is_read", 1);
        db.update("notifications", cv, "user_id=?", new String[]{String.valueOf(userId)});
    }

    // ====== GET PENDING REQUESTS COUNT ======
    public int getPendingRequestsCount(int trainerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT COUNT(*) FROM trainer_requests WHERE trainer_id=? AND status='pending'",
                new String[]{String.valueOf(trainerId)}
        );

        if (c.moveToFirst()) {
            int count = c.getInt(0);
            c.close();
            return count;
        }
        c.close();
        return 0;
    }

    // ====== DELETE/REMOVE TRAINEE FROM TRAINER ======
    public boolean removeTrainee(int trainerId, int traineeId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Delete trainer_trainee relation
        int deleted = db.delete("trainer_trainee",
                "trainer_id=? AND trainee_id=?",
                new String[]{String.valueOf(trainerId), String.valueOf(traineeId)});

        if (deleted > 0) {
            // Notify trainee
            insertNotification(traineeId, "Your trainer has removed you from their client list.");

            // Update trainer_requests to cancelled
            ContentValues cv = new ContentValues();
            cv.put("status", "cancelled");
            cv.put("date_updated", String.valueOf(System.currentTimeMillis()));
            db.update("trainer_requests", cv,
                    "trainer_id=? AND trainee_id=? AND status='accepted'",
                    new String[]{String.valueOf(trainerId), String.valueOf(traineeId)});

            return true;
        }
        return false;
    }

    // ====== GET TRAINEE DETAIL BY ID ======
    public Cursor getTraineeDetailById(int traineeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT id, name, email, age, height, weight FROM users WHERE id=? AND role='client'",
                new String[]{String.valueOf(traineeId)}
        );
    }

    // ====== GET TRAINEE WORKOUT STATS ======
    public Cursor getTraineeWorkoutStats(int traineeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT " +
                        "COUNT(*) as total_workouts, " +
                        "SUM(CASE WHEN status='completed' THEN 1 ELSE 0 END) as completed, " +
                        "SUM(CASE WHEN status='failed' THEN 1 ELSE 0 END) as failed, " +
                        "SUM(calories) as total_calories " +
                        "FROM workout_history WHERE trainee_id=?",
                new String[]{String.valueOf(traineeId)}
        );
    }

    // ====== GET TRAINEE COUNT FOR TRAINER ======
    public int getTraineeCount(int trainerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT COUNT(*) FROM trainer_trainee WHERE trainer_id=?",
                new String[]{String.valueOf(trainerId)}
        );

        if (c.moveToFirst()) {
            int count = c.getInt(0);
            c.close();
            return count;
        }
        c.close();
        return 0;
    }

    // ====== GET EXERCISES BY PLAN ID ======
    public Cursor getExercisesByPlanId(int planId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT id, name, sets, reps, notes FROM exercises WHERE plan_id=?",
                new String[]{String.valueOf(planId)}
        );
    }

    // ====== MARK WORKOUT AS COMPLETED ======
    public boolean markWorkoutAsCompleted(int assignedWorkoutId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("is_completed", 1);

        int rowsAffected = db.update("assigned_workouts", cv, "id=?",
                new String[]{String.valueOf(assignedWorkoutId)});

        return rowsAffected > 0;
    }

    // ====== GET PLAN ID FROM ASSIGNED WORKOUT ======
    public int getPlanIdFromAssignedWorkout(int assignedWorkoutId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT plan_id FROM assigned_workouts WHERE id=?",
                new String[]{String.valueOf(assignedWorkoutId)}
        );

        if (c.moveToFirst()) {
            int planId = c.getInt(0);
            c.close();
            return planId;
        }
        c.close();
        return -1;
    }

    // ====== GET TODAY'S WORKOUTS ======
    public Cursor getTodayWorkouts(int traineeId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Get workouts assigned today OR not completed yet
        return db.rawQuery(
                "SELECT aw.id, p.id, p.title, p.description, aw.is_completed, " +
                        "(SELECT COUNT(*) FROM exercises WHERE plan_id = p.id) as exercise_count " +
                        "FROM assigned_workouts aw " +
                        "JOIN workout_plans p ON aw.plan_id = p.id " +
                        "WHERE aw.trainee_id=? AND aw.is_completed=0 " +
                        "ORDER BY aw.date_assigned DESC " +
                        "LIMIT 5",
                new String[]{String.valueOf(traineeId)}
        );
    }

    // ====== GET COMPLETED ASSIGNED WORKOUTS COUNT ======
    public int getCompletedAssignedWorkoutsCount(int traineeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT COUNT(*) FROM assigned_workouts WHERE trainee_id=? AND is_completed=1",
                new String[]{String.valueOf(traineeId)}
        );

        int count = 0;
        if (c.moveToFirst()) {
            count = c.getInt(0);
        }
        c.close();
        return count;
    }

    // ====== GET MEALS TODAY WITH ID ======
    public Cursor getMealsTodayWithId(int traineeId) {
        long dayStart = System.currentTimeMillis() - (24L * 60 * 60 * 1000);

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT id, meal_name, calories FROM meals WHERE trainee_id=? AND date>=?",
                new String[]{String.valueOf(traineeId), String.valueOf(dayStart)}
        );
    }

    // ====== DELETE MEAL BY ID ======
    public boolean deleteMeal(int mealId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deleted = db.delete("meals", "id=?", new String[]{String.valueOf(mealId)});
        return deleted > 0;
    }

    // ====== UPDATE PASSWORD ======

    public boolean updatePassword(String email, String oldPassword, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();

        // First, verify old password is correct
        Cursor c = db.rawQuery(
                "SELECT id FROM users WHERE email=? AND password=?",
                new String[]{email, oldPassword}
        );

        if (!c.moveToFirst()) {
            c.close();
            return false; // Old password is incorrect
        }
        c.close();

        // Update to new password
        ContentValues cv = new ContentValues();
        cv.put("password", newPassword);

        int rowsAffected = db.update("users", cv, "email=?", new String[]{email});
        return rowsAffected > 0;
    }
}