package nextstep.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Size;
import java.util.Objects;

@Embeddable
public class UserProfile {

    @Size(min = 3, max = 20)
    @Column(nullable = false)
    private String name;

    @Size(max = 50)
    private String email;

    public UserProfile() {
    }

    public UserProfile(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public UserProfile setName(String name) {
        this.name = name;
        return this;
    }

    public UserProfile setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public boolean equalsNameAndEmail(User target) {
        if (Objects.isNull(target)) {
            return false;
        }

        return equalsName(target.getName()) &&
                equalsEmail(target.getEmail());
    }

    private boolean equalsName(String name) {
        return this.name.equals(name);
    }

    private boolean equalsEmail(String email) {
        return this.email.equals(email);
    }

    @Override
    public String toString() {
        return "UserProfile [name=" + name + ", email=" + email + "]";
    }
}
