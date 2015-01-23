package in.xebia.repository;

import in.xebia.jobs.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends MongoRepository<Customer, String> {

  public Customer findByEmail(@Param("email") String email);

  public Customer findByGender(@Param("gender") String gender);
}
