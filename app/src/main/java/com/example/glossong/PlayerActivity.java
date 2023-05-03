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
import java.util.List;

public class PlayerActivity extends AppCompatActivity {
    public static final int UPDATE_SONG_REQUEST = 1;

    SeekBar seekBar;
    TextView timePassedTV, timeOverTV, songTV, artistTV;
    Button playButton, shuffleButton, loopButton,
            homeButton, updateButton, dictionaryButton,
            notesButton, lyricsButton;

    ExoPlayer player;
    List<SongAndArtist> songs;
    int nowPlaying;
    NoteViewModel noteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Intent intent = getIntent();

        SongViewModel songViewModel = new ViewModelProvider(this).get(SongViewModel.class);
        songViewModel.getSongs().observe(this, new Observer<List<SongAndArtist>>() {
            @Override
            public void onChanged(List<SongAndArtist> _songs) {
                songs = _songs;
                List<MediaItem> playlist = new ArrayList<>();

                for (SongAndArtist i: _songs) {
                    MediaItem item = new MediaItem.Builder()
                            .setUri(i.song.getSource())
                            .setMimeType(MimeTypes.BASE_TYPE_AUDIO)
                            .build();
                    playlist.add(item);
                }

                player.setMediaItems(playlist, nowPlaying, 0);
                player.prepare();

                Log.d("mytag", "prepared " + MyMediaPlayer.nowPlaying);
            }
        });

