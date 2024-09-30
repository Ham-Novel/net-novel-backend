package com.ham.netnovel.common.OAuth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final RequestCache requestCache = new HttpSessionRequestCache();


    //TODO 리다이렉트 URL 도메인으로 변경 필수!!!
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
//        SavedRequest savedRequest = requestCache.getRequest(request, response);

        String redirectUrl = request.getSession().getAttribute("redirectUrl") != null
                ? (String) request.getSession().getAttribute("redirectUrl")
                : "http://localhost:5173/";//

        // 로그인 성공 시 세션에 저장된 URL로 리다이렉트
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}




//}
