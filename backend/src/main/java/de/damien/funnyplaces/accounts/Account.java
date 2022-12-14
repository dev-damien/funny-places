package de.damien.funnyplaces.accounts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.damien.funnyplaces.comments.Comment;
import de.damien.funnyplaces.places.Place;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Account {

    @Id
    private String name;
    private String password;
    @OneToMany(mappedBy = "creator")
    private Set<Place> createdPlaces;

    @OneToMany(mappedBy = "writer")
    @JsonIgnore
    private Set<Comment> comments;

    @Override
    public String toString() {
        return "Account{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
