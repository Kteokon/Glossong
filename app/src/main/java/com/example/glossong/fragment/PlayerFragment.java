package com.example.glossong.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.glossong.Functions;
import com.example.glossong.PlayerActivity;
import com.example.glossong.R;
import com.example.glossong.model.MyMediaPlayer;
import com.example.glossong.model.SongAndArtist;
import com.example.glossong.viewmodel.SongViewModel;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.MimeTypes;

import java.util.ArrayList;
import java.util.List;

public class PlayerFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {
    private SongViewModel songViewModel;

    TextView timePassedTV;

    ExoPlayer player;

    public PlayerFragment() {
        // Required empty public constructor
    }

    public static PlayerFragment newInstance() {
        PlayerFragment fragment = new PlayerFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);

        ImageButton playButton = view.findViewById(R.id.playButton);
        ImageButton shuffleButton = view.findViewById(R.id.shuffleButton);
        ImageButton loopButton = view.findViewById(R.id.loopButton);
        ImageButton nextSongButton = view.findViewById(R.id.skipRightButton);
        ImageButton previousSongButton = view.findViewById(R.id.skipLeftButton);
        SeekBar seekBar = view.findViewById(R.id.seekBar);
        timePassedTV = view.findViewById(R.id.timePassed);
        TextView timeOverTV = view.findViewById(R.id.timeOver);
        TextView songTV = view.findViewById(R.id.songName);
        TextView artistTV = view.findViewById(R.id.artistName);

        seekBar.setOnSeekBarChangeListener(this);

        Handler handler = new Handler();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (player != null){
                    int currentPosition = (int) player.getCurrentPosition();
                    seekBar.setProgress(currentPosition / 1000);
                    timePassedTV.setText(Functions.getSongDuration(currentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        });
        player = MyMediaPlayer.getInstance(getContext());

        MyMediaPlayer.seekBar = seekBar;
        MyMediaPlayer.timeOverTV = timeOverTV;
        MyMediaPlayer.songTV = songTV;
        MyMediaPlayer.artistTV = artistTV;
        MyMediaPlayer.playButton = playButton;

        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);
        songViewModel.getSongs().observe(getViewLifecycleOwner(), new Observer<List<SongAndArtist>>() {
            @Override
            public void onChanged(List<SongAndArtist> _songs) {
                List<MediaItem> playlist = new ArrayList<>();
//                MyMediaPlayer.nowPlayingSong.setVisibility(View.VISIBLE);

                player = MyMediaPlayer.getInstance(getContext());

                if (player.getMediaItemCount() == 0) {
                    MyMediaPlayer.songs = _songs;
                    for (SongAndArtist i : _songs) {
                        MediaItem item = new MediaItem.Builder()
                                .setUri(i.song.getSource())
                                .setMimeType(MimeTypes.BASE_TYPE_AUDIO)
                                .build();
                        playlist.add(item);
                    }

                    player.setMediaItems(playlist, MyMediaPlayer.nowPlaying, 0);
                    player.play();
                }
                else {
                    if (MyMediaPlayer.nowPlaying == player.getCurrentMediaItemIndex()) {

                        songTV.setText(_songs.get(MyMediaPlayer.nowPlaying).song.getName());
                        artistTV.setText(_songs.get(MyMediaPlayer.nowPlaying).artist.getName());
                        timeOverTV.setText(Functions.getSongDuration(player.getDuration()));
                        if (player.isPlaying()) {
                            playButton.setImageResource(R.drawable.pause_icon);
                        }
                        if (player.getRepeatMode() == Player.REPEAT_MODE_ONE) {
                            loopButton.setImageResource(R.drawable.repeat_one_icon);
                        }
                        else {
                            if (player.getRepeatMode() == Player.REPEAT_MODE_ALL) {
                                loopButton.setImageResource(R.drawable.repeat_on_icon);
                            }
                        }
                        seekBar.setMax((int) (player.getDuration() / 1000));
                    }
                    else {
                        player.seekTo(MyMediaPlayer.nowPlaying, 0);
                    }
                }
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player.isPlaying()) {
                    player.pause();
                }
                else {
                    player.play();
                }
            }
        });

        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<SongAndArtist> songs = MyMediaPlayer.songs;
                int r = (int)(Math.random() * songs.size());
                MyMediaPlayer.nowPlaying = r;

                player.seekTo(r, 0);
                player.play();
                playButton.setImageResource(R.drawable.pause_icon);
            }
        });

        loopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player.getRepeatMode() == Player.REPEAT_MODE_OFF) {
                    player.setRepeatMode(Player.REPEAT_MODE_ONE);
                    loopButton.setImageResource(R.drawable.repeat_one_icon);
                }
                else {
                    if (player.getRepeatMode() == Player.REPEAT_MODE_ONE) {
                        player.setRepeatMode(Player.REPEAT_MODE_ALL);
                        loopButton.setImageResource(R.drawable.repeat_on_icon);
                    }
                    else {
                        player.setRepeatMode(Player.REPEAT_MODE_OFF);
                        loopButton.setImageResource(R.drawable.repeat_off_icon);
                    }
                }
            }
        });

        nextSongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyMediaPlayer.nowPlaying < MyMediaPlayer.songs.size() - 1) {
                    MyMediaPlayer.nowPlaying++;
                    player.seekTo(MyMediaPlayer.nowPlaying, 0);
                }
            }
        });

        previousSongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyMediaPlayer.nowPlaying > 0) {
                    MyMediaPlayer.nowPlaying--;
                    player.seekTo(MyMediaPlayer.nowPlaying, 0);
                }
            }
        });

        return view;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(player != null && fromUser){
            player.seekTo(progress * 1000);
            timePassedTV.setText(Functions.getSongDuration(progress * 1000));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.d("mytag", "on start tracking touch");
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.d("mytag", "on stop tracking touch");
    }
}