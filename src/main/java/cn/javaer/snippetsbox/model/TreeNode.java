package cn.javaer.snippetsbox.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 树节点.
 *
 * @author cn-src
 */
@Data
class TreeNode<T> {
    private T title;
    private List<TreeNode<T>> children;

    TreeNode() {
    }

    TreeNode(T title) {
        this.title = title;
    }

    @SafeVarargs
    final TreeNode<T> addChildren(TreeNode<T>... children) {
        if (children == null || children.length == 0) {
            return this;
        }
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.addAll(Arrays.asList(children));
        return this;
    }
}
