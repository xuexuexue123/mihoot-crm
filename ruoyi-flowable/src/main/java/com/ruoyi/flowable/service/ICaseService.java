package com.ruoyi.flowable.service;

import com.alibaba.fastjson2.JSONArray;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.ems.domain.SysCaseinstance;
import com.ruoyi.ems.domain.SysReActitem;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.task.api.Task;

import java.util.List;
import java.util.Map;

public interface ICaseService {
    //
    int create(String caseTypeId) throws Exception;

//    void next(int id);


    /**
     * 根据caseDefId来创建一个case的实例
     * @param caseDefId
     * @param variables
     * @return
     */
    AjaxResult startCaseInstanceById(String caseDefId, String caseId, Map<String, Object> variables);


    /**
     * 根据instanceId获取case的信息
     * @param instanceId
     * @return
     */
    List<Task> queryListByInstanceId(String instanceId);

    /**
     * 结束case实例
     *
     * @param vo
     */
//    void stopCaseInstance(OrderVo vo);


    /**
     * 当流程完成后，执行以下操作
     * @param pyCaseId
     * @return
     */
    void postActionAfterProcessCompleted(String pyCaseId, String flowExecutionId);

//    boolean moveToAssignedStage(String pyCaseId, String assignedStage, String flowExecutionId);
    boolean updateProcessStatus(String pyCaseId, String flowId);

    void updateCaseVariables(String flowInstanceId, String flowExecutionId);

    boolean executeProcessActivity(String pyId, String flowExecutionId);
    /**
     * 删除case实例ID
     *
     * @param instanceId   流程实例ID
     * @param deleteReason 删除原因
     */
    void delete(String instanceId, String deleteReason);

    /**
     * 根据实例ID查询历史实例数据
     *
     * @param caseInstanceId
     * @return
     */
    HistoricProcessInstance getHistoricCaseInstanceById(String caseInstanceId);

}
