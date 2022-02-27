package com.study.jpa_setting_study.admin.manager.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Manager {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "MANAGER_ID")
    private Long managerId;
    @Column(name = "MANAGER_NAME")
    private String managerName;

    /*
    아래 방식으로도 저장 및 조회는 가능
    하지만 연관관계가 설정되어 있지 않아 조회 시에도 따로 두번 불러야만 조회 가능한 구조
    너무 데이터 지향적인 코딩이며 객체 지향과는 어울리지 않는 코딩 방식
     > 객체지향적인 방식을 보고 싶다면 Player 참조
    */
    @Column(name = "TEAM_ID")
    private Long teamId;
}
