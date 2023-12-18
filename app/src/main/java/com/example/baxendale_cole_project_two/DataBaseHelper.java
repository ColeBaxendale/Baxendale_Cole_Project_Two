package com.example.baxendale_cole_project_two;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "your_database_name.db";
    private static final int DATABASE_VERSION = 8;

    private static final String USER_TABLE_NAME = "user";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_PHONE_NUMBER = "phone_number";
    private static final String COLUMN_SMS_CONSENT = "sms_consent";

    private static final String USER_TABLE_CREATE =
            "CREATE TABLE " + USER_TABLE_NAME + " (" +
                    COLUMN_USERNAME + " TEXT PRIMARY KEY, " +
                    COLUMN_PASSWORD + " TEXT, " +
                    COLUMN_PHONE_NUMBER + " TEXT, " +
                    COLUMN_SMS_CONSENT + " INTEGER);"; // 0 for false, 1 for true


    // Inventory table constants
    private static final String INVENTORY_TABLE_NAME = "inventory";
    private static final String COLUMN_ID = "item_id";
    private static final String COLUMN_ITEM_NAME = "itemName";
    private static final String COLUMN_ITEM_DESCRIPTION = "itemDescription";
    private static final String COLUMN_QUANTITY = "quantity";

    // Inventory table creation SQL statement
    private static final String INVENTORY_TABLE_CREATE =
            "CREATE TABLE " + INVENTORY_TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_ITEM_NAME + " TEXT, " +
                    COLUMN_ITEM_DESCRIPTION + " TEXT, " +
                    COLUMN_QUANTITY + " INTEGER);";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the user and inventory tables
        db.execSQL(USER_TABLE_CREATE);
        db.execSQL(INVENTORY_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + INVENTORY_TABLE_NAME);

        // Create new tables
        onCreate(db);
    }

    // User authentication methods

    public void registerUser(String username, String password) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);

        db.insert(USER_TABLE_NAME, null, values);
        db.close();
    }


    public void updateUserDetails(String username, String phoneNumber, boolean smsConsent) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PHONE_NUMBER, phoneNumber);
        values.put(COLUMN_SMS_CONSENT, smsConsent ? 1 : 0);

        String selection = COLUMN_USERNAME + " = ?";
        String[] selectionArgs = { username };

        db.update(USER_TABLE_NAME, values, selection, selectionArgs);
        db.close();
    }

    public void updateUserDetailsFalse(String username, boolean smsConsent) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_SMS_CONSENT, smsConsent ? 1 : 0);

        String selection = COLUMN_USERNAME + " = ?";
        String[] selectionArgs = { username };

        db.update(USER_TABLE_NAME, values, selection, selectionArgs);
        db.close();
    }

    public boolean doesUserExist(String username) {
        SQLiteDatabase db = getReadableDatabase();

        String selection = COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(
                USER_TABLE_NAME, null, selection, selectionArgs, null, null, null);

        boolean userExists = cursor != null && cursor.getCount() > 0;

        if (cursor != null) {
            cursor.close();
        }

        db.close();
        return userExists;
    }

    public boolean loginUser(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();

        String selection = COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};

        Cursor cursor = db.query(
                USER_TABLE_NAME, // Corrected to the user table
                null, selection, selectionArgs, null, null, null);

        boolean isLoggedIn = cursor != null && cursor.getCount() > 0;

        if (cursor != null) {
            cursor.close();
        }

        db.close();
        return isLoggedIn;
    }

    public String getPhoneNumberIfSmsConsentTrue(String username) {
        SQLiteDatabase db = getReadableDatabase();

        String selection = COLUMN_USERNAME + " = ? AND " + COLUMN_SMS_CONSENT + " = ?";
        String[] selectionArgs = {username, "1"}; // Check if COLUMN_SMS_CONSENT is equal to "1"

        Cursor cursor = db.query(
                USER_TABLE_NAME, // The table name for the user
                new String[]{COLUMN_PHONE_NUMBER}, // Select only the phone number column
                selection, // WHERE clause
                selectionArgs, // Values for the WHERE clause
                null, // No GROUP BY
                null, // No HAVING
                null); // No ORDER BY

        String phoneNumber = null;

        if (cursor != null && cursor.moveToFirst()) {
            // Read phone number from the cursor
            phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE_NUMBER));
        }

        if (cursor != null) {
            cursor.close();
        }

        db.close();

        return phoneNumber;
    }




    public boolean addItem(InventoryItem item) {
        SQLiteDatabase db = getWritableDatabase();

        // Check if an item with the same name already exists
        String selection = COLUMN_ITEM_NAME + " = ?";
        String[] selectionArgs = { item.getItemName() };
        Cursor cursor = db.query(INVENTORY_TABLE_NAME, null, selection, selectionArgs, null, null, null);
        boolean itemExists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }

        if (itemExists) {
            // An item with the same name already exists
            db.close();
            return false;
        } else {
            // No item with the same name, proceed to insert
            ContentValues values = new ContentValues();
            values.put(COLUMN_ID, item.getId());
            values.put(COLUMN_ITEM_NAME, item.getItemName());
            values.put(COLUMN_ITEM_DESCRIPTION, item.getItemDescription());
            values.put(COLUMN_QUANTITY, item.getQuantity());
            db.insert(INVENTORY_TABLE_NAME, null, values);
            db.close();
            return true;
        }
    }

    public Map<Long, InventoryItem> getAllItems() {
        Map<Long, InventoryItem> items = new HashMap<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(INVENTORY_TABLE_NAME, new String[]{COLUMN_ID, COLUMN_ITEM_NAME, COLUMN_ITEM_DESCRIPTION, COLUMN_QUANTITY}, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String itemName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_NAME));
                String itemDescription = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_DESCRIPTION));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY));
                items.put(id, new InventoryItem(id, itemName, itemDescription, quantity));
            }
            cursor.close();
        }
        db.close();
        return items;
    }

    public void deleteItem(long id) {
        SQLiteDatabase db = getWritableDatabase();
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };
        db.delete(INVENTORY_TABLE_NAME, selection, selectionArgs);
        db.close();
    }

    public void updateItem(InventoryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_NAME, item.getItemName());
        values.put(COLUMN_ITEM_DESCRIPTION, item.getItemDescription());
        values.put(COLUMN_QUANTITY, item.getQuantity());
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(item.getId()) };
        db.update(INVENTORY_TABLE_NAME, values, selection, selectionArgs);
        db.close();
    }

    public boolean doesItemExist(String itemName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + INVENTORY_TABLE_NAME + " WHERE " + COLUMN_ITEM_NAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[] { itemName });

        boolean exists = false;
        if (cursor != null && cursor.moveToFirst()) {
            exists = cursor.getInt(0) > 0;
            cursor.close();
        }

        db.close();
        return exists;
    }
}









