package aut.milou.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 250)
    private String password;

    public User() {}

    public User(String name ,String email ,String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    @PrePersist
    @PreUpdate
    private void normalize() {
        if(this.email != null)
            this.email = this.email.trim().toLowerCase();
        if (this.name != null)
            this.name = this.name.trim();
        if (this.password != null)
            this.password = this.password.trim();
        if (this.name == null || this.name.isEmpty())
            throw new IllegalArgumentException("name cannot be empty.");
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof User))
            return false;

        User user = (User) o;
        if (this.email == null || user.email == null)
            return false;
        return email.equalsIgnoreCase(user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email.toLowerCase());
    }
}