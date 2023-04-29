package com.example.glossong;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.glossong.model.Artist;
import com.example.glossong.model.Note;
import com.example.glossong.model.SongAndArtist;
import com.example.glossong.model.MyMediaPlayer;
import com.example.glossong.model.Song;
import com.example.glossong.tuple.NoteTuple;
import com.example.glossong.viewmodel.NoteViewModel;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PlayerActivity extends AppCompatActivity {
    public static final int UPDATE_SONG_REQUEST = 1;
    public static final int ADD_NOTE_REQUEST = 2;

    SeekBar seekBar;
    TextView timePassedTV, timeOverTV, songTV, artistTV;
    Button playButton, shuffleButton, loopButton,
            homeButton, updateButton, notesButton;

    ExoPlayer player;
    List<MediaItem> playlist;
    List<SongAndArtist> songs;
    List<Integer> music;
    int nowPlaying;
    Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Intent intent = getIntent();
        songs = (List<SongAndArtist>) intent.getSerializableExtra("songs");
        nowPlaying = intent.getIntExtra("nowPlaying", 0);

        player = MyMediaPlayer.getInstance(getApplicationContext());
//        player = new ExoPlayer.Builder(getApplicationContext()).build();
        Log.d("mytag", "Назначаем листенер");

//        player.addListener(new ExoPlayer.Listener() {
//            @Override
//            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
//                Player.Listener.super.onMediaItemTransition(mediaItem, reason);
//
//                Log.d("mytag", "On media item transition");
//                Song song = songs.get(nowPlaying).song;
//                Artist artist = songs.get(nowPlaying).artist;
//                songTV.setText(song.getName());
//                artistTV.setText(artist.getName());
//                if (Character.valueOf(song.getSource().charAt(0)) == Character.valueOf('g')) {
//                    updateButton.setVisibility(View.GONE);
//                }
//                else {
//                    updateButton.setVisibility(View.VISIBLE);
//                }
//            }
//
//            @Override
//            public void onPlaybackStateChanged(int playbackState) {
//                Player.Listener.super.onPlaybackStateChanged(playbackState);
//
//                if (playbackState == ExoPlayer.STATE_READY) {
//                    Log.d("mytag", "State ready");
////                    timeOverTV.setText(getSongDuration((int) exoPlayer.getDuration()));
//                    seekBar.setMax((int) (player.getDuration() / 1000));
//                }
//                if (playbackState == ExoPlayer.STATE_IDLE) {
//                    Log.d("mytag", "State idle");
//                }
//                if (playbackState == ExoPlayer.STATE_BUFFERING) {
//                    Log.d("mytag", "State buffering");
//                }
//                if (playbackState == ExoPlayer.STATE_ENDED) {
//                    Log.d("mytag", "State ended");
//                }
//            }
//        });

        playlist = new ArrayList<>();

        playButton = findViewById(R.id.playButton);
        shuffleButton = findViewById(R.id.shuffleButton);
        loopButton = findViewById(R.id.loopButton);
        seekBar = findViewById(R.id.seekBar);
        timePassedTV = findViewById(R.id.timePassed);
        timeOverTV = findViewById(R.id.timeOver);
        songTV = findViewById(R.id.songName);
        artistTV = findViewById(R.id.songArtist);
        homeButton = findViewById(R.id.homeButton);
        updateButton = findViewById(R.id.updateButton);
        notesButton = findViewById(R.id.notesButton);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        for (SongAndArtist i: songs) {
            StorageReference islandRef = storage.getReferenceFromUrl(i.song.getSource());
            islandRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Log.d("mytag", "Назначаем песни из хранилища");
                    MediaItem mi = MediaItem.fromUri(uri.toString());
                    playlist.add(mi);
                    Log.d("mytag", "" + playlist.size() + " / " + songs.size());
                    if (playlist.size() == songs.size()) {
                        player.setMediaItems(playlist, nowPlaying, 0);
                        player.prepare();
                    }
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("mytag", "Назначаем пользовательские песни");
                    Uri uri = Uri.parse(songs.get(nowPlaying).song.getSource());
                    MediaItem mi = MediaItem.fromUri(uri.toString());
                    playlist.add(mi);
                    Log.d("mytag", "" + playlist.size() + " / " + songs.size());
                    if (playlist.size() == songs.size()) {
                        player.setMediaItems(playlist, nowPlaying, 0);
                        player.prepare();
                    }
                }
            });
        }

        Log.d("mytag", "" + nowPlaying + " " + songs.size());

        NoteViewModel noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        noteViewModel.getNoteBySongId(songs.get(nowPlaying).song.getId()).observe(this, new Observer<Note>() {
            @Override
            public void onChanged(Note _note) {
                note = _note;
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.prepare();
                if (player.isPlaying()) {
                    player.pause();
                    playButton.setBackgroundResource(R.drawable.play_button);
                }
                else {
                    player.play();
                    playButton.setBackgroundResource(R.drawable.pause_button);
                }
            }
        });

        Handler handler = new Handler();
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(player != null){
                    int currentPosition = (int) player.getCurrentPosition();
                    seekBar.setProgress(currentPosition / 1000);
//                    timePassedTV.setText(getSongDuration(currentPosition));
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
//                    timePassedTV.setText(getSongDuration(progress * 1000));
                }
            }
        });
    }

    public void addUpdateNotes(View v) {
        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra("note", (Serializable) note);

        if (note == null) {
            startActivityForResult(intent, ADD_NOTE_REQUEST);
        }
        else {
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_NOTE_REQUEST && resultCode == RESULT_OK) {
            String text = data.getStringExtra(NoteActivity.NOTE_TEXT);
            Song song = songs.get(nowPlaying).song;

            Note note = new Note(text, song.getId());
            NoteViewModel noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
            noteViewModel.insert(note);
        }
    }

    class PlayerTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            player.addListener(new ExoPlayer.Listener() {
                @Override
                public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                    Player.Listener.super.onMediaItemTransition(mediaItem, reason);

                    Log.d("mytag", "On media item transition");
                    Song song = songs.get(nowPlaying).song;
                    Artist artist = songs.get(nowPlaying).artist;
                    songTV.setText(song.getName());
                    artistTV.setText(artist.getName());
                    if (Character.valueOf(song.getSource().charAt(0)) == Character.valueOf('g')) {
                        updateButton.setVisibility(View.GONE);
                    }
                    else {
                        updateButton.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onPlaybackStateChanged(int playbackState) {
                    Player.Listener.super.onPlaybackStateChanged(playbackState);

                    if (playbackState == ExoPlayer.STATE_READY) {
                        Log.d("mytag", "State ready");
//                    timeOverTV.setText(getSongDuration((int) exoPlayer.getDuration()));
                        seekBar.setMax((int) (player.getDuration() / 1000));
                    }
                    if (playbackState == ExoPlayer.STATE_IDLE) {
                        Log.d("mytag", "State idle");
                    }
                    if (playbackState == ExoPlayer.STATE_BUFFERING) {
                        Log.d("mytag", "State buffering");
                    }
                    if (playbackState == ExoPlayer.STATE_ENDED) {
                        Log.d("mytag", "State ended");
                    }
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);



            Log.d("mytag", "prepared " + MyMediaPlayer.nowPlaying);
        }
    }
}