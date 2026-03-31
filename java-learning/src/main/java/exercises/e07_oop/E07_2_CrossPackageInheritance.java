package exercises.e07_oop;

/**
 * Exercise 7.2: Cross-Package + Inheritance Access
 *
 * Muc tieu:
 * - Hieu ro su khac nhau giua protected va default khi class con nam KHAC package.
 * - Biet khi nao can method public/protected de mo rong an toan.
 *
 * Bai nay gom 2 phan:
 * 1) Sua file hien tai (package exercises.e07_oop)
 * 2) Tao them 2 file theo TODO o package khac de test thuc te.
 */
public class E07_2_CrossPackageInheritance {

    public static class Account {
        private double balance;
        String branchCode = "HCM"; // default
        protected String ownerName;
        public String accountNo;

        public Account(String ownerName, String accountNo, double balance) {
            this.ownerName = ownerName;
            this.accountNo = accountNo;
            this.balance = balance;
        }

        protected void deposit(double amount) {
            if (amount > 0) {
                balance += amount;
            }
        }

        public double getBalance() {
            return balance;
        }
    }

    static class SamePackageChild extends Account {
        public SamePackageChild(String ownerName, String accountNo, double balance) {
            super(ownerName, accountNo, balance);
        }

        public void probe() {
            // Trong cung package, class con truy cap duoc:
            // - ownerName (protected)
            // - branchCode (default)
            // - accountNo (public)
            // Khong truy cap truc tiep duoc balance (private)
            System.out.println("ownerName=" + ownerName);
            System.out.println("branchCode=" + branchCode);
            System.out.println("accountNo=" + accountNo);
        }
    }

    public static void main(String[] args) {
        SamePackageChild child = new SamePackageChild("Bob", "001", 1000);
        child.deposit(500);
        child.probe();

        // TODO 1:
        // Tao file moi:
        //   exercises/e07_oop/crosspkg/ExternalChild.java
        // voi package: exercises.e07_oop.crosspkg
        // ExternalChild extends E07_2_CrossPackageInheritance.Account
        //
        // Trong ExternalChild, thu truy cap ownerName, branchCode, accountNo.
        // Ghi ket qua thanh comment tai cho do.

        // TODO 2:
        // Tao them method receiveBonus() trong ExternalChild goi deposit(200).
        // Xac nhan class con KHAC package van goi duoc protected method deposit().

        // TODO 3:
        // Tao them class ExternalFriend (khong extends Account) trong cung package crosspkg.
        // Truyen vao 1 Account object va thu truy cap ownerName/branchCode/deposit.
        // Ghi lai field/method nao truy cap duoc, field/method nao khong.

        System.out.println("balance=" + child.getBalance());
        System.out.println("=== Exercise 7.2 Done ===");
    }
}
