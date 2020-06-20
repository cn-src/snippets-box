package cn.javaer.snippets.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 映射转换工具类。
 *
 * @author cn-src
 */
public class MappingUtils {
    private MappingUtils() {}

    /**
     * 一对多合并转换，以唯一 Key 为基准，将对象中部分数据合并转换成对象的 Map 属性。
     *
     * 合并以 uniqueKeyGetter 获取的对象进行比较。源对象和目标对象是同一类型。
     *
     * @param srcList 源对象 List
     * @param uniqueKeyGetter 源对象获取唯一 Key 的函数
     * @param mapGetter 对象 Map 属性的读取函数
     * @param mapSetter 对象 Map 属性的写入函数
     * @param keyCreator 对象 Map 属性的 key 创建函数
     * @param valueCreator 对象 Map 属性的 value 创建函数
     * @param <T> 对T象类型
     * @param <UK> 唯一主键的类型
     * @param <MK> 对象 Map 属性的 key 的类型
     * @param <MV> 对象 Map 属性的 value 的类型
     *
     * @return 合并转换之后的对象 List
     */
    public static <T, UK, MK, MV> List<T> mergePropertyToMap(final List<T> srcList,
                                                             final Function<T, UK> uniqueKeyGetter,
                                                             final Function<T, Map<MK, MV>> mapGetter,
                                                             final BiConsumer<T, Map<MK, MV>> mapSetter,
                                                             final Function<T, MK> keyCreator,
                                                             final BiFunction<T, MV, MV> valueCreator) {
        return mergePropertyToMap(srcList,
                uniqueKeyGetter, uniqueKeyGetter,
                mapGetter, mapSetter,
                keyCreator,
                valueCreator,
                t -> t);
    }

    /**
     * 一对多合并转换，以唯一 Key 为基准，将源对象中部分数据合并转换成目标对象的 Map 属性。
     *
     * 合并以 sourceUniqueKeyGetter 和 targetUniqueKeyGetter 获取的对象进行比较。
     *
     * @param srcList 源对象 List
     * @param sourceUniqueKeyGetter 源对象获取唯一 Key 的函数
     * @param targetUniqueKeyGetter 目标对象获取唯一 Key 的函数
     * @param mapGetter 目标对象 Map 属性的读取函数
     * @param mapSetter 目标对象 Map 属性的写入函数
     * @param keyCreator 目标对象 Map 属性的 key 创建函数
     * @param valueCreator 目标对象 Map 属性的 value 创建函数
     * @param rCreator 目标对象的创建函数
     * @param <S> 源对象类型
     * @param <R> 目标对象类型
     * @param <UK> 唯一主键的类型
     * @param <MK> 目标对象 Map 属性的 key 的类型
     * @param <MV> 目标对象 Map 属性的 value 的类型
     *
     * @return 合并转换之后的对象 List
     */
    public static <S, R, UK, MK, MV> List<R> mergePropertyToMap(final List<S> srcList,

                                                                final Function<S, UK> sourceUniqueKeyGetter,
                                                                final Function<R, UK> targetUniqueKeyGetter,
                                                                final Function<R, Map<MK, MV>> mapGetter,
                                                                final BiConsumer<R, Map<MK, MV>> mapSetter,

                                                                final Function<S, MK> keyCreator,
                                                                final BiFunction<S, MV, MV> valueCreator,
                                                                final Function<S, R> rCreator) {
        final List<R> results = new ArrayList<>();

        for (final S src : srcList) {
            final R r = results.stream()
                    .filter(it -> targetUniqueKeyGetter.apply(it).equals(sourceUniqueKeyGetter.apply(src)))
                    .findFirst()
                    .orElseGet(() -> {
                        final R initR = rCreator.apply(src);
                        results.add(initR);
                        return initR;
                    });

            Map<MK, MV> map = mapGetter.apply(r);
            if (map == null) {
                final Map<MK, MV> initMap = new HashMap<>();
                mapSetter.accept(r, initMap);
                map = initMap;
            }
            final MK key = keyCreator.apply(src);
            map.put(key, valueCreator.apply(src, map.get(key)));
        }
        return results;
    }
}
