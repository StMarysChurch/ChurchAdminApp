package ca.stmarysorthodoxchurch.churchadmin.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ca.stmarysorthodoxchurch.churchadmin.BuildConfig;
import ca.stmarysorthodoxchurch.churchadmin.R;
import ca.stmarysorthodoxchurch.churchadmin.databinding.ActivityScheduleBinding;
import ca.stmarysorthodoxchurch.churchadmin.databinding.ScheduleListItemBinding;
import ca.stmarysorthodoxchurch.churchadmin.helper.ScheduleLab;
import ca.stmarysorthodoxchurch.churchadmin.helper.TouchHelper;
import ca.stmarysorthodoxchurch.churchadmin.models.Schedule;

import static ca.stmarysorthodoxchurch.churchadmin.Activity.SignInActivity.signOut;

public class ScheduleActivity extends AppCompatActivity {
    private static final String TAG = "ScheduleActivity";
    ArrayList<Schedule> mSchedule = new ArrayList<>();
    ArrayList<String> mKeys = new ArrayList<>();
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    private ActivityScheduleBinding binding;
    private ScheduleAdapter mScheduleAdapter = new ScheduleAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_schedule);
        binding.scheduleFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddScheduleActivity.class));
            }
        });
        Log.d(TAG, "onCreate: " + mSchedule.size());
        binding.scheduleRecyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(getApplicationContext(),
                layoutManager.getOrientation());
        binding.scheduleRecyclerView.addItemDecoration(mDividerItemDecoration);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TouchHelper(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT, getApplicationContext()) {

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Log.d(TAG, "onSwiped: " + direction);
                int position = viewHolder.getAdapterPosition();
                ScheduleLab.getDatabase("/schedule").child(mKeys.get(position)).removeValue();
                mKeys.remove(position);
                mSchedule.remove(position);
                mScheduleAdapter.notifyItemRemoved(position);
            }
        });
        itemTouchHelper.attachToRecyclerView(binding.scheduleRecyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ScheduleLab.getDatabase("/schedule").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mKeys.clear();
                mSchedule.clear();
                try {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        mKeys.add(child.getKey());
                        mSchedule.add(child.getValue(Schedule.class));
                    }
                    Log.d(TAG, "onDataChange: " + mSchedule.size());
                    binding.scheduleRecyclerView.setAdapter(mScheduleAdapter);
                } catch (DatabaseException e) {
                    binding.scheduleFab.hide();
                    AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleActivity.this);
                    builder.setTitle("Stale App").setMessage("Please update app to the latest version");
                    builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
                        }
                    });
                    AlertDialog updateDialog = builder.create();
                    updateDialog.show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: databaseError " + databaseError.getDetails());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_schedule, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                signOut();
                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class ScheduleHolder extends RecyclerView.ViewHolder {
        ScheduleListItemBinding binding;

        ScheduleHolder(View itemView, ScheduleListItemBinding binding) {
            super(itemView);
            this.binding = binding;
            this.binding.executePendingBindings();
        }

        void bindEvent(final int position) {
            Log.d(TAG, "bindEvent: " + mSchedule.get(position).getTitle());
            binding.setSchedule(mSchedule.get(position));
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getApplicationContext(), AddScheduleActivity.class).putExtra(AddScheduleActivity.KEY, mKeys.get(position)));
                }
            });
        }
    }

    private class ScheduleAdapter extends RecyclerView.Adapter<ScheduleHolder> {

        @Override
        public ScheduleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ScheduleListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.schedule_list_item, parent, false);
            Log.d(TAG, "onCreateViewHolder: ");
            return new ScheduleHolder(binding.getRoot(), binding);
        }

        @Override
        public void onBindViewHolder(ScheduleHolder holder, int position) {
            Log.d(TAG, "onBindViewHolder: " + position);
            holder.bindEvent(position);
        }

        @Override
        public int getItemCount() {
            Log.d(TAG, "getItemCount: I am being called " + mSchedule.size());
            return mSchedule.size();
        }
    }
}
