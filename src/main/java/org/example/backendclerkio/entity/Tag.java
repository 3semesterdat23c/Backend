package org.example.backendclerkio.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tag")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id", nullable = false)
    private int tagId;

    @Column(name = "tag_name", nullable = false)
    private String tagName;

    // You can optionally remove this if you're not using the reverse relationship in Tag.
    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    @JsonBackReference // Prevents circular reference
    private Set<Product> products;

    public Tag(String tagName) {
        this.tagName = tagName;
    }
}