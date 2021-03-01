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
        1. 다대일
            - 연관 관계가 N:1인 관계 (다수의 선수들은 하나의 팀에만 속해 있다.)
            - 연관 관계의 주인이 N인 객체가 되는 관계
        2. 일대다
            - 연관 관계가 1:N인 관계 (하나의 팀에 다수의 선수들이 속해 있다.)
            - 연관 관계의 주인이 1인 객체가 되는 관계
            - 권장 하지는 않는 모델 : 결국 외래키는 N인 테이블에 존재하므로 연관 관계의 주인인 객체와 외래키가 있는 테이블의 불일치로 객체와 관련 없는 테이블의 키를 넣는 추가 쿼리 발생
        3. 일대일
            - 연관 관계가 1:1인 관계 (한명의 회원은 하나의 락커를 가질수 있다.)
            - 연관 관계의 주인은 회원도 가능, 락커도 가능
            - 매핑 방법은 다대일 연관 관계와 비슷
        4. 다대다
            - 실무에서 권장하지 않는 모델 : 단순하게 연결 정보만 가지고 있는 테이블은 거의 없다. (등록시간, 등록ID 같은 정보들이 대부분 들어감.)
            - 매핑 테이블(엔티티)를 중간에 추가해서 일대다 - 다대일 관계를 가지는 것을 권장

1. 상속 관계 매핑
    - 객체는 상속 관계가 있지만 데이터베이스는 상속 관계가 없다. 그러므로 객체의 상속 구조와 DB의 슈퍼타입 서브타입 관계를 매핑한다.
    ```java
    @Entity
    @Inheritance
    public class Item {
        @Id @GeneratedValue
        @Column(name = "ITEM_ID")
        private Long id;
        private String name;
        private int price;
    }
    
    @Entity
    @DiscriminatorValue("a")
    public class Album extends Item {
        private String artist;
    }
   
    @Entity
    @DiscriminatorValue("b")
    public class Book extends Item {
        private String author;
        private String isbn;
    }
    ```
    - @Inheritance
        - InheritanceType.SINGLE_TABLE (기본 전략) : 하나의 테이블(Item)에 모든 값(Item + Album + Book)이 들어감
            - 조인 쿼리가 없으므로 쿼리가 단순해진다.
            - 자손테이블의 정보를 모두 가지고 있으므로 테이블이 커질수 있고 nullable이 많아진다.
        - InheritanceType.JOINED : 슈퍼타입 서브타입 관계의 테이블로 생성된다.
            - 기본적으로 많이 사용하는 전략
            - 테이블에 데이터가 정규화되어 있기 때문에 효율적
            - 조인을 많이 사용하므로 쿼리가 복잡해진다.
            - Insert가 두번 (부모테이블, 자손테이블) 된다.
        - InheritanceType.TABLE_PER_CLASS :  슈퍼타입 서브타입 관계없이 (즉 Item은 만들지 않고) 각각의 테이블이 부모테이블의 컬럼까지 가진채로 생성된다.
            - 추천하지 않는 전략, 객체 지향적이지 않고 테이블도 관계가 없으므로 애매 함.
            - 다형성을 이용해서 부모 객체로 조회시 모든 자손테이블을 UNION해서 가져오는 단점이 있다. (DType을 가지고 있지 않으므로 어느 테이블이 이 값인지 알 수 없으므로)
    - @DiscriminatorColumn
        - 단일테이블 전략은 생략해도 추가된다.
        - 부모테이블에 자손테이블의 이름을 남길수 있도록 컬럼을 생성한다. (해당 row가 어느 자손테이블의 데이터인지)
        - @DiscriminatorValue("name") 자손 객체에 추가하면 자손테이블의 이름을 name으로 저장한다.

1. 공통 정보 매핑
    - @MappedSuperclass
        - 부모 클래스에 추가하면 자식 클래스에 매핑 정보만 제공한다. 엔티티가 아니므로 테이블과 매핑하지 않는다.
        - 직접 생성하서 사용할 일이 없으므로(그리고 사용 해서는 안되므로) 추상 클래스로 만드는것을 권장
    ```java
    @MappedSuperclass
    public class BaseEntity {
        private String createdBy;
        private LocalDateTime createdDate;
        private String lastModifiedBy;
        private LocalDateTime lastModifiedDate;
    }
   
    @Entity
    public class Member extends BaseEntity {
        @Id @GeneratedValue
        @Column(name = "MEMBER_ID")
        private Long id;
   
        @Column(name = "USER_NAME")
        private String username;   
    }
    ```

