package com.example.glossong.model;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

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
    public static ImageButton playButton, updateButton;

    public static int nowPlaying = -1;
    public static List<SongAndArtist> songs;

    public static ExoPlayer getInstance(Context context) {
        if (instance == null) {
            instance = new ExoPlayer.Builder(context).build();

            instance.addListener(new ExoPlayer.Listener() {
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

                    Log.d("PlayerListener", "On media item transition " + nowPlaying);

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
                    timeOverTV.setText(getSongDuration((int) instance.getDuration()));
                    seekBar.setMax((int) (instance.getDuration() / 1000));
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
                        Log.d("PlayerListener", "State ready");
                        timeOverTV.setText(getSongDuration((int) instance.getDuration()));
                        seekBar.setMax((int) (instance.getDuration() / 1000));
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

                    if (isPlaying) {
                        playButton.setImageResource(R.drawable.pause_icon);
                    }
                    else {
                        playButton.setImageResource(R.drawable.play_icon);
                    }

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

                    nowPlaying = newPosition.mediaItemIndex;

                    Log.d("Player listener", "On position discontinuity old position new position " + oldPosition.mediaItemIndex + " " + newPosition.mediaItemIndex);
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
        }
        return instance;
    }

    private static String getSongDuration(int dur) {
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
