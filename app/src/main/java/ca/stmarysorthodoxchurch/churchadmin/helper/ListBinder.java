package ca.stmarysorthodoxchurch.churchadmin.helper;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import ca.stmarysorthodoxchurch.churchadmin.R;
import ca.stmarysorthodoxchurch.churchadmin.models.Schedule;


/**
 * Created by roneythomas on 2016-11-01.
 */

public class ListBinder {

    @BindingAdapter("events")
    public static void bindEvents(LinearLayout layout, Schedule schedule) {
        LayoutInflater inflater = (LayoutInflater) layout.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (schedule != null) {
            for (int a = 0; a < schedule.getEvents().size(); a++) {
                String x = schedule.getTimes().get(a) + ":  " + schedule.getEvents().get(a);
                if (schedule.getTimes().get(a).length() < 8) {
                    x = "  " + x;
                }
                SpannableStringBuilder str = new SpannableStringBuilder(x);
                str.setSpan(new StyleSpan(Typeface.BOLD),
                        0,
                        10,
                        SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
                );
                TextView textView = (TextView) inflater.inflate(R.layout.event_list_item, null);
                textView.setText(str);
                layout.addView(textView);
            }
        }
    }
}
