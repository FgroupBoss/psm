package com.fgroupboss.ai.psm.identity.notification.controller;

import com.fgroupboss.ai.psm.common.LoginContext;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContext;
import com.fgroupboss.ai.psm.identity.notification.model.dto.NotificationSendRequest;
import com.fgroupboss.ai.psm.identity.notification.model.vo.NotificationChannelStatusVO;
import com.fgroupboss.ai.psm.identity.notification.model.vo.NotificationHealthVO;
import com.fgroupboss.ai.psm.identity.notification.model.vo.NotificationMessageVO;
import com.fgroupboss.ai.psm.identity.notification.model.vo.NotificationUnreadCountVO;
import com.fgroupboss.ai.psm.identity.notification.service.NotificationService;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Notification 模块 HTTP API。
 * <p>基础路径：{@code /api/notifications}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；租户与用户 ID 从登录上下文（X-PSM-* 头）解析，不接受前端 query 传参。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 服务健康检查。
     * <p>HTTP GET {@code /api/notifications/health}</p>
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/health")
    public ResponseVO<NotificationHealthVO> health() {
        return ResponseVO.success(notificationService.health());
    }

    /**
     * 新增send或触发send相关动作。
     * <p>HTTP POST {@code /api/notifications/send}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/send")
    public ResponseVO<NotificationMessageVO> send(@LoginContext UserContext loginContext,
                                                  @Valid @RequestBody NotificationSendRequest request) {
        request.setTenantId(loginContext.getTenantId());
        request.setUserId(loginContext.getUserId());
        return ResponseVO.success(notificationService.send(request));
    }

    /**
     * 查询inbox。
     * <p>HTTP GET {@code /api/notifications/inbox}</p>
     * <p>所有查询与变更均按租户隔离。写操作从请求头解析操作人并写入审计字段。</p>
     * @param loginContext 当前登录租户与用户（由网关/身份域注入请求头）
     * @param read read 参数
     * @param bizType 业务类型过滤（可选）
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/inbox")
    public ResponseVO<PageResult<NotificationMessageVO>> inbox(@LoginContext UserContext loginContext,
                                                               @RequestParam(required = false) Boolean read,
                                                               @RequestParam(required = false) String bizType,
                                                               @RequestParam(defaultValue = "1") int pageNo,
                                                               @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(notificationService.inbox(
                loginContext.getTenantId(), loginContext.getUserId(), read, bizType, pageNo, pageSize));
    }

    /**
     * 未读消息数量（角标）。
     * <p>HTTP GET {@code /api/notifications/unread-count}</p>
     *
     * @param loginContext 当前登录租户与用户
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/unread-count")
    public ResponseVO<NotificationUnreadCountVO> unreadCount(@LoginContext UserContext loginContext) {
        return ResponseVO.success(notificationService.unreadCount(
                loginContext.getTenantId(), loginContext.getUserId()));
    }

    /**
     * 外通道配置状态（不含密钥）。
     * <p>HTTP GET {@code /api/notifications/channels/status}</p>
     *
     * @return 各通道 enabled/mode/是否已配置 HTTP 网关，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/channels/status")
    public ResponseVO<List<NotificationChannelStatusVO>> channelStatus() {
        return ResponseVO.success(notificationService.channelStatus());
    }

    /**
     * 查询作业票详情。
     * <p>HTTP GET {@code /api/notifications/{id}}</p>
     * <p>所有查询与变更均按租户隔离。写操作从请求头解析操作人并写入审计字段。</p>
     * @param id 资源主键 ID
     * @param loginContext 当前登录租户与用户
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}")
    public ResponseVO<NotificationMessageVO> detail(@PathVariable Long id,
                                                    @LoginContext UserContext loginContext) {
        return ResponseVO.success(notificationService.get(
                loginContext.getTenantId(), loginContext.getUserId(), id));
    }

    /**
     * 新增read或触发read相关动作。
     * <p>HTTP POST {@code /api/notifications/{id}/read}</p>
     * <p>所有查询与变更均按租户隔离。写操作从请求头解析操作人并写入审计字段。</p>
     * @param id 资源主键 ID
     * @param loginContext 当前登录租户与用户
     * @return 无业务载荷（成功即可），统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/read")
    public ResponseVO<Void> markRead(@PathVariable Long id,
                                     @LoginContext UserContext loginContext) {
        notificationService.markRead(loginContext.getTenantId(), loginContext.getUserId(), id);
        return ResponseVO.success(null);
    }

    /**
     * 新增read all或触发read all相关动作。
     * <p>HTTP POST {@code /api/notifications/read-all}</p>
     * <p>所有查询与变更均按租户隔离。写操作从请求头解析操作人并写入审计字段。</p>
     * @param loginContext 当前登录租户与用户
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/read-all")
    public ResponseVO<Integer> markAllRead(@LoginContext UserContext loginContext) {
        return ResponseVO.success(notificationService.markAllRead(
                loginContext.getTenantId(), loginContext.getUserId()));
    }
}
