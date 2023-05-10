package com.example.glossong.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.glossong.R;
import com.example.glossong.model.TaskSettings;
import com.google.android.material.slider.Slider;

public class TaskSettingsDialog extends DialogFragment {
    Long dictionarySize;

    public static TaskSettingsDialog newInstance(Long dictionarySize) {
        TaskSettingsDialog myTaskSettingsDialog = new TaskSettingsDialog();
        Bundle args = new Bundle();
        args.putLong("dictionarySize", dictionarySize);
        myTaskSettingsDialog.setArguments(args);
        return myTaskSettingsDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.task_settings_dialog, container, false);

        ImageButton saveButton = view.findViewById(R.id.saveButton);
        ImageButton closeButton = view.findViewById(R.id.closeButton);
        Slider slider = view.findViewById(R.id.wordsAmount);
        this.dictionarySize = getArguments().getLong("dictionarySize");
        if (this.dictionarySize == 2) { // Если в словаре 2 слова, то прокрутка неактивна
            slider.setEnabled(false);
        }
        else {
            slider.setValueTo(this.dictionarySize);
            slider.setValue(TaskSettings.amountOfWords);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskSettings.amountOfWords = (long) slider.getValue();
                dismiss();
            }
        });
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }
}
