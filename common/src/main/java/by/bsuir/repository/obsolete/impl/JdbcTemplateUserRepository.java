package by.bsuir.repository.obsolete.impl;

import by.bsuir.domain.Role;
import by.bsuir.domain.User;
import by.bsuir.exception.NoSuchEntityException;
import by.bsuir.repository.obsolete.columns.IUserColumns;
import by.bsuir.repository.obsolete.IJDBCUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
@Deprecated
public class JdbcTemplateUserRepository implements IJDBCUserRepository {

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private User getUserRowMapper(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getLong(IUserColumns.ID));
        user.setName(rs.getString(IUserColumns.NAME));
        user.setSurname(rs.getString(IUserColumns.SURNAME));
        user.setMiddleName(rs.getString(IUserColumns.MIDDLE_NAME));
        user.setEmail(rs.getString(IUserColumns.EMAIL));
        user.setBirthDay((rs.getDate(IUserColumns.BIRTHDAY)).toLocalDate());
        //       user.setBirthDay((rs.getDate(IUserColumns.BIRTHDAY)));
       // user.setDepartmentId(rs.getInt(IUserColumns.DEPARTMENT_ID));
        user.setCreated(rs.getTimestamp(IUserColumns.CREATED));
        user.setChanged(rs.getTimestamp(IUserColumns.CHANGED));
        user.setIsDeleted(rs.getBoolean(IUserColumns.IS_DELETED));
        //user.setRateId(rs.getInt(IUserColumns.RATE_ID));
        //user.setRoomId(rs.getInt(IUserColumns.ROOM_ID));
        return user;
    }

    @Override
    public List<User> findAll() {
        return jdbcTemplate.query("select * from users order by id",
                this::getUserRowMapper);
    }

    @Override
    public User findOne(Long id) {

        final String findOneWithId = "select * from users where id = :id";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);

        try {
            return namedParameterJdbcTemplate.queryForObject(findOneWithId,
                    parameters, this::getUserRowMapper);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchEntityException("No such user with id:" + id);
        }
    }

    @Override
    public User save(User entity) {
        final String createQuery = "insert into users (name, surname, middle_name, " +
                "email, birth_day, department_id, created, changed, is_deleted, " +
                "rate_id, room_id) " + "values (:name, :surname, :middleName, " +
                ":email, :birthDay, :departmentId, :created, :changed, :isDeleted, " +
                ":rateId, :roomId);";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource params = generateUserParamsMap(entity);

        namedParameterJdbcTemplate.update(createQuery, params, keyHolder, new String[]{"id"});

        long createdUserId = Objects.requireNonNull(keyHolder.getKey()).longValue();

        return findOne(createdUserId);
    }

//    @Override
//    public void addOne(User entity) {
//        final String createQuery = "insert into users (name, surname, middle_name, " +
//                "email, birth_day, department_id, created, changed, is_deleted, " +
//                "rate_id, room_id) " + "values (:name, :surname, :middleName, " +
//                ":email, :birthDay, :departmentId, :created, :changed, :isDeleted, " +
//                ":rateId, :roomId);";
//
//        MapSqlParameterSource params = generateUserParamsMap(entity);
//        namedParameterJdbcTemplate.update(createQuery, params);
//    }

