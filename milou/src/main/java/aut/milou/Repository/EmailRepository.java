package aut.milou.Repository;

import aut.milou.model.Email;
import aut.milou.model.Recipient;
import aut.milou.util.HibernateUtil;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class EmailRepository {

    public void save(Email email) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.save(email);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public Optional<Email> findByCode(String code) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Email> cq = cb.createQuery(Email.class);
            Root<Email> root = cq.from(Email.class);
            cq.select(root).where(cb.equal(root.get("code"), code));
            Email email = session.createQuery(cq).uniqueResult();
            return Optional.ofNullable(email);
        } finally {
            session.close();
        }
    }

    public List<Email> findInbox(String recipientEmail) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Email> cq = cb.createQuery(Email.class);
            Root<Recipient> root = cq.from(Recipient.class);
            cq.select(root.get("email")).where(cb.equal(root.get("recipientEmail"), recipientEmail));
            cq.distinct(true);
            cq.orderBy(cb.desc(root.get("email").get("date")));
            return session.createQuery(cq).getResultList();
        } finally {
            session.close();
        }
    }

    public List<Email> findUnread(String recipientEmail) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT r.email FROM Recipient r WHERE r.recipientEmail = :recipientEmail AND r.isRead = false ORDER BY r.email.date DESC", Email.class).setParameter("recipientEmail", recipientEmail).list();
        }
    }

    public List<Email> findSent(String senderEmail) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Email e WHERE e.sender.email = :senderEmail ORDER BY e.date DESC", Email.class).setParameter("senderEmail", senderEmail).list();
        }
    }

    public void update(Email email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(email);
            tx.commit();
        }
    }

    public void markRecipientRead(Long emailId, String recipientEmail) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.createQuery("UPDATE Recipient r SET r.isRead = true WHERE r.email.id = :emailId AND r.recipientEmail = :recipientEmail").setParameter("emailId", emailId).setParameter("recipientEmail", recipientEmail).executeUpdate();
            tx.commit();
        }
    }
}