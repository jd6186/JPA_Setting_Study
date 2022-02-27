package com.study.jpa_setting_study.admin.player.domain;

import com.study.jpa_setting_study.admin.team.domain.Team;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Player {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "PLAYER_ID")
    private Long playerId;
    @Column(name = "PLAYER_NAME")
    private String playerName;

    // FetchType을 Lazy로 설정하지 않으면 무조건 Team을 조회하게 됨
    // Lazy로 설정하게 되면 Team에 대한 정보를 조회할 때 그 때 따로 조회하기 때문에 일반적인 상황에서는 Lazy를 작성하는 것이 성능면에서 효율적
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID")
    private Team team;
}
