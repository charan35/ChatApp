package com.example.altachatapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.example.altachatapp.model.FetchContacts;
import com.example.altachatapp.model.Friend;
import com.example.altachatapp.model.ListContact;
import com.example.altachatapp.model.ListFriend;

public final class ContactsDB {
    private static ContactDBHelper mDbHelper = null;
    Context context;

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private ContactsDB() {
    }

    private static ContactsDB instance = null;

    public static ContactsDB getInstance(Context context) {
        if (instance == null) {
            instance = new ContactsDB();
            mDbHelper = new ContactDBHelper(context);
        }
        return instance;
    }


    public long addContact(FetchContacts fetchContacts) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_NAME_ID, fetchContacts.id);
        values.put(FeedEntry.COLUMN_NAME_NAME, fetchContacts.name);
        values.put(FeedEntry.COLUMN_NAME_EMAIL, fetchContacts.email);
        values.put(FeedEntry.COLUMN_NAME_PHONE, fetchContacts.phone);
       // values.put(FeedEntry.COLUMN_NAME_ID_ROOM, friend.idRoom);
        values.put(FeedEntry.COLUMN_NAME_AVATA, fetchContacts.avata);
        // Insert the new row, returning the primary key value of the new row
       return db.insert(FeedEntry.TABLE_NAME, null, values);
    }


    /*public void addListFriend(ListFriend listFriend){
        for(Friend friend: listFriend.getListFriend()){
            addContact();
        }
    }*/

    public ListContact getListContact() {
        ListContact listContact = new ListContact();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        // Define a projection that specifies which columns from the database
// you will actually use after this query.
        try {
            Cursor cursor = db.rawQuery("select * from " + FeedEntry.TABLE_NAME, null);
            while (cursor.moveToNext()) {
                FetchContacts fetchContacts = new FetchContacts();
                fetchContacts.id = cursor.getString(0);
                fetchContacts.name = cursor.getString(1);
                fetchContacts.email = cursor.getString(2);
                fetchContacts.phone = cursor.getString(3);
                fetchContacts.avata = cursor.getString(4);
                listContact.getListContact().add(fetchContacts);
            }
            cursor.close();
        }catch (Exception e){
            return new ListContact();
        }
        return listContact;
    }

    public void dropDB(){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        static final String TABLE_NAME = "contactlist";
        static final String COLUMN_NAME_ID = "friendID";
        static final String COLUMN_NAME_NAME = "name";
        static final String COLUMN_NAME_EMAIL = "email";
        static final String COLUMN_NAME_PHONE = "phone";
        static final String COLUMN_NAME_AVATA = "avata";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry.COLUMN_NAME_ID + " TEXT PRIMARY KEY," +
                    FeedEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_EMAIL + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_PHONE + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_AVATA + TEXT_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;


    private static class ContactDBHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        static final int DATABASE_VERSION = 2;
        static final String DATABASE_NAME = "Contacts.db";

        ContactDBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }
}
