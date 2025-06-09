
package advancedinventorymanagementsystem;



public class Product {
    
    private int productID;  // ➕ ADD this
    private String name;
    private double price;
    private int quantity;

    // ➕ UPDATED Constructor to include productID
    public Product(int productID, String name, double price) {
        this.productID = productID;
        this.name = name;
        this.price = price;
    }

    // ➕ Getter and Setter for productID
    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    // ✅ Existing methods
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return name;
    }
}


