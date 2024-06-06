package hellojpa;

import jakarta.persistence.*;

@Entity
@SequenceGenerator(name = "MEMBER_SEQ_GENERATOR",
        sequenceName = "MEMBER_SEQ", //매핑할 데이터베이스 시퀀스 이름
        initialValue = 1, allocationSize = 50) // 시퀀스 호출 한 번에 50개씩 DB에 미리 생성해놓는다면 여러 문제를 해결할 수 있다.
public class Member {

    //기본 키 생성 전략을 IDENTITY로 할 경우에는 persist 호출 시점에 바로 INSERT가 날라간다
    //만약 커밋 시점에 쿼리문이 날라간다면 영속성 컨텍스트에서 PK값을 Id로 지정하는데 그렇게 한다면 PK가 없기 때문이다.
    //하지만 IDENTITY의 경우 JPA에서는 persist를 할 때 쿼리문을 날리기 때문에 영속성 컨텍스트에 정상적으로 PK값을 넣을 수 있다.

    //SEQUENCE 전략일 때는 나중에 커밋을 하기 전에 시퀀스에서 PK값을 미리 받아와야 하기 때문에
    //persist 호출 시점에 시퀀스에서 PK값을 가져온다
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
    generator = "MEMBER_SEQ_GENERATOR")
    private Long id;

    @Column(name = "name", nullable = false)
    private String username;

    public Member() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
