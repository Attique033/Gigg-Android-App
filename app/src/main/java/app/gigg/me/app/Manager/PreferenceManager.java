package app.gigg.me.app.Manager;

import android.content.Context;
import android.content.SharedPreferences;

import app.gigg.me.app.Model.Subscription;
import app.gigg.me.app.R;

public class PreferenceManager {
    private Context context;
    private SharedPreferences sharedPreferences;

    private void getSharedPreference() {
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.my_preference), Context.MODE_PRIVATE);
    }
    public PreferenceManager(Context context) {
        this.context = context;
        getSharedPreference();
    }

    public void writePreference() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.my_preference_key), "INIT_OK");
        editor.commit();
    }

    public boolean checkPreference() {
        boolean status = false;
        if (sharedPreferences.getString(context.getString(R.string.my_preference_key), "null").equals("null")){
            status = false;
        } else {
            status = true;
        }
        return status;
    }

    public void setPurchase(Subscription subscription){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isSubscribed", subscription.isSubscribed());
        editor.putString("SKU", subscription.getSKU());
        editor.apply();
    }

    public Subscription getPurchase(){
        return new Subscription(sharedPreferences.getString("SKU", null), sharedPreferences.getBoolean("isSubscribed", false));
    }

    public boolean isSubscribed(){
        return sharedPreferences.getBoolean("isSubscribed", false);
    }
}
