# JPA_Setting_Study

### 목차
#### [1. JPA 소개](#JPA-소개)
#### [2. Entity 내 각 컬럼 속성 관리 어노테이션 정리](#Entity-내-각-컬럼-속성-관리-어노테이션-정리)
#### [3. 연관관계 매핑 방법](#연관관계-매핑-방법)
#### [4. JPA 사용 시 주의사항](#JPA-사용-시-주의사항)
#### [5. 활용한 강의들](#활용한-강의들)
<br/><br/><br/><br/>

### JPA 소개
* 등장 배경
  * Java Persistence API의 약자로 객체 관계 매핑을 위한 POJO persistence Model을 제공하는 API
    * 관계형 데이터베이스 내 테이블과 Java 내 POJO 객체를 매핑이 가능
      * 매핑 정보를 기반으로 객체를 활용해 데이터 조회, 생성, 수정, 삭제 등의 작업이 가능
    * 이를 통해 더 이상 개발자들이 SQL문을 작성하는데 불필요한 시간을 줄여주고 핵심 로직을 개발하는데 시간을 사용할 수 있게 함으로써 개발 효율성 향상
    * DB 마이그레이션 시에도 원하는 관계형 데이터베이스 정보만 속성 값에서 변경해주면 JPA가 알아서 해당 DB에 맞게 매핑하기 때문에 마이그레이션 시간도 단축 가능
    * 기존 쿼리 사용방식보다 더 객체지향적인 설계 가능
  * 기존에는 EJB 전문가 그룹에 의해 개발되었지만 추후 웹 애플리케이션 및 애플리케이션 클라이언트가 직접하숑할 수 있게 Java EE, Java SE 등에서도 사용 가능해진 상태
* 용어정리
  * 상위 객체, 하위 객체
    * 글을 보다보면 상위 객체, 하위 객체라고 작성한 부분이 존재
    * **Team과 Member**와 같이 **연관관계에 있지만** **더 큰 규모를 가진 객체를 '상위 객체'**라고 지칭하고 **더 작은 규모를 가진 객체를 '하위 객체'**라고 지칭함
  * 영속성이란?
    * Java 내 여러 Entity(DB로 치면 Table)객체들을 DB와 통신하며 데이터를 관리할 수 있도록 **EntityManager에 등록 후 관리하는 것**
    * EntityManager 객체의 persist Method를 통해 영속성 등록 가능
    * 영속성 등록 시 EntityManager에 의해 관리되는 기능들
      * 1차 Cache 지원
        * 등록 시 내부적으로 Cache가 되며 이를 통해 저장된 데이터 조회 가능
        * 이 때문에 빠른 데이터 조회가 가능. 만약 데이터가 없다면 그 때 DB에서 데이터 조회
        * 다만 EntityManager를 통해 생성한 Transaction 객체가 close되기 전까지 thread 하나에서 임시로 저장하는 cache이기 때문에 주의 필요
        * 만약 1차 Cache에만 저장하지 않고 DB에 바로 동기화해야 한다면 EntityManager의 flush() 메서드를 활용하면 됨
        * transaction 객체의 commit() 메서드가 **여러 EntityManager의 SQL들을 말아두었던 것들 모두를 flush()**하고 DB에 commit하는 것이라고 보면 됨
          * 이 때 **1차 Cache가 지워지는 것이 아니라 말아두었던 SQL만 DB로 보내는 것**
        * 그럼 1차 Cache를 다 지우고 싶다면? EntityManager 내 clear() 메서드를 활용하면 됨
        * persist만 한 상태에서 flush()나 commit()을 안 하고 clear() 또는 close()를 해버리면 이는 준영속상태에서 **DB와 동기화되지 않고 삭제됨**
          * 문제는 주로 연관관계 매핑을 할 때 FetchType을 LAZY로 해야하는데<br/>
            이 **LAZY 모드는 1차 Cache를 기반**으로하기 때문에 clear()를 잘못해 버리면 여기서 에러가 발생함 **반드시 주의 필요**

      * 변경감지(Drity Check) 기능 제공
        * 값을 바꾸고 싶을 때 우선 조회를 하게 되는데 조회 시 이미 실제 DB에 있던 데이터가 1차 Cache에 저장됨
        * 이후 변경을 원해서 setter를 이용해 값을 바꾸게 되면 EntityManager가 이를 감지하고(스냅샷 메모리가 따로 있어서 변경되면 감지 가능) commit 시 update문을 날리게 됨
        * 왜냐하면 사용자가 조회한 그 1차 Cache 내 데이터가 사용자가 설정한 가장 최근 데이터이므로 이 값이 변경되었다면 DB의 값도 변경해야 하기 때문
        * 따라서 따로 update()같은 메서드는 존재하지도 않고 유저가 알 필요도 없음
        * 이렇게 만든 이유는 Java는 원래 객체를 다룰 때 이렇게 다루기 때문<br/>
          즉, 객체 내 값을 변경하는데 Update 쿼리 날리면서 변경하지 않기 때문에 여기서도 이런 기능을 만들어둔 것

      * JPQL 쿼리 실행 시 자동으로 flush() 실행
        * 자동으로 실행되므로 주의 필요
