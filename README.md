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

##### 영속성 컨택스트
- 엔티티를 영구 저장하는 환경
- 엔티티 매니저를 통해서 영속성 컨텍스트에 접근할 수 있다. (엔티티 매니저와 영속성 컨택스트는 기본적으로 1:1 관계를 가진다.)

###### 영속성 컨택스트의 이점
1. 1차 캐시
- 영속성 컨택스는 key(pk) & value(객체)의 형태로 영속 상태의 엔티티를 관리한다.
- 조회시 영속성 컨택스트(1차 캐시)에서 먼저 조회하고 있다면 즉시 반환하고 없다면 DB에서 조회 후 영속성 컨택스트에 저장한다.
- 단, 영속성 컨택스트는 트랜잭션 시작시 생성되었다가 종료시 삭제되므로 1차 캐시라 한다.

1. 동일성 보장
- 영속된 엔티티는 동일성을 보장 해주므로 == 비교시 true를 반환한다.

1. 트랜잭션을 지원하는 쓰기 지연
- `em.persist(member)`는 바로 insert를 DB에 보내지 않고 쓰기 지연 저장소에 저장했다가 트랜잭션 커밋하는 시점에 보내진다(flush).
- 한번에 모아서 보낼수 있는 옵션이 있어서 batch insert와 같은 성능상 이점이 있도록 코딩 가능

1. 변경 감지 (Dirty Check)
- 영속 상태의 엔티티를 변경시 영속성 컨택스트는 쓰기 지연 SQL 저장소에 update 쿼리를 생성한다.

1. 지연 로딩


###### 플러시
영속성 컨택스트의 변경 내용을 데이터베이스에 전송 (플러시 해도 영속 상태의 객체들은 유지된다.)
플러시 방법은 아래와 같다.
1. `em.flush()` 직접 호출
1. 트랜잭션 커밋시 자동 호출
1. JPQL 쿼리 실행시 자동 호출
    - JPQL은 SQL로 변경되어 DB에 전송되므로 오동작을 방지하기 위해서 기본으로 플러시 된후 변경된 SQL이 실행된다.
    
####### 플러시 발생시 영속성 컨택스트의 동작
1. 변경 감지
1. 쓰기 지연 SQL 저장소의 쿼리를 데이터베이스에 전송

####### 플러시 모드 옵션
`em.setFlushMode(FlushModeType.COMMIT)`
1. `FlushModeType.AUTO` : 커밋이나 쿼리를 실행할 때 플러시 (기본값)
1. `FlushModeType.COMMIT` : 커밋할 때만 플러시


##### 엔티티의 생명주기
1. 비영속
    - 영속성 컨택스트와 전혀 관계가 없는 상태
    ```java
    // 객체 생성
    Member member = new Member();
    member.setName("member"); 
    // 객체만 생성된 상태이므로 영속성 컨택스트와는 전혀 관계가 없다.
    ```
1. 영속
    - 영속성 컨택스트에 관리되는 상태
    ```java
    // 객체 생성
    Member member = new Member();
    member.setName("member"); 
       
    EntityManager em = emf.createEntityManager();
    // 객체를 저장하므로 영속 상태로 변경된다.
    em.persist(member);
    ```
1. 준영속
    - 영속성 컨택스트에 저장 되었다가 분리된 상태 (영속 -> 준영속)
    ```java
    // 엔티티를 영속성 컨택스트에서 분리하여 준영속 상태로 변경한다.
    em.detach(member);
    ```
    - 영속성 컨택스트가 제공하는 이점을 사용할 수 없다.
    - 준영속 상태로 만드는 방법
        1. `em.detach(entity)` : 특정 엔티티만 준영속 상태로 전환
        1. `em.clear()` : 영속성 컨택스트를 완전히 초기화
        1. `em.close()` : 영속성 컨택스트를 종료
1. 삭제
    - 삭제된 상태
    ```java
    // 엔티티를 삭제한다.
    em.detach(member);
    ```

##### 엔티티 매핑
1. 객체와 테이블 매핑
    - `@Entity` 클래스에 붙인다. `@Entity`가 붙은 클래스는 JPA가 관리, 엔티티라고 부른다.
    ```java
    /*
        1. 기본생성자 필수 (public 또는 protected)
        2. final 클래스, enum, interface, inner 클래스에는 사용할 수 없음.
        3. final 필드 사용할 수 없음.  
    */
    @Entity
    @Table(name="MBR") // 매핑할 테이블 이름 지정
    public class Member {
       public Member() {
   
       }      
    }
    ```
