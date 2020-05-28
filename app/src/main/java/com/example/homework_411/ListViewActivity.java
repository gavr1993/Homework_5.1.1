package com.example.homework_411;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListViewActivity extends AppCompatActivity {

    private static final String KEY_TEXT = "key_text";
    private static final String KEY_COUNT = "key_count";
    private static final String STORAGE_FILE = "storage_file.txt";

    private List<Map<String, String>> values;

    private BaseAdapter listContentAdapter;


    private ListViewActivity() {
        listContentAdapter = null;
    }

    private ImageButton add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        final ListView list = findViewById(R.id.list);
        values = prepareContent();
        listContentAdapter = createAdapter(values);
        list.setAdapter(listContentAdapter);
        add = findViewById(R.id.add);
        addItem();
    }

    private void addItem() {
        EditText edtx = findViewById(R.id.edtx);
        String author = String.valueOf(edtx.getText());
        Map<String, String> map = new HashMap<>();
        map.put("key_text", author);
        map.put("key_count", author.length() + "");
        values.add(map);
        listContentAdapter.notifyDataSetChanged();
    }

    private File getValuesFile() {
        return new File(getExternalFilesDir(null), STORAGE_FILE);
    }

    @NonNull
    private BaseAdapter createAdapter(List<Map<String, String>> text) {
        return new SimpleAdapter(this, text, R.layout.double_text, new String[]
                {KEY_TEXT, KEY_COUNT}, new int[]{R.id.textViewTop, R.id.textViewBottom}) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ImageButton del = v.findViewById(R.id.del);
                del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        values.remove(position);
                        listContentAdapter.notifyDataSetChanged();
                    }
                });
                return v;
            }
        };
    }

    @NonNull
    private List<Map<String, String>> prepareContent() {
        List<Map<String, String>> result = new ArrayList<>();
        File file = getValuesFile();
        if (file.exists()) {
            return loadFromFile(file, KEY_TEXT, KEY_COUNT);
        } else {
            String[] titles = getString(R.string.large_text).split("\n\n");
            for (String title : titles) {
                Map<String, String> map = new HashMap<>();
                map.put(KEY_TEXT, title);
                map.put(KEY_COUNT, title.length() + "");
                result.add(map);
            }
            saveList(result, KEY_TEXT, KEY_COUNT, file);
        }
        return result;
    }

    public static void saveList(List<Map<String, String>> data, String key1, String key2, File file) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            for (Map<String, String> map : data) {
                String text = map.get(key1);
                String countText = map.get(key2);
                writer.write(text + ";" + countText);
                writer.write("\n\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static List<Map<String, String>> loadFromFile(File file, String key1, String key2) {
        List<Map<String, String>> result = new ArrayList<>();
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new FileReader(file));
            int symbol;
            while ((symbol = reader.read()) != -1) {
                sb.append((char) symbol);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        String fullFileContent = sb.toString();
        String[] titles = fullFileContent.split("\n\n");
        for (String title : titles) {
            String[] values = title.split(";");
            Map<String, String> map = new HashMap<>();
            map.put(key1, values[0]);
            map.put(key2, values[1]);
            result.add(map);
        }
        return result;
    }
}