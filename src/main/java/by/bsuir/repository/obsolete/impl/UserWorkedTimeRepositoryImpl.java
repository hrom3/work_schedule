package by.bsuir.repository.obsolete.impl;

import by.bsuir.domain.UserWorkedTime;
import by.bsuir.repository.obsolete.IUserWorkedTimeRepository;
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
public class UserWorkedTimeRepositoryImpl implements IUserWorkedTimeRepository {

    private final SessionFactory sessionFactory;

    @Override
    public List<UserWorkedTime> findAll() {
        try (Session session = sessionFactory.openSession()) {

            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<UserWorkedTime> criteriaBuilderQuery = criteriaBuilder
                    .createQuery(UserWorkedTime.class);
            Root<UserWorkedTime> hibernateUserWorkedTimeRoot = criteriaBuilderQuery
                    .from(UserWorkedTime.class);
            CriteriaQuery<UserWorkedTime> all = criteriaBuilderQuery
                    .select(hibernateUserWorkedTimeRoot);

            TypedQuery<UserWorkedTime> allQuery = session.createQuery(all);

            return allQuery.getResultList();
        }
    }

    @Override
    public UserWorkedTime findOne(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(UserWorkedTime.class, id);
        }
    }

    @Override
    public UserWorkedTime save(UserWorkedTime entity) {
        try (Session session = sessionFactory.openSession()) {
            session.saveOrUpdate(entity);
        }
        return entity;
    }

    @Override
    public UserWorkedTime update(UserWorkedTime entity) {
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
        UserWorkedTime departmentToDelete = findOne(id);
        try (Session session = sessionFactory.openSession()) {
            session.delete(departmentToDelete);
        }
    }
}
