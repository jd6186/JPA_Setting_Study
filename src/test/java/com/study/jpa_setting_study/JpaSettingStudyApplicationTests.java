package com.study.jpa_setting_study;

import com.study.jpa_setting_study.admin.manager.domain.Manager;
import com.study.jpa_setting_study.admin.member.domain.Member;
import com.study.jpa_setting_study.admin.member.domain.MemberType;
import com.study.jpa_setting_study.admin.player.domain.Player;
import com.study.jpa_setting_study.admin.stadium.domain.Stadium;
import com.study.jpa_setting_study.admin.team.domain.Team;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.transaction.Transaction;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@SpringBootTest
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
class JpaSettingStudyApplicationTests {
	@Test
	void contextLoads() {
	}

	/**
	 * Hibernate EntityManager를 직접 컨트롤 해서 DB Connection하는 방법
	 */
	@Test
	void dbConnectionTest(){
		// 서버 실행 시 EntityManagerFactory는 한번만 실행
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa_setting_study");

		//  EntityManager는 transaction에 맞춰 개별 생성 - 왜냐면 EntityManager는 자신에게 할당된 하나의 Transaction만 관리함
		EntityManager em = emf.createEntityManager();
		
		// EntityManager가 관리한 트랜젝션 생성 및 시작
		EntityTransaction et = em.getTransaction();
		et.begin();
		
		// DB에 Insert할 데이터 생성
		try {
			Member member = new Member();
			member.setId(1L);
			member.setName("동욱");
			member.setMemberType(MemberType.ADMIN);
			member.setUser_phone_member("010-2558-6186");
			member.setTempData("불필요 데이터");

			Date newDate = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul")).getTime();
			member.setReg_date(newDate);
			member.setUpdate_date(newDate);

			// 생성한 데이터를 DB에 영구 저장
			em.persist(member);
			// 실제 DB에 반영
			et.commit();
		} catch (Exception ex){
			et.rollback();
			System.err.println("Error Rollback : " + ex);
		} finally {
			em.close();
			emf.close();
		}
		System.out.println("Hello~");
	}

	/**
	 * 연관관계 매핑 없이 연결하는 법
	 */
	@Test
	void tableRelationshipMapping(){
		// 서버 실행 시 EntityManagerFactory는 한번만 실행
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa_setting_study");
		EntityManager em1 = emf.createEntityManager();
		EntityTransaction et1 = em1.getTransaction();
		et1.begin();

		try {
			Team team = new Team();
			team.setTeamName("리더스");
			em1.persist(team);

			Manager manager = new Manager();
			manager.setManagerName("동욱");
			manager.setTeamId(team.getTeamId());
			em1.persist(manager);
			et1.commit();

			Team findOneTeam = em1.find(Team.class, team.getTeamId());
			System.out.println("findOneTeam : " + findOneTeam.getTeamName());
			Manager findOneManager = em1.find(Manager.class, manager.getManagerId());
			System.out.println("findOneManager : " + findOneManager.getManagerName());

		} catch (Exception ex){
			et1.rollback();
			System.err.println("Error Rollback : " + ex);
		} finally {
			em1.close();
			emf.close();
		}
	}

	/**
	 * ManyToOne(단방향) 연관관계 매핑
	 */
	@Test
	void manyToOneSingleRelationshipMapping(){
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa_setting_study");
		EntityManager em = emf.createEntityManager();
		EntityTransaction et = em.getTransaction();
		et.begin();

		try{
			Team team = new Team();
			team.setTeamName("필라델피아 식서스");
			em.persist(team);
			System.out.println("??");

			Player player = new Player();
			player.setPlayerName("제임스하든");
			player.setTeam(team);
			em.persist(player);
			System.out.println("???");
			et.commit();

			Player newPlayer = em.find(Player.class, player.getPlayerId());
			System.out.println("Player Team Name : " + newPlayer.getTeam().getTeamName());
		} catch (Exception ex){
			et.rollback();
			System.err.println("Error Rollback : " + ex);
		} finally {
			em.close();
			emf.close();
		}
	}

	/**
	 * ManyToOne(단방향) 연관관계 매핑
	 */
	@Test
	void manyToOneMultiRelationshipMapping(){
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa_setting_study");
		EntityManager em = emf.createEntityManager();
		EntityTransaction et = em.getTransaction();
		et.begin();

		try{
			Team team = new Team();
			team.setTeamName("필라델피아 식서스");
			em.persist(team);
			System.out.println("??");

			// Persist는 Entity Manager가 파라미터에 입력된 객체를 관리하도록 등록만 한 상태
			Stadium stadium1 = new Stadium();
			stadium1.setStadiumName("뉴욕 경기장");
			stadium1.setTeam(team);
			em.persist(stadium1);

			Stadium stadium2 = new Stadium();
			stadium2.setStadiumName("필라델피아 경기장");
			stadium2.setTeam(team);
			em.persist(stadium2);

			// team객체에 stadium객체들 add
			// 오류를 줄이기 위해 작성할 뿐 사실 주인 객체인 team객체를 생성할 때 이미 반영됨
			team.getStadiums().add(stadium1);
			team.getStadiums().add(stadium2);

			// Transaction 처리
			et.commit();

			// 저장된 데이터 조회
			Stadium newStadium1 = em.find(Stadium.class, stadium2.getStadiumId());
			Team tempTeam = newStadium1.getTeam();
			List<Stadium> stadiums = tempTeam.getStadiums();
			stadiums.forEach((stadi)->{
				System.out.println("S_ID : " + stadi.getStadiumId() + "\nS_NAME : " + stadi.getStadiumName());
			});

		} catch (Exception ex){
			et.rollback();
			System.err.println("Error Rollback : " + ex);
		} finally {
			em.close();
			emf.close();
		}
	}
}
