package ca.stmarysorthodoxchurch.churchadmin.helper;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ca.stmarysorthodoxchurch.churchadmin.R;


/**
 * Created by roneythomas on 2016-11-01.
 */

public class ListBinder {

    @BindingAdapter("bind:events")
    public static void bindList(LinearLayout layout, ArrayList<String> list) {
        LayoutInflater inflater = (LayoutInflater) layout.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (list != null) {
            for (String x : list) {
                TextView textView = (TextView) inflater.inflate(R.layout.event_list_item, null);
                textView.setText(x);
                layout.addView(textView);
            }
        }
    }
}
