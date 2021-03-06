package com.lx.viewevent;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class ViewActivity extends Activity {

    private TextView tvMove;
    private RelativeLayout reParent;

    private int startX = 0;
    private int startY = 0;
    private int topHei = 0;
    private int moveNum = 0;
    private boolean isLongClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        tvMove = (TextView) findViewById(R.id.tv_main_move);
        reParent = (RelativeLayout) findViewById(R.id.container);

        tvMove.setOnLongClickListener(new View.OnLongClickListener() {

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

        tvMove.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                boolean bRet = false;
                topHei = Utils.getStatusHeight(ViewActivity.this) + 100;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        bRet = false;

                        moveNum = 0;
                        v.setTag(moveNum);
                        isLongClick = false;
                        break;

                    case MotionEvent.ACTION_MOVE:

                        if (isLongClick) {
                            bRet = true;
                        } else {
                            moveNum++;
                            v.setTag(moveNum);
                            updateLayout(v, event);
                        }

                        bRet = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (isLongClick) {
                            bRet = true;
                        } else {
                            reParent.invalidate();

                            int curX = (int) event.getRawX();
                            int curY = (int) event.getRawY();

                            if (Math.abs(curX - startX) < 10
                                    || Math.abs(curY - startY) < 10) {
                                if (Integer.valueOf(v.getTag().toString()) != 2000) {
                                    Toast.makeText(getApplicationContext(),
                                            "click", Toast.LENGTH_SHORT).show();
                                }
                            }

                            bRet = false;
                        }
                        moveNum = 0;
                        v.setTag(moveNum);
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
            int wid = 200;
            int hei = 200;

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
}