package com.study.jpa_setting_study.admin.team.domain;

import com.study.jpa_setting_study.admin.stadium.domain.Stadium;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class Team {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "TEAM_ID")
    private Long teamId;
    @Column(name = "TEAM_NAME")
    private String teamName;
    
    // 양방향 관계일 때는 부모 테이블에 @OneToMany 어노테이션을 붙여줘야 함
    @OneToMany(mappedBy = "team")
    private List<Stadium> stadiums;
}