##### 프록시
- `em.getReference()` : 데이터베이스 조회를 미루는 가짜 객체(프록시) 조회
 
####### 프록시 특징
- 프록시 객체는 실제 객체의 참조를 보관, 프록시 객체를 호출하면 값이 없다면 영속성 컨택스트에 초기화를 요청 후 실제 객체의 메서드 호출
- 프록시 객체는 한 번만 초기화 (초기화된 실제 객체는 멤버 변수로 접근, 즉 프록시가 초기화 된다고 실제 객체가 되는것은 아님.)
- 실제 클래스를 상속 받아 만들어 짐. 따라서 타입 체크시 ==는 실패, 대신 instance of 사용
- 찾는 엔티티가 이미 영속 상태이면 `em.getReference()`를 호출해도 실제 객체가 반환된다.
- 준영속 상태에서 프록시를 초기화하면 `org.hibernate.LazyInitializationException` 발생 (영속성 컨택스트에게 초기화를 요청 해야하는데 준 영속 상태이므로 초기화 할 수 없다.)

####### 프록시 확인
- `PersistenceUnitUtil.isLoaded(Object entity)` : 프록시 인스턴스의 초기화 여부 확인
- `Hibernate.initialize(entity)` : 프록시 강제 초기화 (JPA 표준은 강제 초기화 없으므로 실제 객체 강제 호출)

##### 즉시 로딩과 지연 로딩
###### 즉시로딩
- `@ManyToOne(fetch = FetchType.EAGER)` : 즉시 로딩을 사용하여 해당 필드를 실제 객체로 조회
- JPA 구현체는 가능하면 조인을 사용하여 SQL 한번으로 함께 조회
- 즉시 로딩을 적용하면 예상하지 못한 SQL이 발생할 수 있다.
- 즉시 로딩은 JPQL에서 N+1 문제를 야기한다. (select만 해서 조회시)

###### 지연로딩
- `@ManyToOne(fetch = FetchType.LAZY)` : 지연 로딩을 사용하여 프록시로 해당 필드를 프록시로 조회
- 가급적 지연 로딩만 사용하는 전략을 가져가는게 좋다. 그리고 개발이 어느정도 된 후 필요한 부분만 최적화 

##### 영속성 전이 : CASCADE
- 특정 엔티티를 영속 상태로 만들 때 연관된 엔티티도 함께 영속 상태로 만들고 싶을 때 (부모 엔티티를 저장할 때 자식 엔티티도 같이 저장)
- 영속성 전이는 연관관계 매핑과 아무 관련이 없음. 영속할 때 연관된 객체도 같이하는 편리 기능
- ALL 또는 PERSIST(등록만 사용하고 싶을때) 주로 사용
- 단일 엔티티에만 완전 종속적일때(게시글과 댓글)만 사용
- CascadeType.ALL + orphanRemoval = true을 활성화하면 부모 엔티티를 통해서 자식의 생명주기를 관리할 수 있다. (등록, 삭제)
```java
@Entity
public class Parent {
    @Id @GeneratedValue
    private Long id;
    private String name;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)  // cascade 설정
    private List<Child> children = new ArrayList<>();

    public void addChild(Child child) {
        children.add(child);
        child.setParent(this);
    }
}

@Entity
public class Child {
    @Id @GeneratedValue
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Parent parent;

    public void setParent(Parent parent) {
        this.parent = parent;
    }
}
```

###### 고아 객체
- 부모 엔티티와 연관관계가 끊어진 자식 엔티티를 자동으로 삭제
- 단일 엔티티에만 완전 종속적일때(게시글과 댓글)만 사용
- 부모가 제거되면 고아 객체들도 같이 제거된다. (CASCADE.REMOVE와 같이 동작)
```java

@Entity
public class Parent {
    @Id @GeneratedValue
    private Long id;

    private String name;

    @OneToMany(mappedBy = "parent", orphanRemoval = true)   // 고아객체 자동 제거
    private List<Child> children = new ArrayList<>();

    public void addChild(Child child) {
        children.add(child);
        child.setParent(this);
    }
}
```

#### 값 타입
- 엔티티 타입 : 식별자가 있다. 생명 주기가 관리된다. 공유할 수 있다.
- 값 타입 : 식별자가 없다. 생명 주기를 엔티티에 의존한다. 공유할 수 있지만 하지 않는것이 안전하다.(불변 객체로 만들자.)

