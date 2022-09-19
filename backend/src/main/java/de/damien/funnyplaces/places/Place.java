package de.damien.funnyplaces.places;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.damien.funnyplaces.accounts.Account;
import de.damien.funnyplaces.comments.Comment;
import de.damien.funnyplaces.images.Image;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Place {

    @Id
    @SequenceGenerator(
            name = "place_sequence",
            sequenceName = "place_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "place_sequence"
    )
    private Long id;
    private String title;
    private String description;
    @ManyToOne()
    @JoinColumn(name = "creator")
    @JsonIgnoreProperties(value = {"password", "createdPlaces", "comments"})
    private Account creator;
    private Double latitude;
    private Double longitude;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "image_id")
    @JsonIgnoreProperties(value = {"name", "type", "place", "imageData"})
    private Image image;

    @OneToMany(mappedBy = "place", cascade = CascadeType.MERGE)
    @JsonIgnoreProperties(value = {"text", "writer", "place"})
    private Set<Comment> comments;

}
