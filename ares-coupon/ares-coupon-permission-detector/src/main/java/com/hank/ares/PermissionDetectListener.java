package com.hank.ares;

import com.hank.ares.feign.client.coupon.permission.PermissionServiceClient;
import com.hank.ares.model.dto.PermissionInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;

/**
 * 权限探测监听器, Spring 容器启动之后自动运行
 */
@Slf4j
@Component
public class PermissionDetectListener implements ApplicationListener<ApplicationReadyEvent> {

    private static final String KEY_SERVER_CTX = "server.servlet.context-path";
    private static final String KEY_SERVICE_NAME = "spring.application.name";

    /**
     * 自线程扫描并注册权限
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

        ApplicationContext ctx = event.getApplicationContext();

        new Thread(() -> {
            registerPermission(scanPermission(ctx), ctx);
        }).start();
    }

    /**
     * 扫描微服务中的 Controller 接口权限信息
     */
    private List<PermissionInfo> scanPermission(ApplicationContext ctx) {

        // 取出 context 前缀
        String contextPath = ctx.getEnvironment().getProperty(KEY_SERVER_CTX);

        // 取出 Spring 的映射 bean
        RequestMappingHandlerMapping mappingBean = (RequestMappingHandlerMapping) ctx.getBean("requestMappingHandlerMapping");

        // 扫描权限
        List<PermissionInfo> permissionInfoList = new AnnotationScanner(contextPath).scanPermission(mappingBean.getHandlerMethods());

        permissionInfoList.forEach(p -> log.info("{}", p));
        log.info("{} permission found", permissionInfoList.size());
        log.info("*************** done scanning ***************");

        return permissionInfoList;
    }

    /**
     * 注册接口权限
     */

    private void registerPermission(List<PermissionInfo> infoList, ApplicationContext ctx) {

        log.info("*************** permission register start ***************");

        PermissionServiceClient permissionClient = ctx.getBean(PermissionServiceClient.class);
        if (permissionClient == null) {
            log.error("no permissionClient bean found");
            return;
        }

        // 取出 服务名
        String serviceName = ctx.getEnvironment().getProperty(KEY_SERVICE_NAME);

        log.info("serviceName: {}", serviceName);

        new PermissionRegistry(permissionClient, serviceName).register(infoList);

        log.info("*************** permission register done ***************");
    }
}
