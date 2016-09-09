package bootTest;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.zsx.controller.App;
import com.zsx.Application;
import com.zsx.service.HelloService;

@RunWith(SpringJUnit4ClassRunner.class)

//指定我们SpringBoot工程的Application启动类
@SpringApplicationConfiguration(classes = Application.class)

//由于是Web项目，Junit需要模拟ServletContext，因此我们需要给我们的测试类加上@WebAppConfiguration
@WebAppConfiguration

public class HelloServiceTest {

//	@Resource
	@Autowired
	private HelloService helloService;
	
	
	@Test
	public void test(){
		Assert.assertEquals("helle", helloService.getName());
	}
	
	
}
