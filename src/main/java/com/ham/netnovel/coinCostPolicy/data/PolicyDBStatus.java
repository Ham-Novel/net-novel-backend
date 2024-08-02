package com.ham.netnovel.coinCostPolicy.data;

//Todo DB 삭제/활성 처리 상태 Enum => 전 도메인 Entity 통합
public enum PolicyDBStatus {
    ACTIVE, // 활성 상태 (기본 상태)
    DELETED_BY_USER, // 사용자에 의해 삭제됨
    HIDDEN_BY_ADMIN, // 관리자에 의해 숨김 처리됨
    REPORTED, // 신고된 상태
    ARCHIVED // 보관된 상태
}
