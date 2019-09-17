package gmedia.net.id.gmediaticketscanner.Util;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import static javax.net.ssl.HttpsURLConnection.*;

public class ApiVolleyManager {
    private final String LOG = "volley_log";

    public static int METHOD_GET = Request.Method.GET;
    public static int METHOD_POST = Request.Method.POST;
    private final int DEFAULT_TIMEOUT = 10000;
    private final int MAX_RETRIES = -1;
    private final String REQ_TAG = "tag";

    private final static ApiVolleyManager ourInstance = new ApiVolleyManager();
    private RequestQueue requestQueue;

    public static ApiVolleyManager getInstance() {
        return ourInstance;
    }

    private ApiVolleyManager() {
    }

    public void addSecureRequest(final Context context, String url, int method, final Map<String, String> header,
                                 final RequestCallback callback){
        trustAllCertivicate();
        addRequest(context, url, method, header, callback);
    }

    public void addRequest(final Context context, String url, int method, final Map<String, String> header,
                           final RequestCallback callback){
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(context);
        }

        StringRequest request = new StringRequest(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Jika hasil respon kosong
                if(response == null || response.equals("null")) {
                    callback.onError("Autentifikasi gagal/respons kosong");
                }

                try {
                    callback.onSuccess(response);
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onError(e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String message;
                if (error instanceof NoConnectionError) {
                    message = "Tidak dapat tersambung. Periksa lagi koneksi anda.";
                } else if (error instanceof NetworkError) {
                    message = "Tidak dapat menyambungkan dalam batas waktu yang ditentukan";
                } else if (error instanceof TimeoutError) {
                    message = "Tidak dapat menyambungkan dalam batas waktu yang ditentukan";
                } else{
                    message = "Terjadi kesalahan koneksi";
                }

                callback.onError(message);
                Log.e(LOG, error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                for(String key : header.keySet()){
                    params.put(key, header.get(key));
                }
                return params;
            }
        };

        //Set timeout
        request.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT,
                MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        request.setTag(REQ_TAG);
        request.setShouldCache(false);
        requestQueue.add(request);
        requestQueue.getCache().clear();
    }

    public void addSecureRequest(final Context context, String url, int method, final Map<String, String> header,
                                 final JSONObject body, final RequestCallback callback){
        trustAllCertivicate();
        addRequest(context, url, method, header, body, DEFAULT_TIMEOUT, callback);
    }

    public void addSecureRequest(final Context context, String url, int method, final Map<String, String> header,
                                 final JSONObject body, int TIMEOUT, final RequestCallback callback){
        trustAllCertivicate();
        addRequest(context, url, method, header, body, TIMEOUT, callback);
    }

    public void addRequest(final Context context, String url, int method, final Map<String, String> header,
                           final JSONObject body, final RequestCallback callback){
        trustAllCertivicate();
        addRequest(context, url, method, header, body, DEFAULT_TIMEOUT, callback);
    }

    public void addRequest(final Context context, String url, int method, final Map<String, String> header,
                           final JSONObject body, int TIMEOUT, final RequestCallback callback){
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(context);
        }

        StringRequest request = new StringRequest(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Jika hasil respon kosong
                if(response == null || response.equals("null")) {
                    callback.onError("Akses tidak valid/respon kosong");
                }

                try {
                    callback.onSuccess(response);
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onError(e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String message;
                if (error instanceof NoConnectionError) {
                    message = "Tidak dapat tersambung. Periksa lagi koneksi anda.";
                } else if (error instanceof NetworkError) {
                    message = "Tidak dapat menyambungkan dalam batas waktu yang ditentukan";
                } else if (error instanceof TimeoutError) {
                    message = "Tidak dapat menyambungkan dalam batas waktu yang ditentukan";
                } else{
                    message = "Terjadi kesalahan koneksi";
                }

                callback.onError(message);
                Log.e(LOG, error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                for(String key : header.keySet()){
                    params.put(key, header.get(key));
                }
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return body == null ? null : body.toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    Log.e(LOG,"Unsupported Encoding");
                    return null;
                }
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT,
                MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        request.setTag(REQ_TAG);
        request.setShouldCache(false);
        requestQueue.add(request);
        requestQueue.getCache().clear();
    }

    private void trustAllCertivicate() {
        try {
            setDefaultHostnameVerifier(new HostnameVerifier(){
                public boolean verify(String hostname, SSLSession session) {
                    if (hostname.equalsIgnoreCase("semargres.gmedia.id") ||
                            hostname.equalsIgnoreCase("app.midtrans.com") ||
                            hostname.equalsIgnoreCase("office.putmasaripratama.co.id") ||
                            hostname.equalsIgnoreCase("api.sandbox.midtrans.com") ||
                            hostname.equalsIgnoreCase("api.crashlytics.com") ||
                            hostname.equalsIgnoreCase("reports.crashlytics.com") ||
                            hostname.equalsIgnoreCase("settings.crashlytics.com") ||
                            hostname.equalsIgnoreCase("clients4.google.com") ||
                            hostname.equalsIgnoreCase("www.facebook.com") ||
                            hostname.equalsIgnoreCase("www.icon_instagram.com") ||
                            hostname.equalsIgnoreCase("lh1.googleusercontent.com") ||
                            hostname.equalsIgnoreCase("lh2.googleusercontent.com") ||
                            hostname.equalsIgnoreCase("lh3.googleusercontent.com") ||
                            hostname.equalsIgnoreCase("lh4.googleusercontent.com") ||
                            hostname.equalsIgnoreCase("lh5.googleusercontent.com") ||
                            hostname.equalsIgnoreCase("lh6.googleusercontent.com") ||
                            hostname.equalsIgnoreCase("lh7.googleusercontent.com") ||
                            hostname.equalsIgnoreCase("lh8.googleusercontent.com") ||
                            hostname.equalsIgnoreCase("lh9.googleusercontent.com") ||
                            hostname.equalsIgnoreCase("graph.facebook.com") ||
                            hostname.equalsIgnoreCase("googleusercontent.com") ||
                            hostname.equalsIgnoreCase("scontent.xx.fbcdn.net") ||
                            hostname.equalsIgnoreCase("lookaside.facebook.com")) {
                        return true;
                    } else {
                        return false;
                    }
                }});
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager(){
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }}}, new SecureRandom());
            setDefaultSSLSocketFactory(
                    context.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void trustCertificate() {
        try {
            setDefaultHostnameVerifier(new HostnameVerifier(){
                public boolean verify(String hostname, SSLSession session) {
                    if (hostname.equalsIgnoreCase("semargres.gmedia.id") ||
                            hostname.equalsIgnoreCase("media.giphy.com") ||
                            hostname.equalsIgnoreCase("office.putmasaripratama.co.id") ||
                            hostname.equalsIgnoreCase("api.crashlytics.com") ||
                            hostname.equalsIgnoreCase("app.midtrans.com") ||
                            hostname.equalsIgnoreCase("api.sandbox.midtrans.com") ||
                            hostname.equalsIgnoreCase("reports.crashlytics.com") ||
                            hostname.equalsIgnoreCase("settings.crashlytics.com") ||
                            hostname.equalsIgnoreCase("clients4.google.com") ||
                            hostname.equalsIgnoreCase("www.facebook.com") ||
                            hostname.equalsIgnoreCase("www.icon_instagram.com") ||
                            hostname.equalsIgnoreCase("lh1.googleusercontent.com") ||
                            hostname.equalsIgnoreCase("lh2.googleusercontent.com") ||
                            hostname.equalsIgnoreCase("lh3.googleusercontent.com") ||
                            hostname.equalsIgnoreCase("lh4.googleusercontent.com") ||
                            hostname.equalsIgnoreCase("lh5.googleusercontent.com") ||
                            hostname.equalsIgnoreCase("lh6.googleusercontent.com") ||
                            hostname.equalsIgnoreCase("lh7.googleusercontent.com") ||
                            hostname.equalsIgnoreCase("lh8.googleusercontent.com") ||
                            hostname.equalsIgnoreCase("lh9.googleusercontent.com") ||
                            hostname.equalsIgnoreCase("graph.facebook.com") ||
                            hostname.equalsIgnoreCase("googleusercontent.com") ||
                            hostname.equalsIgnoreCase("scontent.xx.fbcdn.net") ||
                            hostname.equalsIgnoreCase("lookaside.facebook.com")) {
                        return true;
                    } else {
                        return false;
                    }
                }});
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager(){
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }}}, new SecureRandom());
            setDefaultSSLSocketFactory(
                    context.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface RequestCallback{
        void onSuccess(String result);
        void onError(String result);
    }

    public void cancelRequest(){
        requestQueue.cancelAll(REQ_TAG);
    }
}