package com.example.glossong;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.glossong.fragment.AddWordDialog;
import com.example.glossong.fragment.DictionaryFragment;
import com.example.glossong.fragment.LyricsFragment;
import com.example.glossong.fragment.PlayerFragment;
import com.example.glossong.model.Artist;
import com.example.glossong.model.SongAndArtist;
import com.example.glossong.model.MyMediaPlayer;
import com.example.glossong.model.Song;
import com.example.glossong.viewmodel.NoteViewModel;
import com.example.glossong.viewmodel.SongViewModel;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class PlayerActivity extends AppCompatActivity {
    public static final int UPDATE_SONG_REQUEST = 1;

    MenuItem songButton, lyricsButton, dictionaryButton, noteButton, editButton, addWordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        MyMediaPlayer.getInstance(getApplicationContext());

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setNavigationIcon(R.drawable.back_square);
        toolbar.setNavigationContentDescription("Вернуться");
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PlayerFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_player, menu);

        addWordButton = menu.findItem(R.id.addWordButton);
        songButton = menu.findItem(R.id.songButton);
        lyricsButton = menu.findItem(R.id.lyrics);
        dictionaryButton = menu.findItem(R.id.dictionaryButton);
        noteButton = menu.findItem(R.id.noteButton);
        editButton = menu.findItem(R.id.editButton);

        MyMediaPlayer.editButton = editButton;
        Song song = MyMediaPlayer.songs.get(MyMediaPlayer.nowPlaying).song;
        if (song.getSource().substring(0, 5).equals("https")){
            editButton.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.songButton: {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PlayerFragment()).commit();
                addWordButton.setVisible(false);
                songButton.setVisible(false);
                lyricsButton.setVisible(true);
                dictionaryButton.setVisible(true);
                noteButton.setVisible(true);
                editButton.setVisible(true);
                break;
            }
            case R.id.addWordButton: {
                DialogFragment dialog = AddWordDialog.newInstance((long) MyMediaPlayer.nowPlaying);
                dialog.show(getSupportFragmentManager(), "tag");
                break;
            }
            case R.id.lyrics: {
                Long nowPlaying = (long) MyMediaPlayer.nowPlaying;
                String lyrics = MyMediaPlayer.songs.get(MyMediaPlayer.nowPlaying).song.getLyrics();
                String translation = MyMediaPlayer.songs.get(MyMediaPlayer.nowPlaying).song.getTranslation();
                LyricsFragment lyricsFragment = LyricsFragment.newInstance(nowPlaying, lyrics, translation);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, lyricsFragment).commit();
                addWordButton.setVisible(false);
                songButton.setVisible(true);
                lyricsButton.setVisible(false);
                dictionaryButton.setVisible(true);
                noteButton.setVisible(true);
                editButton.setVisible(true);
                break;
            }
            case R.id.dictionaryButton: {
                DictionaryFragment dictionaryFragment = DictionaryFragment.newInstance((long) MyMediaPlayer.nowPlaying);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, dictionaryFragment).commit();
                addWordButton.setVisible(true);
                songButton.setVisible(true);
                lyricsButton.setVisible(true);
                dictionaryButton.setVisible(false);
                noteButton.setVisible(true);
                editButton.setVisible(true);
                break;
            }
            case R.id.noteButton: {
                List<SongAndArtist> songs = MyMediaPlayer.songs;
                int nowPlaying = MyMediaPlayer.nowPlaying;

                Intent intent = new Intent(this, NoteActivity.class);
                intent.putExtra("songId", songs.get(nowPlaying).song.getId());
                startActivity(intent);
                break;
            }
            case R.id.editButton: {
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
                break;
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UPDATE_SONG_REQUEST && resultCode == RESULT_OK) {
            Long artistId = data.getLongExtra(UpdateSongActivity.ARTIST_ID, 1L);
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
                ExoPlayer player = MyMediaPlayer.getInstance(getApplicationContext());
                player.removeMediaItem(nowPlaying);
                songs.remove(nowPlaying);
                if (nowPlaying == player.getMediaItemCount()) {
                    nowPlaying--;
                    player.seekTo(nowPlaying, 0);
                }
                MyMediaPlayer.nowPlaying = player.getCurrentMediaItemIndex();
            }
            else{
                SongViewModel songViewModel = new ViewModelProvider(this).get(SongViewModel.class);
                songViewModel.update(song);

                Log.d("mytag", "Updated");
            }
        }
    }
}