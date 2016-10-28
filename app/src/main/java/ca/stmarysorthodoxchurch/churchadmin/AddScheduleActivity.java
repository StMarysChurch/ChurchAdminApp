package ca.stmarysorthodoxchurch.churchadmin;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormatSymbols;
import java.util.ArrayList;

import ca.stmarysorthodoxchurch.churchadmin.databinding.ActivityAddscheduleBinding;
import ca.stmarysorthodoxchurch.churchadmin.databinding.EditTextListItemBinding;
import ca.stmarysorthodoxchurch.churchadmin.helper.ScheduleLab;
import ca.stmarysorthodoxchurch.churchadmin.models.Schedule;

/**
 * Created by roneythomas on 2016-10-05.
 */

public class AddScheduleActivity extends AppCompatActivity {
    private static final String TAG = "AddScheduleActivity";
    Schedule schedule = new Schedule();
    private ArrayAdapter<String> suggestionAdapter;
    private ItemEventAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityAddscheduleBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_addschedule);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        suggestionAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, new String[]{"Prabhatha Namaskaram", "Holy Qurbana", "Sunday School"});
        binding.eventRecylerView.setLayoutManager(new LinearLayoutManager(this));
        binding.titleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                schedule.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        adapter = new ItemEventAdapter();
        schedule.setEvents(new ArrayList<String>());
        binding.eventRecylerView.setAdapter(adapter);
        binding.dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddScheduleActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        binding.titleEditText.setText(new DateFormatSymbols().getMonths()[month - 1]+", ");
                    }
                }, 2016, 7, 7).show();
            }
        });
        binding.addTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                schedule.getEvents().add("");
                adapter.notifyItemChanged(schedule.getEvents().size() - 1);
                Log.d(TAG, "onClick: " + schedule.getEvents().size());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_schedule, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.save_menu:
                Toast.makeText(this, "Yes this is working", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onOptionsItemSelected: " + schedule.getTitle());
                for (String x : schedule.getEvents()) {
                    Log.d(TAG, "onOptionsItemSelected: " + x);
                }
                schedule.setExpiryDate(String.valueOf(System.currentTimeMillis()));
                ScheduleLab.getDatabase("/schedule").push().setValue(schedule);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class ItemEventHolder extends RecyclerView.ViewHolder {
        EditTextListItemBinding binding;

        public ItemEventHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.getBinding(itemView);
        }

        public void bindEvent(final int position) {
            binding.dateTextView.setText("Current Date");
            binding.dateTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new TimePickerDialog(AddScheduleActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            binding.dateTextView.setText(String.format("%d:%02d", hourOfDay, minute));
                        }
                    }, 8, 7, false).show();
                }
            });
            binding.eventEditText.setText(schedule.getEvents().get(position));
            binding.eventEditText.setAdapter(suggestionAdapter);
            binding.eventEditText.showDropDown();
            binding.eventEditText.requestFocus();
            binding.eventEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    schedule.getEvents().set(position, s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
    }

    private class ItemEventAdapter extends RecyclerView.Adapter<ItemEventHolder> {

        @Override
        public ItemEventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.d(TAG, "onCreateViewHolder: called");
            EditTextListItemBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.edit_text_list_item, parent, false);
            return new ItemEventHolder(binding.getRoot());
        }

        @Override
        public void onBindViewHolder(ItemEventHolder holder, int position) {
            holder.bindEvent(position);
        }

        @Override
        public int getItemCount() {
            Log.d(TAG, "getItemCount: I am being called " + schedule.getEvents().size());
            return schedule.getEvents().size();
        }
    }
}
