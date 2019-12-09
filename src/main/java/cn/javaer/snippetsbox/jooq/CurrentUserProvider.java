package cn.javaer.snippetsbox.jooq;

/**
 * @author cn-src
 */
@FunctionalInterface
public interface CurrentUserProvider {

    /**
     * 获取当前用户.
     *
     * @return 当前用户
     */
    Object currentUser();
}
