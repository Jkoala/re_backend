package cn.lingjiatong.re.common.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * openfeign请求拦截器，用于处理一些openfeign调用丢失token等问题
 *
 * @author Ling, Jiatong
 * Date: 2022/11/4 17:09
 */
@Slf4j
public class FeignBasicAuthRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        // 放行re-job相关的请求
        String path = requestTemplate.path();
        if (path.contains("/schedule")) {
            // 定时任务属于非web调用，所以直接跳过
            return;
        }
        // 对于所有请求设置请求头
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                // 跳过 content-length，解决too many bites written的问题
                if (name.equalsIgnoreCase("content-length")){
                    continue;
                }
                String values = request.getHeader(name);
                requestTemplate.header(name, values);
            }
        }
    }
}
