package gmedia.net.id.gmediaticketscanner;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import gmedia.net.id.gmediaticketscanner.Util.AppSharedPreferences;

public class Constant {

    public final static String TAG = "tiket_scanner_log";

    private final static String BASE_URL = "http://mgmt.tukutiket.com/api/";
    public final static String URL_LOGIN = BASE_URL + "auth/login";
    public final static String URL_SCAN = BASE_URL + "scan/tiket";

    public static Map<String, String> getTokenHeader(Context context){
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Auth-Key", "frontend_client");
        headers.put("Client-Service", "Gmedia_EVENT");
        headers.put("User-Id", AppSharedPreferences.getId(context));
        headers.put("Token", AppSharedPreferences.getToken(context));

        return headers;
    }
}