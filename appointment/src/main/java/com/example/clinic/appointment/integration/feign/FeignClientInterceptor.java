package com.example.clinic.appointment.integration.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
//        String token = getToken();
//        if (token != null) {
//            requestTemplate.header("Authorization", "Bearer " + token);
//        }
    }

//    public String getToken() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.getCredentials() instanceof String) {
//            return (String) authentication.getCredentials();
//        }
//        return null;
//    }
}
