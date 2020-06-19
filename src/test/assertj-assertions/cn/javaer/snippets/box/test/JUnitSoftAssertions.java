package cn.javaer.snippets.box.test;

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
   * Creates a new "soft" instance of <code>{@link cn.javaer.snippets.box.model.pojo.AreasAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  @org.assertj.core.util.CheckReturnValue
  public cn.javaer.snippets.box.model.pojo.AreasAssert assertThat(cn.javaer.snippets.box.model.pojo.Areas actual) {
    return proxy(cn.javaer.snippets.box.model.pojo.AreasAssert.class, cn.javaer.snippets.box.model.pojo.Areas.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link cn.javaer.snippets.box.model.pojo.ProductAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  @org.assertj.core.util.CheckReturnValue
  public cn.javaer.snippets.box.model.pojo.ProductAssert assertThat(cn.javaer.snippets.box.model.pojo.Product actual) {
    return proxy(cn.javaer.snippets.box.model.pojo.ProductAssert.class, cn.javaer.snippets.box.model.pojo.Product.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link cn.javaer.snippets.box.model.pojo.Product2Assert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  @org.assertj.core.util.CheckReturnValue
  public cn.javaer.snippets.box.model.pojo.Product2Assert assertThat(cn.javaer.snippets.box.model.pojo.Product2 actual) {
    return proxy(cn.javaer.snippets.box.model.pojo.Product2Assert.class, cn.javaer.snippets.box.model.pojo.Product2.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link cn.javaer.snippets.box.spring.data.jooq.jdbc.pojo.UserAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  @org.assertj.core.util.CheckReturnValue
  public cn.javaer.snippets.box.spring.data.jooq.jdbc.pojo.UserAssert assertThat(cn.javaer.snippets.box.spring.data.jooq.jdbc.pojo.User actual) {
    return proxy(cn.javaer.snippets.box.spring.data.jooq.jdbc.pojo.UserAssert.class, cn.javaer.snippets.box.spring.data.jooq.jdbc.pojo.User.class, actual);
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