##### 기본 값 타입
- 자바 기본 타입 (primitive type), Wrapper 클래스, String
- 생명주기를 엔티티에 의존
- 기본 값 타입은 공유되지 않는다. (기본 타입은 항상 **값**을 복사함. Wrapper 클래스나 String 같은 특수한 클래스는 공유 가능한 객체지만 불변 클래스이다.)

##### 임베디드 타입
- 엔티티가 아닌 그냥 값 타입, 주로 기본 값 타입을 모아서 만드는 복합 값으 형태이다
- 재사용이 가능하고, Address.isSeoul()와 같이 해당 값 타입에서만 사용하는 메서드를 정의하는등 객체지향적으로 이점을 가질수 있다.
- 객체와 테이블을 아주 세밀하게 매핑하는 것이 가능하다 
- 하나의 엔티티에서 같은 임베디트 타입을 사용하려면 `@AttributeOverrides`,`@AttributeOverride` 사용하여 컬럼명 재 정의 가능
- 임베디드 타입은 주소에 의한 참조가 되므로 여러 객체에서 공유하면 사이드 이펙트가 발생할 수 있다. 그러므로 불변 객체로 설계하여 생성 시점 이후에 절대 값을 변경할 수 없도록 만든다. (Setter를 제공하지 않는다.)
```java
@Embeddable // 임베디드 타입이라고 정의
public class Address {
    private String city;
    private String street;
    private String zipcode;
    
    public boolean isSeoul() {
        return "SEOUL".equals(city);
    }
}

@Entity
public class Member extends BaseEntity {
    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;
    private String name;
    @Embedded   // 임베디드 타입 사용
    private Address address;
}
```

##### 컬렉션 값 타입
- 값 타입(엔티티 타입이 아님)을 컬렉션에 담아서 사용하는 타입
- 데이터베이스는 컬렉션을 같은 테이블에 저장할 수 없으므로 컬렉션을 저장하기 위한 별도의 테이블이 필요하므로 테이블이 만들어진다
- 조회시 지연 로딩 전략이 기본적으로 사용된다
- 영속성 전이 + 고아 객체 제거 기능을 필수로 가진다. (부모 엔티티에 의해 생명주기가 관리된다.)
- 값 타입은 엔티티 타입과 다르게 식별자 개념이 없으므로 변경 추적이 어렵다. 그러므로 값 타입 컬렉션에 변경 사항이 발생하면 주인 엔티티와 연관된 모든 데이터를 삭제하고, 값 타입 컬렉션에 있는 현재 값을 모두 다시 저장한다
```java
@Entity
public class Member extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    private String name;
    
    @ElementCollection  // 컬렉션 타입 사용
    @CollectionTable(name = "FAVORITE_FOODS", joinColumns = @JoinColumn(name = "MEMBER_ID")) // 컬렉션 타입 테이블 정보 지정
    private Set<String> favoriteFoods = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "ADDRESS", joinColumns = @JoinColumn(name = "MEMBER_ID"))   
    private List<Address> addressHistory = new ArrayList<>();
}
```
- 실무에서는 정말 아주 단순한 경우만 사용하고 대부분의 상황에서는 컬렉션 값 타입 대신 일대다 관계를 사용 하는것을 권장 (컬렉션 값 타입에서 발생하는 모호함을 해소하고 영속성 전이 + 고아 객체 제거를 사용하면 값 타입 컬렉션 처럼 사용 가능)
- 식별자가 필요하고 주기적으로 관리가 필요하다면 값 타입이 아닌 엔티티로 만들어야 한다.

#### 객체지향쿼리 (JPQL)
- SQL을 추상화한 객체지향쿼리
- JPA를 사용하면 엔티티 객체를 중심으로 개발한다
- 검색 쿼리도 테이블이 아닌 엔티티 객체를 대상으로 검색해야 하지만 모든 DB 데이터를 객체로 변환해서 검색하는 것은 불가능하므로 결국 검색 조건이 포함된 SQL이 필요
```java
List<Member> members = 
            em.createQuery(
                    "select m from Member m where m.name like '%kim%'", // JPQL
                    Member.class
            ).getResultList();
            
```

####### JPQL 문법
```text
select_문 :: =
    select
    from
    [where]
    [groupby]
    [having]
    [orderby]

update_문 :: = 
    update
    [where]

delete_문 :: =
    delete
    [where]
```
- 엔티티와 속성은 대소문자 구분한다. (Member, age)
- JPQL 키워드는 대소문자 구분하지 않는다. (select, from, where)
- 별칭을 꼭 넣어야 한다. `Member [as] m`

