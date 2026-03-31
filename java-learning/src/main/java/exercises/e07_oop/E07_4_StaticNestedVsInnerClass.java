package exercises.e07_oop;

/**
 * Exercise 7.4: Static Nested Class vs Inner Class
 *
 * Muc tieu:
 * - Hieu khi nao can static nested class, khi nao can inner class (non-static).
 * - Biet rang inner class gan voi instance cua outer class.
 */
public class E07_4_StaticNestedVsInnerClass {

    static class ShoppingCart {
        private String owner;
        private double total;

        public ShoppingCart(String owner) {
            this.owner = owner;
        }

        public void add(double amount) {
            if (amount > 0) {
                total += amount;
            }
        }

        public double getTotal() {
            return total;
        }

        public String getOwner() {
            return owner;
        }

        // static nested class: khong can instance ShoppingCart de tao.
        static class PriceRule {
            // TODO 1:
            // Viet static method vat(double amount, double rate).

            // TODO 2:
            // Viet static method shippingFee(double amount):
            // - amount >= 500000 -> 0
            // - nguoc lai -> 30000
        }

        // inner class: can instance ShoppingCart, co the truy cap owner/total truc tiep.
        class Receipt {
            // TODO 3:
            // Viet method print() in owner + total + shipping fee + grand total.
            // Goi y: shipping fee su dung PriceRule.shippingFee(total)
        }
    }

    public static void main(String[] args) {
        // TODO 4:
        // Tao PriceRule va goi method ma KHONG tao ShoppingCart.
        // Goi y: ShoppingCart.PriceRule.vat(...)

        ShoppingCart cart = new ShoppingCart("Alice");
        cart.add(120000);
        cart.add(380000);

        // TODO 5:
        // Tao inner class Receipt dung syntax:
        // ShoppingCart.Receipt receipt = cart.new Receipt();
        // Sau do goi receipt.print().

        System.out.println("Owner=" + cart.getOwner());
        System.out.println("Cart total=" + cart.getTotal());
        System.out.println("=== Exercise 7.4 Done ===");
    }
}
