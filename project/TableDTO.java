package project;

public class TableDTO {
    private int tableID;
    private int capacity;
    private boolean status;

    public TableDTO(int tableID, int capacity, boolean status) {
        this.tableID = tableID;
        this.capacity = capacity;
        this.status = status;
    }

    // Getters and Setters
    public int getTableID() { return tableID; }
    public void setTableID(int tableID) { this.tableID = tableID; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public boolean getStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }
}
