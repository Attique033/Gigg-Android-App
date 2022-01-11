package app.gigg.me.app.Activity.freelance.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefHelper {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String token = "TOKEN";
    private String vault = "VAULT";

    @SuppressLint("CommitPrefEdits")
    public SharedPrefHelper(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("GIGG.ME", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setSubmit(String name, boolean value){
        editor.putBoolean(name, value);
        editor.commit();
    }

    public boolean getSubmit(String name){
        return sharedPreferences.getBoolean(name, false);
    }

    public void setVault(float value){
        editor.putFloat(vault, value);
        editor.commit();
    }

    public float getVault(){
        return sharedPreferences.getFloat(vault, 0);
    }

    public void setToken(String value){
        editor.putString(token, value);
        editor.commit();
    }

    public String getToken(){
        return sharedPreferences.getString(token, "");
    }

}
