package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AnnouncementWithImages {
    Long id;
    String title;
    int area;
    int numberOfRooms;
    String propertyType;
    String location;
    String state;
    double price;
    String description;
    String owner;
    List<byte[]> images;
}
