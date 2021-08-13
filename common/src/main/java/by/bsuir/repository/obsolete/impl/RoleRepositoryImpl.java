package by.bsuir.repository.obsolete.impl;

import by.bsuir.domain.Role;
import by.bsuir.repository.obsolete.IRoleRepository;

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

/**
 * @deprecated (Use Spring Data Repositories)
 */

@Deprecated(since = "version 0.1.20210731")
@Repository
@Primary
@RequiredArgsConstructor
public class RoleRepositoryImpl implements IRoleRepository {

    private final SessionFactory sessionFactory;

    @Override
    public List<Role> findAll() {
        try (Session session = sessionFactory.openSession()) {

            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Role> criteriaBuilderQuery = criteriaBuilder
                    .createQuery(Role.class);
            Root<Role> hibernateRoleRoot = criteriaBuilderQuery
                    .from(Role.class);
            CriteriaQuery<Role> all = criteriaBuilderQuery
                    .select(hibernateRoleRoot);

            TypedQuery<Role> allQuery = session.createQuery(all);

            return allQuery.getResultList();
        }
    }

    @Override
    public Role findOne(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(Role.class, id);
        }
    }

    @Override
    public Role save(Role entity) {
        try (Session session = sessionFactory.openSession()) {
            session.saveOrUpdate(entity);
        }
        return entity;
    }

    @Override
    public Role update(Role entity) {
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
        Role roleToDelete = findOne(id);
        try (Session session = sessionFactory.openSession()) {
            session.delete(roleToDelete);
        }
    }
}
