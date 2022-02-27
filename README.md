# JPA_Setting_Study

#### JPA 소개
* Java Persistence API의 약자로 객체 관계 매핑을 위한 POJO persistence Model을 제공하는 API
  * 관계형 데이터베이스와 매핑이 가능하며 매핑 정보를 기반으로 객체를 활용해 데이터 조회, 생성, 수정, 삭제 등의 작업이 가능
  * 이를 통해 더 이상 개발자들이 SQL문을 작성하는데 불필요한 시간을 줄여주고 핵심 로직을 개발하는데 시간을 사용할 수 있게 함으로써 개발 효율성 향상
  * DB 마이그레이션 시에도 원하는 관계형 데이터베이스 정보만 속성 값에서 변경해주면 JPA가 알아서 해당 DB에 맞게 매핑하기 때문에 마이그레이션 시간도 단축 가능
* 기존에는 EJB 전문가 그룹에 의해 개발되었지만 추후 웹 애플리케이션 및 애플리케이션 클라이언트가 직접하숑할 수 있게 Java EE, Java SE 등에서도 사용 가능해진 상태
<br/><br/>

#### 활용한 강의들
1. JPA에 대한 초기 개념 학습 : https://www.youtube.com/watch?v=egVZusxSeKw&list=PL9mhQYIlKEhfpMVndI23RwWTL9-VL-B7U&index=2
