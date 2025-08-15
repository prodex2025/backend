package com.backend.backend.app.status;

public enum StatusFilter {
    APPROVED,       // 承認済み（approved = true）
    NOT_APPROVED,   // 未承認（approved = false）
    PUBLISHED,      // 公開（isPublished = true）
    UNPUBLISHED     // 未公開（isPublished = false）
}
