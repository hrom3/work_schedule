package by.bsuir.repository.impl;

import by.bsuir.domain.Rate;
import by.bsuir.repository.IRateRepository;
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
public class RateRepositoryImpl implements IRateRepository {

    private final SessionFactory sessionFactory;

    @Override
    public List<Rate> findAll() {
        try (Session session = sessionFactory.openSession()) {

            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Rate> criteriaBuilderQuery = criteriaBuilder
                    .createQuery(Rate.class);
            Root<Rate> hibernateRateRoot = criteriaBuilderQuery
                    .from(Rate.class);
            CriteriaQuery<Rate> all = criteriaBuilderQuery
                    .select(hibernateRateRoot);

            TypedQuery<Rate> allQuery = session.createQuery(all);

            return allQuery.getResultList();
        }
    }

    @Override
    public Rate findOne(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(Rate.class, id);
        }
    }

    @Override
    public Rate save(Rate entity) {
        try (Session session = sessionFactory.openSession()) {
            session.saveOrUpdate(entity);
        }
        return entity;
    }

    @Override
    public Rate update(Rate entity) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.getTransaction();
            transaction.begin();
            session.saveOrUpdate(entity);
            transaction.commit();
            return entity;
        }
    }

    @Override
    public void deleteHard(Integer id) {
        Rate departmentToDelete = findOne(id);
        try (Session session = sessionFactory.openSession()) {
            session.delete(departmentToDelete);
        }
    }
}
