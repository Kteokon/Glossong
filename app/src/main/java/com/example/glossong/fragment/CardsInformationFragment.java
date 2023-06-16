package com.example.glossong.fragment;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.glossong.R;

public class CardsInformationFragment extends DialogFragment {
    String information;

    public CardsInformationFragment() {
        // Required empty public constructor
    }

    public static CardsInformationFragment newInstance(String information) {
        CardsInformationFragment fragment = new CardsInformationFragment();
        Bundle args = new Bundle();
        args.putString("information", information);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            information = getArguments().getString("information");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cards_information, container, false);

        TextView informationTV = view.findViewById(R.id.informationTV);
        informationTV.setText(information);
        ImageButton closeButton = view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }
}