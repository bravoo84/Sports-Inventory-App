package com.sportsCommittee.sportsinventory.Entity;

public class Inventory {

    private Long available;
    private String iconURL;
    private String inventoryName;
    private Long totalStock;


    public Inventory(){}

    public Inventory(Long available, String iconURL, String inventoryName, Long totalStock) {
        this.available = available;
        this.iconURL = iconURL;
        this.inventoryName = inventoryName;
        this.totalStock = totalStock;
    }

    public Long getAvailable() {
        return available;
    }

    public void setAvailable(Long available) {
        this.available = available;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    public String getInventoryName() {
        return inventoryName;
    }

    public void setInventoryName(String inventoryName) {
        this.inventoryName = inventoryName;
    }

    public Long getTotalStock() {
        return totalStock;
    }

    public void setTotalStock(Long totalStock) {
        this.totalStock = totalStock;
    }
}
