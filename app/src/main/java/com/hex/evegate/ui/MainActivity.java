package com.hex.evegate.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.hex.evegate.R;
import com.hex.evegate.api.StationApi;
import com.hex.evegate.api.dto.NowPlayingDto;
import com.hex.evegate.net.RetrofitClient;
import com.hex.evegate.radio.PlaybackStatus;
import com.hex.evegate.radio.RadioManager;
import com.hex.evegate.util.Shoutcast;
import com.hex.evegate.util.ShoutcastHelper;
import com.hex.evegate.util.ShoutcastListAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    ImageButton trigger;
    ListView listView;
    TextView tvCount;
    TextView tvSongName;
    ImageView ivImage;
    ImageView ivBackground;
    TextView textView;
    LinearLayout llFirst;
    TextView tvFirst;
    ImageView ivFirst;
    LinearLayout llSecond;
    TextView tvSecond;
    ImageView ivSecond;
    View subPlayer;

    RadioManager radioManager;

    String streamURL;
    private long lastBackPressTime;

    private CompositeDisposable compositeDisposable;
    private Retrofit retrofit;
    private StationApi stationApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialize();
        configNet();
        getNowPlayingDto();
    }

    private void initialize() {

        setContentView(R.layout.activity_main);

        radioManager = RadioManager.with(this);

        trigger = findViewById(R.id.playTrigger);
        listView = findViewById(R.id.listview);
        listView.setAdapter(new ShoutcastListAdapter(this, ShoutcastHelper.retrieveShoutcasts(this)));
        tvCount = findViewById(R.id.tvCount);
        tvSongName = findViewById(R.id.tvSongName);
        ivImage = findViewById(R.id.ivImage);
        ivBackground = findViewById(R.id.ivBackground);
        textView = findViewById(R.id.name);
        llFirst = findViewById(R.id.llFirst);
        tvFirst = findViewById(R.id.tvFirst);
        ivFirst = findViewById(R.id.ivFirst);
        llSecond = findViewById(R.id.llSecond);
        tvSecond = findViewById(R.id.tvSecond);
        ivSecond = findViewById(R.id.ivSecond);
        subPlayer = findViewById(R.id.sub_player);

        trigger.setOnClickListener(v -> {
            if(TextUtils.isEmpty(streamURL)) return;

            radioManager.playOrPause(streamURL);
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Shoutcast shoutcast = (Shoutcast) parent.getItemAtPosition(position);
            if(shoutcast == null){

                return;

            }

            textView.setText(shoutcast.getName());

            subPlayer.setVisibility(View.VISIBLE);

            streamURL = shoutcast.getUrl();

            radioManager.playOrPause(streamURL);
        });

        llFirst.setOnClickListener(firstClickListener);
        tvFirst.setOnClickListener(firstClickListener);
        ivFirst.setOnClickListener(firstClickListener);

        llSecond.setOnClickListener(secondClickListener);
        tvSecond.setOnClickListener(secondClickListener);
        ivSecond.setOnClickListener(secondClickListener);
    }

    private void getNowPlayingDto() {
        compositeDisposable.add(stationApi.nowPlaying()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(this::handleNowPlayingResponse, this::handleNowPlayingError));
    }

    private void handleNowPlayingResponse(Response<NowPlayingDto> result) {
        if (result.isSuccessful()) {
            if (result.body() != null) {
                tvCount.setText(result.body().getListeners().getTotal());
                tvSongName.setText(result.body().getNow_playing().getSong().getText());
                try {
                    Glide.with(this).load(result.body().getNow_playing().getSong().getArt())
                            .into(ivBackground);
                } catch (Exception e) {
                    /*ignored*/
                }
            }
        } else {
            Toast.makeText(this, "Ашипко!", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleNowPlayingError(Throwable error) {
        Toast.makeText(this, "Ашипко!", Toast.LENGTH_SHORT).show();
    }

    private void configNet() {
        compositeDisposable = new CompositeDisposable();
        retrofit = RetrofitClient.INSTANCE.getInstance();
        stationApi = retrofit.create(StationApi.class);
    }

    View.OnClickListener firstClickListener = v -> startActivity(new Intent(Intent.ACTION_VIEW,
            Uri.parse("https://t.me/evegateradio")));

    View.OnClickListener secondClickListener = v -> startActivity(new Intent(Intent.ACTION_VIEW,
            Uri.parse("https://t.me/EVE_ONLINE_RUS")));

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
        compositeDisposable.clear();

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
        if (System.currentTimeMillis() - lastBackPressTime < 1000) {
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
}
