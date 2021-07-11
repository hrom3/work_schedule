package by.bsuir.repository.impl;

import by.bsuir.domain.Department;
import by.bsuir.repository.IDepartmentRepository;
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
public class DepartmentRepositoryImpl implements IDepartmentRepository {

    private final SessionFactory sessionFactory;

    @Override
    public List<Department> findAll() {
        try (Session session = sessionFactory.openSession()) {

            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Department> criteriaBuilderQuery = criteriaBuilder
                    .createQuery(Department.class);
            Root<Department> hibernateDepartmentRoot = criteriaBuilderQuery
                    .from(Department.class);
            CriteriaQuery<Department> all = criteriaBuilderQuery
                    .select(hibernateDepartmentRoot);

            TypedQuery<Department> allQuery = session.createQuery(all);

            return allQuery.getResultList();
        }
    }

    @Override
    public Department findOne(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(Department.class, id);
        }
    }

    @Override
    public Department save(Department entity) {
        try (Session session = sessionFactory.openSession()) {
            session.saveOrUpdate(entity);
        }
        return entity;
    }

    @Override
    public Department update(Department entity) {
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
        Department departmentToDelete = findOne(id);
        try (Session session = sessionFactory.openSession()) {
            session.delete(departmentToDelete);
        }
    }

    @Override
    public List<Department> findDepartmentByQuery(String departmentName) {
        try (Session session = sessionFactory.openSession()) {
            TypedQuery<Department> query = session.createQuery(
                    "from Department where departmentName like :name");
            query.setParameter("name", departmentName);
            return query.getResultList();
        }
    }
}
