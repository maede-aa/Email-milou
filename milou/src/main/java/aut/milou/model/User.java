package aut.milou.model;

import java.util.Objects;

public class User {
    private final String name;
    private final String email;
    private final String password;

    public User(String name ,String email ,String password) {
        if (! email.contains("@milou.com"))
            email += "@milou.com";
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof aut.milou.model.User))
            return false;

        aut.milou.model.User user = (aut.milou.model.User) o;
        return email.equalsIgnoreCase(user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email.toLowerCase());
    }
}
