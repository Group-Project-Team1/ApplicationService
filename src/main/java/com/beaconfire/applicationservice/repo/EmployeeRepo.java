package com.beaconfire.applicationservice.repo;

import com.beaconfire.applicationservice.domain.entity.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepo extends MongoRepository<Employee, String> {
    List<Employee> findEmployeeByUserId(Integer userId);

}
