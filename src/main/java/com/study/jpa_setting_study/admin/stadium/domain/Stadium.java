package com.study.jpa_setting_study.admin.stadium.domain;

import com.study.jpa_setting_study.admin.team.domain.Team;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class Stadium {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "STADIUM_ID")
    private Long stadiumId;
    @Column(name = "STADIUM_NAME")
    private String stadiumName;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID")
    private Team team;
}
