package com.example.jpabasic.jpateam.main;

import com.example.jpabasic.jpateam.domain.Member;
import com.example.jpabasic.jpateam.domain.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class TeamMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("team");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {

            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("member1");
            member.setTeam(team);

            Member member2 = new Member();
            member.setUsername("member2");
            member2.setTeam(team);
            em.persist(member);
            em.persist(member2);

            System.out.println("============================");
            System.out.println("============================");
            System.out.println("============================");
            System.out.println(team.getMembers().size());

            for (Member m : team.getMembers()) {
                System.out.println(m);
            }

            System.out.println("============================");
            System.out.println("============================");
            System.out.println("============================");


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
