package com.example.glossong.listener;

import android.content.ClipData;
import android.content.ClipDescription;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class MyDragListener implements View.OnDragListener{
    @Override
    public boolean onDrag(View v, DragEvent event) {
        switch (event.getAction()){
            case DragEvent.ACTION_DRAG_STARTED: {
                return event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);
            }
            case DragEvent.ACTION_DRAG_ENTERED: case DragEvent.ACTION_DRAG_EXITED: {
                v.invalidate(); // перерисовка view без изменения размеров
                return true;
            }
            case DragEvent.ACTION_DRAG_ENDED: {
                v.invalidate();
                View view = (View) event.getLocalState();
                view.setVisibility(View.VISIBLE);
                return true;
            }
            case DragEvent.ACTION_DRAG_LOCATION: {
                return true;
            }
            case DragEvent.ACTION_DROP: {
                ClipData.Item item = event.getClipData().getItemAt(0);
                String dragData = (String) item.getText();

                v.invalidate();

                View view = (View) event.getLocalState();
                ViewGroup owner = (ViewGroup) view.getParent();
                owner.removeView(view);
                LinearLayout destination = (LinearLayout) v;
                if (destination.getChildCount() > 0) {
                    View view2 = destination.getChildAt(0);
                    destination.removeView(view2);
                    owner.addView(view2);
                }
                destination.addView(view);
                view.setVisibility(View.VISIBLE);
                return true;
            }
        }
        return false;
    }
}
