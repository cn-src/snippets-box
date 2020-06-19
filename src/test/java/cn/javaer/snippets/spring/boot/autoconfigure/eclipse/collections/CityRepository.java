package cn.javaer.snippets.spring.boot.autoconfigure.eclipse.collections;

import cn.javaer.snippets.spring.boot.autoconfigure.eclipse.collections.pojo.City;
import org.eclipse.collections.api.list.ImmutableList;
import org.springframework.data.repository.CrudRepository;

public interface CityRepository extends CrudRepository<City, Long> {
    @Override
    ImmutableList<City> findAll();
}