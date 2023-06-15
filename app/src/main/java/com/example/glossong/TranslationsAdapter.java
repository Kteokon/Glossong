package com.example.glossong;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glossong.fragment.MyAlertDialog;
import com.example.glossong.fragment.TranslationAlertDialog;
import com.example.glossong.fragment.WordDialog;
import com.example.glossong.listener.ItemClickListener;
import com.example.glossong.model.EngToRusWord;
import com.example.glossong.model.RusWord;

import java.util.List;

public class TranslationsAdapter extends RecyclerView.Adapter<TranslationsAdapter.CustomViewHolder> {
    ViewModelStoreOwner viewModelStoreOwner;
    EngToRusWord engToRusWord;
    Long songId;
    LayoutInflater inflater;
    WordDialog wordDialog;
    FragmentManager fragmentManager;

    public TranslationsAdapter(Context context, ViewModelStoreOwner viewModelStoreOwner, EngToRusWord engToRusWord, Long songId, WordDialog wordDialog, FragmentManager fragmentManager) {
        this.inflater = LayoutInflater.from(context);
        this.viewModelStoreOwner = viewModelStoreOwner;
        this.engToRusWord = engToRusWord;
        this.songId = songId;
        this.wordDialog = wordDialog;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.rus_word_item, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        RusWord word = engToRusWord.translations.get(position);
        int index = position;

        holder.wordSpellingTV.setText(word.getSpelling());
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TranslationAlertDialog dialog = new TranslationAlertDialog();
                dialog.setItems(viewModelStoreOwner, engToRusWord, index, songId, wordDialog);
                dialog.show(fragmentManager, "deleteWordAlert");
            }
        });
    }

    @Override
    public int getItemCount() {
        return engToRusWord.translations.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        private TextView wordSpellingTV;
        ImageButton deleteButton;

        public CustomViewHolder(View view) {
            super(view);

            wordSpellingTV = view.findViewById(R.id.wordSpelling);
            deleteButton = view.findViewById(R.id.deleteButton);
        }
    }
}
