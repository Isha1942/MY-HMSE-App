package com.spark.newapp2;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "command_history")
public class CommandHistory {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String command;
    private long timestamp;

    public CommandHistory(String command, long timestamp) {
        this.command = command;
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCommand() { return command; }
    public void setCommand(String command) { this.command = command; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
