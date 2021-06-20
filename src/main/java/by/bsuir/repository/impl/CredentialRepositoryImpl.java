package by.bsuir.repository.impl;

import by.bsuir.domain.Credential;
import by.bsuir.domain.ESystemRoles;
import by.bsuir.domain.Role;
import by.bsuir.domain.User;
import by.bsuir.exception.NoSuchEntityException;
import by.bsuir.repository.ICredentialRepository;
import by.bsuir.repository.IRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Repository
@Primary
@RequiredArgsConstructor
public class CredentialRepositoryImpl implements ICredentialRepository {

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String CREDENTIAL_ID = "id";
    private static final String CREDENTIAL_LOGIN = "login";
    private static final String CREDENTIAL_PASSWORD = "password";
    private static final String CREDENTIAL_USERS_ID = "id_users";

    private Credential getCredentialRowMapper(ResultSet rs, int i)
            throws SQLException {

        Credential credential = new Credential();

        credential.setId(rs.getLong(CREDENTIAL_ID));
        credential.setLogin(rs.getString(CREDENTIAL_LOGIN));
        credential.setPassword(rs.getString(CREDENTIAL_PASSWORD));
        credential.setIdUser(rs.getLong(CREDENTIAL_USERS_ID));

        return credential;
    }

    @Override
    public List<Credential> findAll() {
        return jdbcTemplate.query("select * from credential order by id",
                this::getCredentialRowMapper);
    }

    @Override
    public Credential findOne(Long id) {
        final String findOneWithId = "select * from credential where id = :id";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);

        try {
            return namedParameterJdbcTemplate.queryForObject(findOneWithId,
                    parameters, this::getCredentialRowMapper);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchEntityException("No such credential with id:" + id);
        }
    }

    @Override
    public Credential save(Credential entity) {
        final String createQuery = "insert into " +
                "credential (id_users, login, password) " +
                "values (:idUsers, :login, :password);";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("idUsers", entity.getIdUser());
        params.addValue("login", entity.getLogin());
        params.addValue("password", entity.getPassword());

        namedParameterJdbcTemplate.update(createQuery, params, keyHolder,
                new String[]{"id"});

        long createdRoleId = Objects.requireNonNull(keyHolder.getKey()).longValue();

        return findOne(createdRoleId);
    }

    @Override
    public Credential update(Credential entity) {
        final String updateQuery = "update " +
                "credential set " +
                "id_users = :idUsers, " +
                "login = :login, " +
                "password = :password " +
                "where id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", entity.getId());
        params.addValue("idUsers", entity.getIdUser());
        params.addValue("login", entity.getLogin());
        params.addValue("password", entity.getPassword());

        namedParameterJdbcTemplate.update(updateQuery, params);

        return findOne(entity.getId());
    }

    @Override
    public void deleteHard(Long id) {
        final String hardDeleteByIdQuery = "delete from credential where id = :id;";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);

        namedParameterJdbcTemplate.update(hardDeleteByIdQuery, parameters);
    }

    @Override
    public Credential findByUser(User user) {
        final String findeUserCredentialQuery = "select * from credential " +
                "where id_users = :userId;";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", user.getId());

        return namedParameterJdbcTemplate.queryForObject(findeUserCredentialQuery,
                params, this::getCredentialRowMapper);
    }
}
