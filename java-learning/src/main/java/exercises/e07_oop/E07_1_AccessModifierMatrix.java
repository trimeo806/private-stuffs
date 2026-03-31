package exercises.e07_oop;

/**
 * Exercise 7.1: Access Modifier Matrix
 *
 * Muc tieu:
 * - Phan biet private, default(package-private), protected, public trong CUNG
 * package.
 * - Nhin thay su khac nhau giua truy cap truc tiep field va truy cap qua
 * method.
 *
 * Huong dan:
 * - Doc TODO theo thu tu 1 -> 6.
 * - Hoan thanh tung TODO, sau do chay main().
 */

class UserProfile {
    private String password;
    String nickname; // default/package-private
    protected String email;
    public String displayName;

    public UserProfile(String password, String nickname, String email, String displayName) {
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.displayName = displayName;
    }

    // TODO 1:
    // Tao getter getMaskedPassword() tra ve "****" + 2 ky tu cuoi (neu co).
    // Vi du: "s3cret" -> "****et".
    public String getMaskedPassword() {
        String last2chars = this.password.substring(this.password.length() - 2, this.password.length());
        return "****" + last2chars;
    }

    // TODO 2:
    // Tao method protected updateEmail(String newEmail) co validate don gian:
    // chi cho phep email chua ky tu '@', nguoc lai in "Invalid email".

    protected String updateEmail(String newEmail) {
        if (!newEmail.contains("@")) {
            return "Invalid email";
        }

        return this.email = newEmail;
    }

    // TODO 3:
    // Tao method default/package-private rename(String newNickname).
    // Neu newNickname rong, bo qua cap nhat.

    String rename(String newNickname) {
        if (newNickname.length() == 0) {
            return "";
        }

        return newNickname;
    }

    public void printInternalState() {
        System.out.println("displayName=" + displayName);
        System.out.println("nickname=" + nickname);
        System.out.println("email=" + email);
        System.out.println("password(length)=" + password.length());
    }
}

class ProfileAdmin {
    public void inspect(UserProfile profile) {
        // TODO 4:
        // Thu truy cap tat ca field cua profile va ghi lai ket qua:
        // - Field nao truy cap duoc trong cung package?
        // - Field nao khong truy cap duoc?
        // Goi y: private password se khong truy cap truc tiep duoc.
        String displayName = profile.displayName;
        String email = profile.email;
        String password = profile.getMaskedPassword();
        System.out.println(password);
    }
}

public class E07_1_AccessModifierMatrix {

    public static void main(String[] args) {
        UserProfile profile = new UserProfile("s3cret", "alice", "alice@mail.com", "Alice A.");
        ProfileAdmin admin = new ProfileAdmin();

        // TODO 5:
        // Goi lan luot cac method vua tao, sau do admin.inspect(profile).
        profile.updateEmail("trimeo@gmail.com");

        // TODO 6:
        // Tu viet bang tong ket nho ngay trong comment:
        // private / default / protected / public khac nhau gi trong CUNG package?

        profile.printInternalState();
        admin.inspect(profile);

        System.out.println("=== Exercise 7.1 Done ===");
    }
}