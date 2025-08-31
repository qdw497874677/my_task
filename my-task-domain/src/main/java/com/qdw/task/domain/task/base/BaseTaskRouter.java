package com.qdw.task.domain.task.base;

import com.google.common.collect.Maps;
import com.qdw.task.domain.lock.Lock;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
public abstract class BaseTaskRouter<S extends BaseFlowStatus, C, T extends BaseTask<S, C>> implements TaskProcessor<S, C, T> {

//    private final Step2Cleaner step2;
//    private final Step3Summarizer step3;

    private Map<S, TaskProcessor<S, C, T>> processorMap;

//    private BaseTaskMapper<S, C, T> baseTaskMapper;

    public abstract Lock getLock();

    public abstract BaseTaskMapper<S, C, T> getBaseTaskMapper();

    public abstract List<TaskProcessor<S, C, T>> register();

    @PostConstruct
    public void init() {
        processorMap = Maps.newHashMap();
        List<TaskProcessor<S, C, T>> processorList = register();
        for (TaskProcessor<S, C, T> p : processorList) {
            processorMap.put(p.getStatus(), p);
        }
    }

    @Override
    public ProcessResult<S, C> process(T task) {
        ProcessResult<S, C> result = null;
        TaskProcessor<S, C, T> stTaskProcessor = null;
        try {
            stTaskProcessor = processorMap.get(task.getStatus());
            if (stTaskProcessor == null) {
                throw new RuntimeException("task not found");
            }
//            try {
//                if (!getLock().tryLock(task.getTaskId(), 500)) {
//                    return;
//                }
//                // 再次校验状态
//                if (!taskMapper.casLock(row)) {
//                    return;
//                }
//
//                ProcessResult r = processor.process(row);
//                taskMapper.finish(row, r);
//
//            } catch (Exception e) {
//                ProcessResult result = new ProcessResult(SummaryFlowStatus.异常, Map.of("errorMsg", e.getMessage()));
//                taskMapper.finish(row, result);
//                getLock().unlock(task.getTaskId());
//            }
            getBaseTaskMapper().casLock(task);
            result = stTaskProcessor.process(task);

            return result;
        } catch (Exception e) {
            handlerError(task, e);
            result = new ProcessResult<S, C>();
            result.setNextStatus(task.getErrorStatus());
            task.setErrorMsg(e.getMessage());
            log.error("router error", e);
            return result;
        } finally {
            if (result!= null && result.getNextStatus() == null && stTaskProcessor != null) {
                result.setNextStatus(stTaskProcessor.nextStatus());
            }
            getBaseTaskMapper().finish(task, result);
        }
    }


    public abstract void handlerError(BaseTask<S, C> task, Throwable t);


//    @Override
//    public ProcessResult process(DemoTask row) {
//        switch (row.getFlowStatus()) {
//            case 待处理:    return step1.process(row);
////            case 等待转写完成: return step2.run(row);
////            case 校准完成: return step3.run(row);
////            case 已完成: return step3.run(row);
////            case 异常: return step3.run(row);
//            default: throw new IllegalStateException("unknown status");
//        }
//    }

}
