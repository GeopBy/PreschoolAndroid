package com.example.preschool.Event;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import com.example.preschool.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Calendar;

public class EventsActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private FloatingActionButton addEvent;
    private DatabaseReference UsersRef, EventsRef;
    private FirebaseAuth mAuth;
    private String current_user_id;
    private String idClass, idTeacher;
    private Bundle bundle;
    private RecyclerView myEventsList;
    private Boolean isTeacher = false;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sự kiện");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // and this
//                startActivity(new Intent(MessageActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        });
        // Get Bundle
        bundle = getIntent().getExtras();
        if (bundle != null) {
            idClass = bundle.getString("ID_CLASS");
            idTeacher = bundle.getString("ID_TEACHER");
        }
        // Set name ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.event);
        mAuth = FirebaseAuth.getInstance();
        myEventsList = findViewById(R.id.recycleEvent);
        myEventsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(EventsActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myEventsList.setLayoutManager(linearLayoutManager);
        current_user_id = mAuth.getCurrentUser().getUid();
        EventsRef = FirebaseDatabase.getInstance().getReference("Class").child(idClass).child("Events");

        addEvent = findViewById(R.id.add_event);
        addEvent.setVisibility(View.GONE);
        if (current_user_id.equals(idTeacher)) {
            isTeacher = true;
            addEvent.setVisibility(View.VISIBLE);
        }
        calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String x;

                if (dayOfMonth < 10) {
                    if ((month + 1) < 10) {
                        x = "0" + dayOfMonth + "-0" + (month + 1) + "-" + year;
                    } else {
                        x = "0" + dayOfMonth + "-" + (month + 1) + "-" + year;
                    }
                } else {
                    if ((month + 1) < 10) {
                        x = dayOfMonth + "-0" + (month + 1) + "-" + year;
                    } else {
                        x = dayOfMonth + "-" + (month + 1) + "-" + year;
                    }
                }
                showAllEvent(x);
            }
        });

        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventsActivity.this, AddEventActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                recreate();
            }
        });
    }

    private void showAllEvent(final String x) {
        Query showAllEventQuery = EventsRef.child(x);
        FirebaseRecyclerOptions<Event> options = new FirebaseRecyclerOptions.Builder<Event>().
                setQuery(showAllEventQuery, Event.class).build();
        FirebaseRecyclerAdapter<Event, EventsViewHolder> adapter = new FirebaseRecyclerAdapter<Event, EventsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull EventsViewHolder eventsViewHolder, int i, @NonNull Event event) {
                eventsViewHolder.txtEvent.setText("Sự kiện: " + event.getNameEvent());
                if (event.getTimeStart().equals("Cả ngày")) {
                    eventsViewHolder.txtTime.setText("Thời gian: Cả ngày");
                } else eventsViewHolder.txtTime.setText("Thời gian từ: " + event.getTimeStart() +
                        " đến " + event.getTimeEnd());
                eventsViewHolder.txtPlace.setText("Địa điểm: " + event.getPosition());
                eventsViewHolder.txtDetail.setText("Mô tả: " + event.getDescription());
                final String visit_event_id = getRef(i).getKey();
                if (isTeacher) {
                    eventsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CharSequence options[] = new CharSequence[]{
                                    "Delete Event ",
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(EventsActivity.this);
//
                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == 0) {
                                        EventsRef.child(x).child(visit_event_id).removeValue();
                                    }
                                }
                            });
                            builder.show();
                        }
                    });
                }
            }

            @NonNull
            @Override
            public EventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_event_layout, parent, false);

                EventsViewHolder viewHolder = new EventsViewHolder(view);
                return viewHolder;
            }
        };

        myEventsList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();

    }


    @Override
    protected void onStart() {
        super.onStart();
        Calendar cal = Calendar.getInstance();
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        String x;
        if (dayOfMonth < 10) {
            if ((month + 1) < 10) {
                x = "0" + dayOfMonth + "-0" + (month + 1) + "-" + year;
            } else {
                x = "0" + dayOfMonth + "-" + (month + 1) + "-" + year;
            }
        } else {
            if ((month + 1) < 10) {
                x = dayOfMonth + "-0" + (month + 1) + "-" + year;
            } else {
                x = dayOfMonth + "-" + (month + 1) + "-" + year;
            }
        }
        showAllEvent(x);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public static class EventsViewHolder extends RecyclerView.ViewHolder {
        private TextView txtEvent;
        private TextView txtTime;
        private TextView txtPlace;
        private TextView txtDetail;

        public EventsViewHolder(View itemView) {
            super(itemView);
            txtEvent = itemView.findViewById(R.id.txtEvent);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtPlace = itemView.findViewById(R.id.txtPlace);
            txtDetail = itemView.findViewById(R.id.txtDetail);
        }
    }
}