1. 필드와 컬럼 매핑
   ```java
   @Id // PK 지정
   private Long id;

   @Column(name = "name") // 필드에 속성 지정
   private String username;

   private Integer age;

   @Enumerated(EnumType.STRING) // Enum 타입
   private RoleType roleType;

   @Temporal(TemporalType.TIMESTAMP) // 날짜의 타입 지정 (Date, Time, Timestamp)
   private Date createdDate;
    
   private LocalDateTime lastModifiedDate;

   @Lob // lob 타입 지정 (필드 타입이 문자 타입이면 CLOB, 아니면 BLOB로 매핑)
   private String description;
   
   @Transient // 해당 필드를 매핑하지 않음
   private int temp;
    ```
   - `@Column`의 속성
       - name : 필드와 매핑할 컬럼명 (기본 값 : 객체의 필드명)
       - insertable : 등록 가능 여부 (기본 값 : true)
       - updatable : 변경 가능 여부, 등록은 하는데 절대로 변경이 되서는 안되면 false로 지정 (기본 값 : true)
       - nullable : null 값의 허용 여부 (기본 값 : true)
       - unique : 유니크 제약 조건을 부여 - 하지만 제약명이 임의로 생성되어 @Table에서 제약을 거는걸 선호
       - columnDefinition : 컬럼 정보를 부여 (문구가 그대로 DB로 보내져서 특정 DB에 종속적인 제약 조건을 걸 수 있다.)
       - length : String 타입에서만 사용가능한 길이 제한
       - precision : BigDecimal 타입에서만 사용할 수 있는 소수점 자릿수 제한
   - `@Enumerated` 사용시 주의 사항
       - value : 기본값이 EnumType.ORDINAL(enum의 순서)다. 순서는 변경이 될 수 있으므로 EnumType.STRING을 사용하는 것이 바람직
   - `@Temporal` 사용시 주의 사항
       - 자바8 LocalDate, LocalDateTime가 나오면서 `@Temporal`은 생략 가능

1. 키본 키 매핑
    - `@Id` : 직접 지정
    - `@GeneratedValue` : 자동 지정
        - IDENTITY : 데이터베이스에 위임, IDENTITY 전략에서는 쓰기 지연을 제공하지 않는다. 이유는 DB에 Insert 해봐야 값을 알 수 있으므로
        - SEQUENCE : 시퀀스 오브젝트 사용, @SequenceGenerator 사용
        ```java
        @SequenceGenerator(name = "member_seq_generator", sequenceName = "member_seq")
        public class Member { 
          
          @Id
          @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_seq_generator")
          private Long id;
        
        }
        ```
            - allocationSize = 50 (기본 값) : 시퀀스 값을 50씩 증가시킨다. (성능 최적화에 사용) 미리 생성해두고(미리 확보해두고) 메모리에서 꺼내서 사용한다.
      
        - TABLE : 키 생성용 테이블 사용, @TableGenerator 사용
        - AUTO : 방언에 따라 위 3가지 타입중 하나가 자동 선택된다. (기본 값)

1. 연관관계 매핑
    - 객체와 테이블을 매핑
    - 방향
        1. 단방향 연관관계
            ```java
            @Entity
            public class Member {
               
                @Id @GeneratedValue
                @Column(name = "MEMBER_ID")
                private Long id;
               
                @Column(name = "USER_NAME")
                private String username;
               
                @ManyToOne                      // member와 team의 관계는 1:N이므로 @ManyToOne으로 매핑
                @JoinColumn(name = "TEAM_ID")   // 외래키 설정
                private Team team;  
            }
            
            @Entity
            public class Team {
           
                @Id @GeneratedValue
                private Long id;
           
                private String name;
            }
            ```
        1. 양방향 연관관계
            - 단방향 관계에서는 Member는 Team을 알 수 있지만 Team은 소속된 Members를 알 수 없으므로 양방향 관계를 맺어 Team도 members를 알 수 있도록 한다.
            ```java
                @Entity
                public class Member {
                          
                    @Id @GeneratedValue
                    @Column(name = "MEMBER_ID")
                    private Long id;
                          
                    @Column(name = "USER_NAME")
                    private String username;
                          
                    @ManyToOne                      // member와 team의 관계는 1:N이므로 @ManyToOne로 매핑
                    @JoinColumn(name = "TEAM_ID")   // 외래키 설정
                    private Team team;  
                }
                       
                @Entity
                public class Team {
                      
                    @Id @GeneratedValue
                    private Long id;
                      
                    private String name;
           
                    @OneToMany(mappedBy = "team")  // team과 member의 관계는 N:1이므로 @OneToMany로 매핑
                                                   // mappedBy 반대편(Member)에서 객체가 매핑되어 있는 필드명
                    private List<Member> members;   
                } 
            ```
            - 연관관계 주인과 mappedBy
                - 객체는 단방향 + 단방향이 합쳐서 양방향으로 작동하고 테이블은 하나의 외래키로 양방향으로 작동(조인)이 가능하다.
                - 외래키를 양쪽에서 관리할 수 없으므로 객체의 두 관계 중 하나를 연관 관계의 주인으로 지정하고 주인만이 외래키를 관리(등록, 수정) 
                  주인이 아닌쪽은 읽기만 가능하다. (즉 `team.getMembers().add(member3)` 해도 아무 소용 없다.)
                - 주인은 mappedBy 속성을 사용하면 안된다. 주인이 아닌 객체만 mappedBy 속성으로 주인 지정
            - 단방향 매핑만으로도 이미 연관관계 매핑은 완료되었지만, 매핑 반대 방향으로 죄회 기능을 추가하기 위해서 양방향 매핑을한다. 하지만 양방향 매핑에서 발생하는 이슈가 몆몆 있으므로 단방향 매핑 위주로 하되 필요할 때 (비즈니스 코드에서 자주 반대 방향의 데이터를 사용해야 될 때) 양방향 매핑을 추가    
    - 다중성 : 다대일, 일대다, 일대일, 다대다
    
    - 연관관계의 주인 : 양방향 객체 관계에서의 관리
    





#### Reference
자바 ORM 표준 JPA 프로그래밍 - 기본.김영한.인프런강의