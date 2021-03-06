package com.lx.viewevent;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class Utils {
  
    public static int getScreenInfo(Context context, int type) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return type == 0 ? outMetrics.widthPixels : outMetrics.heightPixels;
    }

    public static int getScreenHeight(Activity activity) {
        return getScreenInfo(activity, 1);
    }

    public static int getScreenWidth(Activity activity) {
        return getScreenInfo(activity, 0);
    }

    public static int getStatusHeight(Activity activity) {
        Rect rectangle = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rectangle);
        return rectangle.top;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
