package exercises.e07_oop;

/**
 * Exercise 7.3: Static Variables and Static Methods
 *
 * Muc tieu:
 * - Biet khi nao nen dung static variable (du lieu dung chung cho moi instance).
 * - Biet khi nao method nen la static utility va khi nao nen de instance method.
 */
public class E07_3_StaticMembersAndState {

    static class Order {
        private static int nextId = 1; // shared state
        private static double taxRate = 0.08; // shared config

        private final int id;
        private final String itemName;
        private int quantity;
        private double unitPrice;

        public Order(String itemName, int quantity, double unitPrice) {
            this.id = nextId++;
            this.itemName = itemName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        // TODO 1:
        // Viet instance method subtotal() = quantity * unitPrice.

        // TODO 2:
        // Viet instance method total() = subtotal + tax.

        // TODO 3:
        // Viet static method setTaxRate(double newRate)
        // - Chi cho phep 0 <= newRate <= 1
        // - Neu sai, in "Invalid tax rate"

        // TODO 4:
        // Viet static method getTaxRate().

        // TODO 5:
        // Viet static utility method isBulkOrder(int quantity)
        // tra ve true neu quantity >= 100.

        public void printSummary() {
            System.out.println(
                    "Order#" + id + " item=" + itemName + " qty=" + quantity + " unit=" + unitPrice + " taxRate=" + taxRate);
        }
    }

    static class MathPriceUtils {
        private MathPriceUtils() {
            // Utility class: chan tao object.
        }

        // TODO 6:
        // Viet static method applyDiscount(double amount, double discountPercent)
        // Vi du amount=1000, discountPercent=10 -> 900.
    }

    public static void main(String[] args) {
        Order o1 = new Order("Keyboard", 2, 50);
        Order o2 = new Order("Laptop", 1, 1000);

        // TODO 7:
        // Hoan thanh cac method va in ket qua truoc/sau khi doi taxRate.
        // Muc tieu: thay ro static taxRate anh huong den TAT CA order.

        o1.printSummary();
        o2.printSummary();

        System.out.println("=== Exercise 7.3 Done ===");
    }
}
