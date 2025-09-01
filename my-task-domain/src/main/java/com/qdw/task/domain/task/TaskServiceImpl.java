package com.qdw.task.domain.task;

import com.qdw.task.api.task.TaskService;
import com.qdw.task.domain.task.yt.YtTaskRouter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private YtTaskRouter ytTaskRouter;


    @Override
    public void pushPendingOnce() {
        ytTaskRouter.poll();
    }
}
