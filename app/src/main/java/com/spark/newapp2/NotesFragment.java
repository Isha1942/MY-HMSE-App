package com.spark.newapp2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;

public class NotesFragment extends Fragment {

    private EditText etNotes;
    private Button btnSaveNotes;
    private ListView lvNotes;

    private ArrayList<String> notesList;
    private NotesAdapter adapter;

    private SharedPreferences prefs;
    private static final String NOTES_KEY = "saved_notes";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_notes_fragment, container, false);

        etNotes = view.findViewById(R.id.et_notes);
        btnSaveNotes = view.findViewById(R.id.btn_save_notes);
        lvNotes = view.findViewById(R.id.lv_notes);

        prefs = requireContext().getSharedPreferences("MySecuronPrefs", Context.MODE_PRIVATE);

        loadNotes();

        btnSaveNotes.setOnClickListener(v -> saveNote());

        return view;
    }

    private void saveNote() {
        String note = etNotes.getText().toString().trim();
        if (note.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter some notes", Toast.LENGTH_SHORT).show();
            return;
        }

        notesList.add(note);
        saveNotesToPrefs();
        adapter.notifyDataSetChanged();
        etNotes.setText("");
        Toast.makeText(getActivity(), "Note saved", Toast.LENGTH_SHORT).show();
    }

    private void loadNotes() {
        String saved = prefs.getString(NOTES_KEY, "");
        if (!saved.isEmpty()) {
            notesList = new ArrayList<>(Arrays.asList(saved.split(";;;")));
        } else {
            notesList = new ArrayList<>();
        }


        adapter = new NotesAdapter(requireContext(), notesList, position -> {
            String note = notesList.get(position);
            notesList.remove(position);
            saveNotesToPrefs();
            adapter.notifyDataSetChanged();
            Toast.makeText(getActivity(), "Deleted: " + note, Toast.LENGTH_SHORT).show();
        });

        lvNotes.setAdapter(adapter);
    }

    private void saveNotesToPrefs() {
        StringBuilder sb = new StringBuilder();
        for (String note : notesList) {
            sb.append(note).append(";;;");
        }
        prefs.edit().putString(NOTES_KEY, sb.toString()).apply();
    }
}
