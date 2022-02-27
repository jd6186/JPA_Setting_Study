package com.study.jpa_setting_study;

import com.study.jpa_setting_study.admin.manager.domain.Manager;
import com.study.jpa_setting_study.admin.manager.domain.Team;
import com.study.jpa_setting_study.admin.member.domain.Member;
import com.study.jpa_setting_study.admin.member.domain.MemberType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.Calendar;
import java.util.Date;
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
	 * 연관관계 매핑 방법
	 */
	@Test
	void tableRelationshipMapping(){
		// 서버 실행 시 EntityManagerFactory는 한번만 실행
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa_setting_study");
		EntityManager em = emf.createEntityManager();
		EntityTransaction et = em.getTransaction();
		et.begin();

		// 아래 Try Catch블럭에 있는 방식으로도 조회는 가능 하지만 연관관계가 설정되어 있지 않아 조회 시에도 따로 두번 불러야만 조회 가능
		try {
			Team team = new Team();
			team.setTeamName("리더스");
			em.persist(team);

			Manager manager = new Manager();
			manager.setManagerName("동욱");
			manager.setTeamId(team.getTeamId());
			em.persist(manager);
			
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
}
