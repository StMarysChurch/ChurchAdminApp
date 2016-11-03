package ca.stmarysorthodoxchurch.churchadmin.helper;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ca.stmarysorthodoxchurch.churchadmin.R;
import ca.stmarysorthodoxchurch.churchadmin.models.Schedule;


/**
 * Created by roneythomas on 2016-11-01.
 */

public class ListBinder {

    @BindingAdapter("bind:events")
    public static void bindEvents(LinearLayout layout, Schedule schedule) {
        LayoutInflater inflater = (LayoutInflater) layout.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (schedule != null) {
            for (int a = 0; a < schedule.getEvents().size(); a++) {
                TextView textView = (TextView) inflater.inflate(R.layout.event_list_item, null);
                textView.setText(schedule.getTimes().get(a)+":    "+schedule.getEvents().get(a));
                layout.addView(textView);
            }
        }
    }
}
