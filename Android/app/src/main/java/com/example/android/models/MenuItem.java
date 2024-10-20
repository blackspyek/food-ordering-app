package com.example.android.models;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem implements Serializable {
    private String name;
    private String description;
    private double price;
    private boolean available;
    private String category;
    private String photoUrl;
    private long id;
}
