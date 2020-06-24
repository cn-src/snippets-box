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
public class TreeNode {
    private String title;
    private List<TreeNode> children;

    public TreeNode() {
    }

    public TreeNode(final String title) {
        this.title = title;
    }

    public final void addChildren(final TreeNode... child) {
        if (child == null || child.length == 0) {
            return;
        }
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.addAll(Arrays.asList(child));
    }
}
