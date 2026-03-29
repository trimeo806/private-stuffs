package exercises.e07_oop;

/**
 * Exercise 07: Classes and Objects
 *
 * In TypeScript you might use interfaces + plain objects.
 * In Java, classes are the foundation of everything.
 * Every file must have exactly one public class matching the filename.
 */
public class E07_ClassesAndObjects {

    // === EXERCISE 1: Create a BankAccount class ===
    // TODO: Define a static inner class 'BankAccount' with:
    // - Fields: String owner, double balance (private)
    // - Constructor that takes owner and initial balance
    // - Methods: deposit(double amount), withdraw(double amount), getBalance()
    // - withdraw should print "Insufficient funds" if amount > balance
    public static class BankAccount {
        private String owner;
        private double balance;
        private static int count = 0;

        public BankAccount(String owner, double initialBalance) {
            this.owner = owner;
            this.balance = initialBalance;
            count++;
        }

        public void deposit(double amount) {
            balance += amount;
        }

        public void withdraw(double amount) {
            if (amount > balance) {
                System.out.println("Insufficient funds");
            } else {
                balance -= amount;
            }
        }

        public double getBalance() {
            return balance;
        }

        // === EXERCISE 4: Static members ===
        // TODO: Add a static field 'count' to BankAccount that tracks
        // how many accounts have been created
        // Add a static method getAccountCount()
        public static int getAccountCount() {
            return count;
        }
    }

    // === EXERCISE 2: Create a Rectangle class ===
    // TODO: Define a static inner class 'Rectangle' with:
    // - Fields: double width, double height
    // - Constructor with both parameters
    // - Methods: area(), perimeter()
    // - Override toString() to return "Rectangle(width x height)"
    public static class Rectangle {
        private double width;
        private double height;

        public Rectangle(double width, double height) {
            this.width = width;
            this.height = height;
        }

        // === EXERCISE 3: Constructor overloading ===
        // TODO: Add a second constructor to Rectangle that takes only one parameter
        // (creates a square â€” width == height)
        public Rectangle(double size) {
            this.width = size;
            this.height = size;
        }

        public double area() {
            return width * height;
        }

        public double perimeter() {
            return 2 * (width + height);
        }

        @Override
        public String toString() {
            return "Rectangle(" + width + " x " + height + ")";
        }
    }

    public static void main(String[] args) {

        // === Test Exercise 1 ===
        // TODO: Create a BankAccount, deposit 500, withdraw 200, print balance
        BankAccount account = new BankAccount("Alice", 1000);
        account.deposit(500);
        account.withdraw(200);
        System.out.println("Balance: " + account.getBalance());

        // === Test Exercise 2 ===
        // TODO: Create a Rectangle(5, 3), print area and perimeter
        // TODO: Create a square Rectangle(4), print area
        Rectangle rect1 = new Rectangle(5, 3);
        System.out.println("Area: " + rect1.area());
        System.out.println("Perimeter: " + rect1.perimeter());

        Rectangle square = new Rectangle(4);
        System.out.println("Square Area: " + square.area());

        // === Test Exercise 4 ===
        // TODO: Create 3 BankAccounts, print the count
        BankAccount account1 = new BankAccount("Bob", 500);
        BankAccount account2 = new BankAccount("Charlie", 700);
        BankAccount account3 = new BankAccount("Dave", 300);
        System.out.println("Account Count: " + BankAccount.getAccountCount());

        System.out.println("=== Exercise 07 Complete ===");
    }
}