<br/><br/><br/><br/>

### Entity 내 각 컬럼 속성 관리 어노테이션 정리
#### 종류
1. @Id - 해당 Table의 Primary Key 컬럼 위에 사용. 무조건 있어야 하며 없을 시 오류 발생
   * @GeneratedValue
     * Id에 해당하는 컬럼의 value들을 자동으로 넣어주고자 할 때 사용(Auto increment와 같이)
     * 사용 예시 
       * @GeneratedValue(strategy = GenerationType.AUTO)
       * GenerationType에 따라 관계형 DB에 매핑
         * IDENTITY - 데이터베이스에 위임(MySQL)
         * SEQUENCE - 데이터베이스 시퀀스 오브젝트 사용(ORACLE)
           * @SequenceGenerator 필요 
         * TABLE - 키 생성용 테이블 사용, 모든 DB에서 사용
           * @TableGenerator 필요
         * AUTO - 방언에 따라 자동 지정, 기본값
   * 키 지정 시 주의사항
     * Long타입 사용을 통해 대량의 데이터 처리가 가능하도록 조정
     * 대체키 사용을 통해 비지니스로직과는 전혀 상관없는 키를 운용
       * 나중에 비지니스 로직이 바뀌어도 변경할 필요가 없게
     * 키 생성전략 사용(GeneratedValue)
2. @Column 
   * 해당 컬럼의 이름, 제약 조건 및 타입을 관리하기 위해 적용
   * name - 컬럼명 설정
   * length - 해당 데이터타입의 길이 설정 기본 255
   * nullable - null값 가능여부
   * unique - DB unique 제약조건과 동일
3. @Temporal
   * 날짜 타입은 @Temporal 사용
   * DATE - 날짜
   * TIME - 시간
   * TIMESTAMP - 날짜와 시간
4. @Transient
   * DB랑 매핑할 필요는 없는데 해당 Domain에서 관리해야되는 변수가 존재할 때는 @Transient 사용

#### 예시
```
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    @Column(name = "PHONE_NUMBER", length = 50, nullable = false)
    private String user_phone_member;

    @Temporal(TemporalType.DATE)
    private Date reg_date;
    @Temporal(TemporalType.TIMESTAMP)
    private Date update_date;

    // DB랑 매핑할 필요는 없는데 해당 Domain에서 관리해야되는 변수가 존재할 때는 @Transient
    @Transient
    private String tempData;
}
```
<br/><br/><br/><br/>

