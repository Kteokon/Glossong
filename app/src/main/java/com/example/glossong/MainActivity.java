package com.example.glossong;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.glossong.fragment.ChooseTaskDialog;
import com.example.glossong.model.Artist;
import com.example.glossong.model.MyMediaPlayer;
import com.example.glossong.model.SongAndArtist;
import com.example.glossong.model.Song;
import com.example.glossong.viewmodel.ArtistViewModel;
import com.example.glossong.viewmodel.SongViewModel;
import com.google.android.exoplayer2.ExoPlayer;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_STORAGE = 101;
    private static final int ADD_SONG_REQUEST = 1;

    DBHelperWithLoader DBHelper;
    MyRoomDB myRoomDB;

    private SongViewModel songViewModel;
    private RecyclerView songList;
    ImageButton addSongButton, dictionaryButton, taskButton, allNotesButton, playButton;
    RelativeLayout nowPlayingSong;
    LinearLayout nameAndArtistLL;
    TextView songTV, artistTV;

    @Override
    protected void onRestart() {
        super.onRestart();

        MyMediaPlayer.songTV = songTV;
        MyMediaPlayer.artistTV = artistTV;
        MyMediaPlayer.playButton = playButton;
        int nowPlaying = MyMediaPlayer.nowPlaying;
        if (nowPlaying != -1) {
            Song song = MyMediaPlayer.songs.get(nowPlaying).song;
            Artist artist = MyMediaPlayer.songs.get(nowPlaying).artist;
            songTV.setText(song.getName());
            artistTV.setText(artist.getName());

            ExoPlayer player = MyMediaPlayer.getInstance(getApplicationContext());
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

            if (player.isPlaying()) {
                playButton.setImageResource(R.drawable.pause_icon);
            }
            else {
                playButton.setImageResource(R.drawable.play_icon);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_with_navigaton);

        DBHelper = new DBHelperWithLoader(this);
        myRoomDB = MyRoomDB.create(this, false);

        songList = findViewById(R.id.songList);
        songList.setLayoutManager(new LinearLayoutManager(this));
        addSongButton = findViewById(R.id.addSongButton);
        dictionaryButton = findViewById(R.id.dictionaryButton);
        taskButton = findViewById(R.id.taskButton);
        allNotesButton = findViewById(R.id.notesButton);
        nowPlayingSong = findViewById(R.id.nowPlaying);
        nameAndArtistLL = findViewById(R.id.nameAndArtist);
        songTV = findViewById(R.id.songName);
        artistTV = findViewById(R.id.artistName);
        playButton = findViewById(R.id.playButton);

        MyMediaPlayer.nowPlayingSong = nowPlayingSong;

        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);
        songViewModel.getSongs().observe(this, new Observer<List<SongAndArtist>>() {
            @Override
            public void onChanged(List<SongAndArtist> songs) {
                SongListAdapter adapter = new SongListAdapter(getApplicationContext(), songs);
                songList.setAdapter(adapter);
            }
        });

        addSongButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (PermissionUtils.hasPermissions(MainActivity.this)) {
                    addSongFile();
                }
                else {
                    PermissionUtils.requestPermissions(MainActivity.this, PERMISSION_STORAGE);
                }
            }
        });

        dictionaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DictionaryActivity.class);
                intent.putExtra("words", "all");
                startActivity(intent);
            }
        });

        taskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialog = ChooseTaskDialog.newInstance();
                dialog.show(getSupportFragmentManager(), "tag");
            }
        });

        allNotesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NotesActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity2, menu);
        return true;
    }

    private void addSongFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        String[] mimetypes = {"audio/mpeg", "audio/x-wav"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);

        startActivityForResult(intent, ADD_SONG_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent resultData) {
        if (requestCode == PERMISSION_STORAGE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (PermissionUtils.hasPermissions(this)) {
                    Log.d("mytag", "Разрешение получено");
                    addSongFile();
                } else {
                    Log.d("mytag", "Разрешение не предоставлено");
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == ADD_SONG_REQUEST && resultCode == Activity.RESULT_OK) {
            Uri uri = null;

            if(resultData != null) {
                uri = resultData.getData();
                String properPath = getProperPath(uri.getPath());
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(properPath);
                String songName = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                String artistName = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

                Long artistId = null;
                if (artistName == null) {
                    artistId = 1L;
                }
                else {
                    ArtistViewModel artistViewModel = new ViewModelProvider(this).get(ArtistViewModel.class);
                    try {
                        artistId = artistViewModel.getArtistIdByName(artistName);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (artistId == null) {
                        Artist newArtist = new Artist(artistName);
                        artistId = artistViewModel.insert(newArtist);
                    }
                }
                Song song = new Song(properPath, "", "");
                song.setArtistId(artistId);
                if (!(songName == null)) {
                    song.setName(songName);
                }
                else {
                    song.setName("Без названия");
                }
                songViewModel.insert(song);
            }
        }
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                     @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("mytag", "Разрешение получено");
            } else {
                Log.d("mytag", "Разрешение не предоставлено");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private String getProperPath(String path) {
        String res = "storage/";
        int colon = path.indexOf(":");
        String beforeColon = path.substring(0, colon);
        String afterColon = path.substring(colon + 1);
        int slash = beforeColon.indexOf("/");
        String afterFirstSlash = beforeColon.substring(slash + 1);
        int slash2 = afterFirstSlash.indexOf("/");
        String storage = afterFirstSlash.substring(slash2 + 1);
        if (storage.equals("primary")) {
            res += "emulated/0/";
        }
        else {
            res += storage + "/";
        }
        res += afterColon;
        return res;
    }
}