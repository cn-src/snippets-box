package cn.javaer.snippets.model;

import cn.javaer.snippets.model.pojo.Areas;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * @author cn-src
 */
class TreeTest {

    @Test
    void of() throws Exception {
        final Areas areas1 = new Areas("河北省", "石家庄市", "长安区");
        final Areas areas2 = new Areas("河北省", "石家庄市", "新华区");
        final Areas areas3 = new Areas("河北省", "唐山市", "开平区");
        final Areas areas4 = new Areas("山东省", "太原市", "小店区");

        final List<TreeNode> treeNodes = Tree.of(Arrays.asList(areas1, areas2, areas3, areas4),
                Areas::getArea1, Areas::getArea2, Areas::getArea3);

        assertThat(treeNodes).hasSize(2);
        JSONAssert.assertEquals("[{\"title\": \"河北省\", \"children\": [{\"title\": \"石家庄市\", \"children\": [{\"title\": \"长安区\", \"children\": null}, {\"title\": \"新华区\", \"children\": null}]}, {\"title\": \"唐山市\", \"children\": [{\"title\": \"开平区\", \"children\": null}]}]}, {\"title\": \"山东省\", \"children\": [{\"title\": \"太原市\", \"children\": [{\"title\": \"小店区\", \"children\": null}]}]}]\n", new ObjectMapper().writeValueAsString(treeNodes), false);
    }

    @Test
    void toModel() {
        final TreeNode tn1 = new TreeNode("河北省");
        final TreeNode tn11 = new TreeNode("石家庄市");
        final TreeNode tn2 = new TreeNode("山东省");
        tn1.addChildren(tn11);
        final List<Areas> areas = Tree.toModel(Arrays.asList(tn1, tn2),
                Areas::new, Areas::setArea1, Areas::setArea2, Areas::setArea3);
        assertThat(areas).hasSize(2)
                .extracting(Areas::getArea1, Areas::getArea2, Areas::getArea3)
                .contains(
                        tuple("河北省", "石家庄市", null),
                        tuple("山东省", null, null)
                );
    }
}