######## 기본 문법
1. TypeQuery
- 반환 타입이 명확할 때 사용
```java
TypedQuery<Member> query = em.createQuery("select m from Member m", Member.class);
```
2. Query
- 반환 타입이 명확하지 않을 때 사용
```java
Query query2 = em.createQuery("select m.username, m.age from Member m");
```

3. `getSingleResult()`, `getResultList()`
- 결과 반환 (단건, 복수건)

4. 파라미터 바인딩 - 이름 기준, 위치 기준
- 이름 기준
```java
List<Member> results = em.createQuery("select m from Member m where m.username=:username", Member.class)
                    .setParameter("username", "kang")
                    .getResultList();
```
- 위치 기준 (위치는 언제든 바뀔수 있으므로 사용하지 않는걸 권장)
```java
TypedQuery<Member> query = em.createQuery("select m from Member m where m.username=?1", Member.class);
query.setParameter(1, "kang");
```

######## 프로젝션
- SELECT 절에 조회할 대상을 지정
- 프로젝션 대상 : 엔티티 타입, 임베디드 타입, 스칼라 타입
1. 엔티티 타입 프로젝션
```text
SELECT m FROM Member m
SELECT m.team FROM Member m
```

2. 임베디드 타입 프로젝션
```text
SELECT o.address FROM Order o
```

3. 스칼라 타입 프로젝션
```text
SELECT m.username, m.age FROM Member m
```
- new 명령어를 이용하여 객체로 프로젝션 (생성자를 이용하여 객체가 생성된다.)
```java
em.createQuery("select new com.example.jpabasic.jpajpql.domain.MemberDto(m.username, m.age) from Member m", MemberDto.class)
```

######## 페이징 API
- JPA는 페이징을 아래 두 API로 추상화 되어 있다
- `setFirstResult(int startPosition)` : 조회 시작 위치
- `setMaxResults(int maxResult)` : 조회할 데이터 수
```java
List<Member> results = em.createQuery("select m from Member m order by m.age desc", Member.class)
                    .setFirstResult(0)
                    .setMaxResults(5)
                    .getResultList();
```

######## 조인
1. 내부조인
```text
SELECT m FROM Member m [INNER] JOIN m.team t
```

2. 외부조인
```text
SELECT m FROM Member m LEFT [OUTER] JOIN m.team t
```

3. 세타조인
```text
SELECT m FROM Member m, Team t WHERE m.username = t.name
```

4. ON절 
- 조인 대상 필터링
```text
팀 이름이 A인 팀만 회원과 조인
SELECT m FROM Member m LEFT [OUTER] JOIN m.team t ON t.name = 'A'
```
- 연관 관계 없는 엔티티 조인
```text
회원의 이름과 팀의 이름이 같은 대상 외부 조인
SELECT m FROM Member m LEFT [OUTER] JOIN m.team t ON m.username = t.name
```

######## 서브쿼리
- JPA는 WHERE, HAVING절에서만 서브쿼리 사용 가능, 하이버네이트에서는 SELECT도 가능, FROM에서는 사용 불가 (조인으로 해결해야 함)
```text
SELECT m FROM Member m WHERE exists (SELECT t FROM m.team t WHERE t.name = '탐A')
SELECT m FROM Member m WHERE m.team = ANY(SELECT t FROM Team t)
```

######## 경로 표현식
- .으로 객체 그래프를 탐색하는 것
- 조인 없이 연관 필드 탐색을하면 묵시적 조인 발생한다. (튜닝, 에러 핸들링의 어려움이 있으므로 모든 조인은 명시적으로 사용해야 한다)
```text
SELECT 
    m.username          // 상태 필드 (단순히 값을 저장하기 위한 필드, 경로 탐색의 끝이므로 더이상 탐색할 곳이 존재하지 않음)
FROM Member m   
    JOIN m.team t       // 단일 값 연관필드 (단일 엔티티, 경로 탐색을 더 할 수 있음)
    JOIN m.orders o     // 컬렉션 값 연관필드 (컬렉션 엔티티, 경로 탐색을 더 할 수 없음 (자바 컬렉션과 동일 탐색하려면 코드에서 하나씩 꺼내거나, 명시적으로 JOIN을 한다면 별칭을 통해 탐색 가능))
WHERE t.name
```

