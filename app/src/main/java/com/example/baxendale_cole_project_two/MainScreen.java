package com.example.baxendale_cole_project_two;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class MainScreen extends AppCompatActivity {
    private GridView gridView;
    private ArrayList<InventoryItem> dataItems;
    private GridAdapter gridAdapter; // Custom adapter
    private DataBaseHelper dataBaseHelper;
    String username = null;
    

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String username = getIntent().getStringExtra("USERNAME_KEY");
        if (username == null) {
            Log.d("MainScreen", "Username is null in onCreate");
        } else {
            this.username = username;

        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_main_screen);

        gridView = findViewById(R.id.gridView);
        dataBaseHelper = new DataBaseHelper(this);

        dataItems = new ArrayList<>(fetchInventoryItemsFromDatabase());
        gridAdapter = new GridAdapter(this, R.layout.grid_item, dataItems);
        gridView.setAdapter(gridAdapter);
        sendLowQuantityAlerts();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Handle item click
            }
        });
    }



    private List<InventoryItem> fetchInventoryItemsFromDatabase() {
        Map<Long, InventoryItem> itemsMap = dataBaseHelper.getAllItems();
        return new ArrayList<>(itemsMap.values());
    }

    private void refreshGridView() {
        dataItems.clear();
        dataItems.addAll(fetchInventoryItemsFromDatabase());
        gridAdapter.notifyDataSetChanged();
        sendLowQuantityAlerts();
    }

    public void onAddItemClick(View view) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.add_item_dialog, null);

        final EditText editItemName = dialogView.findViewById(R.id.editItemName);
        final EditText editItemQuantity = dialogView.findViewById(R.id.editItemQuantity);
        final EditText editItemDescription = dialogView.findViewById(R.id.editItemDescription);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setTitle("Add New Item");

        builder.setPositiveButton("Add", null); // Set to null. We override the click listener later.
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            Button addButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            addButton.setOnClickListener(view1 -> {
                String itemName = editItemName.getText().toString();
                int quantity = Integer.parseInt(editItemQuantity.getText().toString());
                String itemDescription = editItemDescription.getText().toString();

                InventoryItem newItem = new InventoryItem(System.currentTimeMillis(), itemName, itemDescription, quantity);

                // Add item and check if added successfully
                if (!dataBaseHelper.addItem(newItem)) {
                    // Show a toast message if item already exists
                    Toast.makeText(MainScreen.this, "Item already exists", Toast.LENGTH_SHORT).show();
                } else {
                    // Item added successfully, dismiss dialog and refresh grid
                    dialog.dismiss();
                    refreshGridView();
                }
            });
        });

        dialog.show();
    }


    public void onDeleteClick(View view) {
        // Get the position of the grid item
        int position = gridView.getPositionForView(view);
        if (position >= 0 && position < dataItems.size()) {
            // Get the item to be deleted
            InventoryItem itemToDelete = dataItems.get(position);

            // Show confirmation dialog
            new AlertDialog.Builder(this)
                    .setTitle("Delete Item")
                    .setMessage("Are you sure you want to delete \"" + itemToDelete.getItemName() + "\"?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Continue with delete
                            dataBaseHelper.deleteItem(itemToDelete.getId());
                            dataItems.remove(position);
                            gridAdapter.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(R.drawable.baseline_restore_from_trash_24)
                    .show();
        }
    }



    public void onEditClick(View view) {
        // Get the position of the grid item
        int position = gridView.getPositionForView(view);
        if (position >= 0 && position < dataItems.size()) {
            // Get the item to be edited
            InventoryItem itemToEdit = dataItems.get(position);

            LayoutInflater inflater = LayoutInflater.from(this);
            View dialogView = inflater.inflate(R.layout.add_item_dialog, null);

            final EditText editItemName = dialogView.findViewById(R.id.editItemName);
            final EditText editItemQuantity = dialogView.findViewById(R.id.editItemQuantity);
            final EditText editItemDescription = dialogView.findViewById(R.id.editItemDescription);

            editItemName.setText(itemToEdit.getItemName());
            editItemQuantity.setText(String.valueOf(itemToEdit.getQuantity()));
            editItemDescription.setText(itemToEdit.getItemDescription());

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(dialogView);
            builder.setTitle("Edit Item");

            builder.setPositiveButton("Update", null); // Initially set to null
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();

            dialog.setOnShowListener(dialogInterface -> {
                Button updateButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                updateButton.setOnClickListener(view1 -> {
                    String newName = editItemName.getText().toString();
                    int newQuantity = Integer.parseInt(editItemQuantity.getText().toString());
                    String newDescription = editItemDescription.getText().toString();

                    if (!newName.equalsIgnoreCase(itemToEdit.getItemName()) && dataBaseHelper.doesItemExist(newName)) {
                        Toast.makeText(MainScreen.this, "An item with this name already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        itemToEdit.setItemName(newName);
                        itemToEdit.setQuantity(newQuantity);
                        itemToEdit.setItemDescription(newDescription);

                        dataBaseHelper.updateItem(itemToEdit);
                        dialog.dismiss();
                        refreshGridView();
                    }
                });
            });

            dialog.show();
        }
    }

    public void onPhoneClick(View view) {
        Intent intent = new Intent(MainScreen.this, PhoneScreen.class);
        intent.putExtra("USERNAME_KEY", username);
        startActivity(intent);
    }

    public void logOut(View view) {
        Intent intent = new Intent(MainScreen.this, MainActivity.class);
        startActivity(intent);
    }

    private void sendLowQuantityAlerts() {
        // Iterate through the dataItems list
        for (InventoryItem item : dataItems) {
            int quantity = item.getQuantity();
            if (quantity < 3) {
                String phoneNumber = dataBaseHelper.getPhoneNumberIfSmsConsentTrue(username);
                if (phoneNumber != null) {
                    // Send an SMS alert for low quantity
                    String itemName = item.getItemName();
                    String message = "Low quantity alert: " + itemName + " is running low.";

                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                        Toast.makeText(getApplicationContext(), "SMS sent for low quantity item: " + itemName + " to phone: " + phoneNumber, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "SMS sending failed for item: " + itemName + " to phone: " + phoneNumber, Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }
        }
    }



}
