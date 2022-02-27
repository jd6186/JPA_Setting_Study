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
    @Column(name = "TEAM_ID")
    private Long teamId;
}
