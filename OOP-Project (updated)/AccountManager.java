import java.util.Map;
import java.util.HashMap;

public class AccountManager {
    private final Map<String, User> users;

    public AccountManager() {
        this.users = new HashMap<>();
    }

    public boolean usernameExists(String username) {
        return users.containsKey(username);
    }

    public boolean createAccount(String username, String password) {
        if (username == null || username.isEmpty() || usernameExists(username)) {
            return false;
        }
        users.put(username, new User(username, password));
        return true;
    }

    public User login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.checkPassword(password)) {
            return user;
        }
        return null;
    }
}