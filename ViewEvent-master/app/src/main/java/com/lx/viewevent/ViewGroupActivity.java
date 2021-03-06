package com.lx.viewevent;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;
public class ViewGroupActivity extends Activity {

    private RelativeLayout reParent;
    private LinearLayout llGroup;

    private int[] startPoint = new int[2];
    private int[] tempPoint = new int[2];

    private ImageView tempView;
    private boolean isCanDrag;
    private WindowManager wm;
    private WindowManager.LayoutParams wmParams;
    private Bitmap bitmap;
    private AtomicInteger count = new AtomicInteger();

    private int startX = 0;
    private int startY = 0;
    private int topHei = 0;
    private int moveNum = 0;
    private boolean isLongClick = false;

    private Timer childCheckLongClickTimer = new Timer();
    private TimerTask childCheckLongClickTask;
    private boolean isChildLongClickShowed = false;

    private Vibrator vibrator;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0 && msg.obj != null) {
//				CustomViewGroup viewGroup = (CustomViewGroup) msg.obj;
                childCheckLongClickTask.cancel();
                childCheckLongClickTimer.cancel();
                if (isChildLongClickShowed) {
                    isChildLongClickShowed = false;
                    return;
                }

                vibrator.vibrate(100);
                Toast.makeText(getApplicationContext(), "long click", Toast.LENGTH_SHORT).show();
                isChildLongClickShowed = true;
            }
        };
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewgroup);

        reParent = (RelativeLayout) findViewById(R.id.container);
        llGroup = (LinearLayout) findViewById(R.id.viewgroup);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        setSourceOnLongClickListener();

        setSourceOnTouchListener();
    }
    private void setSourceOnTouchListener() {
        llGroup.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startPoint[0] = (int) (event.getRawX());
                        startPoint[1] = (int) (event.getRawY());
                        break;

                    case MotionEvent.ACTION_MOVE:

                        if (isCanDrag && tempView != null) {
                            wmParams.x = (int) (event.getRawX() - tempView .getWidth() / 2);
                            wmParams.y = (int) (event.getRawY() - tempView .getHeight());
                            wm.updateViewLayout(tempView, wmParams);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (isCanDrag && tempView != null) {
                            isCanDrag = false;
                            int[] tempLocation = new int[2];
                            tempView.getLocationOnScreen(tempLocation);
                            wm.removeView(tempView);
                            tempView = null;

                            addView((int) event.getRawX(), (int) event.getRawY());
                        }
                        break;
                }
                return false;
            }
        });
    }
    private void setSourceOnLongClickListener() {
        llGroup.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {

                view.getLocationInWindow(tempPoint);
                wm = getWindowManager();
                wmParams = new WindowManager.LayoutParams();
                wmParams.gravity = Gravity.TOP | Gravity.LEFT;
                wmParams.x = startPoint[0] + tempPoint[0];
                wmParams.y = startPoint[1] - llGroup.getHeight();
                wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

                wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE// 不需获取焦点
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE// 不需接受触摸事件
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;// 保持设备常开，并保持亮度不变。
                wmParams.windowAnimations = 0;
// 获得拖动的item展示内容，以bitmap对象形式
                view.setDrawingCacheEnabled(true);

                bitmap = Bitmap.createBitmap(view.getDrawingCache());
                view.setDrawingCacheEnabled(false);

                // 拖动的view对象
                tempView = new ImageView(getApplicationContext());
                tempView.setImageBitmap(bitmap);

                wm.addView(tempView, wmParams);
                isCanDrag = true;

                return false;
            }
        });
    }
    private void addView(int rawX, int rawY) {
        final OnClickListener onChildClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_group_1:
                        Toast.makeText(getApplicationContext(), "click 1", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.tv_group_2:
                        Toast.makeText(getApplicationContext(), "click 2", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.tv_group_3:
                        Toast.makeText(getApplicationContext(), "click 3", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        OnTouchListener onChildTouchListener = new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ViewParent viewParent = v.getParent();
                while(! (viewParent instanceof CustomViewGroup)) {
                    viewParent = viewParent.getParent();
                }

                boolean bRet = false;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        isChildLongClickShowed = false;
                        childCheckLongClickTimer = new Timer();
                        childCheckLongClickTask = new ChildCheckLongClickTask((CustomViewGroup)viewParent);
                        childCheckLongClickTimer.schedule(childCheckLongClickTask, 1000);

                        bRet = true;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        bRet = true;
                        break;
                    case MotionEvent.ACTION_UP:

                        childCheckLongClickTask.cancel();
                        childCheckLongClickTimer.cancel();

                        if (!isChildLongClickShowed) {
                            onChildClickListener.onClick(v);
                        }

                        isChildLongClickShowed = true;
                        break;
                }
                return bRet;
            }
        };
        CustomViewGroup mView = new CustomViewGroup(getApplicationContext(), onChildTouchListener);
        mView.setId(count.getAndIncrement());

        setGroupOnTouchListener(mView);

        setGroupOnLongClickListener(mView);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        setGroupViewLayout(rawX, rawY, mView, params);

        reParent.addView(mView, params);
        reParent.invalidate();

    }
    private void setGroupOnLongClickListener(CustomViewGroup mView) {
        mView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                boolean bRet = false;
                if (Integer.valueOf(v.getTag().toString()) == 0) {
                    v.setTag(2000);
                    bRet = true;
                    isLongClick = true;
                    Toast.makeText(getApplicationContext(), "long click", Toast.LENGTH_SHORT).show();
                }
                return bRet;
            }
        });
    }

    private void setGroupOnTouchListener(CustomViewGroup mView) {
        mView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean bRet = false;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) (event.getRawX());
                        startY = (int) (event.getRawY());
                        v.setTag(moveNum);
                        isLongClick = false;
                        break;

                    case MotionEvent.ACTION_MOVE:

                        isChildLongClickShowed = true;
                        childCheckLongClickTask.cancel();
                        childCheckLongClickTimer.cancel();

                        if (isLongClick) {
                            bRet = true;
                        } else {

                            updateLayout(v, event);
                            moveNum ++;
                            v.setTag(moveNum);
                        }
                        break;
                    case MotionEvent.ACTION_UP:

                        isChildLongClickShowed = true;
                        childCheckLongClickTask.cancel();
                        childCheckLongClickTimer.cancel();

                        if (isLongClick) {
                            bRet = true;
                        } else {
                            reParent.invalidate();
                            moveNum = 0;
                            v.setTag(moveNum);
                        }
                        break;
                }
                return bRet;
            }
        });
    }

    private void updateLayout(View v, MotionEvent event) {
        int currentX = (int) event.getRawX();
        int currentY = (int) event.getRawY();

        if (Math.abs(startX - currentX) > 20
                || Math.abs(startY - currentY) > 20) {
            RelativeLayout.LayoutParams params = (LayoutParams) v
                    .getLayoutParams();
            int wid = Utils.dip2px(getApplicationContext(), 70);
            int hei = Utils.dip2px(getApplicationContext(), 92);
            topHei = Utils.dip2px(getApplicationContext(), 50);

            if (currentX > Utils.getScreenWidth(this) - wid / 2) {
                currentX = Utils.getScreenWidth(this) - wid / 2;
            }
            if (currentX < wid / 2) {
                currentX = wid / 2;
            }
            if (currentY > Utils.getScreenHeight(this) - hei) {
                currentY = Utils.getScreenHeight(this) - hei;
            }
            if (currentY < topHei) {
                currentY = topHei;
            }
            params.leftMargin = currentX - wid / 2;
            params.topMargin = currentY - topHei;

            reParent.updateViewLayout(v, params);
            reParent.invalidate();
        }
    }

    private void setGroupViewLayout(int x, int y, CustomViewGroup mView,
                                    RelativeLayout.LayoutParams params) {
        int nScreenWidth = Utils.getScreenWidth(this);
        int nScreenHeight = Utils.getScreenHeight(this);
        int nViewWidth = mView.getViewWidth();
        int nViewHeight = mView.getViewHeight();

        params.width = nViewWidth;
        params.height = nViewHeight;

        int nlimHei = Utils.dip2px(this, 51);
        if (x < nViewWidth / 2) {
            x = nViewWidth / 2;
        }
        if (x >  nScreenWidth - nViewWidth / 2) {
            x = nScreenWidth - nViewWidth / 2;
        }
        if (y < nlimHei) {
            y = nlimHei + nViewWidth / 2;
        }
        if (y > nScreenHeight - Utils.getStatusHeight(this) - nViewHeight / 2) {
            y = nScreenHeight - Utils.getStatusHeight(this) - nViewHeight / 2;
        }
        params.leftMargin = x - nViewWidth / 2;
        params.topMargin = y - nViewHeight / 2;
        if (params.leftMargin < 0) {
            params.leftMargin = 0;
        }
        if (params.topMargin < Utils.dip2px(this, 51)) {
            params.topMargin = Utils.dip2px(this, 51);
        }
    }

    class ChildCheckLongClickTask extends TimerTask {
        private CustomViewGroup viewGroup;

        public ChildCheckLongClickTask(CustomViewGroup viewGroup) {
            super();
            this.viewGroup = viewGroup;
        }

        @Override
        public void run() {
            Message msg = handler.obtainMessage();
            msg.what = 0;
            msg.obj = viewGroup;
            handler.sendMessage(msg);
        }
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}