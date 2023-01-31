package fr.takima.training.simpleapi.dao;

import fr.takima.training.simpleapi.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentDAO extends JpaRepository<Department, Long> {
    Department findDepartmentByName(String name);
}
