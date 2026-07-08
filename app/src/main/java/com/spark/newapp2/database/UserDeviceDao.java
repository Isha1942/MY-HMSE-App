package com.spark.newapp2.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface UserDeviceDao {

    // Insert or replace if conflict (same primary key)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(UserDevice user);

    @Update
    void updateUser(UserDevice user);

    @Delete
    void deleteUser(UserDevice user);

    // Get all users
    @Query("SELECT * FROM user_device")
    List<UserDevice> getAllUsers();

    // Get the last inserted user
    @Query("SELECT * FROM user_device ORDER BY id DESC LIMIT 1")
    UserDevice getLastUser();

    // Optional: get the selected user
    @Query("SELECT * FROM user_device WHERE isSelected = 1 LIMIT 1")
    UserDevice getSelectedUser();

    // Optional: delete all users
    @Query("DELETE FROM user_device")
    void deleteAllUsers();

    // ✅ Get user(s) by name
    @Query("SELECT * FROM user_device WHERE userName = :name")
    List<UserDevice> getUserByName(String name);
}
