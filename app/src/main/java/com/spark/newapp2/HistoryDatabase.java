package com.spark.newapp2;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {CommandHistory.class}, version = 2, exportSchema = false)
public abstract class HistoryDatabase extends RoomDatabase {

    private static HistoryDatabase instance;

    public abstract HistoryDao historyDao();

    public static synchronized HistoryDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            HistoryDatabase.class, "history_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
