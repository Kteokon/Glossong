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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glossong.fragment.ChooseTaskDialog;
import com.example.glossong.fragment.WordDialog;
import com.example.glossong.listener.ItemClickListener;
import com.example.glossong.model.EngToRusWord;
import com.example.glossong.model.RusWord;
import com.example.glossong.model.Word;

import java.util.ArrayList;
import java.util.List;

public class DictionaryAdapter extends RecyclerView.Adapter<DictionaryAdapter.CustomViewHolder> {
    private List<EngToRusWord> engWords = new ArrayList<>();
    Long songId;
    LayoutInflater inflater;
    FragmentManager fragmentManager;

    public DictionaryAdapter(Context context, List<EngToRusWord> engWords, Long songId, FragmentManager fragmentManager) {
        this.inflater = LayoutInflater.from(context);
        this.engWords = engWords;
        this.songId = songId;
        this.fragmentManager = fragmentManager;
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
        holder.updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialog = WordDialog.newInstance(wordWithTranslations.word.getId(), songId);
                dialog.show(fragmentManager, "tag");
            }
        });
    }

    @Override
    public int getItemCount() {
        return engWords.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        private TextView wordSpellingTV, wordTranslationTV;
        ImageButton updateButton;
        ConstraintLayout cl;

        public CustomViewHolder(View view) {
            super(view);

            wordSpellingTV = view.findViewById(R.id.wordSpelling);
            wordTranslationTV = view.findViewById(R.id.wordTranslation);
            updateButton = view.findViewById(R.id.updateButton);
            cl = view.findViewById(R.id.recycle_view_layout);
        }
    }
}
