package com.example.walkinghadang

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, "user.db", null, 2) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS ALARM_TB (
                _id INTEGER PRIMARY KEY AUTOINCREMENT,
                hour INTEGER,
                minute INTEGER
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 5) {
//            db.execSQL("DROP TABLE IF EXISTS ALARM_TB")
            db.execSQL("""
                CREATE TABLE ALARM_TB (
                    _id INTEGER PRIMARY KEY AUTOINCREMENT,
                    hour INTEGER,
                    minute INTEGER
                )
            """.trimIndent())
        }
    }
}