######## fetch 조인
- 연관된 엔티티나 컬렉션을 SQL 한 번에 함게 조회하는 기능
- JPQL에서 성능 최적화를 위해 제공
```text
fetch join ::= [LEFT [OUTER] | INNER] JOIN FETCH 조인경로

SELECT m FROM Member m JOIN FETCH m.team
    -> SELECT M.*, T.* FROM MEMBER M INNER JOIN TEAM T ON M.TEAM_ID = T.ID
```
- 페치 조인과 DISTINCT
    - 1:N 관계에서 조인시에는 DB에서 rows는 여러건이 조회되어 나오므로(하나의 팀에서는 여러 선수가 소속 되어 있다면 조회시 팀은 같지만 선수 데이터는 다르므로 여러 rows가 반환 됨)
    1. JPQL의 distinct를 이용하여 중복 제거
        ```text
        // 같은 식별자를 가진 Team 엔티티 중복 제거
        SELECT distinct t FROM Team t JOIN FETCH t.members
        ```
- 일반 조인 실행시 SELECT절에 지정하지 않는다면 연관된 엔티티를 함께 조회하지 않지만 fetch 조인은 지정된 연관 관계로 함께 조회(즉시로딩)한다

######### fetch 조인의 특징과 한계
- fetch 조인 대상에게는 별칭을 줄 수 없다 (fetch 조인은 연관 관계인 모든 엔티티를 가져오는것으로 설계 되었는데 별칭을 주게되면 where등에서 사용이 가능하게되어 데이터 정합이 깨질수 있다.)
- 둘 이상 컬렉션은 fetch 조인 할 수 없다 (데이터가 예상하지 못하게 늘어 날수 있으므로 사용 => 1\*N\*N)
- 페이징 API를 사용할 수 없다 (별칭을 줄수 없는 이유와 비슷, 페이징이란 조회 갯수를 조정 하는것이므로)(하이버네이트는 가능하지만 모든 연관관계를 가져와서 메모리에서 페이징하므로 운영 서버에서 사용시 매우 위험)
- fetch 조인은 객체 그래프를 유지할 때 사용하면 효과적

######## 다형성 쿼리
- `type()` 조회 대상을 특정 자식으로 한정
```text
SELECT i FROM Item i WHERE type(i) IN (Book, Movie)
```
- `treat()`
```text
SELECT i FROM Item i WHERE treat(i as Book).auther = 'kang'
```

######## Named 쿼리
- 쿼리 재활용을 위해 미리 정의하여 이름을 부여하는 쿼리
- 정적 쿼리만 가능
- 어노테이션, XML에 정의
- 애플리케이션 로딩 시점에 JPA가 쿼리 파싱을 시도하므로 컴파일 에러 발생한다
```java
// 정의
@NamedQuery(
        name = "Member.findByUsername",
        query = "SELECT m FROM Member m WHERE m.username = :username"
)
public class Member {
}
```
```java
// 사용
List<Member> results = em.createNamedQuery("Member.findByUsername", Member.class)
                    .setParameter("username", memberA.getUsername())
                    .getResultList();
```

######## 벌크연산
- 쿼리 한번으로 여러 테이블의 row 변경
- UPDATE, DELETE 지원
- 벌크 연산은 영속성 컨텍스트를 무시하고 데이터베이스에 직접 질의한다. 그러므로 벌크 연산 후 영속성 컨텍스트 초기화하고 엔티티를 다시 조회 해야한다.






###### Criteria
- JPQL 빌더 역할 : 자바 코드로 JPQL을 작성할 수 있음
- 공식 스펙이간 하지만.. 너무 복잡하고 코드가 장황해지는 단점때문에 실무에서는 사용하지 않는 추세
```java
CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
CriteriaQuery<Member> query = criteriaBuilder.createQuery(Member.class);
Root<Member> m = query.from(Member.class);
CriteriaQuery criteriaQuery = query.select(m).where(criteriaBuilder.equal(m.get("name"), "kim"))
List<Member> members = em.createQuery(criteriaQuery).getResultList();
```

###### QueryDSL
- JPQL 빌더 역할 : 자바 코드로 JPQL을 작성할 수 있음
- Criteria에 비해 단순하고 쉬워서 실무에서 사용 권장

###### 네이티브 SQL
- `em.createNativeQuery("SELECT * FROM MEMBER")`로 SQL을 직접 사용
- JPQL로 해결할 수 없는 특정 데이터베이스에 의존적인 기능을 사용할 때 선택(CONNECT BY, 힌트 등)

###### JDBC API 직접 사용, SpringJdbcTemplete 사용
- 사용 가능하지만, `em.flush()`을 이용하여 영속성 컨택스트를 동기화 후 쿼리가 실행 되어야함


#### Reference
자바 ORM 표준 JPA 프로그래밍 - 기본.김영한.인프런강의