package gmedia.net.id.gmediaticketscanner;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import gmedia.net.id.gmediaticketscanner.Util.ApiVolleyManager;
import gmedia.net.id.gmediaticketscanner.Util.AppLoading;
import gmedia.net.id.gmediaticketscanner.Util.AppRequestCallback;
import gmedia.net.id.gmediaticketscanner.Util.AppSharedPreferences;
import gmedia.net.id.gmediaticketscanner.Util.JSONBuilder;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class ScannerActivity extends AppCompatActivity {

    private final int PERMISSION_CODE = 900;
    private DecoratedBarcodeView barcodeView;
    private MediaPlayer mp_success, mp_fail;

    private boolean dialog_show = false;

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if(result.getText() == null) {
                return;
            }

            //pakai api dengan parameter result
            scanQr(result.getText());
            barcodeView.pause();
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("");
        }

        barcodeView = findViewById(R.id.barcode_scanner);
        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39);
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeView.initializeFromIntent(getIntent());
        barcodeView.decodeContinuous(callback);
        barcodeView.setStatusText("Posisikan barcode didalam area pindai");

        if(!checkPermission()){
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.CAMERA}, PERMISSION_CODE);
        }

        //AUTO VOLUME
        /*AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, AudioManager.FLAG_SHOW_UI);*/

        mp_success = MediaPlayer.create(this, R.raw.beep);
        mp_fail = MediaPlayer.create(this, R.raw.fail);
    }

    private void playBeep(boolean success){
        if(success){
            if(mp_success != null){
                mp_success.start();
            }
        }
        else{
            if(mp_fail != null){
                mp_fail.start();
            }
        }
    }

    private void scanQr(String qr){
        AppLoading.getInstance().showLoading(this);
        JSONBuilder body = new JSONBuilder();
        body.add("kode_qr", qr);
        Log.d(Constant.TAG, body.create().toString());

        ApiVolleyManager.getInstance().addSecureRequest(this, Constant.URL_SCAN,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(this), body.create(),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        playBeep(false);
                        showGagal(message);
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result, String message) {
                        try{
                            JSONObject response = new JSONObject(result);
                            showBerhasil(response.getString("nama_pembeli"),
                                    response.getString("email_pembeli"),
                                    response.getString("kode_qr"));
                            playBeep(true);
                        }
                        catch (JSONException e){
                            playBeep(false);
                            Log.e(Constant.TAG, e.getMessage());
                            showGagal("Terjadi kesalahan data");
                        }
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        playBeep(false);
                        showGagal(message);
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    void showBerhasil(String nama, String jenis, String barcode){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        int device_TotalWidth = size.x;
        int device_TotalHeight = size.y;

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_scan_berhasil);

        if(dialog.getWindow() != null){
            dialog.getWindow().setLayout(device_TotalWidth * 100 / 100 ,
                    WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.gravity = Gravity.BOTTOM;
            lp.windowAnimations = R.style.DialogAnimation;
            dialog.getWindow().setAttributes(lp);
        }

        dialog.setCancelable(false);

        TextView txt_barcode = dialog.findViewById(R.id.txt_barcode);
        TextView txt_nama = dialog.findViewById(R.id.txt_nama);
        TextView txt_jenis = dialog.findViewById(R.id.txt_jenis);
        txt_barcode.setText(barcode);
        txt_nama.setText(nama);
        txt_jenis.setText(jenis);

        Button button = dialog.findViewById(R.id.btn_exit);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                barcodeView.resume();

                dialog.dismiss();
                dialog_show = false;
            }
        });

        dialog.show();
        dialog_show = true;
    }

    void showGagal(String message){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        int device_TotalWidth = size.x;
        int device_TotalHeight = size.y;

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_scan_gagal);

        if(dialog.getWindow() != null){
            dialog.getWindow().setLayout(device_TotalWidth * 100 / 100 ,
                    device_TotalHeight * 40 / 100);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.gravity = Gravity.BOTTOM;
            lp.windowAnimations = R.style.DialogAnimation;
            dialog.getWindow().setAttributes(lp);
        }

        TextView txt_pesan = dialog.findViewById(R.id.txt_pesan);
        txt_pesan.setText(message);

        dialog.setCancelable(false);

        Button button = dialog.findViewById(R.id.btn_exit);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                barcodeView.resume();

                dialog.dismiss();
                dialog_show = false;
            }
        });

        dialog.show();
        dialog_show = true;
    }

    @Override
    protected void onResume() {
        if(!dialog_show){
            barcodeView.resume();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        barcodeView.pause();
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    //========================= FUNGSI CEK PERMISSION ==============================================

    private boolean checkPermission(){
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[]
            permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_CODE){
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                barcodeView.resume();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Logout");
            builder.setMessage("Apakah anda yakin ingin keluar?");
            builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Intent i = new Intent(ScannerActivity.this, LoginActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    AppSharedPreferences.Logout(ScannerActivity.this);
                }
            });
            builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.create().show();
            return true;
        }
        else{
            return super.onOptionsItemSelected(item);
        }
    }
}
