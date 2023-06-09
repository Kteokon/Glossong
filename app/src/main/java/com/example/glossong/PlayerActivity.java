package com.example.glossong;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.glossong.fragment.DeleteSongDialog;
import com.example.glossong.fragment.FullscreenDialog;
import com.example.glossong.fragment.MyAlertDialog;
import com.example.glossong.model.Artist;
import com.example.glossong.model.SongAndArtist;
import com.example.glossong.model.MyMediaPlayer;
import com.example.glossong.model.Song;
import com.example.glossong.viewmodel.NoteViewModel;
import com.example.glossong.viewmodel.SongViewModel;
import com.google.android.exoplayer2.DeviceInfo;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MediaMetadata;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Tracks;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.text.CueGroup;
import com.google.android.exoplayer2.trackselection.TrackSelectionParameters;
import com.google.android.exoplayer2.util.MimeTypes;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PlayerActivity extends AppCompatActivity {
    public static final int UPDATE_SONG_REQUEST = 1;

    SeekBar seekBar;
    TextView timePassedTV, timeOverTV, songTV, artistTV;
    ImageButton homeButton, updateButton, dictionaryButton,
            noteButton, playButton, shuffleButton, loopButton;
    Button lyricsButton;

    SongViewModel songViewModel;

    ExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);
        songViewModel.getSongs().observe(this, new Observer<List<SongAndArtist>>() {
            @Override
            public void onChanged(List<SongAndArtist> _songs) {
                List<MediaItem> playlist = new ArrayList<>();

                MyMediaPlayer.seekBar = seekBar;
                MyMediaPlayer.timeOverTV = timeOverTV;
                MyMediaPlayer.songTV = songTV;
                MyMediaPlayer.artistTV = artistTV;
                MyMediaPlayer.updateButton = updateButton;
                MyMediaPlayer.playButton = playButton;
                MyMediaPlayer.nowPlayingSong.setVisibility(View.VISIBLE);

                player = MyMediaPlayer.getInstance(getApplicationContext());

                Intent intent = getIntent();
                int nowPlaying = intent.getIntExtra("nowPlaying", 0);

//                if (player.getMediaItemCount() == 0) {
                    MyMediaPlayer.songs = _songs;
                    for (SongAndArtist i: _songs) {
                        MediaItem item = new MediaItem.Builder()
                                .setUri(i.song.getSource())
                                .setMimeType(MimeTypes.BASE_TYPE_AUDIO)
                                .build();
                        playlist.add(item);
                    }

                    MyMediaPlayer.nowPlaying = nowPlaying;
                    player.setMediaItems(playlist, MyMediaPlayer.nowPlaying, 0);
                    player.play();
//                }
//                else {
//                    if (! MyMediaPlayer.songs.equals(_songs)) {
//                        Log.d("mytag", "update");
//                        MyMediaPlayer.songs = _songs;
//                        for (SongAndArtist i: _songs) {
//                            MediaItem item = new MediaItem.Builder()
//                                    .setUri(i.song.getSource())
//                                    .setMimeType(MimeTypes.BASE_TYPE_AUDIO)
//                                    .build();
//                            playlist.add(item);
//                        }
//                    }
//                    if (nowPlaying != MyMediaPlayer.nowPlaying){
//                        MyMediaPlayer.nowPlaying = nowPlaying;
//                        player.seekTo(MyMediaPlayer.nowPlaying, 0);
//                        player.play();
//                    }
//                    else {
//                        Song song = _songs.get(MyMediaPlayer.nowPlaying).song;
//                        Artist artist = _songs.get(MyMediaPlayer.nowPlaying).artist;
//                        songTV.setText(song.getName());
//                        artistTV.setText(artist.getName());
//                        if (song.getSource().substring(0, 5).equals("https")) {
//                            updateButton.setVisibility(View.GONE);
//                        }
//                        else {
//                            updateButton.setVisibility(View.VISIBLE);
//                        }
//                        timePassedTV.setText(Functions.getSongDuration(player.getCurrentPosition()));
//                        timeOverTV.setText(Functions.getSongDuration(player.getDuration()));
//                        seekBar.setMax((int) (player.getDuration() / 1000));
//                        seekBar.setProgress((int) (player.getCurrentPosition() / 1000));
//
//                        if (player.isPlaying()) {
//                            playButton.setImageResource(R.drawable.pause_icon);
//                        }
//                        else {
//                            playButton.setImageResource(R.drawable.play_icon);
//                        }
//                    }
//                }
            }
        });

        playButton = findViewById(R.id.playButton);
        shuffleButton = findViewById(R.id.shuffleButton);
        loopButton = findViewById(R.id.loopButton);
        seekBar = findViewById(R.id.seekBar);
        timePassedTV = findViewById(R.id.timePassed);
        timeOverTV = findViewById(R.id.timeOver);
        songTV = findViewById(R.id.songName);
        artistTV = findViewById(R.id.artistName);
        homeButton = findViewById(R.id.homeButton);
        updateButton = findViewById(R.id.updateButton);
        dictionaryButton = findViewById(R.id.dictionaryButton);
        noteButton = findViewById(R.id.noteButton);
        lyricsButton = findViewById(R.id.lyricsButton);

        Handler handler = new Handler();
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(player != null){
                    int currentPosition = (int) player.getCurrentPosition();
                    seekBar.setProgress(currentPosition / 1000);
                    timePassedTV.setText(Functions.getSongDuration(currentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(player != null && fromUser){
                    player.seekTo(progress * 1000);
                    timePassedTV.setText(Functions.getSongDuration(progress * 1000));
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

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lyricsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<SongAndArtist> songs = MyMediaPlayer.songs;
                int nowPlaying = MyMediaPlayer.nowPlaying;

                DialogFragment dialog = FullscreenDialog.newInstance(songs.get(nowPlaying).song.getId(), songs.get(nowPlaying).song.getLyrics(), songs.get(nowPlaying).song.getTranslation());
                dialog.show(getSupportFragmentManager(), "tag");
            }
        });

        dictionaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<SongAndArtist> songs = MyMediaPlayer.songs;
                int nowPlaying = MyMediaPlayer.nowPlaying;
                Intent intent = new Intent(getApplicationContext(), DictionaryActivity.class);
                intent.putExtra("words", "song");
                intent.putExtra("songId", songs.get(nowPlaying).song.getId());
                startActivity(intent);
            }
        });
    }

    public void skipSong(View v){
        List<SongAndArtist> songs = MyMediaPlayer.songs;
        int nowPlaying = MyMediaPlayer.nowPlaying;
        switch (v.getId()) {
            case R.id.skipLeftButton: {
                if (nowPlaying > 0) {
                    nowPlaying--;
                    player.seekTo(nowPlaying, 0);
                }
                break;
            }
            case R.id.skipRightButton: {
                if (nowPlaying < songs.size() - 1) {
                    nowPlaying++;
                    player.seekTo(nowPlaying, 0);
                }
                break;
            }
        }
        MyMediaPlayer.nowPlaying = nowPlaying;
    }

    public void updateSong(View v) {
        List<SongAndArtist> songs = MyMediaPlayer.songs;
        int nowPlaying = MyMediaPlayer.nowPlaying;

        Intent intent = new Intent(this, UpdateSongActivity.class);

        Song song = songs.get(nowPlaying).song;
        Artist artist = songs.get(nowPlaying).artist;

        intent.putExtra(UpdateSongActivity.ARTIST_NAME, artist.getName());
        intent.putExtra(UpdateSongActivity.SONG_NAME, song.getName());
        intent.putExtra(UpdateSongActivity.SONG_LYRICS, song.getLyrics());
        intent.putExtra(UpdateSongActivity.SONG_TRANSLATION, song.getTranslation());

        startActivityForResult(intent, UPDATE_SONG_REQUEST);
    }

    public void addUpdateNotes(View v) {
        List<SongAndArtist> songs = MyMediaPlayer.songs;
        int nowPlaying = MyMediaPlayer.nowPlaying;

        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra("songId", songs.get(nowPlaying).song.getId());
        startActivity(intent);
    }

    public void songDictionary(View v) {
        Intent intent = new Intent(this, DictionaryActivity.class);
        intent.putExtra("words", "song");
        intent.putExtra("words", "song");
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UPDATE_SONG_REQUEST && resultCode == RESULT_OK) {
            Long artistId = data.getLongExtra(UpdateSongActivity.ARTIST_ID, 1L);
            String artistName = data.getStringExtra(UpdateSongActivity.ARTIST_NAME);
            String songName = data.getStringExtra(UpdateSongActivity.SONG_NAME);
            String songLyrics = data.getStringExtra(UpdateSongActivity.SONG_LYRICS);
            String songTranslation = data.getStringExtra(UpdateSongActivity.SONG_TRANSLATION);

            List<SongAndArtist> songs = MyMediaPlayer.songs;
            int nowPlaying = MyMediaPlayer.nowPlaying;

            Song song = new Song(songs.get(nowPlaying).song.getSource(), songLyrics, songTranslation);
            song.setId(songs.get(nowPlaying).song.getId());
            song.setArtistId(artistId);
            song.setName(songName);

            String button = data.getStringExtra("button");
            if (button.equals("delete")) {
                boolean deleteWords = data.getBooleanExtra("deleteWords", false);
                ViewModelStoreOwner viewModelStoreOwner = this;
                new Functions().deleteSong(viewModelStoreOwner, this, song, deleteWords);
                player.stop();
                finish();
            }
            else{
                songViewModel.update(song);

                songTV.setText(songName);
                artistTV.setText(artistName);

                Log.d("mytag", "Updated");
            }
        }
        else {
            Log.d("mytag", "return");
        }
    }
}