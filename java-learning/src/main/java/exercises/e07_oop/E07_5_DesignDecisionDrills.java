package exercises.e07_oop;

/**
 * Exercise 7.5: Design Decision Drills
 *
 * Muc tieu:
 * - Luyen quyet dinh: nen dung access modifier nao cho field/method/class.
 * - Luyen quyet dinh: static hay non-static cho variable/method.
 *
 * Cach hoc:
 * - Moi tinh huong ben duoi: ghi dap an vao comment + sua code theo lua chon cua ban.
 * - Sau do chay main de tu kiem tra bang output.
 */
public class E07_5_DesignDecisionDrills {

    // Scenario A:
    // Password hash cua user co nen public khong?
    // TODO A1: Chon modifier hop ly cho passwordHash (private/protected/default/public) va giai thich.
    static class User {
        // TODO A2: Sua modifier
        String passwordHash;

        public User(String passwordHash) {
            this.passwordHash = passwordHash;
        }

        // TODO A3:
        // Neu can expose thong tin, ban muon expose gi?
        // - toan bo hash
        // - hay chi thong tin da mask
    }

    // Scenario B:
    // Company tax rate ap dung cho tat ca invoice.
    // TODO B1: taxRate nen la static hay non-static? Vi sao?
    static class Invoice {
        // TODO B2: Sua cho hop ly.
        double taxRate = 0.1;

        private double amount;

        public Invoice(double amount) {
            this.amount = amount;
        }

        // TODO B3:
        // Method total() co can static khong? Giai thich va sua code.
        public double total() {
            return amount * (1 + taxRate);
        }
    }

    // Scenario C:
    // Utility class convert text (trim, normalize, capitalize)
    // TODO C1: Co can tao object TextUtils khong?
    static class TextUtils {
        // TODO C2:
        // Neu khong can object -> chan new TextUtils() bang private constructor.

        // TODO C3:
        // Method capitalizeWords nen static hay non-static?
        String capitalizeWords(String input) {
            if (input == null || input.isBlank()) {
                return input;
            }
            String[] parts = input.trim().split("\\s+");
            StringBuilder sb = new StringBuilder();
            for (String p : parts) {
                sb.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1).toLowerCase()).append(" ");
            }
            return sb.toString().trim();
        }
    }

    // Scenario D:
    // EmployeeService can duoc mo rong boi class con o package khac.
    static class EmployeeService {
        // TODO D1:
        // choose modifier cho method validateEmployeeCode(String code)
        // de class con package khac co the override/reuse, nhung khong mo qua rong.
        boolean validateEmployeeCode(String code) {
            return code != null && code.startsWith("EMP-");
        }
    }

    public static void main(String[] args) {
        // TODO E1:
        // Sau khi sua code theo cac scenario, viet 5 dong note tong ket:
        // 1) Khi nao dung private field + public getter
        // 2) Khi nao dung protected method
        // 3) Khi nao dung default/package-private
        // 4) Khi nao dung static variable
        // 5) Khi nao dung static utility method

        User user = new User("abc123hash");
        Invoice invoice = new Invoice(1000);
        TextUtils textUtils = new TextUtils();

        System.out.println("Invoice total=" + invoice.total());
        System.out.println(textUtils.capitalizeWords("  hELLo   jaVa  world "));
        System.out.println("User hash raw=" + user.passwordHash);

        System.out.println("=== Exercise 7.5 Done ===");
    }
}
