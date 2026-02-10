package ru.ilya.dao;

import java.util.List;

public interface GenericDao<T, K> {
   T create(T model);

   T findById(K id);

   List<T> findAll();

   T update(T model);

   boolean delete(K id);

   void deleteAll();
}
