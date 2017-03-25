package tech.doujiang.launcher.view;

import java.util.HashMap;
import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.*;
import android.widget.*;
import tech.doujiang.launcher.R;

/**
 * Created by 豆浆 on 2016-07-12.
 */
public class QuickAlphabeticBar extends ImageButton{
    private TextView mDialogText;
    private Handler mHandler;
    private ListView mList;
    private float mHeight;

    private String[] letters = new String[] { "#", "A", "B", "C", "D", "E",
            "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
            "S", "T", "U", "V", "W", "X", "Y", "Z" };

    private HashMap<String, Integer> alphaIndexer;
    Paint paint = new Paint();
    boolean showBkg = false;
    int choose = -1;

    public QuickAlphabeticBar(Context context) {
        super(context);
    }

    public QuickAlphabeticBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void init(View ctx) {
        mDialogText = (TextView) ctx.findViewById(R.id.fast_position);
        mDialogText.setVisibility(View.INVISIBLE);
        mHandler = new Handler();
    }

    public void setListView(ListView mList) {
        this.mList = mList;
    }

    public void setAlphaIndexer(HashMap<String, Integer> alphaIndexer) {
        this.alphaIndexer = alphaIndexer;
    }

    public void setHeight(float mHeight) {
        this.mHeight = mHeight;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int act = event.getAction();
        float y = event.getY();
        final int oldChoose = choose;

        int selectIndex = (int) (y / (mHeight / letters.length));

        if (selectIndex > -1 && selectIndex < (letters.length)) {
            String key = letters[selectIndex];
            if (alphaIndexer.containsKey(key)) {
                int pos = alphaIndexer.get(key);
                if (mList.getHeaderViewsCount() > 0) {
                    this.mList.setSelectionFromTop(
                            pos + mList.getHeaderViewsCount(), 0);
                } else {
                    this.mList.setSelectionFromTop(pos, 0);
                }
                mDialogText.setText(letters[selectIndex]);
            }
        }
        switch (act) {
            case MotionEvent.ACTION_DOWN:
                showBkg = true;
                if (oldChoose != selectIndex) {
                    if (selectIndex > 0 && selectIndex < letters.length) {
                        choose = selectIndex;
                        invalidate();

                    }
                }
                if (mHandler != null) {
                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            if (mDialogText != null
                                    && mDialogText.getVisibility() == View.INVISIBLE) {
                                mDialogText.setVisibility(VISIBLE);
                            }

                        }
                    });
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (oldChoose != selectIndex) {
                    if (selectIndex > 0 && selectIndex < letters.length) {
                        choose = selectIndex;
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                showBkg = false;
                choose = -1;
                if (mHandler != null) {
                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            if (mDialogText != null
                                    && mDialogText.getVisibility() == View.VISIBLE) {
                                mDialogText.setVisibility(INVISIBLE);
                            }
                        }
                    });
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height = getHeight();
        int width = getWidth();
        int singleHeight = height / letters.length;
        for (int i = 0; i < letters.length; i ++) {
            paint.setColor(Color.GRAY);
            paint.setTextSize(20);
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            paint.setAntiAlias(true);
            if (i == choose) {
                paint.setColor(Color.parseColor("#00BFFF"));
                paint.setFakeBoldText(true);
            }

            float xPos = width / 2 - paint.measureText(letters[i]) / 2;
            float yPos = singleHeight * i + singleHeight;
            canvas.drawText(letters[i], xPos, yPos, paint);
            paint.reset();
        }
    }

    public void assignContactUri(Uri contactUri) {
//		mContactUri = contactUri;
//		mContactEmail =null;
//		mContactPhone = null;
//		onContactCahenged()
    }
}
