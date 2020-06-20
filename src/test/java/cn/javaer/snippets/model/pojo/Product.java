package cn.javaer.snippets.model.pojo;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.util.Map;

/**
 * @author cn-src
 */
@Data
@FieldNameConstants
public class Product {
    final Long id;
    final String name;
    final String category1;
    final String category2;
    final Long count;
    Map<String, Long> dynamicData;
}