        player = MyMediaPlayer.getInstance(this);
        nowPlaying = MyMediaPlayer.nowPlaying;

        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);

        player.addListener(new ExoPlayer.Listener() {
            @Override
            public void onTracksChanged(Tracks tracks) {
                Player.Listener.super.onTracksChanged(tracks);

                Log.d("Player listener", "On track changed");
            }

            @Override
            public void onEvents(Player player, Player.Events events) {
                Player.Listener.super.onEvents(player, events);
                Log.d("Player listener", "On events");
            }

            @Override
            public void onTimelineChanged(Timeline timeline, int reason) {
                Player.Listener.super.onTimelineChanged(timeline, reason);

                Log.d("Player listener", "On timeline changed");
            }

            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                Player.Listener.super.onMediaItemTransition(mediaItem, reason);

                Log.d("Player listener", "On media item transition");
                Song song = songs.get(nowPlaying).song;
                Artist artist = songs.get(nowPlaying).artist;

                songTV.setText(song.getName());
                artistTV.setText(artist.getName());
                if (song.getSource().substring(0, 5).equals("https")) {
                    updateButton.setVisibility(View.GONE);
                }
                else {
                    updateButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onMediaMetadataChanged(MediaMetadata mediaMetadata) {
                Player.Listener.super.onMediaMetadataChanged(mediaMetadata);

                Log.d("Player listener", "On media metadata changed");
            }

            @Override
            public void onPlaylistMetadataChanged(MediaMetadata mediaMetadata) {
                Player.Listener.super.onPlaylistMetadataChanged(mediaMetadata);

                Log.d("Player listener", "On playlist metadata changed");
            }

            @Override
            public void onIsLoadingChanged(boolean isLoading) {
                Player.Listener.super.onIsLoadingChanged(isLoading);

                Log.d("Player listener", "On is loading changed");
            }

            @Override
            public void onAvailableCommandsChanged(Player.Commands availableCommands) {
                Player.Listener.super.onAvailableCommandsChanged(availableCommands);

                Log.d("Player listener", "On available commands changed");
            }

            @Override
            public void onTrackSelectionParametersChanged(TrackSelectionParameters parameters) {
                Player.Listener.super.onTrackSelectionParametersChanged(parameters);

                Log.d("Player listener", "On track selection parameters changed");
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                Player.Listener.super.onPlaybackStateChanged(playbackState);

                if (playbackState == ExoPlayer.STATE_READY) {
                    Log.d("Player listener", "State ready");
                    timeOverTV.setText(getSongDuration((int) player.getDuration()));
                    seekBar.setMax((int) (player.getDuration() / 1000));
                }
                if (playbackState == ExoPlayer.STATE_IDLE) {
                    Log.d("Player listener", "State idle");
                }
                if (playbackState == ExoPlayer.STATE_BUFFERING) {
                    Log.d("Player listener", "State buffering");
                }
                if (playbackState == ExoPlayer.STATE_ENDED) {
                    Log.d("Player listener", "State ended");
                }
            }

            @Override
            public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
                Player.Listener.super.onPlayWhenReadyChanged(playWhenReady, reason);

                Log.d("Player listener", "On play when ready changed");
            }

            @Override
            public void onPlaybackSuppressionReasonChanged(int playbackSuppressionReason) {
                Player.Listener.super.onPlaybackSuppressionReasonChanged(playbackSuppressionReason);

                Log.d("Player listener", "On playback suppression reason changed");
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                Player.Listener.super.onIsPlayingChanged(isPlaying);

                Log.d("Player listener", "On is playing changed");
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {
                Player.Listener.super.onRepeatModeChanged(repeatMode);

                Log.d("Player listener", "On repeat mode changed");
            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
                Player.Listener.super.onShuffleModeEnabledChanged(shuffleModeEnabled);

                Log.d("Player listener", "On shuffle mode enabled changed");
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                Player.Listener.super.onPlayerError(error);

                Log.d("Player error", "On player error " + error.toString());
            }

            @Override
            public void onPlayerErrorChanged(@Nullable PlaybackException error) {
                Player.Listener.super.onPlayerErrorChanged(error);

                Log.d("Player error", "On player error changed " + error.toString());
            }

            @Override
            public void onPositionDiscontinuity(Player.PositionInfo oldPosition, Player.PositionInfo newPosition, int reason) {
                Player.Listener.super.onPositionDiscontinuity(oldPosition, newPosition, reason);

                Log.d("Player listener", "On position discontinuity old position new position");
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                Player.Listener.super.onPlaybackParametersChanged(playbackParameters);

                Log.d("Player listener", "On playback parameters changed");
            }

            @Override
            public void onSeekBackIncrementChanged(long seekBackIncrementMs) {
                Player.Listener.super.onSeekBackIncrementChanged(seekBackIncrementMs);

                Log.d("Player listener", "On seek back increment changed");
            }

            @Override
            public void onSeekForwardIncrementChanged(long seekForwardIncrementMs) {
                Player.Listener.super.onSeekForwardIncrementChanged(seekForwardIncrementMs);

                Log.d("Player listener", "On seek forward increment changed");
            }

            @Override
            public void onMaxSeekToPreviousPositionChanged(long maxSeekToPreviousPositionMs) {
                Player.Listener.super.onMaxSeekToPreviousPositionChanged(maxSeekToPreviousPositionMs);

                Log.d("Player listener", "On max seek to previous position changed");
            }

            @Override
            public void onAudioSessionIdChanged(int audioSessionId) {
                Player.Listener.super.onAudioSessionIdChanged(audioSessionId);

                Log.d("Player listener", "On audio session id changed");
            }

            @Override
            public void onAudioAttributesChanged(AudioAttributes audioAttributes) {
                Player.Listener.super.onAudioAttributesChanged(audioAttributes);

                Log.d("Player listener", "On audio attributes changed");
            }

            @Override
            public void onVolumeChanged(float volume) {
                Player.Listener.super.onVolumeChanged(volume);

                Log.d("Player listener", "On volume changed");
            }

            @Override
            public void onSkipSilenceEnabledChanged(boolean skipSilenceEnabled) {
                Player.Listener.super.onSkipSilenceEnabledChanged(skipSilenceEnabled);

                Log.d("Player listener", "On skip silence enabled changed");
            }

            @Override
            public void onDeviceInfoChanged(DeviceInfo deviceInfo) {
                Player.Listener.super.onDeviceInfoChanged(deviceInfo);

                Log.d("Player listener", "On device info changed");
            }

            @Override
            public void onDeviceVolumeChanged(int volume, boolean muted) {
                Player.Listener.super.onDeviceVolumeChanged(volume, muted);

                Log.d("Player listener", "On device volume changed");
            }

            @Override
            public void onCues(CueGroup cueGroup) {
                Player.Listener.super.onCues(cueGroup);

                Log.d("Player listener", "On cues cue group");
            }

            @Override
            public void onMetadata(Metadata metadata) {
                Player.Listener.super.onMetadata(metadata);

                Log.d("Player listener", "On metadata");
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
        notesButton = findViewById(R.id.notesButton);
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
                player.prepare();
                if (player.isPlaying()) {
                    player.pause();
                    Drawable img = getResources().getDrawable(R.drawable.play_button, null);
                    playButton.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                }
                else {
                    player.play();
                    Drawable img = getResources().getDrawable(R.drawable.pause_button, null);
                    playButton.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                }
            }
        });

        loopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player.getRepeatMode() == Player.REPEAT_MODE_OFF) {
                    player.setRepeatMode(Player.REPEAT_MODE_ONE);
                    loopButton.setBackgroundResource(R.drawable.repeat_one_button);
                    //loopButton.setBackgroundColor("#BAA4FE");
                }
                else {
                    if (player.getRepeatMode() == Player.REPEAT_MODE_ONE) {
                        player.setRepeatMode(Player.REPEAT_MODE_ALL);
                        loopButton.setBackgroundResource(R.drawable.repeat_on_button);
                    }
                    else {
                        player.setRepeatMode(Player.REPEAT_MODE_OFF);
                        loopButton.setBackgroundResource(R.drawable.repeat_off_button);
                    }
                }
            }
        });

        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int r = (int)(Math.random() * songs.size());
                nowPlaying = r;

                player.seekTo(r, 0);
                player.prepare();
                player.play();
            }
        });

        lyricsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialog = FullscreenDialog.newInstance(songs.get(nowPlaying).song.getId(), songs.get(nowPlaying).song.getLyrics(), songs.get(nowPlaying).song.getTranslation());
                dialog.show(getSupportFragmentManager(), "tag");
            }
        });

        dictionaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DictionaryActivity.class);
                intent.putExtra("words", "song");
                intent.putExtra("songId", songs.get(nowPlaying).song.getId());
                startActivity(intent);
            }
        });
    }

    public void skipSong(View v){
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
    }

    public void updateSong(View v) {
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

            Song song = new Song(songs.get(nowPlaying).song.getSource(), songLyrics, songTranslation);
            song.setId(songs.get(nowPlaying).song.getId());
            song.setArtistId(artistId);
            song.setName(songName);

            Log.d("mytag", "" + songs.get(nowPlaying).song.getId() + " " + artistId + " " + artistName + " " + songName + " " + songs.get(nowPlaying).song.getSource() + " " + songLyrics + " " + songTranslation);

            String button = data.getStringExtra("button");
            SongViewModel songViewModel = new ViewModelProvider(this).get(SongViewModel.class);
            if (button.equals("delete")) {
                songViewModel.delete(song);

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