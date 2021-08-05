package com.chenfan.finance.controller;

import com.alibaba.fastjson.JSONObject;
import com.chenfan.common.config.Constant;
import com.chenfan.common.vo.BaseResultVO;
import com.chenfan.common.vo.Response;
import com.chenfan.common.vo.ResponseCode;
import com.chenfan.common.vo.UserVO;
import com.chenfan.finance.commons.annotation.Login;
import com.chenfan.finance.enums.ActualPaymentStateEnum;
import com.chenfan.finance.exception.ModuleBizState;
import com.chenfan.finance.model.CfBankAndCash;
import com.chenfan.finance.model.CfBankAndCashInvoiceExtend;
import com.chenfan.finance.model.dto.BankAndCashBatchDeleteDto;
import com.chenfan.finance.model.dto.BankAndCashDto;
import com.chenfan.finance.model.dto.CreateAndClearDto;
import com.chenfan.finance.model.vo.BankAndCashVo;
import com.chenfan.finance.model.vo.CfBankAndCashInfoVo;
import com.chenfan.finance.model.vo.CfBankAndCashListShowVo;
import com.chenfan.finance.model.vo.CfBankAndCashShowRequestVo;
import com.chenfan.finance.service.CfBankAndCashService;
import com.chenfan.finance.service.common.BankAndCashCommonService;
import com.chenfan.finance.utils.pageinfo.PageInfoUtil;
import com.chenfan.finance.utils.pageinfo.model.CreateVo;
import com.chenfan.privilege.common.config.SearchAuthority;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 财务-实收付款单Controller
 *
 * @param
 * @author zq
 * @date 2020/8/19 14:49
 * @return
 */
