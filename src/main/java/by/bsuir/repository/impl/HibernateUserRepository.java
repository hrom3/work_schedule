package by.bsuir.repository.impl;

import by.bsuir.domain.Credential;
import by.bsuir.domain.Role;
import by.bsuir.domain.User;
import by.bsuir.repository.ICredentialRepository;
import by.bsuir.repository.IHibernateUserRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.Query;
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
import java.util.Optional;

@Repository
@Primary
@RequiredArgsConstructor
public class HibernateUserRepository implements IHibernateUserRepository {

    private final SessionFactory sessionFactory;

    private final ICredentialRepository credentialRepository;

    @Override
    public List<User> findAll() {

        try (Session session = sessionFactory.openSession()) {

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<User> cq = cb.createQuery(User.class);
            Root<User> hibernateUserRoot = cq.from(User.class);
            CriteriaQuery<User> all = cq.select(hibernateUserRoot);

            TypedQuery<User> allQuery = session.createQuery(all);

            return allQuery.getResultList();
        }
    }

    @Override
    public User findOne(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(User.class, id);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
            return Optional.of(findOne(id));
    }

    @Override
    public User save(User entity) {
        try (Session session = sessionFactory.openSession()) {
            session.saveOrUpdate(entity);
            return entity;
        }
    }

    @Override
    public User update(User entity) {
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
        try (Session session = sessionFactory.openSession()) {
            User userToDelete = findOne(id);
            session.delete(userToDelete);
        }
    }

    @Override
    public void delete(Long id) {

        User userToDelete = findOne(id);

        userToDelete.setIsDeleted(true);

        update(userToDelete);
    }

    @Override
    public List<User> findUsersByQuery(Integer limit, String name) {
        try (Session session = sessionFactory.openSession()) {
            TypedQuery<User> query = session.createQuery(
                    "from User where name like :name");
            query.setParameter("name", name);
            return query.getResultList();
        }
    }

    public User findUserByLogin(String login) {
        Credential credential = credentialRepository.findByLogin(login);
        return  credential.getUser();
    }
}
