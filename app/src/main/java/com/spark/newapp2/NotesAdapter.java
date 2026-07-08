package com.spark.newapp2;

import android.content.Context;
import android.view.*;
import android.widget.*;
import java.util.ArrayList;

public class NotesAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> notes;
    private OnDeleteClickListener deleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public NotesAdapter(Context context, ArrayList<String> notes, OnDeleteClickListener listener) {
        this.context = context;
        this.notes = notes;
        this.deleteClickListener = listener;
    }

    @Override
    public int getCount() {
        return notes.size();
    }

    @Override
    public Object getItem(int position) {
        return notes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.note_item, parent, false);
        }

        TextView tvNote = convertView.findViewById(R.id.tv_note);
        ImageButton btnDelete = convertView.findViewById(R.id.btn_delete);

        tvNote.setText(notes.get(position));

        btnDelete.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(position);
            }
        });

        return convertView;
    }
}
