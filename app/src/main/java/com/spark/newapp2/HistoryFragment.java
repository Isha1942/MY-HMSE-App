package com.spark.newapp2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.Executors;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private HistoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_history_fragment, container, false);

        recyclerView = view.findViewById(R.id.recyclerView_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new HistoryAdapter();
        recyclerView.setAdapter(adapter);

        loadHistory();

        return view;
    }

    private void loadHistory() {
        Executors.newSingleThreadExecutor().execute(() -> {
            if (!isAdded()) return;

            try {
                HistoryDao dao = HistoryDatabase.getInstance(requireContext()).historyDao();
                List<CommandHistory> historyList = dao.getAllHistory();
                
                // Add sample data if history is empty (for testing)
                if (historyList.isEmpty()) {
                    addSampleData(dao);
                    historyList = dao.getAllHistory();
                }

                final List<CommandHistory> finalList = historyList;
                requireActivity().runOnUiThread(() -> adapter.setHistory(finalList));

            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Failed to load history", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void addSampleData(HistoryDao dao) {
        try {
            long now = System.currentTimeMillis();
            dao.insert(new CommandHistory("*A#", now - 3600000)); // 1 hour ago
            dao.insert(new CommandHistory("*D#", now - 1800000)); // 30 minutes ago
            dao.insert(new CommandHistory("*R#", now - 900000));  // 15 minutes ago
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
