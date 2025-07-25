package com.backend.backend.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestDishDto {
    private UUID id;
    private String name;
    private Integer price;
    private String imageUrl;
    private String videoUrl;
    private String description;
    private List<AllergyDto> allergyDtoList;
}
