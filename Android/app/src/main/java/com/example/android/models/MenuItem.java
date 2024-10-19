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
    private String photoUrl;
    private String name;
    private Double price;
    private boolean available;
    private String description;

}
