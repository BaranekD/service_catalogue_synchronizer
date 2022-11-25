package cz.muni.ics.spreg_sync.base.scheduled;

import com.fasterxml.jackson.core.JsonProcessingException;
import cz.muni.ics.spreg_sync.service.SpRegSyncService;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ScheduledTasks {

    private final SpRegSyncService syncService;

    @Autowired
    public ScheduledTasks(SpRegSyncService spRegSyncService) {
        this.syncService = spRegSyncService;
    }

    @Scheduled(cron = "0 0/30 * ? * * *")
    public void sync() {
        try {
            Document res = syncService.sync();

            // TODO according to specification
        } catch (JsonProcessingException e) {
            log.error("{}: An error occurred while processing the json: {}",
                    this.getClass().getSimpleName(), e.getMessage());
        }
    }
}
