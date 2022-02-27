# JPA_Setting_Study

### JPA 소개
* Java Persistence API의 약자로 객체 관계 매핑을 위한 POJO persistence Model을 제공하는 API
  * 관계형 데이터베이스와 매핑이 가능하며 매핑 정보를 기반으로 객체를 활용해 데이터 조회, 생성, 수정, 삭제 등의 작업이 가능
  * 이를 통해 더 이상 개발자들이 SQL문을 작성하는데 불필요한 시간을 줄여주고 핵심 로직을 개발하는데 시간을 사용할 수 있게 함으로써 개발 효율성 향상
  * DB 마이그레이션 시에도 원하는 관계형 데이터베이스 정보만 속성 값에서 변경해주면 JPA가 알아서 해당 DB에 맞게 매핑하기 때문에 마이그레이션 시간도 단축 가능
* 기존에는 EJB 전문가 그룹에 의해 개발되었지만 추후 웹 애플리케이션 및 애플리케이션 클라이언트가 직접하숑할 수 있게 Java EE, Java SE 등에서도 사용 가능해진 상태
<br/><br/><br/><br/>

### 주의사항
1. EntityManagerFactory는 서버 실행 시 단일 인스턴스 후 전체 EntityManager 인스턴스 시 공유해 사용
   * 관계형 데이터베이스와 Connection 연결하는 부분이므로 여러번 할 필요가 없음

2. EntityManager는 각 작업 단위별로 개별 생성 후 처리
   * EntityManager는 트랜젝션을 관리하기 때문에 데이터 정합성 오류가 발생할 위험 요소를 제거하기 위해 각 작업 단위별로 트랜젝션 처리 후 close 필요

3. persistence.xml 내 property들 중 hibernate.hbm2ddl.auto의 value값은 개발, 운영 서버에서는 무조건 none으로 처리
   * create, update 등 사용 시 개발, 운영 데이터가 소실 또는 시스템 장애가 발생할 위험이 있음

4. Column 데이터 타입으로 Enum을 쓸 때는 EnumType.ORDINAL을 쓰게 되면 각 enum들을 0, 1, 2 ... 순서대로 숫자로 DB에 저장하게 됨
   * 이렇게 되면 Enum 타입이 하나 중간에 추가되면 DB전체 데이터를 모두 바꿔야하는 상황이 되버리므로 EnumType.STRING으로 저장 필수!
   * Member.java
    ```java
      @Entity
      @Getter
      @Setter
      public class Member {
          @Id
          private Long id;
          private String name;
          @Enumerated(EnumType.STRING)
          private MemberType memberType;
      }
    ```
  
   * MemberType.enum
    ```java
      public enum MemberType {
          ADMIN, USER
      }
    ```
  
   * TestCode
    ```java
      @Test
      void dbConnectionTest(){
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa_setting_study");
        EntityManager em = emf.createEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();

        try {
          Member member = new Member();
          member.setId(1L);
          member.setName("동욱");
          member.setMemberType(MemberType.ADMIN);

          // EntitiyManager에 변경 내역 반영
          em.persist(member);
          // 실 DB에 Commit
          et.commit();
        } catch (Exception ex){
          et.rollback();
          System.err.println("Error Rollback : " + ex);
        } finally {
          em.close();
          emf.close();
       }
      }
    ```
    
5. DB랑 매핑할 필요는 없는데 해당 Domain에서 관리해야되는 변수가 존재할 때는 @Transient
   * 예시
    ```java
    @Entity
    @Getter
    @Setter
    public class Member {
        @Id
        private Long id;
        private String name;

        // EnumType.ORDINAL로 하면 index번호가 들어가버리므로 추후 수정 시 데이터 정합성이 오류가 생김
        // 따라서 EnumType은 STRING으로 해야 함(변수타입명이 그대로 들어감)
        @Enumerated(EnumType.STRING)
        private MemberType memberType;

        //DB 내 Column명을 java 변수명 말고 따로 관리하고자할 때 활용
        @Column(name = "PHONE_NUMBER")
        private String user_phone_member;

        // 날짜 타입은 @Temporal 사용
        // DATE: 날짜, TIME: 시간, TIMESTAMP: 날짜와 시간
        @Temporal(TemporalType.DATE)
        private String reg_date;

        // DB랑 매핑할 필요는 없는데 해당 Domain에서 관리해야되는 변수가 존재할 때는 @Transient
        @Transient
        private String tempData;
    }
    ```
<br/><br/><br/><br/>

### 활용한 강의들
1. JPA에 대한 초기 개념 학습 : https://www.youtube.com/watch?v=egVZusxSeKw&list=PL9mhQYIlKEhfpMVndI23RwWTL9-VL-B7U&index=2
