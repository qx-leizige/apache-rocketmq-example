package qx.leizige.test;

import java.math.BigDecimal;
import java.util.UUID;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import qx.leizige.TransactionMessageProducer;
import qx.leizige.common.constants.MqConstants;
import qx.leizige.mp.po.Account;
import qx.leizige.mp.po.MQTransactionLog;
import qx.leizige.mp.service.AccountService;
import qx.leizige.mp.service.MQTransactionLogService;
import qx.leizige.mq.RocketMqProducer;
import qx.leizige.mq.executor.LocalTransactionExecutor;
import qx.leizige.mq.executor.LocalTransactionFactory;
import qx.leizige.mq.message.TransferAccountDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TransactionMessageProducer.class)
public class TransactionMessageProducerTest {

	@Autowired
	private RocketMqProducer rocketMqProducer;

	@Autowired
	private AccountService accountService;

	@Autowired
	private MQTransactionLogService transactionLogService;

	@Test
	public void testTransactionMessage() throws Exception {
		BigDecimal balance = BigDecimal.valueOf(2000);
		//张三给李四转2000
		Account account = accountService.getById(1L);
		account.setBalance(account.getBalance().subtract(balance));

		LocalTransactionExecutor<Void, String> localTransactionExecutor = LocalTransactionFactory.execute((transactionId) -> {
			accountService.updateById(account);
			transactionLogService.save(MQTransactionLog.builder()
					.transactionId(transactionId).extInfo(JSON.toJSONString(account)).build());
		});

		String transactionId = UUID.randomUUID().toString().replace("-", "");
		rocketMqProducer.transactionalSend(MqConstants.TRANSFER_ACCOUNT_TAG,
				new TransferAccountDto("6223473345625666", balance), transactionId, localTransactionExecutor);

		localTransactionExecutor.getResult();
	}

}
