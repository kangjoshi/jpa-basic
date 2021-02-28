package com.example.jpabasic.jpajpql.main;

import com.example.jpabasic.jpajpql.domain.Member;
import com.example.jpabasic.jpajpql.domain.MemberDto;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class JpqlMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpql");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {

            for (int i=0; i<100; i++) {
                Member member = new Member();
                member.setUsername("member" + i);
                member.setAge(i);
                em.persist(member);
            }


            em.flush();
            em.clear();

            List<Member> results = em.createQuery("select m from Member m order by m.age desc", Member.class)
                    .setFirstResult(0)
                    .setMaxResults(5)
                    .getResultList();

            System.out.println("====================");
            System.out.println("====================");
            System.out.println("====================");
            System.out.println("results size : " + results.size());
            for (Member member1 : results) {
                System.out.println(member1);
            }
            System.out.println("====================");
            System.out.println("====================");
            System.out.println("====================");

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
            emf.close();
        }
    }


}
