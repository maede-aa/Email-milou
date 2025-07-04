package aut.milou.Repository;

import aut.milou.model.Email;
import aut.milou.util.HibernateUtil;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class EmailRepository {

    public void save(Email email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();session.persist(email);tx.commit();
        }
    }

    public Optional<Email> findByCode(String code) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.createQuery("FROM Email WHERE code = :code", Email.class).setParameter("code", code).uniqueResult());
        }
    }

    public List<Email> findInbox(String recipientEmail) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT r.email FROM Recipient r WHERE r.recipientEmail = :recipientEmail ORDER BY r.email.date DESC", Email.class).setParameter("recipientEmail", recipientEmail).list();
        }
    }

    public List<Email> findUnread(String recipientEmail) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT r.email FROM Recipient r WHERE r.recipientEmail = :recipientEmail AND r.email.isRead = false ORDER BY r.email.date DESC", Email.class).setParameter("recipientEmail", recipientEmail).list();
        }
    }

    public List<Email> findSent(String senderEmail) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Email e WHERE e.sender.email = :senderEmail ORDER BY e.date DESC", Email.class).setParameter("senderEmail", senderEmail).list();
        }
    }

    public void update(Email email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();session.merge(email);tx.commit();
        }
    }
}
