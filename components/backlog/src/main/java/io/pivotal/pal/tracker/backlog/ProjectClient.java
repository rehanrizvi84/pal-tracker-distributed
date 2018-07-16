package io.pivotal.pal.tracker.backlog;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestOperations;

import java.util.concurrent.ConcurrentHashMap;

public class ProjectClient {

    private final RestOperations restOperations;
    private final String endpoint;
    private final ConcurrentHashMap<Long, ProjectInfo> projectCache = new ConcurrentHashMap();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public ProjectClient(RestOperations restOperations, String registrationServerEndpoint) {
        this.restOperations = restOperations;
        this.endpoint = registrationServerEndpoint;
    }



    @HystrixCommand(fallbackMethod = "getProjectFromCache")
    public ProjectInfo getProject(long projectId) {
        ProjectInfo projectInfo  = restOperations.getForObject(endpoint + "/projects/" + projectId, ProjectInfo.class);
        projectCache.put(projectId,projectInfo);
        logger.info("Getting value from actual implementation");
        return projectInfo;

    }

    public ProjectInfo getProjectFromCache(long projectId) {
        logger.info("Getting value from Cache");
        return projectCache.get(projectId);

    }
}
