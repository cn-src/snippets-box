package cn.javaer.snippets.model;

/**
 * @author cn-src
 */
public interface TreeNodeHandler {

    TreeNodeHandler EMPTY = (treeNode, depth, index) -> { };

    void apply(TreeNode treeNode, int depth, int index);
}
