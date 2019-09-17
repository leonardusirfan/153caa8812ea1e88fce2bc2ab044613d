package gmedia.net.id.gmediaticketscanner.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppSharedPreferences {
    private static final String LOGIN_PREF = "login_status";
    private static final String ID_PREF = "user_id";
    private static final String TOKEN_PREF = "token";

    private static SharedPreferences getPreferences(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean isLoggedIn(Context context){
        return getPreferences(context).getBoolean(LOGIN_PREF, false);
    }

    public static String getToken(Context context){
        return getPreferences(context).getString(TOKEN_PREF, "");
    }

    public static String getId(Context context){
        return getPreferences(context).getString(ID_PREF, "");
    }

    public static void Login(Context context, String id, String token){
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(LOGIN_PREF, true);
        editor.putString(ID_PREF, id);
        editor.putString(TOKEN_PREF, token);
        editor.apply();
    }

    public static void Logout(Context context){
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(LOGIN_PREF, false);
        editor.putString(TOKEN_PREF, "");
        editor.apply();
    }
}