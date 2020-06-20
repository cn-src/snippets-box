package cn.javaer.snippets.test;

import cn.javaer.snippets.model.pojo.Areas;
import cn.javaer.snippets.model.pojo.AreasAssert;
import cn.javaer.snippets.model.pojo.Product;
import cn.javaer.snippets.model.pojo.Product2;
import cn.javaer.snippets.model.pojo.Product2Assert;
import cn.javaer.snippets.model.pojo.ProductAssert;
import cn.javaer.snippets.spring.data.jooq.jdbc.pojo.User;
import cn.javaer.snippets.spring.data.jooq.jdbc.pojo.UserAssert;

/**
 * Entry point for assertions of different data types. Each method in this class is a static factory for the
 * type-specific assertion objects.
 */
@javax.annotation.Generated(value="assertj-assertions-generator")
public class Assertions extends org.assertj.core.api.Assertions{

  /**
   * Creates a new instance of <code>{@link AreasAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  @org.assertj.core.util.CheckReturnValue
  public static AreasAssert assertThat(Areas actual) {
    return new AreasAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link ProductAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  @org.assertj.core.util.CheckReturnValue
  public static ProductAssert assertThat(Product actual) {
    return new ProductAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link Product2Assert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  @org.assertj.core.util.CheckReturnValue
  public static Product2Assert assertThat(Product2 actual) {
    return new Product2Assert(actual);
  }

  /**
   * Creates a new instance of <code>{@link UserAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  @org.assertj.core.util.CheckReturnValue
  public static UserAssert assertThat(User actual) {
    return new UserAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link cn.javaer.snippets.spring.boot.autoconfigure.eclipse.collections.pojo.CityAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  @org.assertj.core.util.CheckReturnValue
  public static cn.javaer.snippets.spring.boot.autoconfigure.eclipse.collections.pojo.CityAssert assertThat(cn.javaer.snippets.spring.boot.autoconfigure.eclipse.collections.pojo.City actual) {
    return new cn.javaer.snippets.spring.boot.autoconfigure.eclipse.collections.pojo.CityAssert(actual);
  }

  /**
   * Creates a new <code>{@link Assertions}</code>.
   */
  protected Assertions() {
    // empty
  }
}
