package com.backend.backend.domain.dto;

import com.backend.backend.domain.model.Restaurant;
import com.backend.backend.domain.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
public class AdminRestaurantDto {
    private UUID id;
    private String applicantName;
    private String name;
    private Boolean approved;
    private Boolean isPublished;
    private Date applicationDate;
    private Date approvalDate;

    public static AdminRestaurantDto from(Restaurant r) {
        return new AdminRestaurantDto(
                r.getId(),
                r.getUser().getUserName(), // 申請者
                r.getName(),
                r.getApproved(),
                r.getIsPublished(),
                r.getApplicationDate(),
                r.getApprovalDate()
        );
    }
}
