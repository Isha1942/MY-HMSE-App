package com.spark.newapp2;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HistoryDao {

    @Insert
    void insert(CommandHistory commandHistory);

    @Query("SELECT * FROM command_history ORDER BY timestamp DESC")
    List<CommandHistory> getAllHistory();
}
