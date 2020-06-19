package cn.javaer.snippets.box.model.pojo;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.util.Map;

/**
 * @author cn-src
 */
@Data
@FieldNameConstants
public class Product2 {
    final Long id;
    final String name;
    Map<String, Long> dynamicData;
}
