package com.example.glossong;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glossong.listener.ItemClickListener;
import com.example.glossong.model.EngToRusWord;
import com.example.glossong.model.RusWord;
import com.example.glossong.model.Word;
import com.example.glossong.viewmodel.WordViewModel;

import java.util.ArrayList;
import java.util.List;

public class DictionaryAdapter extends RecyclerView.Adapter<DictionaryAdapter.CustomViewHolder> {
    private List<EngToRusWord> engWords = new ArrayList<>();
    LayoutInflater inflater;
    ItemClickListener listener;

    public DictionaryAdapter(Context context, List<EngToRusWord> engWords, ItemClickListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.engWords = engWords;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.word_item, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        Word word;
        String translations = "";
        EngToRusWord wordWithTranslations = engWords.get(position);
        List<RusWord> trs = wordWithTranslations.translations;
        for (int i = 0; i < trs.size(); i++) {
            if (i == 0) {
                translations += trs.get(i).getSpelling();
            }
            else {
                translations += (", " + trs.get(i).getSpelling());
            }
        }
        word = wordWithTranslations.word;
        holder.wordSpellingTV.setText(word.getSpelling());
        holder.wordTranslationTV.setText(translations);
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(word);
            }
        });
    }

    @Override
    public int getItemCount() {
        return engWords.size();
    }

    public Word getWordAt(int position) {
        return engWords.get(position).word;
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        private TextView wordSpellingTV, wordTranslationTV;
        Button deleteButton;
        ConstraintLayout cl;

        public CustomViewHolder(View view) {
            super(view);

            wordSpellingTV = view.findViewById(R.id.wordSpelling);
            wordTranslationTV = view.findViewById(R.id.wordTranslation);
            deleteButton = view.findViewById(R.id.deleteButton);
            cl = view.findViewById(R.id.recycle_view_layout);
        }
    }
}
