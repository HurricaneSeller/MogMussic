package com.example.moan.mogmussic.show;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moan.mogmussic.R;
import com.example.moan.mogmussic.show.showmain.ShowFragment;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowActivity extends AppCompatActivity implements ShowContract.IChangeFra {
    @BindView(R.id.activity_main_name)
    TextView nameView;
    @BindView(R.id.activity_main_artist)
    TextView artistView;
    @BindView(R.id.activity_main_control)
    ImageButton controlButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorOrange400));
        change(new ShowFragment());

        IntentFilter intentFilter = new IntentFilter();
        registerReceiver(changeBarBroadcastReceiver, intentFilter);
    }

    @Override
    public void change(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.activity_main_frame_layout, fragment);
        transaction.commit();
    }

    private BroadcastReceiver changeBarBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action == null) {
                return;
            }
            switch (action) {
                // TODO: 11/29/18
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(changeBarBroadcastReceiver);
    }
}