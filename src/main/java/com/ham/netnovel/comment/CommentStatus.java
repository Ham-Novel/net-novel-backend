package com.ham.netnovel.comment;

public enum CommentStatus {
    ACTIVE, // 활성 상태 (기본 상태)
    DELETED_BY_USER, // 사용자에 의해 삭제됨
    HIDDEN_BY_ADMIN, // 관리자에 의해 숨김 처리됨
    REPORTED, // 신고된 상태
    ARCHIVED // 보관된 상태
}
