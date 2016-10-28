package ca.stmarysorthodoxchurch.churchadmin;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ca.stmarysorthodoxchurch.churchadmin.databinding.ActivityScheduleBinding;
import ca.stmarysorthodoxchurch.churchadmin.databinding.ScheduleListItemBinding;
import ca.stmarysorthodoxchurch.churchadmin.helper.ScheduleLab;
import ca.stmarysorthodoxchurch.churchadmin.models.Schedule;

public class ScheduleActivity extends AppCompatActivity {
    private static final String TAG = "ScheduleActivity";
    ArrayList<Schedule> mSchedule = new ArrayList<>();
    ArrayList<String> mKeys = new ArrayList<>();
    private ActivityScheduleBinding binding;
    private ScheduleAdapter mScheduleAdapter = new ScheduleAdapter();
    private Paint p = new Paint();

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
        binding.scheduleRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Log.d(TAG, "onSwiped: " + direction);
                int position = viewHolder.getAdapterPosition();
                ScheduleLab.getDatabase("/schedule").child(mKeys.get(position)).removeValue();
                mKeys.remove(position);
                mSchedule.remove(position);
                mScheduleAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Log.d("skdkskdf", "onChildDraw "+dX);
                Bitmap icon;
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if(dX > 0){
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                        c.drawRect(background,p);
                        Drawable drawable = AppCompatDrawableManager.get().getDrawable(getApplicationContext(), R.drawable.ic_delete_black_24dp);
                        icon = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(icon);
                        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                        drawable.draw(canvas);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
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
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    mKeys.add(child.getKey());
                    mSchedule.add(child.getValue(Schedule.class));
                }
                Log.d(TAG, "onDataChange: " + mSchedule.size());
                binding.scheduleRecyclerView.setAdapter(mScheduleAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: databaseError " + databaseError.getDetails());
            }
        });
    }

    private class ScheduleHolder extends RecyclerView.ViewHolder {
        ScheduleListItemBinding binding;

        public ScheduleHolder(View itemView, ScheduleListItemBinding binding) {
            super(itemView);
            this.binding = binding;
            this.binding.executePendingBindings();
        }

        public void bindEvent(int position) {
            Log.d(TAG, "bindEvent: " + mSchedule.get(position).getTitle());
            binding.setSchedule(mSchedule.get(position));
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
