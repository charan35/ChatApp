package com.example.altachatapp.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.altachatapp.model.User;


public class SharedPreferenceHelper {
    private static SharedPreferenceHelper instance = null;
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;
    private static String SHARE_USER_INFO = "userinfo";
    private static String SHARE_KEY_NAME = "name";
    private static String SHARE_KEY_EMAIL = "email";
    private static String SHARE_KEY_AVATA = "avata";
    private static String SHARE_KEY_ID = "ID";
    private static String SHARE_KEY_PHONE="phone";
    private static String SHARE_KEY_UID = "uid";
    private static String SHARE_KEY_DOB = "dob";


    private SharedPreferenceHelper() {}

    public static SharedPreferenceHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferenceHelper();
            preferences = context.getSharedPreferences(SHARE_USER_INFO, Context.MODE_PRIVATE);
            editor = preferences.edit();
        }
        return instance;
    }

    public void saveUserInfo(User user) {
        editor.putString(SHARE_KEY_NAME, user.name);
        editor.putString(SHARE_KEY_EMAIL, user.email);
        editor.putString(SHARE_KEY_AVATA, user.avata);
        editor.putString(SHARE_KEY_DOB,user.dateofbirth);
        editor.putString(SHARE_KEY_PHONE,user.phone);
        editor.putString(SHARE_KEY_ID,user.ID);
        editor.putString(SHARE_KEY_UID, StaticConfig.UID);
        editor.apply();
    }

    public User getUserInfo(){
        String userName = preferences.getString(SHARE_KEY_NAME, "");
        String email = preferences.getString(SHARE_KEY_EMAIL, "");
        String phone=preferences.getString(SHARE_KEY_PHONE,"");
        String id=preferences.getString(SHARE_KEY_ID,"");
        String avatar = preferences.getString(SHARE_KEY_AVATA, "default");
        String dob=preferences.getString(SHARE_KEY_DOB,"");
        User user = new User();
        user.name = userName;
        user.email = email;
        user.avata = avatar;
        user.dateofbirth=dob;
        user.ID=id;
        user.phone=phone;
        return user;
    }

    public String getUID(){
        return preferences.getString(SHARE_KEY_UID, "");
    }

}
