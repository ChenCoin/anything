package xyz.cyan.touchbar;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import xyz.cyan.R;

public class TouchBar extends FrameLayout {

    private final View touchArea;

    private final View viewArea;

    private final View viewContent;

    private final View realLine;

    private final View fakeLine;

    private final float touchBarHeight;

    private final float fakeHeight;

    private final float fakePosition;

    private float downY = 0F;

    private boolean touchActive = false;

    public TouchBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.touch_bar, this);
        touchArea = findViewById(R.id.touchArea);
        viewArea = findViewById(R.id.viewArea);
        viewContent = findViewById(R.id.viewContent);
        realLine = findViewById(R.id.realLine);
        fakeLine = findViewById(R.id.fakeLine);

        Resources resources = context.getResources();

        fakeHeight = resources.getDimension(R.dimen.fakeHeight);
        touchBarHeight = resources.getDimension(R.dimen.touchBarHeight);
        fakePosition = touchBarHeight - fakeHeight;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void init() {
        View.OnTouchListener onTouchAreaTouch = (view, event) ->
                onTouchAction(event, () -> {
                    viewArea.setY(fakePosition);
                    viewContent.setAlpha(0F);
                    downY = event.getRawY() - viewArea.getY();
                    viewArea.setVisibility(View.VISIBLE);
                    realLine.setVisibility(View.INVISIBLE);
                    fakeLine.setAlpha(1F);
                });
        touchArea.setOnTouchListener(onTouchAreaTouch);

        View.OnTouchListener onViewAreaTouch = (view, event) ->
                onTouchAction(event, () -> downY = event.getRawY());
        viewArea.setOnTouchListener(onViewAreaTouch);
    }

    private boolean onTouchAction(MotionEvent event, Runnable onDown) {
        if (touchActive && event.getAction() == MotionEvent.ACTION_DOWN) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                touchActive = true;
                onDown.run();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                float targetY = event.getRawY() - downY;
                viewArea.setY(Math.min(Math.max(targetY, 0), fakePosition));
                float alpha = viewArea.getY() / fakePosition;
                fakeLine.setAlpha(alpha);
                viewContent.setAlpha(1 - alpha);
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                float nowY = viewArea.getY();
                if (nowY == 0) {
                    touchActive = false;
                } else if (nowY < touchBarHeight * 2 / 5) {
                    animationShowViewArea(() -> touchActive = false);
                } else {
                    animationHideViewArea(() -> {
                        touchActive = false;
                        viewArea.setVisibility(View.INVISIBLE);
                        realLine.setVisibility(View.VISIBLE);
                    });
                }
                break;
            }
        }
        return true;
    }

    private void animationShowViewArea(Runnable onAnimEnd) {
        ObjectAnimator animMove = ObjectAnimator.ofFloat(viewArea, "translationY",
                viewArea.getY(), 0F);
        ObjectAnimator animAlpha = ObjectAnimator.ofFloat(viewContent, "alpha",
                viewContent.getAlpha(), 1F);
        ObjectAnimator animAlphaLine = ObjectAnimator.ofFloat(fakeLine, "alpha",
                fakeLine.getAlpha(), 0F);
        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(animMove, animAlpha, animAlphaLine);
        animSet.setInterpolator(new DecelerateInterpolator());
        animSet.setDuration((long) (1000 * fakeLine.getAlpha()));
        animSet.addListener(new AnimEndListener(onAnimEnd));
        animSet.start();
    }

    private void animationHideViewArea(Runnable onAnimEnd) {
        ObjectAnimator animMove = ObjectAnimator.ofFloat(viewArea, "translationY",
                viewArea.getY(), fakePosition);
        ObjectAnimator animAlpha = ObjectAnimator.ofFloat(viewContent, "alpha",
                viewContent.getAlpha(), 0F);
        ObjectAnimator animAlphaLine = ObjectAnimator.ofFloat(fakeLine, "alpha",
                fakeLine.getAlpha(), 1F);
        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(animMove, animAlpha, animAlphaLine);
        animSet.setInterpolator(new DecelerateInterpolator());
        animSet.setDuration((long) (1000 * (1 - fakeLine.getAlpha())));
        animSet.addListener(new AnimEndListener(onAnimEnd));
        animSet.start();
    }

    private static class AnimEndListener implements Animator.AnimatorListener {
        private final Runnable onEnd;

        public AnimEndListener(Runnable onEnd) {
            this.onEnd = onEnd;
        }

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            onEnd.run();
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

}
