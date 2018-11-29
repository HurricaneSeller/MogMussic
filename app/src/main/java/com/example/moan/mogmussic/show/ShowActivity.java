package com.example.moan.mogmussic.show;

import android.os.Bundle;

import com.example.moan.mogmussic.R;
import com.example.moan.mogmussic.show.showmain.ShowFragment;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class ShowActivity extends AppCompatActivity implements ShowContract.IChangeFra {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorOrange400));
        change(new ShowFragment());
    }

    @Override
    public void change(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.activity_main_frame_layout, fragment);
        transaction.commit();

    }
}