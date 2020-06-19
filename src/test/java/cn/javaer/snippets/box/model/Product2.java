package cn.javaer.snippets.box.model;

import lombok.Data;

import java.util.Map;

/**
 * @author cn-src
 */
@Data
public class Product2 {
    final Long id;
    final String name;
    Map<String, Long> dynamicData;
}
