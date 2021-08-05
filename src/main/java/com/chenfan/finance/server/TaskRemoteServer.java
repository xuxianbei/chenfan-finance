package com.chenfan.finance.server;

import com.chenfan.common.vo.Response;
import com.chenfan.common.vo.TaskFlowVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author liulingqiong
 * @date 2019/1/8
 */
@FeignClient(name = "chenfan-cloud-efficiency")
public interface TaskRemoteServer {


    /**
     * 创建任务
     * @param taskFlowVo
     * @return taskFlowVo
     */
    @RequestMapping(value = "/taskflow/createTaskFlow",method = RequestMethod.POST)
    Response createTaskFlow(@RequestBody TaskFlowVo taskFlowVo);

    /**
     * 修改任务状态
     * @param taskFlowVo
     * @return taskFlowVo
     */
    @RequestMapping(value = "/taskflow/updateTaskFlow",method = RequestMethod.POST)
    Response updateTaskFlow(@RequestBody TaskFlowVo taskFlowVo);


    /**
     * 创建任务消息
     * @param taskFlowVo
     * @return
     */
    @PostMapping(value = "/taskflow/createTaskMsg")
    Response createTaskMsg(@RequestBody TaskFlowVo taskFlowVo);

}
