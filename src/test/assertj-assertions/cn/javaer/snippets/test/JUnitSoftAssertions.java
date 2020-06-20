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
 * Like {@link SoftAssertions} but as a junit rule that takes care of calling
 * {@link SoftAssertions#assertAll() assertAll()} at the end of each test.
 * <p>
 * Example:
 * <pre><code class='java'> public class SoftlyTest {
 *
 *     &#064;Rule
 *     public final JUnitBDDSoftAssertions softly = new JUnitBDDSoftAssertions();
 *
 *     &#064;Test
 *     public void soft_bdd_assertions() throws Exception {
 *       softly.assertThat(1).isEqualTo(2);
 *       softly.assertThat(Lists.newArrayList(1, 2)).containsOnly(1, 2);
 *       // no need to call assertAll(), this is done automatically.
 *     }
 *  }</code></pre>
 */
@javax.annotation.Generated(value="assertj-assertions-generator")
public class JUnitSoftAssertions extends org.assertj.core.api.JUnitSoftAssertions {

  /**
   * Creates a new "soft" instance of <code>{@link AreasAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  @org.assertj.core.util.CheckReturnValue
  public AreasAssert assertThat(Areas actual) {
    return proxy(AreasAssert.class, Areas.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link ProductAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  @org.assertj.core.util.CheckReturnValue
  public ProductAssert assertThat(Product actual) {
    return proxy(ProductAssert.class, Product.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link Product2Assert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  @org.assertj.core.util.CheckReturnValue
  public Product2Assert assertThat(Product2 actual) {
    return proxy(Product2Assert.class, Product2.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link UserAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  @org.assertj.core.util.CheckReturnValue
  public UserAssert assertThat(User actual) {
    return proxy(UserAssert.class, User.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link cn.javaer.snippets.spring.boot.autoconfigure.eclipse.collections.pojo.CityAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  @org.assertj.core.util.CheckReturnValue
  public cn.javaer.snippets.spring.boot.autoconfigure.eclipse.collections.pojo.CityAssert assertThat(cn.javaer.snippets.spring.boot.autoconfigure.eclipse.collections.pojo.City actual) {
    return proxy(cn.javaer.snippets.spring.boot.autoconfigure.eclipse.collections.pojo.CityAssert.class, cn.javaer.snippets.spring.boot.autoconfigure.eclipse.collections.pojo.City.class, actual);
  }

}
