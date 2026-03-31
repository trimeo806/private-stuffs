package exercises.e07_oop.crosspkg;

import exercises.e07_oop.E07_2_CrossPackageInheritance;

/**
 * Starter file cho Exercise 7.2.
 *
 * ExternalFriend KHONG extends Account.
 * Muc tieu: thay su khac nhau giua subclass va non-subclass o package khac.
 */
public class ExternalFriend {

    public void probe(E07_2_CrossPackageInheritance.Account account) {
        // TODO 3:
        // Thu bo comment tung dong va ghi nhan ket qua compile.

        // System.out.println(account.ownerName); // protected: KHONG qua object neu khong la subclass context
        System.out.println(account.accountNo); // public: OK
        // System.out.println(account.branchCode); // default: KHONG OK khac package
        // account.deposit(100); // protected: KHONG OK vi khong phai subclass context

        System.out.println("balance via getter=" + account.getBalance());
    }
}
