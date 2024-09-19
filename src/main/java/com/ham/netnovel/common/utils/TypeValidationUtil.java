package com.ham.netnovel.common.utils;

public class TypeValidationUtil {


    /**
     * String 타입으로 받은 파라미터를 Long 타입으로 변환하는 메서드
     * Long 타입이 아니거나 null일경우 예외로 던짐
     *
     * @param value value 검증할 문자열
     * @return Long 타입으로 파싱후 변환된 문자열
     * @throws IllegalArgumentException 유효하지 않은 파라미터일경우 던져질 예외
     */
    public static Long validateLong(String value) {
        //null 체크
        if (value == null) {
            throw new IllegalArgumentException("validateLong 에러, 파라미터가 null 입니다");
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("validateLong 에러, 파라미터가 Long 타입이 아닙니다");
        }


    }

    //Novel 별점 유효성 검사, null 이거나 음수 또는 10초과여서는 안됨
    public static void validateNovelRating(Integer rating) {
        //null 체크
        if (rating == null) {
            throw new IllegalArgumentException("validateNovelRating 에러: 파라미터가 null 입니다");
        }
        // 범위 체크 (0~10)
        if (rating < 1 || rating > 10) {
            throw new IllegalArgumentException("validateNovelRating 에러: 파라미터가 1~10 범위를 벗어났습니다.");
        }
    }

    //코인수 유효성 검사, null 이거나 음수일수 없음
    public static void validateCoinAmount(Integer coinAmount) {
        if (coinAmount == null) {

            throw new IllegalArgumentException("validateCoinUseAmount 에러: 사용한 코인 갯수가 null 입니다");
        } else if (coinAmount < 0) {
            throw new IllegalArgumentException("validateCoinUseAmount 에러: 사용한 코인 갯수가 음수입니다.");
        }
    }

    //에피소드 조회수 파라미터 검증 로직
    public static void validateViewCount(Integer viewCount) {
        if (viewCount == null) {
            throw new IllegalArgumentException("validateViewCount 에러: 조회수가 null 입니다");
        }
        if (viewCount >= Integer.MAX_VALUE) {
            throw new IllegalArgumentException("validateViewCount 에러: 조회수가 Integer의 최대값을 초과했습니다");
        }


    }

    //SQL Injection 방지 로직
    public static String validateSearchWord(String searchWord) {
        if (searchWord == null) {
            return "";
        }
        //인젝션에 사용되는 특수문자 필터링
        String sanitizedWord = searchWord
                .replaceAll("--", "") // SQL 주석 제거
                .replaceAll("/\\*.*?\\*/", "") // SQL 블록 주석 제거
                .replaceAll("\\s+", " ");  // 여러 공백을 단일 공백으로 변환


        // 앞뒤 공백 제거
        return sanitizedWord.trim();

    }


}
