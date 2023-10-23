package spofo.support.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import spofo.global.config.restclient.RestClientConfig;
import spofo.holdingstock.controller.HoldingStockController;
import spofo.mock.FakeAuthServerService;
import spofo.portfolio.controller.PortfolioController;
import spofo.tradelog.controller.TradeLogController;

/**
 * @see ControllerTestMockBeanSupport
 */
@ActiveProfiles("test")
@WebMvcTest(controllers = {PortfolioController.class, HoldingStockController.class,
        TradeLogController.class})
@Import({FakeAuthServerService.class, RestClientConfig.class})
public abstract class ControllerTestSupport extends ControllerTestMockBeanSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

}
