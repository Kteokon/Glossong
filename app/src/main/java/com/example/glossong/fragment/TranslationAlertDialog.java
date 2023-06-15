package com.example.glossong.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.glossong.DictionaryActivity;
import com.example.glossong.Functions;
import com.example.glossong.model.EngToRusWord;

public class TranslationAlertDialog extends DialogFragment {
    ViewModelStoreOwner viewModelStoreOwner;
    EngToRusWord item;
    int index;
    Long songId;
    WordDialog wordDialog;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Внимание!")
                .setMessage("Вы уверены, что хотите удалить " + item.translations.get(index).getSpelling() + " - перевод слова " + item.word.getSpelling() + "?")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Functions.deleteTranslation(viewModelStoreOwner, item, index, songId, wordDialog);
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

    public void setItems(ViewModelStoreOwner viewModelStoreOwner, EngToRusWord item, int index, Long songId, WordDialog dialog) {
        this.viewModelStoreOwner = viewModelStoreOwner;
        this.item = item;
        this.index = index;
        this.songId = songId;
        this.wordDialog = dialog;
    }
}
