package by.bsuir.repository.obsolete.impl;

import by.bsuir.domain.Credential;
import by.bsuir.domain.User;
import by.bsuir.repository.obsolete.ICredentialRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;


@Repository
@Primary
@RequiredArgsConstructor
@Deprecated
public class CredentialRepositoryImpl implements ICredentialRepository {

    private final SessionFactory sessionFactory;

    @Override
    public List<Credential> findAll() {
        try (Session session = sessionFactory.openSession()) {

            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Credential> criteriaBuilderQuery = criteriaBuilder
                    .createQuery(Credential.class);
            Root<Credential> hibernateCredentialRoot = criteriaBuilderQuery
                    .from(Credential.class);
            CriteriaQuery<Credential> all = criteriaBuilderQuery
                    .select(hibernateCredentialRoot);

            TypedQuery<Credential> allQuery = session.createQuery(all);

            return allQuery.getResultList();
        }
    }

    @Override
    public Credential findOne(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(Credential.class, id);
        }
    }

    @Override
    public Credential save(Credential entity) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.getTransaction();
            transaction.begin();
            session.saveOrUpdate(entity);
            transaction.commit();
        }
        return entity;
    }

    @Override
    public Credential update(Credential entity) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.getTransaction();
            transaction.begin();
            session.saveOrUpdate(entity);
            transaction.commit();
            return entity;
        }
    }

    @Override
    public void deleteHard(Long id) {
        Credential credentialToDelete = findOne(id);
        try (Session session = sessionFactory.openSession()) {
            session.delete(credentialToDelete);
        }
    }

    @Override
    public Credential findByUser(User user) {
        return user.getCredential();
    }

    //TODO remove method to service
    public void saveUserCredentials(User user, Credential userCredential) {

        user.setCredential(userCredential);
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.getTransaction();
            transaction.begin();
            session.saveOrUpdate(user);
            session.saveOrUpdate(userCredential);
            transaction.commit();
        }
    }

    public Credential findByLogin(String login) {
        try (Session session = sessionFactory.openSession()) {
            TypedQuery<Credential> query = session.createQuery(
                    "from Credential where login like :login");
            query.setParameter("login", login);
            return query.getSingleResult();
        }
    }
}
