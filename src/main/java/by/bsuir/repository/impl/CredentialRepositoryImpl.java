package by.bsuir.repository.impl;

import by.bsuir.domain.Credential;
import by.bsuir.domain.ESystemRoles;
import by.bsuir.domain.Role;
import by.bsuir.domain.User;
import by.bsuir.exception.NoSuchEntityException;
import by.bsuir.repository.ICredentialRepository;
import by.bsuir.repository.IRoleRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
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

@Repository
@Primary
@RequiredArgsConstructor
public class CredentialRepositoryImpl implements ICredentialRepository {

    private final SessionFactory sessionFactory;

//    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
//
//    private static final String CREDENTIAL_ID = "id";
//    private static final String CREDENTIAL_LOGIN = "login";
//    private static final String CREDENTIAL_PASSWORD = "password";
//    private static final String CREDENTIAL_USERS_ID = "id_users";
//
//    private Credential getCredentialRowMapper(ResultSet rs, int i)
//            throws SQLException {
//
//        Credential credential = new Credential();
//
//        credential.setId(rs.getLong(CREDENTIAL_ID));
//        credential.setLogin(rs.getString(CREDENTIAL_LOGIN));
//        credential.setPassword(rs.getString(CREDENTIAL_PASSWORD));
//        credential.setIdUser(rs.getLong(CREDENTIAL_USERS_ID));
//
//        return credential;
//    }

    @Override
    public List<Credential> findAll() {
        try (Session session = sessionFactory.openSession()) {

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Credential> cq = cb.createQuery(Credential.class);
            Root<Credential> hibernateCredentialRoot = cq.from(Credential.class);
            CriteriaQuery<Credential> all = cq.select(hibernateCredentialRoot);

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
            session.saveOrUpdate(entity);
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
        try (Session session = sessionFactory.openSession()) {
            Credential credentialToDelete = findOne(id);
            session.delete(credentialToDelete);
        }
    }

    @Override
    public Credential findByUser(User user) {
//        Long userId = user.getId();
//        try (Session session = sessionFactory.openSession()) {
//            Credential credential = session.byNaturalId(Credential.class)
//                    .using("idUser", userId)
//                    .load();
//
//          return  user.getCredential();
//        }
        return user.getCredential();
    }

    public void saveUserCredentials(User user, Credential userCredential) {


        //       userCredential.setIdUser(user.getId());
        user.setCredential(userCredential);
        try (Session session = sessionFactory.openSession()) {
            session.saveOrUpdate(user);
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
