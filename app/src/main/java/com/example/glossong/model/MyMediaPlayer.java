package com.example.glossong.model;

import android.content.Context;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.glossong.Functions;
import com.example.glossong.PlayerActivity;
import com.example.glossong.R;
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

import java.util.List;

public class MyMediaPlayer {
    static ExoPlayer instance;

    public static SeekBar seekBar;
    public static TextView timeOverTV, songTV, artistTV;
    public static ImageButton playButton;
    public static MenuItem editButton;
    public static RelativeLayout nowPlayingSong;

    public static int nowPlaying = -1;
    public static List<SongAndArtist> songs;

    public static ExoPlayer getInstance(Context context) {
        if (instance == null) {
            instance = new ExoPlayer.Builder(context).build();

            instance.addListener(new ExoPlayer.Listener() {
                @Override
                public void onEvents(Player player, Player.Events events) {
                    Player.Listener.super.onEvents(player, events);
                    Log.d("Player listener", "On events " + events.get(0));
                    if (events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION)) {
                        player.prepare();
                    }
                }

                @Override
                public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                    Player.Listener.super.onMediaItemTransition(mediaItem, reason);

                    Log.d("PlayerListener", "On media item transition " + nowPlaying + " " + instance.getCurrentMediaItemIndex());

                    Song song = songs.get(nowPlaying).song;
                    Artist artist = songs.get(nowPlaying).artist;

                    songTV.setText(song.getName());
                    artistTV.setText(artist.getName());
                    if (editButton != null) {
                        if (song.getSource().substring(0, 5).equals("https")) {
                            editButton.setVisible(false);
                        }
                        else {
                            editButton.setVisible(true);
                        }
                    }
                }

                @Override
                public void onPlaybackStateChanged(int playbackState) {
                    Player.Listener.super.onPlaybackStateChanged(playbackState);

                    if (playbackState == ExoPlayer.STATE_READY) {
                        Log.d("PlayerListener", "State ready");
                        timeOverTV.setText(Functions.getSongDuration(instance.getDuration()));
                        seekBar.setMax((int) (instance.getDuration() / 1000));
                    }
                }

                @Override
                public void onIsPlayingChanged(boolean isPlaying) {
                    Player.Listener.super.onIsPlayingChanged(isPlaying);

                    Log.d("Player listener", "On is playing changed");

                    if (isPlaying) {
                        playButton.setImageResource(R.drawable.pause_icon);
                    }
                    else {
                        playButton.setImageResource(R.drawable.play_icon);
                    }

                }

                @Override
                public void onPlayerError(PlaybackException error) {
                    Player.Listener.super.onPlayerError(error);

                    Log.d("Player error", "On player error " + error.getErrorCodeName());

                    switch (error.getErrorCodeName()) {
                        case "ERROR_CODE_IO_FILE_NOT_FOUND": {
                            Toast.makeText(context, "Ошибка: файл не найден", Toast.LENGTH_LONG).show();
                            break;
                        }
                        case "ERROR_CODE_IO_NETWORK_CONNECTION_FAILED": {
                            // TODO: делать проверку на наличие файла на телефоне (Glossong music/<Исполнитель - название>
                            Toast.makeText(context, "Ошибка: отсутствует соединение с сетью", Toast.LENGTH_LONG).show();
                            break;
                        }
                        default: {
                            Toast.makeText(context, "Неизвестная ошибка " + error.getErrorCodeName(), Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onPositionDiscontinuity(Player.PositionInfo oldPosition, Player.PositionInfo newPosition, int reason) {
                    Player.Listener.super.onPositionDiscontinuity(oldPosition, newPosition, reason);

                    nowPlaying = newPosition.mediaItemIndex;

                    Log.d("Player listener", "On position discontinuity old position new position " + oldPosition.mediaItemIndex + " " + newPosition.mediaItemIndex);
                }
            });
        }
        return instance;
    }
}
