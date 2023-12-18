package com.example.baxendale_cole_project_two;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class GridAdapter extends ArrayAdapter<InventoryItem> {
    private Context context;
    private int resource;

    public GridAdapter(Context context, int resource, List<InventoryItem> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(resource, null);
        }

        InventoryItem item = getItem(position);

        TextView itemIdTextView = view.findViewById(R.id.itemIdTextView);
        TextView textView = view.findViewById(R.id.textView);

        if (item != null) {
            itemIdTextView.setText(""+item.getId());
            String itemDetails = item.getItemName() + "\n" + item.getItemDescription() + "\nQuantity: " + item.getQuantity();
            textView.setText(itemDetails);
        }

        return view;
    }
}
