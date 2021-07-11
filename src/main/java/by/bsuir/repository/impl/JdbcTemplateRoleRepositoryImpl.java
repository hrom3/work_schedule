/*
package by.bsuir.repository.impl;

import by.bsuir.domain.ESystemRoles;
import by.bsuir.domain.Role;
import by.bsuir.domain.User;
import by.bsuir.domain.UsersRole;
import by.bsuir.exception.NoSuchEntityException;
import by.bsuir.repository.IRoleRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;


import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JdbcTemplateRoleRepositoryImpl implements IRoleRepository {

    private final SessionFactory sessionFactory;

    @Override
    public List<Role> findAll() {
        try (Session session = sessionFactory.openSession()) {

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Role> cq = cb.createQuery(Role.class);
            Root<Role> hibernateRoleRoot = cq.from(Role.class);
            CriteriaQuery<Role> all = cq.select(hibernateRoleRoot);

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
            return entity;
        }
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
        try (Session session = sessionFactory.openSession()) {
            Role roleToDelete = findOne(id);
            session.delete(roleToDelete);
        }
    }

    @Override
    public List<Role> getUserRoles(User user) {
        try (Session session = sessionFactory.openSession()) {
            TypedQuery<UsersRole> query = session.createQuery(
                    "from UsersRole where userId = :userId");
            query.setParameter("userId", user.getId());
            List<UsersRole> roles = query.getResultList();
            return roles.stream()
                    .map(UsersRole::getRole)
                    .collect(Collectors.toList());
        }
    }
}
*/
