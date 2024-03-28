package piano;

public class User {
    
    private static User instance = null;

    protected User() {

    }

    public static User getInstance() {
        if(instance == null) {
            instance = new User();
        }

        return instance;
    }
}
