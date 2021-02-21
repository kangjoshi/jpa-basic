package com.example.jpabasic.example;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();

        tx.begin();

        Member member = new Member();
        //member.setId(1l);
        member.setUsername("HelloA");

        System.out.println(member.getId());

        em.persist(member);

        tx.commit();

        em.close();
        emf.close();
    }

}
