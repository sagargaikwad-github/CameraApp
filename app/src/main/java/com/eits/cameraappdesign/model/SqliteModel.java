package com.eits.cameraappdesign.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class SqliteModel extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "CameraApp.db";
    public static final int DATABASE_VERSION = 1;

    public SqliteModel(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String facility_table = "create table Facility(facId Integer Primary key Autoincrement, facName text)";
        sqLiteDatabase.execSQL(facility_table);

        String component_table = "create table Component(compId Integer Primary key, compName text)";
        sqLiteDatabase.execSQL(component_table);

        String video_table = "create table VideoFile(fileName text," +
                "fileDateTime text,compID Integer,facID Integer,fileSiteLocation String,fileMin float,fileMax float," +
                "fileAverage float,filePath text,fileDuration text,fileNote text)";
        sqLiteDatabase.execSQL(video_table);

        sqLiteDatabase.execSQL("insert into Component Values(0,'None')");
        sqLiteDatabase.execSQL("insert into Component Values(101,'Flange')");
        sqLiteDatabase.execSQL("insert into Component Values(102,'Valve')");
        sqLiteDatabase.execSQL("insert into Component Values(103,'Valve-Check')");
        sqLiteDatabase.execSQL("insert into Component Values(104,'Pump')");
        sqLiteDatabase.execSQL("insert into Component Values(105,'Compressor')");
        sqLiteDatabase.execSQL("insert into Component Values(106,'Valve-T')");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    //Retrieve Component Data
    public ArrayList<ComponentModel> getComponentData() {
        ArrayList<ComponentModel> arrayList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("select * from Component", null);
        if (cursor.moveToFirst()) {
            do {
                int compId = cursor.getInt(0);
                String compName = cursor.getString(1);

                arrayList.add(new ComponentModel(compId, compName));
            } while (cursor.moveToNext());
        } else {
        }
        return arrayList;
    }

    //Add data in Facility
    public boolean addInFacility(String facilityText) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("facName", facilityText);

        long res = sqLiteDatabase.insert("Facility", null, cv);

        if (res == -1) {
            return false;
        } else {
            return true;
        }

    }

    //Retrieve Facility Data
    public ArrayList<FacilityModel> getFacilityData() {
        ArrayList<FacilityModel> arrayList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("select * from Facility ORDER BY FacID desc", null);
        if (cursor.moveToFirst()) {
            do {
                int facId = cursor.getInt(0);
                String facName = cursor.getString(1);

                arrayList.add(new FacilityModel(facId, facName));
            } while (cursor.moveToNext());
        } else {

        }
        return arrayList;
    }


    public boolean addInVideoFile(String fileName,
                                  String fileDateTime,
                                  Integer compID, Integer facID,
                                  String fileSiteLocation,
                                  float fileMin, float fileMax, float fileAverage,
                                  String filePath, String fileDuration, String fileNote) {


        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("fileName", fileName);
        cv.put("fileDateTime", fileDateTime);
        cv.put("compID", compID);
        cv.put("facID", facID);
        cv.put("fileSiteLocation", fileSiteLocation);
        cv.put("fileMin", fileMin);
        cv.put("fileMax", fileMax);
        cv.put("fileAverage", fileAverage);
        cv.put("filePath", filePath);
        cv.put("fileDuration", fileDuration);
        cv.put("fileNote", fileNote);

        long res = sqLiteDatabase.insert("VideoFile", null, cv);
        if (res == -1) {
            return false;
        } else {
            return true;
        }

    }

    public ArrayList<FileModel> getVideoFileList() {
        ArrayList<FileModel> arrayList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("select * from VideoFile", null);
        if (cursor.moveToFirst()) {
            do {
                String fileName = cursor.getString(0);
                String fileDateTime = cursor.getString(1);
                int compID = cursor.getInt(2);
                int facID = cursor.getInt(3);
                String fileSiteLocation = cursor.getString(4);
                float fileMin = cursor.getFloat(5);
                float fileMax = cursor.getFloat(6);
                float fileAverage = cursor.getFloat(7);
                String filePath = cursor.getString(8);
                String fileDuration = cursor.getString(9);
                String fileNote = cursor.getString(10);

                arrayList.add(new FileModel(fileName,fileDateTime,compID,facID,fileSiteLocation,
                        fileMax,fileMin,fileAverage,filePath,fileDuration,fileNote));
            } while (cursor.moveToNext());
        } else {
        }
        return arrayList;
    }


    public String getFacilityName(int facID) {
        String FacilityName=null;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("select facName from Facility where facID=?",new String[]{String.valueOf(facID)});
        if (cursor.moveToFirst()) {
            do {

                 FacilityName = cursor.getString(0);

            } while (cursor.moveToNext());
        } else {

        }
        return FacilityName;
    }

    public String getComponentName(int compID) {
        String ComponentName=null;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("select compName from Component where compID=?",new String[]{String.valueOf(compID)});
        if (cursor.moveToFirst()) {
            do {

                ComponentName = cursor.getString(0);

            } while (cursor.moveToNext());
        } else {

        }
        return ComponentName;
    }
}
