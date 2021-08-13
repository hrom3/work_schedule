package by.bsuir.repository.obsolete.impl;

import by.bsuir.domain.IssueFromJira;
import by.bsuir.repository.obsolete.IIssueFromJiraRepository;
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
public class IssueFromJiraRepositoryImpl implements IIssueFromJiraRepository {

    private final SessionFactory sessionFactory;

    @Override
    public List<IssueFromJira> findAll() {
        try (Session session = sessionFactory.openSession()) {

            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<IssueFromJira> criteriaBuilderQuery = criteriaBuilder
                    .createQuery(IssueFromJira.class);
            Root<IssueFromJira> hibernateIssueFromJiraRoot =
                    criteriaBuilderQuery.from(IssueFromJira.class);
            CriteriaQuery<IssueFromJira> all = criteriaBuilderQuery
                    .select(hibernateIssueFromJiraRoot);

            TypedQuery<IssueFromJira> allQuery = session.createQuery(all);

            return allQuery.getResultList();
        }
    }

    @Override
    public IssueFromJira findOne(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(IssueFromJira.class, id);
        }
    }

    @Override
    public IssueFromJira save(IssueFromJira entity) {
        try (Session session = sessionFactory.openSession()) {
            session.saveOrUpdate(entity);
        }
        return entity;
    }

    @Override
    public IssueFromJira update(IssueFromJira entity) {
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
        IssueFromJira departmentToDelete = findOne(id);
        try (Session session = sessionFactory.openSession()) {
            session.delete(departmentToDelete);
        }
    }
}