### 연관관계 매핑 방법
#### 1. 연관관계 미설정 시 
   * 저장 자체에는 문제가 없음<br/>
   하지만 연관관계가 설정되어 있지 않아 조회 시에도 따로 두번 불러야만 조회 가능한 구조<br/>
   너무 데이터 지향적인 코딩이며 객체 지향과는 어울리지 않는 코딩 방식<br/>
   * Manager
   ```java
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

   ```

   * Team
   ```java
   @Entity
   @Getter
   @Setter
   public class Team {
       @Id @GeneratedValue(strategy = GenerationType.AUTO)
       private Long teamId;
       private String teamName;
   }
   ```

   * TestCode
   ```java
   @Test
   void tableRelationshipMapping(){
    // 서버 실행 시 EntityManagerFactory는 한번만 실행
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa_setting_study");
    EntityManager em1 = emf.createEntityManager();
    EntityTransaction et1 = em1.getTransaction();
    et1.begin();

    // 아래 Try Catch블럭에 있는 방식으로도 조회는 가능 하지만 연관관계가 설정되어 있지 않아 조회 시에도 따로 두번 불러야만 조회 가능
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
       System.out.println("findOneTeam : " + findOneTeam.getTeamName()); // findOneTeam : 리더스
       Manager findOneManager = em1.find(Manager.class, manager.getManagerId());
       System.out.println("findOneManager : " + findOneManager.getManagerName()); // findOneManager : 동욱
     } catch (Exception ex){
       et1.rollback();
       System.err.println("Error Rollback : " + ex);
     } finally {
       em1.close();
       emf.close();
     }
   }
   ```

   * 결과
   <img src='src/main/resources/static/img/NotRelationMapping.png'/>
<br/><br/><br/><br/>

#### 2. 연관관계 설정을 통해 해당 객체 자체를 가져와 매핑하는 방법
   1. @ManyToOne(단방향 관계) 매핑
      * @JoinColumn을 활용해 Join할 객체의 PK값을 적어주어 FK 제약사항 설정<br/>
        1:N 관계 시에 사용되며 상위 객체에서는 하위 객체를 알 수 없고 하위 객체에서만 상위 객체를 조회 가능<br/>
        아래 예시에서는 Team에 Player들이 속해 있으므로 Team이 상위 객체, Player가 하위 객체<br/>
        따라서 Player에 ManyToOne으로 Team을 주입한 것을 볼 수 있음
        
      > 이를 통해 연관관계를 설정하지 않았을 때와 다르게 이번에는 *Player만 조회하면 매핑된 Team을 조회 가능*

      * Player.java
        ```java
          @Entity
          @Getter
          @Setter
          public class Player {
              @Id @GeneratedValue(strategy = GenerationType.AUTO)
              @Column(name = "PLAYER_ID")
              private Long playerId;
              @Column(name = "PLAYER_NAME")
              private String playerName;
              @ManyToOne(fetch = FetchType.LAZY)
              @JoinColumn(name = "TEAM_ID")
              private Team team;
          }
        ```
        
      * Team.java
        ```java
          @Entity
          @Getter
          @Setter
          public class Team {
              @Id @GeneratedValue(strategy = GenerationType.AUTO)
              @Column(name = "TEAM_ID")
              private Long teamId;
              @Column(name = "TEAM_NAME")
              private String teamName;
          }
        ```
        
      * TestCode
        ```java
          @Test
          void manyToOneRelationshipMapping(){
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
        ```
