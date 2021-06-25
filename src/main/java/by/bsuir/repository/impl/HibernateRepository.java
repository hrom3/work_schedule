package by.bsuir.repository.impl;

import by.bsuir.domain.Role;
import by.bsuir.domain.User;
import by.bsuir.repository.IHibernateUserRepository;
import by.bsuir.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class HibernateRepository implements IHibernateUserRepository {

    private final SessionFactory sessionFactory;

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
    public User save(User entity) {
        return null;
    }

    @Override
    public User update(User entity) {
        return null;
    }

    @Override
    public void deleteHard(Long id) {

    }
}
