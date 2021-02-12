package com.example.socialapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.socialapp.data.AppData;
import com.example.socialapp.models.Bleep;
import com.mikhaellopez.circularimageview.CircularImageView;

public class BleepDetailsActivity extends AppCompatActivity {

    Bleep bleep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bleep_details);
        setup();
    }

    private void setup() {
        bleep = AppData.bleepList.get(getIntent().getIntExtra("bleepPos", -1));
        TextView bleepDetailsNick = findViewById(R.id.bleepDetailsNick);
        TextView bleepDetailsTime = findViewById(R.id.bleepDetailsTime);
        TextView bleepDetailsContent = findViewById(R.id.bleepDetailsContent);
        CircularImageView bleepDetailsPic = findViewById(R.id.bleepDetailsPic);

        Glide.with(this).load(bleep.getUser().getImage()).into(bleepDetailsPic);
        bleepDetailsNick.setText(bleep.getUser().getNick());
        bleepDetailsTime.setText(Bleep.timeStringFromMillis(bleep.getTimeMillis()));
        bleepDetailsContent.setText(bleep.getContent());

        Toolbar toolbar = findViewById(R.id.bleepDetailsToolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }
}