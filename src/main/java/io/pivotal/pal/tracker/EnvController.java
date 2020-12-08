package io.pivotal.pal.tracker;

import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EnvController {

    private Map<String, String> env;

    public EnvController(@Value("${vcap.app.port:8080}") String port, @Value("${memory.limit:1G}") String memoryLimit, @Value("${cf.instance.index:NOT SET}") String instanceIdx, @Value("${cf.instance.addr:127.0.0.1}") String instanceAddr) {
        env = new HashMap<>(4);

        env.put("PORT", port);
        env.put("MEMORY_LIMIT", memoryLimit);
        env.put("CF_INSTANCE_INDEX", instanceIdx);
        env.put("CF_INSTANCE_ADDR", instanceAddr);
    }

    @GetMapping("/env")
    public Map<String, String> getEnv() {
        return env;
    }
}