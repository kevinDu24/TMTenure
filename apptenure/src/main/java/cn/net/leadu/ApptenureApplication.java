package cn.net.leadu;

import cn.net.leadu.util.esign.SignHelper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableFeignClients
@EnableCaching
@EnableJpaAuditing
public class ApptenureApplication {

	public static void main(String[] args) {
		//e签宝环境初始化
		SignHelper.initProject();
		SpringApplication.run(ApptenureApplication.class, args);
	}
}
