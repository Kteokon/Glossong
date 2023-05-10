package com.example.glossong;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.glossong.fragment.FullscreenDialog;
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

                MyMediaPlayer.songs = _songs;
                MyMediaPlayer.seekBar = seekBar;
                MyMediaPlayer.timeOverTV = timeOverTV;
                MyMediaPlayer.songTV = songTV;
                MyMediaPlayer.artistTV = artistTV;
                MyMediaPlayer.updateButton = updateButton;
                MyMediaPlayer.playButton = playButton;

                player = MyMediaPlayer.getInstance(getApplicationContext());

                Log.d("PlayerListener", "setting songs");

                for (SongAndArtist i: _songs) {
                    MediaItem item = new MediaItem.Builder()
                            .setUri(i.song.getSource()) // TODO: делать проверку на интернет. Если нет, то проверяем местонахождение на устройстве
                            .setMimeType(MimeTypes.BASE_TYPE_AUDIO)
                            .build();
                    playlist.add(item);
                }

                player.setMediaItems(playlist, MyMediaPlayer.nowPlaying, 0);
                player.prepare();
                player.play();
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
                    timePassedTV.setText(getSongDuration(currentPosition));
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
                    timePassedTV.setText(getSongDuration(progress * 1000));
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
                    player.play(); // TODO: если объект null, то выводить сообщение
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
                player.prepare();
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
                songViewModel.delete(song);
                player.stop();

                Log.d("mytag", "Deleted");
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

    private String getSongDuration(int dur) {
        int songMin = dur / 1000 / 60;
        int songSec = dur / 1000 % 60;
        String res = Integer.toString(songMin) + ":";
        if (songMin / 10 == 0) {
            res = "0" + res;
        }
        if (songSec / 10 == 0) {
            res = res + "0" + Integer.toString(songSec);
        }
        else {
            res = res + Integer.toString(songSec);
        }
        return res;
    }
}