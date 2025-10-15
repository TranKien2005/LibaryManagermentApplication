package data;

import model.Manager;
import java.util.concurrent.CompletableFuture;

public interface ManagerRepository extends BaseRepository<Manager, Integer> {
    // Additional methods can be added here if needed
}