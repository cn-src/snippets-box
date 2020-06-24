package cn.javaer.snippets.model;

import org.junit.jupiter.api.Test;

import static cn.javaer.snippets.box.test.Assertions.assertThat;

/**
 * @author cn-src
 */
class TreeNodeTest {

    @Test
    void addChildren() {
        final TreeNode t1 = new TreeNode("t1");
        t1.addChildren(new TreeNode("t1_1"));
        t1.addChildren(new TreeNode("t1_2"));
        assertThat(t1.getChildren()).hasSize(2);
        assertThat(t1).hasTitle("t1");
        assertThat(t1.getChildren().get(0)).hasTitle("t1_1");
        assertThat(t1.getChildren().get(1)).hasTitle("t1_2");
    }
}