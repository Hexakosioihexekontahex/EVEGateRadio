package com.hex.evegate.ui;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hex.evegate.R;
import com.hex.evegate.radio.PlaybackStatus;
import com.hex.evegate.radio.RadioManager;
import com.hex.evegate.util.Shoutcast;
import com.hex.evegate.util.ShoutcastHelper;
import com.hex.evegate.util.ShoutcastListAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class MainActivity extends AppCompatActivity {

    boolean back = true;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.playTrigger)
    ImageButton trigger;

    @BindView(R.id.listview)
    ListView listView;

    @BindView(R.id.name)
    TextView textView;

    @BindView(R.id.llFirst)
    LinearLayout llFirst;

    @BindView(R.id.tvFirst)
    TextView tvFirst;

    @BindView(R.id.ivFirst)
    ImageView ivFirst;

    @BindView(R.id.llSecond)
    LinearLayout llSecond;

    @BindView(R.id.tvSecond)
    TextView tvSecond;

    @BindView(R.id.ivSecond)
    ImageView ivSecond;

    @BindView(R.id.sub_player)
    View subPlayer;

    RadioManager radioManager;

    String streamURL;
    private long lastBackPressTime;
    private long DOUBLE_CLICK_DELAY = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        radioManager = RadioManager.with(this);

        listView.setAdapter(new ShoutcastListAdapter(this, ShoutcastHelper.retrieveShoutcasts(this)));

        llFirst.setOnClickListener(firstClickListener);
        tvFirst.setOnClickListener(firstClickListener);
        ivFirst.setOnClickListener(firstClickListener);

        llSecond.setOnClickListener(secondClickListener);
        tvSecond.setOnClickListener(secondClickListener);
        ivSecond.setOnClickListener(secondClickListener);
    }

    View.OnClickListener firstClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/evegateradio")));
        }
    };

    View.OnClickListener secondClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/EVE_ONLINE_RUS")));
        }
    };

    @Override
    public void onStart() {

        super.onStart();

        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {

        EventBus.getDefault().unregister(this);

        super.onStop();
    }

    @Override
    protected void onDestroy() {

        radioManager.unbind();

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        radioManager.bind();
    }

    @Override
    public void onBackPressed() {
        long now = System.currentTimeMillis();
        if (now - lastBackPressTime < DOUBLE_CLICK_DELAY) {
            finish();
        } else {
            lastBackPressTime = now;
            Toast toast = Toast.makeText(MainActivity.this, "Нажмите \"Назад\" еще раз для выхода их приложения.\nДля продолжения прослушивания в фоновом режиме нажмите \"Свернуть\"", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    @Subscribe
    public void onEvent(String status){

        switch (status){

            case PlaybackStatus.LOADING:

                // loading

                break;

            case PlaybackStatus.ERROR:

                Toast.makeText(this, R.string.no_stream, Toast.LENGTH_SHORT).show();

                break;

        }

        trigger.setImageResource(status.equals(PlaybackStatus.PLAYING)
                ? R.drawable.ic_pause_black
                : R.drawable.ic_play_arrow_black);

    }

    @OnClick(R.id.playTrigger)
    public void onClicked(){

        if(TextUtils.isEmpty(streamURL)) return;

        radioManager.playOrPause(streamURL);
    }

    @OnItemClick(R.id.listview)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){

        Shoutcast shoutcast = (Shoutcast) parent.getItemAtPosition(position);
        if(shoutcast == null){

            return;

        }

        textView.setText(shoutcast.getName());

        subPlayer.setVisibility(View.VISIBLE);

        streamURL = shoutcast.getUrl();

        radioManager.playOrPause(streamURL);
    }
}