@RestController
@RequestMapping(value = "bank_and_cash")
@Api(tags = "财务-实收付款相关接口")
public class CfBankAndCashController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CfBankAndCashController.class);

    @Autowired
    private CfBankAndCashService actualPaymentService;

    @Autowired
    private BankAndCashCommonService bankAndCashCommonService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 新增
     *
     * @return com.chenfan.common.vo.Response<java.lang.Object>
     * @author zq
     * @date 2020/8/19 16:49
     */
    @ApiOperation(value = "财务-实收付款单新建", notes = "实收付款单新建")
    @PostMapping(value = "create")
    public Response<Object> create(@RequestBody CfBankAndCash apVo, @RequestHeader(Constant.AUTHORIZATION_TOKEN) String token) {
        LOGGER.info("进入方法>>实收付款新增...");
        UserVO user = JSONObject.parseObject(stringRedisTemplate.opsForValue().get(Constant.AUTHORIZATION + ":" + token), UserVO.class);
        if (null == user) {
            return new Response(ResponseCode.LOGIN_ERROR);
        }
        Integer i = actualPaymentService.create(apVo, user);
        if (i <= 0) {
            //新增失败
            return new Response(ResponseCode.SAVE_ERROR);
        }
        Long bankAndCashId = apVo.getBankAndCashId();
        CfBankAndCashInfoVo info = actualPaymentService.info(bankAndCashId);
        String recordSeqNo = info.getRecordSeqNo();
        //这里还需要将刚刚生成的单据号和id返回
        CfBankAndCash response = new CfBankAndCash();
        response.setBankAndCashId(bankAndCashId);
        response.setRecordSeqNo(recordSeqNo);

        LOGGER.info("退出方法>>实收付款新增...");
        //返回新增的id
        return new Response(ResponseCode.SAVE_SUCCESS, response);
    }


    /**
     * 创建实收付核销
     *
     * @param createAndClearDto
     * @return
     */
    @PostMapping("create/and/clear")
    public Response<Integer> createAndClear(@Validated(CreateAndClearDto.Clear.class) @RequestBody CreateAndClearDto createAndClearDto) {
        return PageInfoUtil.smartSuccess(actualPaymentService.createAndClear(createAndClearDto));
    }

    /**
     * 新增实收付
     *
     * @param createAndClearDto
     * @return
     */
    @PostMapping("add/mcn")
    public Response<Integer> add(@Validated(CreateAndClearDto.Add.class) @RequestBody CreateAndClearDto createAndClearDto) {
        Assert.isTrue("1".equals(createAndClearDto.getArapType()), ModuleBizState.DATE_ERROR.message());
        return PageInfoUtil.smartSuccess(Objects.nonNull(bankAndCashCommonService.add(createAndClearDto)) ? 1 : 0);
    }

    /**
     * 更新实收付
     *
     * @param createAndClearDto
     * @return
     */
    @PostMapping("update/mcn")
    public Response<Integer> updateMcn(@Validated(CreateAndClearDto.Add.class) @RequestBody CreateAndClearDto createAndClearDto) {
        Assert.isTrue(Objects.nonNull(createAndClearDto.getBankAndCashId()) && "1".equals(createAndClearDto.getArapType()), ModuleBizState.DATE_ERROR.message());
        return PageInfoUtil.smartSuccess(bankAndCashCommonService.update(createAndClearDto));
    }

    /**
     * 新增实收付且提交
     *
     * @param createAndClearDto
     * @return
     */
    @PostMapping("add/commit")
    public Response<Integer> addCommit(@Validated(CreateAndClearDto.Add.class) @RequestBody CreateAndClearDto createAndClearDto) {
        return PageInfoUtil.smartSuccess(Objects.nonNull(bankAndCashCommonService.addCommit(createAndClearDto)) ? 1 : 0);
    }

    /**
     * 更改状态
     *
     * @param bankAndCashId
     * @param status        状态1=草稿（待提交）, 2=已入账（待核销） 3=部分核销  4=已核销,5=作废，0=已删除, 6审批中，7审批拒绝，8已撤回
     * @return
     */
    @GetMapping("commit")
    public Response<Integer> updateState(@RequestParam Long bankAndCashId, @RequestParam Integer status) {
        return PageInfoUtil.smartSuccess(bankAndCashCommonService.commit(bankAndCashId, status));
    }

    /**
     * 批量删除
     * @param bankAndCashBatchDeleteDto
     * @return
     */
    @PostMapping("batch/delete")
    public Response<Integer> batchDelete(@Validated @RequestBody BankAndCashBatchDeleteDto bankAndCashBatchDeleteDto) {
        return PageInfoUtil.smartSuccess(bankAndCashCommonService.batchDelete(bankAndCashBatchDeleteDto));
    }

    /**
     * Excel导入
     *
     * @param multipartFile
     * @return
     */
    @PostMapping("excel/import")
    public Response<Integer> excelImport(@RequestParam MultipartFile multipartFile) {
        return PageInfoUtil.smartSuccess(bankAndCashCommonService.excelImport(multipartFile));
    }

    /**
     * 下载失败原因
     *
     * @param response
     * @throws Exception
     */
    @GetMapping("excel/output")
    public void excelOutput(HttpServletResponse response) throws Exception {
        bankAndCashCommonService.excelOutput(response);
    }


    /**
     * 批量删除（伪）
     *
     * @return com.chenfan.common.vo.Response<java.lang.Object>
     * @author zq
     * @date 2020/8/19 16:51
     */
    @ApiOperation(value = "财务-实收付款单批量删除", notes = "财务-实收付款单批量删除")
    @PostMapping(value = "delete_batch")
    public Response<Object> delete(@RequestBody CfBankAndCashShowRequestVo apVo, @RequestHeader(Constant.AUTHORIZATION_TOKEN) String token) {
        LOGGER.info("进入方法>>实收付款删除...");
        UserVO user = JSONObject.parseObject(stringRedisTemplate.opsForValue().get(Constant.AUTHORIZATION + ":" + token), UserVO.class);
        if (null == user) {
            return new Response(ResponseCode.LOGIN_ERROR);
        }
        if (null == apVo || CollectionUtils.isEmpty(apVo.getRecordSeqNos())) {
            return new Response(500, "收付款单号不能为空");
        }
        List<String> recordSeqNos = apVo.getRecordSeqNos();
        //批量删除时，拿到每一个单子状态值，如果有已审核或待审核的则整体删除失败
        for (String pcs : recordSeqNos) {
            CfBankAndCashInfoVo infoByCode = actualPaymentService.getInfoByCode(pcs);
            Integer status = infoByCode.getBankAndCashStatus();
            if (ActualPaymentStateEnum.CANCELLED.getState() != status) {
                return new Response(500, "只有作废状态的单据可以删除，请重新选择");
            }
        }
        int i = actualPaymentService.delete(apVo);
        if (i <= 0) {
            //删除失败
            return new Response(ResponseCode.DELETE_ERROR);
        }
        LOGGER.info("退出方法>>实收付款删除...");
        return new Response(ResponseCode.BATCH_DELETE_SUCCESS);
    }

    /**
     * 列表
     *
     * @param
     * @return com.chenfan.common.vo.Response<java.lang.Object>
     * @author zq
     * @date 2020/8/19 16:51
     */
    @ApiOperation(value = "财务-实收付款单列表查询", notes = "财务-实收付款单列表查询")
    @PostMapping(value = "getList")
    public Response<BaseResultVO<List<CfBankAndCashListShowVo>>> getList(@SearchAuthority CfBankAndCashShowRequestVo apsVo) {
        LOGGER.info("进入方法>>实收付款列表查询...");
        LOGGER.info("获取的关联的brandIds:{},companyIds:{},userIds:{}", apsVo.getBrandIds(),
                apsVo.getCompanyIds(), apsVo.getUserIds());
        if (Objects.isNull(apsVo.getJobType())) {
            apsVo.setJobTypes(Arrays.asList(1, 2));
        } else {
            apsVo.setJobTypes(Arrays.asList(apsVo.getJobType()));
        }
        List<CfBankAndCashListShowVo> list = actualPaymentService.getList(apsVo);
        int count = actualPaymentService.getCount(apsVo);
        LOGGER.info("退出方法>>实收付款列表查询...");
        return new Response(ResponseCode.SUCCESS, new BaseResultVO<>(list, count));
    }

    /**
     * 详情
     *
     * @return com.chenfan.common.vo.Response<java.lang.Object>
     * @author zq
     * @date 2020/8/19 16:52
     */
    @ApiOperation(value = "财务-实收付款单详情", notes = "财务-实收付款单详情")
    @GetMapping(value = "info")
    public Response<CfBankAndCashInfoVo> getInfo(Long bankAndCashId) {
        LOGGER.info("进入方法>>实收付款详情...");
        if (null == bankAndCashId) {
            return new Response<>(500, "收付款单id不能为空!");
        }
        CfBankAndCashInfoVo info = actualPaymentService.info(bankAndCashId);
        LOGGER.info("退出方法>>实收付款详情...");
        return new Response(ResponseCode.SUCCESS, info);
    }

    /**
     * 编辑
     *
     * @return com.chenfan.common.vo.Response<java.lang.Object>
     * @author zq
     * @date 2020/8/19 16:52
     */
    @ApiOperation(value = "财务-实收付款单编辑", notes = "财务-实收付款单编辑")
    @PostMapping(value = "update")
    public Response<Object> update(@RequestBody CfBankAndCash ap, @RequestHeader(Constant.AUTHORIZATION_TOKEN) String token) {
        LOGGER.info("进入方法>>实收付款编辑...");
        UserVO user = JSONObject.parseObject(stringRedisTemplate.opsForValue().get(Constant.AUTHORIZATION + ":" + token), UserVO.class);
        if (null == user) {
            return new Response(ResponseCode.LOGIN_ERROR);
        }
        if (null == ap) {
            return new Response<>(500, "参数不可以为空");
        }
        int update = actualPaymentService.update(ap, user);
        if (update <= 0) {
            return new Response<>(500, "修改失败");
        }
        LOGGER.info("退出方法>>实收付款编辑...");
        return new Response(ResponseCode.UPDATE_SUCCESS);
    }

    /**
     * 审核流
     *
     * @return com.chenfan.common.vo.Response<java.lang.Object>
     * @author zq
     * @date 2020/8/19 16:52
     */
    @ApiOperation(value = "财务-实收付款单审核流", notes = "财务-实收付款单审核流")
    @PostMapping(value = "review_flow")
    public Response<Object> review(@RequestBody CfBankAndCash payment, @RequestHeader(Constant.AUTHORIZATION_TOKEN) String token) {
        LOGGER.info("进入方法>>实收付款审核...");
        /**
         * 单据状态 1草稿 2已入账（待核销） 3部分核销 4已核销 5作废 0已删除(隐藏)
         * 默认1
         */
        //单据审核结果信息
        Integer review = 0;
        UserVO user = JSONObject.parseObject(stringRedisTemplate.opsForValue().get(Constant.AUTHORIZATION + ":" + token), UserVO.class);
        if (null == user) {
            return new Response(ResponseCode.LOGIN_ERROR);
        }
        if (null == payment) {
            return new Response(500, "数据不可为空");
        }
        synchronized (this) {
            review = actualPaymentService.review(payment, user);
        }
        LOGGER.info("退出方法>>实收付款审核...");
        return new Response(ResponseCode.UPDATE_SUCCESS, review);
    }

    /**
     * 导出
     *
     * @return com.chenfan.common.vo.Response<java.lang.Object>
     * @author zq
     * @date 2020/8/19 16:53
     */
    @ApiOperation(value = "财务-实收付款单导出", notes = "财务-实收付款单导出")
    @PostMapping(value = "export")
    public Response<Object> export(@RequestHeader(Constant.AUTHORIZATION_TOKEN) String token, @SearchAuthority CfBankAndCashShowRequestVo apsVo,
                                   HttpServletResponse response) {
        LOGGER.info("进入方法>>实收付款导出...");
        UserVO userVO = JSONObject.parseObject(stringRedisTemplate.opsForValue().get(Constant.AUTHORIZATION + ":" + token), UserVO.class);
        if (null == userVO) {
            return new Response<>(Constant.UNAUTHORIZED, "用户登录失效");
        }
        try {
            actualPaymentService.export(apsVo, userVO, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOGGER.info("退出方法>>实收付款导出...");
        return new Response(ResponseCode.EXPORT_SUCCESS);
    }


    @ApiOperation(value = "账单创建实收付进行核销")
    @PostMapping(value = "createBankCashAndClear")
    @Login
    public Response<Object> createBankCashAndClear(@RequestBody CfBankAndCashInvoiceExtend apVo, @ApiIgnore UserVO userVO, @RequestHeader(value = Constant.AUTHORIZATION_TOKEN) String token) {
        LOGGER.info("进入方法>>账单创建实收付进行核销...");
        actualPaymentService.createBankCashAndClear(apVo, userVO);
        LOGGER.info("退出方法>>账单创建实收付进行核销...");
        return new Response(ResponseCode.SAVE_SUCCESS);
    }

    /**
     * MCN列表
     *
     * @param bankAndCashDto
     * @return
     */
    @PostMapping("list")
    public Response<PageInfo<BankAndCashVo>> list(@SearchAuthority @RequestBody BankAndCashDto bankAndCashDto) {
        LOGGER.info("获取的关联的brandIds:{},companyIds:{},userIds:{}", bankAndCashDto.getBrandIds(),
                bankAndCashDto.getCompanyIds(), bankAndCashDto.getUserIds());
        return PageInfoUtil.smartSuccess(actualPaymentService.list(bankAndCashDto));
    }

    /**
     * 交易方公司名称列表
     *
     * @return
     */
    @GetMapping("/payment/branch")
    public Response<List<String>> getPaymentBranch(String paymentBranch) {
        return PageInfoUtil.smartSuccess(bankAndCashCommonService.getPaymentBranch(paymentBranch));
    }


    /**
     * 创建人
     *
     * @return
     */
    @GetMapping("/createNameList")
    public Response<List<CreateVo>> createNameList() {
        return PageInfoUtil.smartSuccess(bankAndCashCommonService.createNameList());
    }
}
