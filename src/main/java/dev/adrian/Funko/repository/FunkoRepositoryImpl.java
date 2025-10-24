package dev.adrian.Funko.repository;

import dev.adrian.Funko.model.Funko;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public class FunkoRepositoryImpl implements FunkoRepository {

    private final ConcurrentHashMap<Long, Funko> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Optional<Funko> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Funko> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Funko save(Funko funko) {
        if (funko.getId() == null) {
            Long newId = idGenerator.getAndIncrement();
            funko.setId(newId);
        }
        store.put(funko.getId(), funko);
        return funko;
    }

    @Override
    public Funko update(Funko funko) {
        if (existsById(funko.getId())) {
            store.put(funko.getId(), funko);
            return funko;
        } else {
            throw new RuntimeException("Funko con ID " + funko.getId() + " no encontrado para actualizar.");
        }
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }

    @Override
    public boolean existsById(Long id) {
        return store.containsKey(id);
    }
}
