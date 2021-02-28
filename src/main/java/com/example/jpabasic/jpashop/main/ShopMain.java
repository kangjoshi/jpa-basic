package com.example.jpabasic.jpashop.main;

import com.example.jpabasic.jpashop.domain.Member;
import com.example.jpabasic.jpashop.domain.Order;
import com.example.jpabasic.jpashop.domain.OrderItem;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class ShopMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {
            List<Member> members =
            em.createQuery(
                    "select m from Member m where m.username like '%kim%'",
                    Member.class
            ).getResultList();


            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Member> query = criteriaBuilder.createQuery(Member.class);

            Root<Member> m = query.from(Member.class);

            CriteriaQuery criteriaQuery = query.select(m).where(criteriaBuilder.equal(m.get("name"), "kim"));

            List<Member> members2 = em.createQuery(criteriaQuery).getResultList();


            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
            emf.close();
        }
    }

}
