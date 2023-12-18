package com.example.baxendale_cole_project_two;

public class InventoryItem {
    private long id; // Unique identifier for the item
    private String itemName;
    private String itemDescription;
    private int quantity;

    public InventoryItem(long id, String itemName, String itemDescription, int quantity) {
        this.id = id;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.quantity = quantity;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
