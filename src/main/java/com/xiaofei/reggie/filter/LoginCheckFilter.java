package com.xiaofei.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.xiaofei.reggie.common.BaseContext;
import com.xiaofei.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 记得在启动类中加@ServletComponentScan
 * 不然无法扫描到该拦截器
 */
@WebFilter(filterName = "loginCheckFilter" , urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher matcher = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1.获取本次请求的URI
        String requestURI = request.getRequestURI();

        //可以直接放行的路径
        String[] urls = new String[]{
            "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg",
                "/user/login"
        };

        //2.判断本次请求是否需要处理
        boolean check = check(urls, requestURI);
        log.info("本次请求URI为 =====> {}",requestURI);

        //3.如果不需要处理，则直接放行
        if(check){
            log.info("本次请求不需要处理");
            filterChain.doFilter(request,response);
            return;
        }

        //4-1.判断登录状态，如果已经登录，则直接放行
        if(request.getSession().getAttribute("employee") != null){
            log.info("用户已经登录");

            Long id = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(id);
            filterChain.doFilter(request,response);
            return;
        }

        //4-2.判断登录状态，如果已经登录，则直接放行
        if(request.getSession().getAttribute("user") != null){
            log.info("用户已经登录");

            Long id = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(id);
            filterChain.doFilter(request,response);
            return;
        }

        //5.如果未登录则返回未登录结果,通过输出流方式，向客户端进行响应数据
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     *将本次请求的URI与urls数组进行匹配，若匹配得上则放行
     * @param urls
     * @param requestURI
     * @return
     */
    public static boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            boolean match = matcher.match(url, requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }
}