<br/><br/>

   2. @ManyToOne, @OneToMany(양방향 관계) 매핑
      * *들어가기전 인지해야 하는 부분*
        > 개발 전 설계 시 **단방향 매핑**으로 이미 연관관계 매핑은 끝난 것이므로 여기서 설계를 끝내는 것이 바람직<br/>
          추후 필요에 따라 양방향 연결은 가능하나 단순히 조회 편의성만 제공할 뿐<br/>
          DB 내부적으로는 순환구조를 만들기 때문에 조회 성능 자체는 떨어짐

      * 1:N 관계에서 **상위 객체와 연관된 모든 하위 객체를 조회**해야 하는 경우가 존재
      * 이 때 사용하는 어노테이션이 바로 OneToMany 즉, 하나를 조회해서 여러개를 조회하는 용도
      * 여기서 중요한 부분은 바로 @OneToMany 내 작성되는 **mappedBy**라는 속성값
        * mappedBy를 사용하는 이유는 관계형 데이터베이스와 Java 객체가 가지는 차이점 때문
        * DB는 양방향 매핑을 통해 하위 객체와 상위 객체를 아무리 조인해도 각각은 단일 객체로써 유지되지만<br/> 
          Java에서 객체를 양방향 매핑으로 설정하면 객체의 참조는 둘인데 반해, 테이블은 하나의 외래 키 만을 사용하므로 괴리가 생김
          이 괴리를 없에기 위해 등장한 개념이 '연관관계의 주인'
            > 아래 두개가 동시에 호출된다면 실제 DB에서는 무엇을 기준으로 값을 양쪽으로 처리해야 하는가?
            > teamA.getMembers().add(memberA)<br/>
            > memberA.setTeam(teamB)
            > 답은 '특정 조건없이는 불가능하다'이다.
          
          teamA에 memberA를 넣되 memberB는 teamB다? 또는 Team에서 members의 team 값을 바꾸도록 한다?
          어떤 객체를 기준으로 값을 변경해야하는지 알 수 없기 때문에 등장한 개념이 **'연관관계의 주인'**
          * *연관관계의 주인*
            * JPA에서는 어차피 양방향 관계라는 것이 '단방향관계 + 단방향관계'로 이루어져 있기 때문에 
              연관관계의 주인을 설정하므로써 어떤 객체를 보고 데이터 생성, 수정, 삭제를 할 것인지 결정하는 주체를 설정하게 만듬
            * 객체의 두 관계중(1:N) 하나를 연관관계의 주인으로 명시적으로 지정
            * 연관관계의 **주인만이 외래키를 관리**(등록/수정)
            * 주인이 아닌쪽은 단순 읽기만 가능
            * **주인은 MappedBy 속성 사용이 필요 없음**
            * **주인이 아니면 MappedBy 속성으로 주인 지정**
         * 주인 지정 가이드
           * **FK가 들어가 있는 하위 객체**를 주인 테이블로 지정하는 것이 바람직
           * 상위 객체를 주인으로 지정 시 **연관된 다른 테이블들에 영향이 갈 수 있음**
         * 주인이 지정되고 난 후 주의사항
           * 주인이 아닌 객체에서 값을 넣을 시 주인에서는 해당 값을 인지할 수 없어 FK값이 Null이 됨
           * 이 때문에 Null Point Exception이 자주 발생하므로 반드시 주의 필요
           * 그래서! 그냥 양쪽다 넣어두면 사실 문제될 부분이 없으므로 JPA Programming 저자 김영한님은 하위 객체, 상위 객체 **양쪽 모두에 값을 넣는 것을 추천**<br/>
             객체 입장에서 보면 당연히 값을 초기화 시켜주는 것이 사실 맞기 때문에 객체지향적인 설계에도 위반되는 내용은 아님
      * 예시
        * Stadium.java
        ```java
          @Entity
          @Getter
          @Setter
          public class Stadium {
              @Id @GeneratedValue(strategy = GenerationType.AUTO)
              @Column(name = "STADIUM_ID")
              private Long stadiumId;
              @Column(name = "STADIUM_NAME")
              private String stadiumName;
              @ManyToOne
              @JoinColumn(name = "TEAM_ID")
              private Team team;
          }
        ```
        
        * Team.java
        ```java
          @Entity
          @Getter
          @Setter
          public class Team {
              @Id @GeneratedValue(strategy = GenerationType.AUTO)
              @Column(name = "TEAM_ID")
              private Long teamId;
              @Column(name = "TEAM_NAME")
              private String teamName;

              @OneToMany(mappedBy = "team")
              // 매핑 시 반드시 mappedBy 기재 필수, new ArrayList<>() 기재 필수!
              private List<Stadium> stadiums = new ArrayList<>();
          }
        ```
        
        * TestCode
        ```java
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

              // 저장된 데이터 조회
              Stadium newStadium1 = em.find(Stadium.class, stadium2.getStadiumId());
              Team tempTeam = newStadium1.getTeam();
              List<Stadium> stadiums = tempTeam.getStadiums();
              stadiums.forEach((stadi)->{
               System.out.println("S_ID : " + stadi.getStadiumId() + "\nS_NAME : " + stadi.getStadiumName());
              });

              // Transaction 처리
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
       

### JPA 사용 시 주의사항
1. EntityManagerFactory는 서버 실행 시 단일 인스턴스 후 전체 EntityManager 인스턴스 시 공유해 사용
   * 관계형 데이터베이스와 Connection 연결하는 부분이므로 여러번 할 필요가 없음
<br/>

2. EntityManager는 각 작업 단위별로 개별 생성 후 처리
   * EntityManager는 트랜젝션을 관리하기 때문에 데이터 정합성 오류가 발생할 위험 요소를 제거하기 위해 각 작업 단위별로 트랜젝션 처리 후 close 필요
<br/>

3. persistence.xml 내 property들 중 hibernate.hbm2ddl.auto의 value값은 개발, 운영 서버에서는 무조건 none으로 처리
   * create, update 등 사용 시 개발, 운영 데이터가 소실 또는 시스템 장애가 발생할 위험이 있음
<br/>

4. Build Tool이 Gradle일 때는 Entity로 활용할 Class들을 직접 등록해줘야 인식 가능
     ```
       <?xml version="1.0" encoding="UTF-8" ?>
       <persistence xmlns="http://xmlns.jcp.org/xml/ns/persistence" version="2.2">
           <persistence-unit name="jpa_setting_study">
               <!-- Gradle로 빌드 시에는 사용할 Entity를 등록해 줘야함 -->
               <class>com.study.jpa_setting_study.admin.member.domain.Member</class>
               <class>com.study.jpa_setting_study.admin.manager.domain.Manager</class>
               <class>com.study.jpa_setting_study.admin.manager.domain.Team</class>

               <!-- DB연결 속성값들 -->
               <properties>
                   <!-- 함수 속성 -->
                   <property name="javax.persistence.jdbc.driver" value="org.h2.Driver"/>
                   <property name="javax.persistence.jdbc.user" value="amway"/>
                   <property name="javax.persistence.jdbc.password" value="1234"/>
                   <property name="javax.persistence.jdbc.url" value="jdbc:h2:tcp://localhost/~/test"/>
                   <property name="hibernate.dialect" value="org.hibernate.dialect.H2Dialect"/>

                   <!-- 옵션 -->
                   <property name="hibernate.show_sql" value="true"/>
                   <property name="hibernate.format_sql" value="true"/>
                   <property name="hibernate.use_sql_comments" value="true"/>
                   <property name="hibernate.id.new_generator_mappings" value="true"/>
                   <property name="hibernate.hbm2ddl.auto" value="none"/>
               </properties>
           </persistence-unit>
       </persistence>
     ```
<br/>

5. Column 데이터 타입으로 @Enumerated를 활용해 Enum을 쓸 때는 EnumType.ORDINAL을 쓰게 되면 각 enum들을 0, 1, 2 ... 순서대로 숫자로 DB에 저장하게 됨
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
<br/>

6. DB랑 매핑할 필요는 없는데 해당 Domain에서 관리해야되는 변수가 존재할 때는 @Transient
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
<br/>
    
7. 관계 매핑은 Lazy로 설정!
   * 관계 매핑 시 fetchType은 EAGER, LAZY가 존재
      * EAGER : 데이터 조회 시 반드시 연관된 테이블을 Join하여 데이터 조회
      * LAZY : 연관된 테이블의 데이터를 조회할 때만 Join하여 데이터 조회(평시에는 조회 X)
   * 때문에 LAZY로 설정하는 것이 보편적인 상황에서는 무조건 성능상 유리!
   * 예시
      ```
        @Entity
        @Getter
        @Setter
        public class Player {
            @Id @GeneratedValue(strategy = GenerationType.AUTO)
            @Column(name = "PLAYER_ID")
            private Long playerId;
            @Column(name = "PLAYER_NAME")
            private String playerName;
            
            @ManyToOne(fetch = FetchType.LAZY)
            @JoinColumn(name = "TEAM_ID")
            private Team team;
        }
      ```
<br/><br/><br/><br/>

### 활용한 강의들
1. JPA에 대한 초기 개념 학습 : https://www.youtube.com/watch?v=egVZusxSeKw&list=PL9mhQYIlKEhfpMVndI23RwWTL9-VL-B7U&index=2
