package com.example.glossong.model;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

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
import com.google.android.exoplayer2.video.VideoSize;

import java.util.ArrayList;
import java.util.List;

public class MyMediaPlayer {
    static ExoPlayer instance;

    public static int nowPlaying = -1;
    public static List<MediaItem> playlist;

    public static ExoPlayer getInstance(Context context) {
        if (instance == null) {
            instance = new ExoPlayer.Builder(context).build();

            playlist = new ArrayList<>();
        }
        instance.addListener(new ExoPlayer.Listener() {

            @Override
            public void onTracksChanged(Tracks tracks) {
                Player.Listener.super.onTracksChanged(tracks);

                Log.d("mytag", "On track changed");
            }

            @Override
            public void onEvents(Player player, Player.Events events) {
                Player.Listener.super.onEvents(player, events);
                Log.d("mytag", "On events");
            }

            @Override
            public void onTimelineChanged(Timeline timeline, int reason) {
                Player.Listener.super.onTimelineChanged(timeline, reason);

                Log.d("mytag", "On timeline changed");
            }

            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                Player.Listener.super.onMediaItemTransition(mediaItem, reason);

                Log.d("mytag", "On media item transition");
//                    Song song = songs.get(nowPlaying).song;
//                    songTV.setText(song.getSong());
//                    artistTV.setText(song.getArtist());
//                    if (song.getSource().equals("base")) {
//                        updateButton.setVisibility(View.GONE);
//                    }
//                    else {
//                        updateButton.setVisibility(View.VISIBLE);
//                    }
            }

            @Override
            public void onMediaMetadataChanged(MediaMetadata mediaMetadata) {
                Player.Listener.super.onMediaMetadataChanged(mediaMetadata);

                Log.d("mytag", "On media metadata changed");
            }

            @Override
            public void onPlaylistMetadataChanged(MediaMetadata mediaMetadata) {
                Player.Listener.super.onPlaylistMetadataChanged(mediaMetadata);

                Log.d("mytag", "On playlist metadata changed");
            }

            @Override
            public void onIsLoadingChanged(boolean isLoading) {
                Player.Listener.super.onIsLoadingChanged(isLoading);

                Log.d("mytag", "On is loading changed");
            }

            @Override
            public void onAvailableCommandsChanged(Player.Commands availableCommands) {
                Player.Listener.super.onAvailableCommandsChanged(availableCommands);

                Log.d("mytag", "On available commands changed");
            }

            @Override
            public void onTrackSelectionParametersChanged(TrackSelectionParameters parameters) {
                Player.Listener.super.onTrackSelectionParametersChanged(parameters);

                Log.d("mytag", "On track selection parameters changed");
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                Player.Listener.super.onPlaybackStateChanged(playbackState);

                if (playbackState == ExoPlayer.STATE_READY) {
                    Log.d("mytag", "State ready");
//                        timeOverTV.setText(getSongDuration((int) exoPlayer.getDuration()));
//                        seekBar.setMax((int) (exoPlayer.getDuration() / 1000));
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

            @Override
            public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
                Player.Listener.super.onPlayWhenReadyChanged(playWhenReady, reason);

                Log.d("mytag", "On play when ready changed");
            }

            @Override
            public void onPlaybackSuppressionReasonChanged(int playbackSuppressionReason) {
                Player.Listener.super.onPlaybackSuppressionReasonChanged(playbackSuppressionReason);

                Log.d("mytag", "On playback suppression reason changed");
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                Player.Listener.super.onIsPlayingChanged(isPlaying);

                Log.d("mytag", "On is playing changed");
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {
                Player.Listener.super.onRepeatModeChanged(repeatMode);

                Log.d("mytag", "On repeat mode changed");
            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
                Player.Listener.super.onShuffleModeEnabledChanged(shuffleModeEnabled);

                Log.d("mytag", "On shuffle mode enabled changed");
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                Player.Listener.super.onPlayerError(error);

                Log.d("mytag", "On player error");
            }

            @Override
            public void onPlayerErrorChanged(@Nullable PlaybackException error) {
                Player.Listener.super.onPlayerErrorChanged(error);

                Log.d("mytag", "On player error changed");
            }

            @Override
            public void onPositionDiscontinuity(Player.PositionInfo oldPosition, Player.PositionInfo newPosition, int reason) {
                Player.Listener.super.onPositionDiscontinuity(oldPosition, newPosition, reason);

                Log.d("mytag", "On position discontinuity old position new position");
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                Player.Listener.super.onPlaybackParametersChanged(playbackParameters);

                Log.d("mytag", "On playback parameters changed");
            }

            @Override
            public void onSeekBackIncrementChanged(long seekBackIncrementMs) {
                Player.Listener.super.onSeekBackIncrementChanged(seekBackIncrementMs);

                Log.d("mytag", "On seek back increment changed");
            }

            @Override
            public void onSeekForwardIncrementChanged(long seekForwardIncrementMs) {
                Player.Listener.super.onSeekForwardIncrementChanged(seekForwardIncrementMs);

                Log.d("mytag", "On seek forward increment changed");
            }

            @Override
            public void onMaxSeekToPreviousPositionChanged(long maxSeekToPreviousPositionMs) {
                Player.Listener.super.onMaxSeekToPreviousPositionChanged(maxSeekToPreviousPositionMs);

                Log.d("mytag", "On max seek to previous position changed");
            }

            @Override
            public void onAudioSessionIdChanged(int audioSessionId) {
                Player.Listener.super.onAudioSessionIdChanged(audioSessionId);

                Log.d("mytag", "On audio session id changed");
            }

            @Override
            public void onAudioAttributesChanged(AudioAttributes audioAttributes) {
                Player.Listener.super.onAudioAttributesChanged(audioAttributes);

                Log.d("mytag", "On audio attributes changed");
            }

            @Override
            public void onVolumeChanged(float volume) {
                Player.Listener.super.onVolumeChanged(volume);

                Log.d("mytag", "On volume changed");
            }

            @Override
            public void onSkipSilenceEnabledChanged(boolean skipSilenceEnabled) {
                Player.Listener.super.onSkipSilenceEnabledChanged(skipSilenceEnabled);

                Log.d("mytag", "On skip silence enabled changed");
            }

            @Override
            public void onDeviceInfoChanged(DeviceInfo deviceInfo) {
                Player.Listener.super.onDeviceInfoChanged(deviceInfo);

                Log.d("mytag", "On device info changed");
            }

            @Override
            public void onDeviceVolumeChanged(int volume, boolean muted) {
                Player.Listener.super.onDeviceVolumeChanged(volume, muted);

                Log.d("mytag", "On device volume changed");
            }

            @Override
            public void onVideoSizeChanged(VideoSize videoSize) {
                Player.Listener.super.onVideoSizeChanged(videoSize);

                Log.d("mytag", "On video size changed");
            }

            @Override
            public void onSurfaceSizeChanged(int width, int height) {
                Player.Listener.super.onSurfaceSizeChanged(width, height);

                Log.d("mytag", "On surface size changed");
            }

            @Override
            public void onRenderedFirstFrame() {
                Player.Listener.super.onRenderedFirstFrame();

                Log.d("mytag", "On rendered first frame");
            }

            @Override
            public void onCues(CueGroup cueGroup) {
                Player.Listener.super.onCues(cueGroup);

                Log.d("mytag", "On cues cue group");
            }

            @Override
            public void onMetadata(Metadata metadata) {
                Player.Listener.super.onMetadata(metadata);

                Log.d("mytag", "On metadata");
            }
        });
        return instance;
    }



}
