package cn.javaer.snippets.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author cn-src
 */
class TreeNodeTest {

    @Test
    void addChildren() {
        final TreeNode<String> t1 = new TreeNode<>("t1");
        t1.addChildren(new TreeNode<>("t1_1"));
        t1.addChildren(new TreeNode<>("t1_2"));
        assertThat(t1.getChildren()).hasSize(2);
    }
}