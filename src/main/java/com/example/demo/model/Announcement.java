package com.example.demo.model;

import com.example.demo.util.StringListConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String title;
    int area;
    int numberOfRooms;
    String propertyType;
    String location;
    String state;
    double price;
    String description;
    String owner;
    @Convert(converter = StringListConverter.class)
    List<String> imageNames = new ArrayList<>();
}
