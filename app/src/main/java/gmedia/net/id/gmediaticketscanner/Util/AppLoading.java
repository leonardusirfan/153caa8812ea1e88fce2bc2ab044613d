package gmedia.net.id.gmediaticketscanner.Util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Window;

import gmedia.net.id.gmediaticketscanner.R;

public class AppLoading {
    private static final AppLoading ourInstance = new AppLoading();
    private Dialog dialog;

    private boolean show = false;

    public static AppLoading getInstance() {
        return ourInstance;
    }

    private AppLoading() {
    }

    public void showLoading(Context context){
        if(!show){
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();

            int device_TotalWidth = metrics.widthPixels;
            int device_TotalHeight = metrics.heightPixels;

            dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_loading);
            if(dialog.getWindow() != null){
                dialog.getWindow().setLayout(device_TotalWidth * 70 / 100 , device_TotalHeight * 15 / 100); // set here your value
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
            dialog.setCancelable(false);

            show = true;
            dialog.show();
        }
    }

    public void showLoading(final Context context, final CancelListener listener){
        if(!show){
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();

            int device_TotalWidth = metrics.widthPixels;
            int device_TotalHeight = metrics.heightPixels;

            dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_loading);
            if(dialog.getWindow() != null){
                dialog.getWindow().setLayout(device_TotalWidth * 70 / 100 , device_TotalHeight * 15 / 100); // set here your value
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
            dialog.setCancelable(false);

            dialog.setOnKeyListener(new Dialog.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if(context instanceof Activity){
                            listener.onCancel();

                            dialog.cancel();
                            show = false;
                        }
                        dialog.cancel();
                    }
                    return true;
                }
            });

            show = true;
            dialog.show();
        }
    }

    public void showLoading(final Context context, int res_id, final CancelListener listener){
        if(!show){
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();

            int device_TotalWidth = metrics.widthPixels;
            int device_TotalHeight = metrics.heightPixels;

            dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(res_id);
            if(dialog.getWindow() != null){
                dialog.getWindow().setLayout(device_TotalWidth * 70 / 100 , device_TotalHeight * 15 / 100); // set here your value
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
            dialog.setCancelable(false);

            dialog.setOnKeyListener(new Dialog.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if(context instanceof Activity){
                            listener.onCancel();

                            dialog.cancel();
                            show = false;
                        }
                        dialog.cancel();
                    }
                    return true;
                }
            });

            show = true;
            dialog.show();
        }
    }

    public void showLoading(Context context, int res_id){
        if(!show){
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();

            int device_TotalWidth = metrics.widthPixels;
            int device_TotalHeight = metrics.heightPixels;

            dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(res_id);
            if(dialog.getWindow() != null){
                dialog.getWindow().setLayout(device_TotalWidth * 70 / 100 , device_TotalHeight * 15 / 100); // set here your value
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
            dialog.setCancelable(false);

            show = true;
            dialog.show();
        }
    }

    public void stopLoading(){
        if(show){
            dialog.dismiss();
            show = false;
        }
    }

    public interface CancelListener{
        void onCancel();
    }
}