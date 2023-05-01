package com.example.glossong.model;

import android.content.Context;
import android.util.Log;
import android.view.View;

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

    public static ExoPlayer getInstance(Context context) {
        if (instance == null) {
            instance = new ExoPlayer.Builder(context).build();
        }
        return instance;
    }



}
