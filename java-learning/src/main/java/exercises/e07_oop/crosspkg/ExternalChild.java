package exercises.e07_oop.crosspkg;

import exercises.e07_oop.E07_2_CrossPackageInheritance;

/**
 * Starter file cho Exercise 7.2.
 *
 * Muc tieu:
 * - Kiem chung class con KHAC package truy cap duoc gi tu class cha.
 */
public class ExternalChild extends E07_2_CrossPackageInheritance.Account {

    public ExternalChild(String ownerName, String accountNo, double balance) {
        super(ownerName, accountNo, balance);
    }

    public void probeFromSubclass() {
        // TODO 1:
        // Thu bo comment tung dong va ghi nhan ket qua compile.

        System.out.println("ownerName=" + ownerName); // protected -> OK trong subclass
        // System.out.println("branchCode=" + branchCode); // default -> KHONG OK khac package
        System.out.println("accountNo=" + accountNo); // public -> OK
        // System.out.println("balance=" + balance); // private -> KHONG OK
    }

    public void receiveBonus() {
        // TODO 2:
        // protected method van goi duoc trong subclass khac package.
        deposit(200);
    }
}
