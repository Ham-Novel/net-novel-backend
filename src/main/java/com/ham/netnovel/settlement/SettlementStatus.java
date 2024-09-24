package com.ham.netnovel.settlement;



public enum SettlementStatus {

    REQUESTED,        // 정산 신청됨
    COMPLETED,          // 정산 완료됨
    REJECTED,         // 정산 거부됨
    CANCELED_BY_USER  // 유저가 정산 취소
}
