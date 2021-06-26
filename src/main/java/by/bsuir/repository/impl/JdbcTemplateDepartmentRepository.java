package by.bsuir.repository.impl;

import by.bsuir.domain.Department;
import by.bsuir.exception.NoSuchEntityException;
import by.bsuir.repository.IDepartmentRepository;
import by.bsuir.repository.columns.IDepartmentColumns;
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
public class JdbcTemplateDepartmentRepository implements IDepartmentRepository {

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private Department getDepartmentRowMapper(ResultSet rs, int rowNum) throws
            SQLException {

        Department department = new Department();
        department.setId(rs.getInt(IDepartmentColumns.ID));
        department.setDepartmentName(rs.getString(IDepartmentColumns.DEPARTMENT_NAME));

        return department;
    }

    @Override
    public List<Department> findAll() {
        return jdbcTemplate.query("select * from department order by id",
                this::getDepartmentRowMapper);
    }

    @Override
    public Department findOne(Integer id) {

        final String findOneWithId = "select * from department where id = :id";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);

        try {
        return namedParameterJdbcTemplate.queryForObject(findOneWithId,
                parameters, this::getDepartmentRowMapper);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchEntityException("No such department with id:" + id);
        }
    }

    @Override
    public Department save(Department entity) {
        final String createQuery = "insert into department (department_name) "
                + "values (:departmenName);";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource params = generateDepartmentParamsMap(entity);

        namedParameterJdbcTemplate.update(createQuery, params, keyHolder,
                new String[]{"id"});

        Integer createdDepartmentId = Objects.requireNonNull(keyHolder.getKey())
                .intValue();

        return findOne(createdDepartmentId);
    }

//    @Override
//    public void addOne(Department entity) {
//        final String createQuery = "insert into department (department_name) "
//                + "values (:departmenName);";
//
//        MapSqlParameterSource params = generateDepartmentParamsMap(entity);
//        namedParameterJdbcTemplate.update(createQuery, params);
//    }

    @Override
    public Department update(Department entity) {
        final String updateQuery = "update department set " +
                "department_name = :departmenName " +
                "where id = :id;";

        MapSqlParameterSource params = generateDepartmentParamsMap(entity);

        namedParameterJdbcTemplate.update(updateQuery, params);

        return findOne(entity.getId());
    }

    @Override
    public void deleteHard(Integer id) {
        final String hardDeleteByIdQuery = "delete from department where " +
                "id = :id;";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);

        namedParameterJdbcTemplate.update(hardDeleteByIdQuery,parameters);
    }

    @Override
    public List<Department> findDepartmentByQuery(String query) {
        final String searchQuery =
                "select * from department where department_name like :query;";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("query", "%" + query + "%");

        return namedParameterJdbcTemplate.query(searchQuery, params,
                this::getDepartmentRowMapper);
    }


    private MapSqlParameterSource generateDepartmentParamsMap(Department entity) {
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("department_name", entity.getDepartmentName());

        return params;
    }
}
