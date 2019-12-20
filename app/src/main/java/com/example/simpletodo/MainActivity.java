package com.example.simpletodo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 8;

    List<String> items;
    Button addBtn;
    EditText editText;
    RecyclerView rvItems;
    ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // this will initialize items:
        loadItems();

        addBtn = (Button) findViewById(R.id.buttonAdd);
        editText = (EditText) findViewById(R.id.edItem);
        rvItems = (RecyclerView) findViewById(R.id.rvItems);

        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                //create the new activity
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                //pass the relevant data to the new activity
                intent.putExtra(KEY_ITEM_TEXT, items.get(position));
                intent.putExtra(KEY_ITEM_POSITION, position);
                // display the activity // request code
                startActivityForResult(intent,EDIT_TEXT_CODE);
            }
        };


        // this creates an instance of OnLongClickListener, which gets passed into the adapter object
        ItemsAdapter.OnLongClickListener onLongClickListener = new ItemsAdapter.OnLongClickListener() {
            @Override
            public void onItemLongClicked(int position) {
                // delete the item that was long clicked
                items.remove(position);
                // notify the adapter what item was deleted
                itemsAdapter.notifyItemRemoved(position);
                saveItems();
                Toast.makeText(getApplicationContext(),"Item was removed", Toast.LENGTH_SHORT).show();
            }
        };

        itemsAdapter = new ItemsAdapter(items,onLongClickListener,onClickListener);
        rvItems.setAdapter(itemsAdapter);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

    }

    public void add(View v){
        String todoItem = editText.getText().toString();
        // add item to model
        items.add(todoItem);
        // Notify adapter that new item has been added
        itemsAdapter.notifyItemInserted(items.size() - 1);
        saveItems();
        editText.setText("");
        Toast.makeText(getApplicationContext(), "Item was added", Toast.LENGTH_SHORT).show();
    }

    //handle the result of the edit activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE){
            // retrieve the updated text value
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
            // extract the original position of the edited item
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);

            // update the model with the new updated item text
            items.set(position, itemText);
            // notify the recycler view addapter that there has been a change
            itemsAdapter.notifyItemChanged(position);
            // persist the changes
            saveItems();
            Toast.makeText(MainActivity.this, "Updated an item", Toast.LENGTH_SHORT).show();
        } else {
            Log.w("MainActivity", "Unknown call to onActivityResults");
        }

    }

    private File getDataFile() {
        return new File(getFilesDir(), "data.txt");
    }

    // this function will load items by reading every line of the data file
    private void loadItems(){
        try {
            items = new ArrayList<String>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity", "Error reading items", e);
            items = new ArrayList<>();
        }
    }

    // this function saves items by writing rhem into the data file
    private void saveItems() {
        try {
            FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e) {
            Log.e("MainActivity", "Error reading items", e);
        }
    }
}
