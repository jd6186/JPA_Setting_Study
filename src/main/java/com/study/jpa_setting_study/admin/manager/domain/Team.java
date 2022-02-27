package com.study.jpa_setting_study.admin.manager.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Team {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long teamId;
    private String teamName;
}
