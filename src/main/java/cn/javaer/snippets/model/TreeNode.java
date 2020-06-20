package cn.javaer.snippets.model;

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
public class TreeNode<T> {
    private T title;
    private List<TreeNode<T>> children;

    public TreeNode() {
    }

    public TreeNode(final T title) {
        this.title = title;
    }

    @SafeVarargs
    public final void addChildren(final TreeNode<T>... children) {
        if (children == null || children.length == 0) {
            return;
        }
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.addAll(Arrays.asList(children));
    }
}
