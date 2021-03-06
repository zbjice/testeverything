package guava.eventbus.config;

import guava.common.TradeAccount;
import guava.eventbus.EventBusTestBase;
import guava.eventbus.events.BuyEvent;
import guava.eventbus.events.SellEvent;
import guava.eventbus.publisher.complex.BuyTradeExecutor;
import guava.eventbus.publisher.complex.SellTradeExecutor;
import guava.eventbus.subscriber.complex.TradeBuyAuditor;
import guava.eventbus.subscriber.complex.TradeSellAuditor;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: Bill Bejeck
 * Date: 4/26/13
 * Time: 4:31 PM
 */
public class MultipleEventBusSpringJavaConfigTest extends EventBusTestBase {

    BuyTradeExecutor buyTradeExecutor;
    SellTradeExecutor sellTradeExecutor;
    TradeBuyAuditor tradeBuyAuditor;
    TradeSellAuditor tradeSellAuditor;

    @Before
    public void setUp(){
        ApplicationContext ctx = new AnnotationConfigApplicationContext(MultipleEventBusConfig.class);
         buyTradeExecutor = ctx.getBean(BuyTradeExecutor.class);
         sellTradeExecutor = ctx.getBean(SellTradeExecutor.class);
         tradeBuyAuditor = ctx.getBean(TradeBuyAuditor.class);
         tradeSellAuditor = ctx.getBean(TradeSellAuditor.class);
    }

    @Test
    public void testBuyMessage(){
        TradeAccount tradeAccount = new TradeAccount.Builder().build();
        buyTradeExecutor.executeBuyTrade(tradeAccount,5000.65);
        List<BuyEvent> buyEvents = tradeBuyAuditor.getBuyEvents();
        assertThat(buyEvents.get(0).getTradeAccount(),is(tradeAccount));
        assertThat(tradeSellAuditor.getSellEvents().isEmpty(),is(true));
    }

    @Test
    public void testSellMessage(){
        TradeAccount tradeAccount = new TradeAccount.Builder().build();
        sellTradeExecutor.executeSaleTrade(tradeAccount, 5000.65);
        List<SellEvent> sellEvents = tradeSellAuditor.getSellEvents();
        assertThat(sellEvents.get(0).getTradeAccount(),is(tradeAccount));
        assertThat(tradeBuyAuditor.getBuyEvents().isEmpty(),is(true));
    }
}
