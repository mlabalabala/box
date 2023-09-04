package com.github.tvbox.osc.bbox.cache;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

/**
 * 类描述:
 *
 * @author pj567
 * @since 2020/5/15
 */
@Entity(tableName = "cache")
public class Cache implements Serializable {
    @PrimaryKey(autoGenerate = false)
    @NonNull
    public String key;
    public byte[] data;
}
