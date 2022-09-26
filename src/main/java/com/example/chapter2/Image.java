package com.example.chapter2;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Image {
    private int id;
    private String name;

    public Image(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
