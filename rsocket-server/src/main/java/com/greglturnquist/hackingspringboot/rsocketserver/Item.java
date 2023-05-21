package com.greglturnquist.hackingspringboot.rsocketserver;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    private @Id String id;
    private String name;
    private String description;
    private double price;
    // end::code[]

    Item(String name, String description, double price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

}