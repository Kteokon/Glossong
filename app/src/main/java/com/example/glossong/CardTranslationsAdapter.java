package com.example.glossong;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glossong.model.RusWord;

import java.util.List;

public class CardTranslationsAdapter extends RecyclerView.Adapter<CardTranslationsAdapter.CustomViewHolder> {
    LayoutInflater inflater;
    List<RusWord> translations;

    public CardTranslationsAdapter(Context context, List<RusWord> _translations) {
        this.inflater = LayoutInflater.from(context);
        this.translations = _translations;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.translation_item, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        RusWord word = translations.get(position);

        holder.translationTV.setText(word.getSpelling());
    }

    @Override
    public int getItemCount() {
        return translations.size();
    }

    public void setTranslations(List<RusWord> _translations) {
        this.translations = _translations;
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        private TextView translationTV;

        public CustomViewHolder(View view) {
            super(view);

            translationTV = view.findViewById(R.id.translation);
        }
    }
}
