package cn.javaer.snippetsbox.jooq;

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
    public boolean supports(@NonNull MethodParameter methodParameter, @NonNull Type targetType,
                            @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return TypeUtils.isAssignable(UpdatableRecord.class, targetType);
    }

    @Override
    @NonNull
    public Object afterBodyRead(
            @NonNull Object body, HttpInputMessage inputMessage, MethodParameter methodParameter,
            Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {

        RecordAttach recordAttach = methodParameter.getParameterAnnotation(RecordAttach.class);
        UpdatableRecord<?> updatableRecord = (UpdatableRecord<?>) body;
        if (null == recordAttach || recordAttach.value().isEmpty()) {
            updatableRecord.attach(Objects.requireNonNull(primaryConfiguration));
        }
        else {
            updatableRecord.attach(Objects.requireNonNull(configurationMap.get(recordAttach.value())));
        }

        return body;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        Pair<Optional<Configuration>, Optional<Map<String, Configuration>>> pair = BeanUtils.getJooqConfiguration(beanFactory);
        primaryConfiguration = pair.getFirst().orElse(null);
        configurationMap = pair.getSecond().orElse(null);
    }
}
