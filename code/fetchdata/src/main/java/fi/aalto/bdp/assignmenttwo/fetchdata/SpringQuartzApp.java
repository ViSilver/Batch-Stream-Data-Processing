package fi.aalto.bdp.assignmenttwo.fetchdata;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class SpringQuartzApp {

    public static void main(String[] args) {
        new SpringApplicationBuilder(SpringQuartzApp.class).bannerMode(Banner.Mode.OFF).run(args);
    }
}
