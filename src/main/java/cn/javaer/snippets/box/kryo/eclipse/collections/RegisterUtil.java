package cn.javaer.snippets.box.kryo.eclipse.collections;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

/**
 * @author cn-src
 */
class RegisterUtil {
    private RegisterUtil() {}

    static void register(final Kryo kryo, final Serializer<?> serializer, final Class<?> interfaceClass, final String... scanPackage) {
        try (final ScanResult scanResult = new ClassGraph().enableClassInfo().ignoreClassVisibility().whitelistPackages(scanPackage)
                .scan()) {
            final ClassInfoList classInfos = scanResult.getClassesImplementing(interfaceClass.getName())
                    .filter(it -> !it.isAbstract() && !it.isInterface());

            for (final Class<?> clazz : classInfos.loadClasses()) {
                kryo.register(clazz, serializer);
            }
        }
    }
}
