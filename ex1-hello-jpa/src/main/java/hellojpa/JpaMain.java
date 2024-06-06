package hellojpa;

import jakarta.persistence.*;

public class JpaMain {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            Member member1 = new Member();
            member1.setUsername("A");

            Member member2 = new Member();
            member2.setUsername("B");

            Member member3 = new Member();
            member3.setUsername("C");




            System.out.println("=====================");

            em.persist(member1); //1, 51
            em.persist(member2); //MEM
            em.persist(member3); //MEM

            System.out.println("member = " + member1.getId());
            System.out.println("member = " + member2.getId());
            System.out.println("member = " + member3.getId());
            System.out.println("=====================");

            // 커밋 시점에 DB 쿼리가 날라간다
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
