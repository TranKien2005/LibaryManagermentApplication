package data;

import model.User;
import java.util.concurrent.CompletableFuture;

public interface UserRepository extends BaseRepository<User, Integer> {
    // Additional methods can be added here if needed
}