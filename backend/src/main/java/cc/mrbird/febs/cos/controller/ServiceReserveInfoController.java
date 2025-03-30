package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.ServiceReserveInfo;
import cc.mrbird.febs.cos.entity.UserInfo;
import cc.mrbird.febs.cos.service.IServiceReserveInfoService;
import cc.mrbird.febs.cos.service.IUserInfoService;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author FanK
 */
@RestController
@RequestMapping("/cos/service-reserve-info")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ServiceReserveInfoController {

    private final IServiceReserveInfoService serviceReserveInfoService;

    private final IUserInfoService userInfoService;

    /**
     * 分页获取服务预约信息
     *
     * @param page               分页对象
     * @param serviceReserveInfo 服务预约信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<ServiceReserveInfo> page, ServiceReserveInfo serviceReserveInfo) {
        return R.ok(serviceReserveInfoService.querySerciceReservePage(page, serviceReserveInfo));
    }

    /**
     * 分页获取服务预约信息
     *
     * @param page               分页对象
     * @param serviceReserveInfo 服务预约信息
     * @return 结果
     */
    @GetMapping("/queryOwnerServicePage")
    public R queryOwnerServicePage(Page<ServiceReserveInfo> page, ServiceReserveInfo serviceReserveInfo) {
        return R.ok(serviceReserveInfoService.queryOwnerServicePage(page, serviceReserveInfo));
    }

    /**
     * 分页获取服务预约信息
     *
     * @param page               分页对象
     * @param serviceReserveInfo 服务预约信息
     * @return 结果
     */
    @GetMapping("/queryWorkerServicePage")
    public R queryWorkerServicePage(Page<ServiceReserveInfo> page, ServiceReserveInfo serviceReserveInfo) {
        return R.ok(serviceReserveInfoService.queryWorkerServicePage(page, serviceReserveInfo));
    }

    /**
     * 获取未接单的订单
     *
     * @return 结果
     */
    @GetMapping("/queryNotCheckOrder")
    public R queryNotCheckOrder(@RequestParam("userId") Integer userId, @RequestParam(value = "key", required = false) String key) {
        return R.ok(serviceReserveInfoService.queryNotCheckOrder(userId, key));
    }

    /**
     * 作业人员接单
     *
     * @param workId  作业人员ID
     * @param orderId 订单ID
     * @return 结果
     */
    @GetMapping("/workOrderCheck")
    public R workOrderCheck(@RequestParam("workId") Integer workId, @RequestParam("orderId") Integer orderId) {
        return R.ok(serviceReserveInfoService.workOrderCheck(workId, orderId));
    }

    /**
     * 作业人员完成订单
     *
     * @param orderId 订单ID
     * @return 结果
     */
    @GetMapping("/wordOrderFinish")
    public R wordOrderFinish(@RequestParam("orderId") Integer orderId) {
        return R.ok(serviceReserveInfoService.update(Wrappers.<ServiceReserveInfo>lambdaUpdate().set(ServiceReserveInfo::getStatus, "3").eq(ServiceReserveInfo::getId, orderId)));
    }

    /**
     * 支付完成后回调
     *
     * @param orderCode 订单编号
     * @return 结果
     */
    @GetMapping("/callbackPayment")
    public R callbackPayment(@RequestParam("orderCode") String orderCode) {
        // 根据订单编号获取订单信息
        ServiceReserveInfo serviceReserveInfo = serviceReserveInfoService.getOne(Wrappers.<ServiceReserveInfo>lambdaQuery().eq(ServiceReserveInfo::getCode, orderCode));
        serviceReserveInfo.setStatus("1");
        return R.ok(serviceReserveInfoService.updateById(serviceReserveInfo));
    }

    /**
     * 服务预约信息详情
     *
     * @param id 服务预约ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(serviceReserveInfoService.getDetail(id));
    }

    /**
     * 服务预约信息列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(serviceReserveInfoService.list());
    }

    /**
     * 新增服务预约信息
     *
     * @param serviceReserveInfo 服务预约信息
     * @return 结果
     */
    @PostMapping
    public R save(ServiceReserveInfo serviceReserveInfo) {
        // 获取所属用户
        UserInfo userInfo = userInfoService.getOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getUserId, serviceReserveInfo.getUserId()));
        if (userInfo != null) {
            serviceReserveInfo.setUserId(userInfo.getId());
        }
        // 设置订单编号
        serviceReserveInfo.setCode("OR-" + System.currentTimeMillis());
        // 订单状态
        serviceReserveInfo.setStatus("0");
        serviceReserveInfo.setCreateDate(DateUtil.formatDateTime(new Date()));
        return R.ok(serviceReserveInfoService.save(serviceReserveInfo));
    }

    /**
     * 修改服务预约信息
     *
     * @param serviceReserveInfo 服务预约信息
     * @return 结果
     */
    @PutMapping
    public R edit(ServiceReserveInfo serviceReserveInfo) {
        return R.ok(serviceReserveInfoService.updateById(serviceReserveInfo));
    }

    /**
     * 删除服务预约信息
     *
     * @param ids ids
     * @return 服务预约信息
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(serviceReserveInfoService.removeByIds(ids));
    }
}
