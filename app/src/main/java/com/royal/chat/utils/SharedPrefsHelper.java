package com.royal.chat.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.quickblox.core.helper.StringifyArrayList;
import com.royal.chat.App;
import com.quickblox.users.model.QBUser;

import java.util.Locale;

public class SharedPrefsHelper {
    private static final String SHARED_PREFS_NAME = "qb";

    private static final String QB_USER_ID = "qb_user_id";
    private static final String QB_USER_LOGIN = "qb_user_login";
    private static final String QB_USER_PASSWORD = "qb_user_password";
    private static final String QB_USER_FULL_NAME = "qb_user_full_name";
    private static final String QB_USER_TAGS = "qb_user_tags";
    private static final String QB_USER_IMAGE = "qb_user_image";
    private static final String SAVED_USER_NAME = "user_name";
    private static final String SAVED_LANG = "lang";

    private static SharedPrefsHelper instance;

    private SharedPreferences sharedPreferences;

    public static synchronized SharedPrefsHelper getInstance() {
        if (instance == null) {
            instance = new SharedPrefsHelper();
        }

        return instance;
    }

    private SharedPrefsHelper() {
        instance = this;
        sharedPreferences = App.getInstance().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void delete(String key) {
        if (sharedPreferences.contains(key)) {
            getEditor().remove(key).commit();
        }
    }

    public void save(String key, Object value) {
        SharedPreferences.Editor editor = getEditor();
        if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Enum) {
            editor.putString(key, value.toString());
        } else if (value != null) {
            throw new RuntimeException("Attempting to save non-supported preference");
        }

        editor.commit();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) sharedPreferences.getAll().get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, T defValue) {
        T returnValue = (T) sharedPreferences.getAll().get(key);
        return returnValue == null ? defValue : returnValue;
    }

    public boolean has(String key) {
        return sharedPreferences.contains(key);
    }


    public void saveQbUser(QBUser qbUser) {
        save(QB_USER_ID, qbUser.getId());
        save(QB_USER_LOGIN, qbUser.getLogin());
        save(QB_USER_PASSWORD, qbUser.getPassword());
        save(QB_USER_FULL_NAME, qbUser.getFullName());
        Integer fileID = qbUser.getFileId();
        save(QB_USER_IMAGE, qbUser.getFileId());
        save(QB_USER_TAGS, qbUser.getTags().getItemsAsString());
        putUserName(qbUser.getFullName());
    }

    public void removeQbUser() {
        delete(QB_USER_ID);
        delete(QB_USER_LOGIN);
        delete(QB_USER_PASSWORD);
        delete(QB_USER_FULL_NAME);
        delete(QB_USER_IMAGE);
        delete(QB_USER_TAGS);
    }

    public QBUser getQbUser() {
        if (hasQbUser()) {
            Integer id = get(QB_USER_ID);
            String login = get(QB_USER_LOGIN);
            String password = get(QB_USER_PASSWORD);
            String fullName = get(QB_USER_FULL_NAME);
            Integer fileID = get(QB_USER_IMAGE);

            String tagsInString = get(QB_USER_TAGS);

            StringifyArrayList<String> tags = null;

            if (tagsInString != null) {
                tags = new StringifyArrayList<>();
                tags.add(tagsInString.split(","));
            }

            QBUser user = new QBUser(login, password);
            user.setId(id);
            user.setFullName(fullName);
            user.setFileId(fileID);
            user.setTags(tags);
            return user;
        } else {
            return null;
        }
    }

    public boolean hasQbUser() {
        return has(QB_USER_LOGIN) && has(QB_USER_PASSWORD);
    }

    public void clearAllData() {
        SharedPreferences.Editor editor = getEditor();
        editor.clear().commit();
    }

    private SharedPreferences.Editor getEditor() {
        return sharedPreferences.edit();
    }

    private void putUserName(String name) {
        storeConfig(SAVED_USER_NAME, name);
    }

    public String getSavedUserName() {
        return readConfig(SAVED_USER_NAME);
    }

    public void putLocale(String locale) {
        storeConfig(SAVED_LANG, locale);
    }

    public String getLocale() {
        String locale = readConfig(SAVED_LANG);
        if (locale == null || locale.equals("")) {
            String defaultLocale = Locale.getDefault().getLanguage();
            if (!defaultLocale.equals(App.LOCALE_EN) && !defaultLocale.equals(App.LOCALE_KO)) {
                defaultLocale = App.LOCALE_EN;
            }
            return defaultLocale;
        }

        return locale;
    }

    private void storeConfig(String field, String value){
        try{
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(field, value);
            editor.apply();

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private String readConfig(String field){
        try{
            return  sharedPreferences.getString(field, "");
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "";
    }
}