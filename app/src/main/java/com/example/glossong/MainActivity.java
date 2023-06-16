package com.example.glossong;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.glossong.fragment.AddWordDialog;
import com.example.glossong.fragment.AllSongsFragment;
import com.example.glossong.fragment.CardsInformationFragment;
import com.example.glossong.fragment.ChooseTaskDialog;
import com.example.glossong.fragment.DictionaryFragment;
import com.example.glossong.fragment.NotesFragment;
import com.example.glossong.model.Artist;
import com.example.glossong.model.MyMediaPlayer;
import com.example.glossong.model.SongAndArtist;
import com.example.glossong.model.Song;
import com.example.glossong.viewmodel.ArtistViewModel;
import com.example.glossong.viewmodel.SongViewModel;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.material.navigation.NavigationView;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final int PERMISSION_STORAGE = 101;
    private static final int ADD_SONG_REQUEST = 1;

    DBHelperWithLoader DBHelper;
    MyRoomDB myRoomDB;

    private DrawerLayout drawerLayout;
    NavigationView navigationView;
    MenuItem addWordButton;

    private SongViewModel songViewModel;
    ImageButton playButton;
    RelativeLayout nowPlayingSong;
    LinearLayout nameAndArtistLL;
    TextView songTV, artistTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBHelper = new DBHelperWithLoader(this);
        myRoomDB = MyRoomDB.create(this, false);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);
        getSupportActionBar().setTitle("Список песен");
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AllSongsFragment()).commit();
            navigationView.setCheckedItem(R.id.allSongs);
        }

        nowPlayingSong = findViewById(R.id.nowPlaying);
        nameAndArtistLL = findViewById(R.id.nameAndArtist);
        songTV = findViewById(R.id.songName);
        artistTV = findViewById(R.id.artistName);
        playButton = findViewById(R.id.playButton);

        MyMediaPlayer.nowPlayingSong = nowPlayingSong;

        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        addWordButton = menu.findItem(R.id.addWord);
        addWordButton.setVisible(false);
        addWordButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                DialogFragment dialog = AddWordDialog.newInstance(-1L);
                dialog.show(getSupportFragmentManager(), "tag");
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.allSongs: {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AllSongsFragment()).commit();
                getSupportActionBar().setTitle("Список песен");
                addWordButton.setVisible(false);
                break;
            }
            case R.id.taskButton: {
                DialogFragment dialog = ChooseTaskDialog.newInstance();
                dialog.show(getSupportFragmentManager(), "tag");
                break;
            }
            case R.id.dictionaryButton: {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DictionaryFragment()).commit();
                getSupportActionBar().setTitle("Общий словарь");
                addWordButton.setVisible(true);
                break;
            }
            case R.id.notesButton: {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NotesFragment()).commit();
                getSupportActionBar().setTitle("Все заметки");
                addWordButton.setVisible(false);
                break;
            }
            case R.id.addSongButton: {
                addSongFile();
                break;
            }
            case R.id.information: {
                DialogFragment dialog = CardsInformationFragment.newInstance(getResources().getString(R.string.app_information));
                dialog.show(getSupportFragmentManager(), "cardsInformationFragment");
                break;
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        MyMediaPlayer.songTV = songTV;
        MyMediaPlayer.artistTV = artistTV;
        MyMediaPlayer.playButton = playButton;
        int nowPlaying = MyMediaPlayer.nowPlaying;

        Log.d("mytag", "" + nowPlaying);
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
                Song song = new Song(properPath, "Отсутствует текст песни", "Отсутствует перевод текста песни");
                song.setArtistId(artistId);
                if (!(songName == null)) {
                    song.setName(songName);
                }
                else {
                    song.setName("Без названия");
                }
                songViewModel.insert(song);
                if (MyMediaPlayer.nowPlaying != -1) {
                    MediaItem item = new MediaItem.Builder()
                            .setUri(properPath)
                            .setMimeType(MimeTypes.BASE_TYPE_AUDIO)
                            .build();
                    ExoPlayer player = MyMediaPlayer.getInstance(getApplicationContext());
                    player.addMediaItem(item);
//                    MyMediaPlayer.songs.add(songViewModel.getSongs().getValue().get(MyMediaPlayer.songs.size()));
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AllSongsFragment()).commit();
                addWordButton.setVisible(false);
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