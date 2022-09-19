package de.damien.funnyplaces.comments;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.damien.funnyplaces.accounts.Account;
import de.damien.funnyplaces.places.Place;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Long id;
    private String text;
    @ManyToOne
    @JoinColumn(name = "name", nullable = false)
    @JsonIgnoreProperties("password")
    private Account writer;

    @ManyToOne
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;
}
