package com.firstzoom.milelog.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Entity(tableName = "tag_table")
public class Tag {
    @PrimaryKey @NonNull
    public String text;


    public Tag(String text) {
        this.text = text;
    }
}
