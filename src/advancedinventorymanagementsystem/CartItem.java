package advancedinventorymanagementsystem;

public class CartItem {
    private Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int qty) {
        this.quantity = qty;
    }

    public double getSubtotal() {
        return quantity * product.getPrice();
    }
}
