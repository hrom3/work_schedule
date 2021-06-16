package by.bsuir.repository.impl;

import by.bsuir.domain.ESystemRoles;
import by.bsuir.domain.Role;
import by.bsuir.exception.NoSuchEntityException;
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
public class RoleRepositoryImpl implements IRoleRepository {

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String ROLE_ID = "id";
    private static final String ROLE_NAME = "role_name";

    private Role getRoleRowMapper(ResultSet rs, int i) throws SQLException {

        Role role = new Role();

        role.setId(rs.getInt(ROLE_ID));
        role.setRoleName(ESystemRoles.valueOf(rs.getString(ROLE_NAME)));

        return role;
    }

    @Override
    public List<Role> findAll() {
        return jdbcTemplate.query("select * from role order by id",
                this::getRoleRowMapper);
    }

    @Override
    public Role findOne(Integer id) {
        final String findOneWithId = "select * from role where id = :id";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);

        try {
            return namedParameterJdbcTemplate.queryForObject(findOneWithId,
                    parameters, this::getRoleRowMapper);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchEntityException("No such role with id:" + id);
        }
    }

    @Override
    public Role save(Role entity) {
        final String createQuery = "insert into role (role_name) values (:roleName);";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("roleName", entity.getRoleName());

        namedParameterJdbcTemplate.update(createQuery, params, keyHolder,
                new String[]{"id"});

        int createdRoleId = Objects.requireNonNull(keyHolder.getKey()).intValue();

        return findOne(createdRoleId);
    }

    @Override
    public Role update(Role entity) {
        final String updateQuery = "update role set role_name = :roleName " +
                "where id = :id";


        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("roleName", entity.getRoleName());
        params.addValue("id", entity.getId());

        namedParameterJdbcTemplate.update(updateQuery, params);

        return findOne(entity.getId());
    }

    @Override
    public void deleteHard(Integer id) {
        final String hardDeleteByIdQuery = "delete from role where id = :id;";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);

        namedParameterJdbcTemplate.update(hardDeleteByIdQuery, parameters);
    }
}
