package com.example.gerardogarcias.myapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Fragmento para el contenido principal
 */
public class PlaceholderFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.section_fragment, container, false);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Fragment Inbox");

        Button buttonChangeText = (Button) view.findViewById(R.id.buttonFragmentInbox);

        final TextView textViewInboxFragment = (TextView) view.findViewById(R.id.textViewInboxFragment);

        buttonChangeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textViewInboxFragment.setText("This is the Inbox Fragment");


            }
        });

        return view;
    }

}