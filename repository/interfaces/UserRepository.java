package repository.interfaces;

import model.User;

public interface UserRepository extends Repository<User>{
   User getByNRIC(String NRIC);
}
