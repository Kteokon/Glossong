package com.example.glossong.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.glossong.DictionaryActivity;
import com.example.glossong.Functions;
import com.example.glossong.UpdateSongActivity;
import com.example.glossong.model.EngToRusWord;
import com.example.glossong.model.Song;
import com.example.glossong.model.SongAndArtist;

public class DeleteSongDialog extends DialogFragment {
    View v;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String[] question = {"Удалить слова"};
        final boolean[] deleteWords = {false};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Вы уверены, что хотите удалить эту песню?")
                .setMultiChoiceItems(question, deleteWords,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which, boolean isChecked) {
                                deleteWords[which] = isChecked;
                            }
                        })
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((UpdateSongActivity) getActivity()).setDeleteWords(deleteWords[0]);
                        ((UpdateSongActivity) getActivity()).onClick(v);
                    }
                }
                ).setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.setCancelable(true);

        return builder.create();
    }

    public void setItems(View v) {
        this.v = v;
    }
}
