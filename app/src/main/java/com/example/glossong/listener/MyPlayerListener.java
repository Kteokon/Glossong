package com.example.glossong.listener;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;

public class MyPlayerListener implements ExoPlayer.Listener {
    public MyPlayerListener() {

    }

    @Override
    public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
        Player.Listener.super.onMediaItemTransition(mediaItem, reason);

//        Log.d("PlayerListener", "On media item transition " + nowPlaying);

//        Song song = songs.get(nowPlaying).song;
//        Artist artist = songs.get(nowPlaying).artist;
//
//        songTV.setText(song.getName());
//        artistTV.setText(artist.getName());
//        if (song.getSource().substring(0, 5).equals("https")) {
//            updateButton.setVisibility(View.GONE);
//        }
//        else {
//            updateButton.setVisibility(View.VISIBLE);
//        }
//        timeOverTV.setText(getSongDuration((int) player.getDuration()));
//        seekBar.setMax((int) (player.getDuration() / 1000));
    }


    @Override
    public void onPlaybackStateChanged(int playbackState) {
        Player.Listener.super.onPlaybackStateChanged(playbackState);

        if (playbackState == ExoPlayer.STATE_READY) {
            Log.d("PlayerListener", "State ready");
//                        timeOverTV.setText(getSongDuration((int) player.getDuration()));
//                        seekBar.setMax((int) (player.getDuration() / 1000));
        }
    }

    @Override
    public void onPositionDiscontinuity(Player.PositionInfo oldPosition, Player.PositionInfo newPosition, int reason) {
        Player.Listener.super.onPositionDiscontinuity(oldPosition, newPosition, reason);

//        nowPlaying = newPosition.mediaItemIndex;

        Log.d("Player listener", "On position discontinuity old position new position " + oldPosition.mediaItemIndex + " " + newPosition.mediaItemIndex);
    }
}
