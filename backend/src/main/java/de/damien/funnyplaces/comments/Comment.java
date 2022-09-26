package de.damien.funnyplaces.comments;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.damien.funnyplaces.accounts.Account;
import de.damien.funnyplaces.places.Place;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @SequenceGenerator(
            name = "comment_sequence",
            sequenceName = "comment_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "comment_sequence"
    )
    private Long commentId;
    private String text;
    @ManyToOne
    @JoinColumn(name = "name", nullable = false)
    @JsonIgnoreProperties(value = {"password", "createdPlaces", "comments"})
    private Account writer;

    @ManyToOne
    @JoinColumn(name = "mapped_place", nullable = false)
    @JsonIgnoreProperties(value = {"title", "description",
            "creator", "latitude", "longitude", "image", "comments"})
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Place place;
}
