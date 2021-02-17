### JPA 기초

##### 성능 최적화 기능
1. 1차 캐시와 동일성 보장
    - 같은 트랜잭션 안에서는 같은 엔티티를 반환
    ```java
       String memberId = "100";
       Member member1 = jpa.find(Member.class, memberId);  // DB 조회
       Member member2 = jpa.find(Member.class, memberId);  // DB 조회 없이 캐싱된 값이 반환
       System.out.println(member1 == member2) // true
    ```
1. 트랜잭션을 지원하는 쓰기 지연
    - 트랜잭션을 커밋할 때 INSERT SQL을 모아서 한번에 DB에 보낸다.
1. 지연로딩
    - 연관 객체는 실제 사용될 때 DB로 부터 조회된다.









#### Reference
자바 ORM 표준 JPA 프로그래밍 - 기본.김영한.인프런강의
