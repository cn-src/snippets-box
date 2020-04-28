package cn.javaer.snippets.box.spring.jooq;

import org.jooq.Configuration;
import org.jooq.UpdatableRecord;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.core.MethodParameter;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.util.TypeUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author cn-src
 */
@ControllerAdvice
public class JooqRecordAttachAdvice extends RequestBodyAdviceAdapter implements BeanFactoryAware {

    private Configuration primaryConfiguration;
    private Map<String, Configuration> configurationMap;

    @Override
    public boolean supports(@NonNull final MethodParameter methodParameter, @NonNull final Type targetType,
                            @NonNull final Class<? extends HttpMessageConverter<?>> converterType) {
        return TypeUtils.isAssignable(UpdatableRecord.class, targetType);
    }

    @Override
    @NonNull
    public Object afterBodyRead(
            @NonNull final Object body, final HttpInputMessage inputMessage, final MethodParameter methodParameter,
            final Type targetType, final Class<? extends HttpMessageConverter<?>> converterType) {

        final RecordAttach recordAttach = methodParameter.getParameterAnnotation(RecordAttach.class);
        final UpdatableRecord<?> updatableRecord = (UpdatableRecord<?>) body;
        if (null == recordAttach || recordAttach.value().isEmpty()) {
            updatableRecord.attach(Objects.requireNonNull(this.primaryConfiguration));
        }
        else {
            updatableRecord.attach(Objects.requireNonNull(this.configurationMap.get(recordAttach.value())));
        }

        return body;
    }

    @Override
    public void setBeanFactory(final BeanFactory beanFactory) {
        final Pair<Optional<Configuration>, Optional<Map<String, Configuration>>> pair = JooqUtils.getJooqConfiguration(beanFactory);
        this.primaryConfiguration = pair.getFirst().orElse(null);
        this.configurationMap = pair.getSecond().orElse(null);
    }
}
