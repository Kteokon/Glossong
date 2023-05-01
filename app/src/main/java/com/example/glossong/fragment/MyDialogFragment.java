package com.example.glossong.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.glossong.model.Word;

public class MyDialogFragment extends DialogFragment {
    Word item;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Предупреждение!")
                .setMessage("Вы уверены, что хотите удалить слово из словаря?")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        ((WordActivity) getActivity()).deleteWord(item);
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

    public void setItem(Word _item) {
        this.item = _item;
    }
}
