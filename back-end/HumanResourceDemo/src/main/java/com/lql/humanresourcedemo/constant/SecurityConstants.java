package com.lql.humanresourcedemo.constant;

import com.lql.humanresourcedemo.controller.LoginController;
import com.lql.humanresourcedemo.controller.ResetPasswordController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SecurityConstants {
    public static final List<String> PUBLIC_URI = new ArrayList<>();

    static {


        PUBLIC_URI.addAll(List.of("/login", "/sign-out"));
        PUBLIC_URI.add("/oauth2/authorization/google");
        PUBLIC_URI.add(ResetPasswordController.class.getAnnotation(RequestMapping.class).value()[0]);
        PUBLIC_URI.add("/swagger-ui/**");
        PUBLIC_URI.add("/swagger-ui.html");
        PUBLIC_URI.add("/v3/api-docs/**");
    }



//    private static List<String> getAllRequestMapping(Class<?> clazz) {
//        RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
//        List<String> classRequestMapping = requestMapping != null ? List.of(requestMapping.value()) : null;
//
//        List<String> methodsRequestMapping = Arrays.stream(clazz.getDeclaredMethods())
//                .filter(method -> method.getAnnotation(RequestMapping.class) != null)
//                .map(method -> method.getAnnotation(RequestMapping.class))
//                .flatMap(rq -> Arrays.stream(rq.value()))
//                .toList();
//        if (classRequestMapping != null) {
//            return classRequestMapping.stream().flatMap(re -> methodsRequestMapping.stream()
//                    .map(re::concat)).toList();
//        }
//        return methodsRequestMapping;
//    }
}
