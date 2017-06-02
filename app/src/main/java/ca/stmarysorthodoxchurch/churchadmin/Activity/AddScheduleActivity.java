package ca.stmarysorthodoxchurch.churchadmin.Activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import ca.stmarysorthodoxchurch.churchadmin.R;
import ca.stmarysorthodoxchurch.churchadmin.databinding.ActivityAddScheduleBinding;
import ca.stmarysorthodoxchurch.churchadmin.databinding.EditTextListItemBinding;
import ca.stmarysorthodoxchurch.churchadmin.helper.ScheduleLab;
import ca.stmarysorthodoxchurch.churchadmin.helper.TouchHelper;
import ca.stmarysorthodoxchurch.churchadmin.models.Schedule;

/**
 * Created by roneythomas on 2016-10-05.
 */

public class AddScheduleActivity extends AppCompatActivity {
    public static final String CURRENT_TIME = "Current Time";
    private static final String TAG = "AddScheduleActivity";
    public static String KEY = "key";
    private Schedule schedule = new Schedule();
    private ArrayAdapter<String> suggestionAdapter;
    private String mKey;
    private ItemEventAdapter adapter;
    private ActivityAddScheduleBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_schedule);
        Bundle extras = getIntent().getExtras();
        // Checking if we have schedule in firebase
        if (extras != null) {
            mKey = extras.getString(KEY);
            ScheduleLab.getDatabase("/schedule").child(mKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    schedule = dataSnapshot.getValue(Schedule.class);
                    initializeRecyclerView();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            // When we are adding a new schedule or not present in firebase
            initializeRecyclerView();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        suggestionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new String[]{"Prabhatha Namaskaram", "Holy Qurbana", "Sunday School"});
        final Calendar today = Calendar.getInstance();
        binding.dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddScheduleActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        GregorianCalendar date = new GregorianCalendar(year, month, dayOfMonth);
                        SimpleDateFormat fmt = new SimpleDateFormat("MMMM d, EEEE");
                        Log.d(TAG, "onDateSet: " + date.get(Calendar.DAY_OF_WEEK));
                        binding.titleEditText.setText(fmt.format(date.getTime()));
                        binding.titleEditText.setSelection(binding.titleEditText.length());
                        schedule.setTitle(fmt.format(date.getTime()));
                        schedule.setExpiryDate(date.getTimeInMillis());
                    }
                }, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        binding.addTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: " + schedule.getEvents().size());
                schedule.getTimes().add(CURRENT_TIME);
                schedule.getEvents().add("");
                if (schedule.getEvents().size() == 1) {
                    initializeRecyclerView();
                }
                adapter.notifyItemChanged(schedule.getEvents().size() - 1);
                Log.d(TAG, "onClick: " + schedule.getEvents().size());
            }
        });
        Log.d(TAG, "onCreate: ");
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TouchHelper(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT, getApplicationContext()) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Log.d(TAG, "onSwiped: " + direction);
                int position = viewHolder.getAdapterPosition();
                schedule.getEvents().remove(position);
                adapter.notifyItemRemoved(position);
            }
        });
        itemTouchHelper.attachToRecyclerView(binding.eventRecylerView);
    }

    private void initializeRecyclerView() {
        binding.eventRecylerView.setLayoutManager(new LinearLayoutManager(this));
        binding.setSchedule(schedule);
        adapter = new ItemEventAdapter();
        binding.eventRecylerView.setAdapter(adapter);
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
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onOptionsItemSelected: title" + schedule.getTitle());
                for (int a = schedule.getEvents().size() - 1; a >= 0; a--) {
                    Log.d(TAG, "onOptionsItemSelected: size " + a);
                    Log.d(TAG, "onOptionsItemSelected: event " + schedule.getEvents().get(a).length());
                    if (schedule.getEvents().get(a).contentEquals("") | schedule.getEvents().get(a).length() == 0) {
                        Log.d(TAG, "onOptionsItemSelected: event remove " + schedule.getEvents().get(a).length());
                        Log.d(TAG, "onOptionsItemSelected: event remove " + a);
                        schedule.getEvents().remove(a);
                    }
                }
                if (mKey != null) {
                    ScheduleLab.getDatabase("/schedule").child(mKey).setValue(schedule).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: " + e.getMessage());
                            SignInActivity.signOut();
                            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                            finish();
                        }
                    });
                } else {
                    Log.d(TAG, "onOptionsItemSelected: " + schedule.getTitle());
                    ScheduleLab.getDatabase("/schedule").push().setValue(schedule).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: " + e.toString());
                            SignInActivity.signOut();
                            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                            finish();
                        }
                    });
                }
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
            final Calendar calendar = Calendar.getInstance();
            if (!schedule.getTimes().get(position).contentEquals(CURRENT_TIME)) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
                try {
                    calendar.setTime(dateFormat.parse(schedule.getTimes().get(position)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            binding.timeTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new TimePickerDialog(AddScheduleActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            String x;
                            if (hourOfDay < 12) {
                                x = String.format("%d:%02d %s", hourOfDay, minute, "am");
                            } else {
                                x = String.format("%d:%02d %s", hourOfDay - 12, minute, "pm");
                            }
                            binding.timeTextView.setText(x);
                            schedule.getTimes().set(position, x);
                        }
                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
                }
            });
            binding.timeTextView.setText(schedule.getTimes().get(position));
            binding.eventEditText.setText(schedule.getEvents().get(position));
            binding.eventEditText.setAdapter(suggestionAdapter);
            if (mKey != null) {
                binding.eventEditText.clearFocus();
            } else {
                binding.eventEditText.showDropDown();
                binding.eventEditText.requestFocus();
            }
            binding.eventEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString().length() != 0) {
                        Log.d(TAG, "onTextChanged: " + s.toString().length());
                        schedule.getEvents().set(position, s.toString());
                    }
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
            if (schedule.getEvents() == null) {
                return 0;
            }
            Log.d(TAG, "getItemCount: I am being called " + schedule.getEvents().size());
            return schedule.getEvents().size();
        }
    }
}
