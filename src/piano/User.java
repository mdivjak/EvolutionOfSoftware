package piano;

public class User {
    
    private static User instance = null;

    private String firstName, lastName, username;

    protected User() {
        firstName = "";
        lastName = "";
        username = "";
    }

    public static User getInstance() {
        if(instance == null) {
            instance = new User();
        }

        return instance;
    }

    public boolean setData(String firstName, String lastName, String username) {
        if(username.contains(" ")) return false;
        instance.firstName = firstName;
        instance.lastName = lastName;
        instance.username = username;

        return true;
    }

    public String getFirstName() {
        return instance.firstName;
    }

    public String getLastName() {
        return instance.lastName;
    }

    public String getUsername() {
        return instance.username;
    }

    public void clearData() {
		instance.firstName = "";
		instance.lastName = "";
		instance.username = "";
	}

    public boolean hasData() {
		if (instance.firstName.equals("") ||
				instance.lastName.equals("") ||
				instance.username.equals("")) return false;
		return true;
	}

    public static String getExportPath(String fileName) throws ExportForbiddenException {
        if(!getInstance().hasData()) throw new ExportForbiddenException();

        return getInstance().username + "/" + fileName;
    }

    public String getSignature() {
        throw new UnsupportedOperationException();
    }
}
