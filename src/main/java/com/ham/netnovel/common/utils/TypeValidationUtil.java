package com.ham.netnovel.common.utils;

public class TypeValidationUtil {


    /**
     * String 타입으로 받은 파라미터를 Long 타입으로 변환하는 메서드
     * Long 타입이 아니거나 null일경우 예외로 던짐
     * @param value value 검증할 문자열
     * @return Long 타입으로 파싱후 변환된 문자열
     * @throws IllegalArgumentException 유효하지 않은 파라미터일경우 던져질 예외
     */
    public static Long validateLong(String value){
        //null 체크
        if (value==null){
            throw new IllegalArgumentException("validateLong 에러, 파라미터가 null 입니다");
        }
        try {
            return Long.parseLong(value);
        }catch (NumberFormatException  ex){
            throw new IllegalArgumentException("validateLong 에러, 파라미터가 Long 타입이 아닙니다");
        }


    }


}
