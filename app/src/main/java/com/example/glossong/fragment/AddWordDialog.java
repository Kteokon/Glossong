package com.example.glossong.fragment;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.glossong.Functions;
import com.example.glossong.R;

import java.util.ArrayList;
import java.util.List;

public class AddWordDialog extends DialogFragment {
    public static AddWordDialog newInstance(Long songId) {
        AddWordDialog myDialog = new AddWordDialog();
        Bundle args = new Bundle();
        args.putLong("songId", songId);
        myDialog.setArguments(args);
        return myDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_word_dialog, container, false);

        ImageButton saveButton = view.findViewById(R.id.saveButton);
        ImageButton closeButton = view.findViewById(R.id.closeButton);
        EditText engWordET = view.findViewById(R.id.engWord);
        ImageButton addTranslationButton = view.findViewById(R.id.addTranslation);
        LinearLayout llMain = view.findViewById(R.id.linearLayoutMain);
        Long songId = getArguments().getLong("songId");
        ViewModelStoreOwner viewModelStoreOwner = this;

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userWord = String.valueOf(engWordET.getText());
                String checkText = userWord.replace(" ", "");
                if (checkText.equals("")) {
                    Toast.makeText(getContext(), "Введите слово", Toast.LENGTH_LONG).show();
                }
                else {
                    List<String> translations = new ArrayList<>();
                    boolean isNotEmpty = true;
                    for (int i = 0; i < llMain.getChildCount(); i++) {
                        RelativeLayout line = (RelativeLayout) llMain.getChildAt(i);
                        EditText translationET = (EditText) line.getChildAt(0);
                        String userTranslation = String.valueOf(translationET.getText());
                        checkText = userTranslation.replace(" ", "");
                        if (checkText.equals("")) {
                            Toast.makeText(getContext(), "Все переводы должны быть введены", Toast.LENGTH_LONG).show();
                            isNotEmpty = false;
                            break;
                        }
                        else {
                            translations.add(userTranslation);
                        }
                    }
                    if (isNotEmpty) {
                        new Functions().addWord(viewModelStoreOwner, userWord, translations, songId);
                        dismiss();
                    }
                }
            }
        });

        addTranslationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout line = new RelativeLayout(getContext());
                EditText translationET = new EditText(getContext());
                ImageButton deleteButton = new ImageButton(getContext());
                int id = View.generateViewId();
                deleteButton.setId(id);

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );
                line.setLayoutParams(params);

                params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 10, 10, 0);
                params.addRule(RelativeLayout.ALIGN_PARENT_START);
                params.addRule(RelativeLayout.START_OF, id);
                params.addRule(RelativeLayout.CENTER_VERTICAL);
                translationET.setLayoutParams(params);
                translationET.setGravity(4);
                translationET.setTextSize(18);
                translationET.setHint("Перевод");

                int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, getResources().getDisplayMetrics());
                params = new RelativeLayout.LayoutParams(value, value);
                params.addRule(RelativeLayout.ALIGN_PARENT_END);
                params.addRule(RelativeLayout.CENTER_VERTICAL);
                deleteButton.setLayoutParams(params);
                deleteButton.setBackgroundColor(getResources().getColor(R.color.nothing));
                deleteButton.setCropToPadding(false);
                deleteButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                deleteButton.setImageResource(R.drawable.delete_icon);

                line.addView(translationET);
                line.addView(deleteButton);

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RelativeLayout parentLine = (RelativeLayout) v.getParent();
                        llMain.removeView(parentLine);
                    }
                });

                llMain.addView(line);
            }
        });

        return view;
    }
}
