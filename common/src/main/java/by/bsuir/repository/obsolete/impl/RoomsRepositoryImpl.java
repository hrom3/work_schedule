package by.bsuir.repository.obsolete.impl;

import by.bsuir.domain.Room;
import by.bsuir.repository.obsolete.IRoomsRepository;

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


@Repository
@Primary
@RequiredArgsConstructor
@Deprecated(since = "version 0.1.20210731")
public class RoomsRepositoryImpl implements IRoomsRepository {

    private final SessionFactory sessionFactory;

    @Override
    public List<Room> findAll() {
        try (Session session = sessionFactory.openSession()) {

            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Room> criteriaBuilderQuery = criteriaBuilder
                    .createQuery(Room.class);
            Root<Room> hibernateRoomRoot = criteriaBuilderQuery
                    .from(Room.class);
            CriteriaQuery<Room> all = criteriaBuilderQuery
                    .select(hibernateRoomRoot);

            TypedQuery<Room> allQuery = session.createQuery(all);

            return allQuery.getResultList();
        }
    }

    @Override
    public Room findOne(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(Room.class, id);
        }
    }

    @Override
    public Room save(Room entity) {
        try (Session session = sessionFactory.openSession()) {
            session.saveOrUpdate(entity);
        }
        return entity;
    }

    @Override
    public Room update(Room entity) {
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
        Room departmentToDelete = findOne(id);
        try (Session session = sessionFactory.openSession()) {
            session.delete(departmentToDelete);
        }
    }
}
