package de.damien.funnyplaces.places;

import de.damien.funnyplaces.accounts.Account;
import de.damien.funnyplaces.comments.Comment;
import de.damien.funnyplaces.images.Image;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Place {

    private String title;
    private String description;
    private Account creator;
    private Image image;
    private Double latitude;
    private Double longitude;
    @OneToMany(mappedBy = "place")
    private ArrayList<Comment> comments;

}
