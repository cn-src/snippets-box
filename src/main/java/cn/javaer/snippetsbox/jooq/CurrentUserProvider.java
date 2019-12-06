package cn.javaer.snippetsbox.jooq;

/**
 * @author cn-src
 */
@FunctionalInterface
public interface CurrentUserProvider {

    Object currentUser();
}
