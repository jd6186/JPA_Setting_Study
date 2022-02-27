package com.study.jpa_setting_study.admin.member.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class Member {
    // GenerationType.AUTO - 각 관계형 DB에 맞게 Auto increment를 시켜주는 기능
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    
    // EnumType.ORDINAL로 하면 index번호가 들어가버리므로 추후 수정 시 데이터 정합성이 오류가 생김
    // 따라서 EnumType은 STRING으로 해야 함(변수타입명이 그대로 들어감)
    @Enumerated(EnumType.STRING)
    private MemberType memberType;
    
    //DB 내 Column 이름, 제약 조건을 java에서 관리하고 싶을 땐 @Column
    // Column명: PHONE_NUMBER(기본은 변수명), 문자열 길이: 50(기본은 255), null 사용 가능 여부: 불가능(기본은 True)
    @Column(name = "PHONE_NUMBER", length = 50, nullable = false)
    private String user_phone_member;

    // 날짜 타입은 @Temporal 사용
    // DATE: 날짜, TIME: 시간, TIMESTAMP: 날짜와 시간
    @Temporal(TemporalType.DATE)
    private Date reg_date;
    @Temporal(TemporalType.TIMESTAMP)
    private Date update_date;

    // DB랑 매핑할 필요는 없는데 해당 Domain에서 관리해야되는 변수가 존재할 때는 @Transient
    @Transient
    private String tempData;
}
