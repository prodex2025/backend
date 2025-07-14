package com.backend.backend.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminRestaurantsDto {
    private UUID id;
    private String name;
    private Boolean isPublished;
    private Date applicationDate;
    private Date approvalDate;
}
