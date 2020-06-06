package edu.tacoma.uw.guilbb.courseswebservicesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;


/**
 * This activity allows the app to send notifications to the device
 */
public class NotificationActivity extends AppCompatActivity {

    /**
     * Creates the Notification Activity from the saved instance state
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        TextView textView = findViewById(R.id.text_view);

        String message = getIntent().getStringExtra("message");
        textView.setText(message);
    }
}
