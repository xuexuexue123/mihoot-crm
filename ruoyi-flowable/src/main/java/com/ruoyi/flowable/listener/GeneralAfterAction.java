package com.ruoyi.flowable.listener;

import com.ruoyi.common.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

@Slf4j
public class GeneralAfterAction implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        log.info("############### after action ############");
        log.info(DateUtils.dateTimeNow("yyyy-MM-dd HH:mm:ss"));
    }
}
