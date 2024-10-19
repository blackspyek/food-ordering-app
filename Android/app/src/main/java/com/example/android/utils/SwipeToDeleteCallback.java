package com.example.android.utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.R;
import com.example.android.adapters.CartAdapter;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
    private static final int SWIPE_DIRECTION = ItemTouchHelper.RIGHT;
    private static final String SWIPE_BACKGROUND_COLOR = "#FF8686";
    private static final float SWIPE_THRESHOLD = 0.3f;
    private final CartAdapter cartAdapter;

    public SwipeToDeleteCallback(CartAdapter cartAdapter) {
        super(0, SWIPE_DIRECTION);
        this.cartAdapter = cartAdapter;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getBindingAdapterPosition();
        if (position > 0) {
            cartAdapter.removeItem(position);
        } else {
            cartAdapter.notifyItemChanged(0);
        }
    }

    @Override
    public int getSwipeDirs(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return viewHolder.getBindingAdapterPosition() == 0 ? 0 : super.getSwipeDirs(recyclerView, viewHolder);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (isSwipeHandled(actionState, viewHolder, c, recyclerView, dX, dY, isCurrentlyActive)) {
            return;
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    private boolean isSwipeHandled(int actionState, RecyclerView.ViewHolder viewHolder, Canvas c, RecyclerView recyclerView, float dX, float dY, boolean isCurrentlyActive) {
        return isSwiping(actionState, viewHolder) && handleSwipeAnimation(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }


    private boolean handleSwipeAnimation(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        float swipeThreshold = calculateSwipeThreshold(viewHolder);

        if (isSwipingRight(dX)) {
            swipeRightAnimation(c, viewHolder, dX);
        } else {
            resetAnimation(c, recyclerView, viewHolder, dY, actionState, isCurrentlyActive);
            return true;
        }

        if (shouldResetView(dX, swipeThreshold, isCurrentlyActive)) {
            resetAnimation(c, recyclerView, viewHolder, dY, actionState, false);
            return true;
        }
        return false;
    }

    private boolean isSwiping(int actionState, @NonNull RecyclerView.ViewHolder viewHolder) {
        return actionState == ItemTouchHelper.ACTION_STATE_SWIPE && viewHolder.getBindingAdapterPosition() > 0;
    }

    private float calculateSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return viewHolder.itemView.getWidth() * SWIPE_THRESHOLD;
    }

    private boolean isSwipingRight(float dX) {
        return dX > 0;
    }

    private boolean shouldResetView(float dX, float swipeThreshold, boolean isCurrentlyActive) {
        return dX < swipeThreshold && !isCurrentlyActive;
    }


    private void resetAnimation(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, 0, dY, actionState, isCurrentlyActive);
    }

    private void swipeRightAnimation(@NonNull Canvas c, RecyclerView.ViewHolder viewHolder, float dX) {
        drawSwipeBackground(c, viewHolder.itemView, dX);
        drawDeleteIcon(c, viewHolder.itemView);
    }

    private void drawSwipeBackground(Canvas canvas, View itemView, float dX) {
        Paint paint = new Paint();
        paint.setColor(Color.parseColor(SWIPE_BACKGROUND_COLOR));

        float cornerRadius = 20 * itemView.getContext().getResources().getDisplayMetrics().density;
        Path path = createRoundedRectPath(itemView, dX, cornerRadius);

        canvas.drawPath(path, paint);
    }

    private Path createRoundedRectPath(View itemView, float dX, float cornerRadius) {
        Path path = new Path();
        addCorner(path, itemView.getLeft(), itemView.getTop(), cornerRadius, true);
        drawEdge(path, itemView.getLeft() + cornerRadius, itemView.getTop(), itemView.getLeft() + dX, itemView.getTop());
        drawEdge(path, itemView.getLeft() + dX, itemView.getTop(), itemView.getLeft() + dX, itemView.getBottom());
        drawEdge(path, itemView.getLeft() + dX, itemView.getBottom(), itemView.getLeft() + cornerRadius, itemView.getBottom());
        addCorner(path, itemView.getLeft(), itemView.getBottom(), cornerRadius, false);
        path.close();
        return path;
    }

    private void addCorner(Path path, float x, float y, float radius, boolean isTop) {
        if (isTop) {
            path.moveTo(x, y + radius);
            path.quadTo(x, y, x + radius, y);
        } else {
            path.lineTo(x + radius, y);
            path.quadTo(x, y, x, y - radius);
        }
    }

    private void drawEdge(Path path, float startX, float startY, float endX, float endY) {
        path.lineTo(endX, endY);
    }

    private void drawDeleteIcon(Canvas canvas, View itemView) {
        Drawable trashIcon = ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_trash);
        if (trashIcon != null) {
            int[] iconBounds = calculateIconBounds(trashIcon, itemView);
            trashIcon.setBounds(iconBounds[0], iconBounds[1], iconBounds[2], iconBounds[3]);
            trashIcon.draw(canvas);
        }
    }

    private int[] calculateIconBounds(Drawable trashIcon, View itemView) {
        float scaleFactor = 1.5f;
        int iconWidth = (int) (trashIcon.getIntrinsicWidth() * scaleFactor);
        int iconHeight = (int) (trashIcon.getIntrinsicHeight() * scaleFactor);

        int left = itemView.getLeft() + 36;
        int top = itemView.getTop() + (itemView.getHeight() - iconHeight) / 2;
        int right = left + iconWidth;
        int bottom = top + iconHeight;

        return new int[]{left, top, right, bottom};
    }
}