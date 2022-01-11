package app.gigg.me.app.Activity.freelance;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by kishon on 23,July,2021
 */
public class SharedHelper {
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private static final String type = "TYPE";

    @SuppressLint("CommitPrefEdits")
    public SharedHelper(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("GIGG", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }


    public int getType(){
        return sharedPreferences.getInt(type, 0);
    }

    public void setType(int value){
        editor.putInt(type, value);
        editor.commit();
    }
}
