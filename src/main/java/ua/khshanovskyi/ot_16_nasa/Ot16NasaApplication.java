package ua.khshanovskyi.ot_16_nasa;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@Log4j2
public class Ot16NasaApplication {

    public static void main(String[] args) {
        SpringApplication.run(Ot16NasaApplication.class, args);
    }


    @Scheduled(timeUnit = TimeUnit.HOURS, fixedRate = 24)
    @CacheEvict(value = "maxImageUrl")
    public void invalidateCashes() {
        log.info("Caches are invalidated");
    }

}
