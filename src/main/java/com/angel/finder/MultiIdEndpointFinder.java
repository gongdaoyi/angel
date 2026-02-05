package com.angel.finder;

import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// 启动时拉出项目中接口入参包含指定字段的接口清单
@Log4j2
public class MultiIdEndpointFinder {

    private final ApplicationContext applicationContext;
    private final Set<String> targetParamNames;
    private final Pattern camelToSnake = Pattern.compile("([a-z0-9])([A-Z])");

    public MultiIdEndpointFinder(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.targetParamNames = Stream.of("clientId", "acptId", "fundAccount", "idNo", "client_id", "acpt_id", "fund_account", "id_no").collect(Collectors.toSet());
    }

    public List<Map<String, Object>> findEndpointsWithTargetParams() {
        List<Map<String, Object>> endpoints = new ArrayList<>();
        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = mapping.getHandlerMethods();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();
            Method method = handlerMethod.getMethod();

            // 获取匹配的参数名
            Set<String> matchedParams = findMatchedParameters(method);
            if (!matchedParams.isEmpty()) {
                Map<String, Object> endpoint = new LinkedHashMap<>();

                // 这是接口的方法名
                String methodName = method.getName();

                // 这是controller文件的名字
                String controllerName = method.getDeclaringClass().getSimpleName();
                endpoint.put("controller", controllerName);

                // 这是接口的url
                RequestMappingInfo mappingInfo = entry.getKey();
                String path = mappingInfo.getPatternsCondition().getPatterns().stream()
                        .findFirst()
                        .orElse("");
                endpoint.put("url", path);

                // 接口的中文名
                endpoint.put("chineseName", getChineseName(method));

                // 接口入参中：匹配到的参数
                endpoint.put("matchedParams", matchedParams);

                endpoints.add(endpoint);
            }
        }

        // 按Controller名称排序
        endpoints.sort(Comparator.comparing(e -> e.get("controller").toString()));
        return endpoints;
    }

    private Set<String> findMatchedParameters(Method method) {
        Set<String> matchedParams = new HashSet<>();

        for (Parameter parameter : method.getParameters()) {
            // 检查参数名
            String paramName = safelyGetParameterName(parameter);
            if (paramName != null) {
                String matchedName = checkParamNameMatch(paramName);
                if (matchedName != null) {
                    matchedParams.add(matchedName);
                    continue;
                }
            }

            // 检查注解中的值
            String annotatedName = checkAnnotationMatch(parameter);
            if (annotatedName != null) {
                matchedParams.add(annotatedName);
                continue;
            }

            // 检查DTO中的字段
            if (parameter.getAnnotation(RequestBody.class) != null) {
                matchedParams.addAll(checkDtoFieldsMatch(parameter.getType()));
            }
        }

        return matchedParams;
    }

    private String safelyGetParameterName(Parameter parameter) {
        try {
            String name = parameter.getName();
            // 检查是否是合成名称(如arg0, arg1)
            if (name != null && name.startsWith("arg")) {
                return null;
            }
            return name;
        } catch (Exception e) {
            return null;
        }
    }

    private String checkParamNameMatch(String paramName) {
        // 直接匹配
        if (targetParamNames.contains(paramName)) {
            return paramName;
        }

        // 下划线格式匹配
        String snakeCase = camelToSnake(paramName);
        if (targetParamNames.contains(snakeCase)) {
            return snakeCase;
        }

        return null;
    }

    private String camelToSnake(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return camelToSnake.matcher(str).replaceAll("$1_$2").toLowerCase();
    }

    private String checkAnnotationMatch(Parameter parameter) {
        for (Annotation annotation : parameter.getAnnotations()) {
            if (annotation instanceof RequestParam) {
                String value = ((RequestParam) annotation).value();
                if (!value.isEmpty() && targetParamNames.contains(value)) {
                    return value;
                }
            } else if (annotation instanceof RequestHeader) {
                String value = ((RequestHeader) annotation).value();
                if (!value.isEmpty() && targetParamNames.contains(value)) {
                    return value;
                }
            } else if (annotation instanceof PathVariable) {
                String value = ((PathVariable) annotation).value();
                if (!value.isEmpty() && targetParamNames.contains(value)) {
                    return value;
                }
            }
        }
        return null;
    }

    private Set<String> checkDtoFieldsMatch(Class<?> dtoClass) {
        Set<String> matchedFields = new HashSet<>();
        if (dtoClass.isPrimitive() || dtoClass.equals(String.class)) {
            return matchedFields;
        }

        try {
            for (java.lang.reflect.Field field : dtoClass.getDeclaredFields()) {
                String fieldName = field.getName();
                if (checkParamNameMatch(fieldName) != null) {
                    matchedFields.add(fieldName);
                }
            }
        } catch (Exception e) {
            // 忽略异常
        }

        return matchedFields;
    }

    private String getChineseName(Method method) {
        // 1. 检查方法上的@ApiDescription注解
        ApiOperation apiDesc = method.getAnnotation(ApiOperation.class);
        if (apiDesc != null && !apiDesc.value().isEmpty()) {
            return apiDesc.value();
        }

        // 2. 检查类上的@ApiDescription注解
        apiDesc = method.getDeclaringClass().getAnnotation(ApiOperation.class);
        if (apiDesc != null && !apiDesc.value().isEmpty()) {
            return apiDesc.value();
        }

        // 3. 返回默认值
        return "未命名接口";
    }
}
