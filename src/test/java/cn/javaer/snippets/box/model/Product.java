package cn.javaer.snippets.box.model;

import lombok.Data;

import java.util.Map;

/**
 * @author cn-src
 */
@Data
public class Product {
    final Long id;
    final String name;
    final String category1;
    final String category2;
    final Long count;
    Map<String, Long> dynamicData;
}
