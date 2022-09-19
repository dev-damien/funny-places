package de.damien.funnyplaces.images;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.damien.funnyplaces.places.Place;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Image {

    @Id
    @SequenceGenerator(
            name = "image_sequence",
            sequenceName = "image_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "image_sequence"
    )
    private Long imageId;
    private String name;
    private String type;
    @OneToOne(mappedBy = "image")
    @JsonIgnoreProperties(value = {"title", "description",
            "creator", "latitude", "longitude", "image", "comments"})
    private Place place;
    @Lob
    @Type(type = "org.hibernate.type.ImageType")
    @Column(length = 1024)
    private byte[] imageData;

}