//    @Override
//    public void save(List<User> entities) {
//        for (User entry : entities) {
//            addOne(entry);
//        }
//    }

    @Override
    public User update(User entity) {
        final String updateQuery = "update users set " +
                "name = :name, " +
                "surname = :surname, " +
                "middle_name =:middleName, " +
                "email = :email, " +
                "birth_day = :birthDay, " +
                "department_id = :departmentId, " +
                "created = :created, " +
                "changed = :changed, " +
                "is_deleted = :is_deleted, " +
                "rate_id = :rate_id, " +
                "room_id = :room_id " +
                "where id = :id";


        MapSqlParameterSource params = generateUserParamsMap(entity);

        namedParameterJdbcTemplate.update(updateQuery, params);

        return findOne(entity.getId());
    }

    @Override
    public void deleteHard(Long id) {
        final String hardDeleteByIdQuery = "delete from users where id = :id;";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);

        namedParameterJdbcTemplate.update(hardDeleteByIdQuery, parameters);

    }

    public boolean delete(Long id) {
        final String deleteByIdQuery = "update users set is_deleted = true" +
                " where id = :id;";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);

        Integer result = namedParameterJdbcTemplate.update(deleteByIdQuery, parameters);

        return result > 0;
    }

    //Specification
    //Criteria API
    //Search Criteria object
    //like '%query%' and like '%query%' and like '%query%'
    //ElasticSearch
    //PostgresFTS
    @Override
    public List<User> findUsersByQuery(Integer limit, String query) {
        final String searchQuery = "select * from users where name like :query " +
                "limit :limit;";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("query", "%" + query + "%");
        params.addValue("limit", limit);

        return namedParameterJdbcTemplate.query(searchQuery, params,
                this::getUserRowMapper);
    }

    @Override
    public void batchInsert(List<User> users) {
        final String createQuery = "insert into users (name, surname, middle_name, " +
                "email, birth_day, department_id, created, changed, is_deleted, " +
                "rate_id, room_id) " + "values (:name, :surname, :middleName, " +
                ":email, :birthDay, :departmentId, :created, :changed, :isDeleted, " +
                ":rateId, :roomId);";

        List<MapSqlParameterSource> batchParams = new ArrayList<>();

        for (User user : users) {
            batchParams.add(generateUserParamsMap(user));

            namedParameterJdbcTemplate.batchUpdate(createQuery,
                    batchParams.toArray(new MapSqlParameterSource[0]));
        }
    }

    @Override
    public void saveUserRoles(User user, List<Role> roles) {
        final String createQuery = "insert into users_role (id_user, id_role) "
                + "values (:idUser, :idRole);";

        List<MapSqlParameterSource> batchParams = new ArrayList<>();

        for (Role role : roles) {

            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("idUser", user.getId());
            params.addValue("idRole", role.getId());

            batchParams.add(params);
        }
            namedParameterJdbcTemplate.batchUpdate(createQuery,
                    batchParams.toArray(new MapSqlParameterSource[0]));

    }

    @Override
    public User findUserByLogin(String login) {
//        final String findUserByLogin = "select * from users " +
//        "where id = :id" +
//                "(select id_users from credential where login = :login);";
        final String findUserByLogin = "select " +
                "u.id as id, " +
                "u.name as name, " +
                "u.surname as surname, " +
                "u.email as email, " +
                "u.birth_day as birth_day, " +
                "u.department_id as department_id, " +
                "u.created as created, " +
                "u.changed as changed, " +
                "u.is_deleted as is_deleted, " +
                "u.rate_id as  rate_id, " +
                "u.middle_name as middle_name, " +
                "u.room_id as room_id " +
                "from users u join credential c on u.id = c.id_users " +
                "where c.login = :login;";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("login", login);

        try {
            return namedParameterJdbcTemplate.queryForObject(findUserByLogin,
                    parameters, this::getUserRowMapper);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchEntityException("No such user with this login");

        }
//        return findOne(2L);
    }

    @Override
    public User findByLoginAndPassword(String login, String password) {

        final String findUserByLoginAndPassword = "select " +
                "u.id as id, " +
                "u.name as name, " +
                "u.surname as surname, " +
                "u.email as email, " +
                "u.birth_day as birth_day, " +
                "u.department_id as department_id, " +
                "u.created as created, " +
                "u.changed as changed, " +
                "u.is_deleted as is_deleted, " +
                "u.rate_id as  rate_id, " +
                "u.middle_name as middle_name, " +
                "u.room_id as room_id " +
                "from users u join credential c on u.id = c.id_users " +
                "where login = :login and password = :password;";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("login", login);
        parameters.addValue("password", password);

        try {
            return namedParameterJdbcTemplate.queryForObject(findUserByLoginAndPassword,
                    parameters, this::getUserRowMapper);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchEntityException("No such user with this credential");
        }
    }


    private MapSqlParameterSource generateUserParamsMap(User entity) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", entity.getName());
        params.addValue("surname", entity.getSurname());
        params.addValue("middleName", entity.getMiddleName());
        params.addValue("email", entity.getEmail());
        params.addValue("birthDay", entity.getBirthDay());
        //params.addValue("departmentId", entity.getDepartmentId());
        params.addValue("created", entity.getCreated());
        params.addValue("changed", entity.getChanged());
        params.addValue("isDeleted", entity.getIsDeleted());
        //params.addValue("rateId", entity.getRateId());
        //params.addValue("roomId", entity.getRoomId());

        return params;
    }
}
