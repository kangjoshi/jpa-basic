package com.example.jpabasic.jpajpql.main;

import com.example.jpabasic.jpajpql.domain.Member;
import com.example.jpabasic.jpajpql.domain.MemberDto;
import com.example.jpabasic.jpajpql.domain.Team;

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
            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member memberA = new Member();
            memberA.setUsername("memberA");
            memberA.setAge(10);
            memberA.setTeam(team);
            em.persist(memberA);

            Member memberB = new Member();
            memberB.setUsername("member");
            memberB.setAge(10);
            memberB.setTeam(team);
            em.persist(memberB);


            em.flush();
            em.clear();

            List<Member> results = em.createNamedQuery("Member.findByUsername", Member.class)
                    .setParameter("username", memberA.getUsername())
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
            e.printStackTrace();
            tx.rollback();
        } finally {
            em.close();
            emf.close();
        }
    }


}
