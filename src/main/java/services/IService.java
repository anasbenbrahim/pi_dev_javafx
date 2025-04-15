package services;

import java.util.List;

public interface IService<T> {
    void insert(T t);
    void update(T t);
    void delete(int id);
    List<T> getAll();
    T getById(int id);
}
