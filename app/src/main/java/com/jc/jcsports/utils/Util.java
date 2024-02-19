package com.jc.jcsports.utils;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsetsAnimation;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.jc.jcsports.R;

import org.json.JSONException;
import org.json.JSONObject;

public class Util {

    public static void hideSystemUI(Window window) {
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );
    }

    public static class RecyclerViewDecoration extends RecyclerView.ItemDecoration {

        private final int divHeight;

        public RecyclerViewDecoration(int divHeight) {
            this.divHeight = divHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.top = divHeight;
        }
    }


    public static int getPxFromDp(Context context, float dp) {
        int px = 0;
        px = (int) (dp * context.getResources().getDisplayMetrics().density);
        return px;
    }

    public static void dialogLayoutInfoUpdate(Context context, Window window, JSONObject object) throws JSONException {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(window.getAttributes());
        int deviceWidth = 0;
        int deviceHeight = 0;
        if (Build.VERSION.SDK_INT < 30) {
            Display defaultDisplay = windowManager.getDefaultDisplay();
            Point point = new Point();
            defaultDisplay.getSize(point);
            deviceWidth = point.x; // 디바이스 가로 길이
            deviceHeight = point.y; // 디바이스 가로 길이
        } else {
            Rect rect = windowManager.getCurrentWindowMetrics().getBounds();
            deviceWidth = rect.width();
            deviceHeight = rect.height();
        }
        params.width = object.getDouble("width") < 0 ? (int) object.getDouble("width") : (int) (Math.round(deviceWidth * object.getDouble("width")));
        params.height = object.getDouble("height") < 0 ? (int) object.getDouble("height") : (int) (Math.round(deviceWidth * object.getDouble("width")));
        window.setAttributes(params);
        Util.hideSystemUI(window);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    public static void setMsg(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }
}
