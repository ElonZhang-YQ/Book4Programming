/**
 * The MIT License
 * Copyright (c) 2014 Ilkka Seppälä
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import com.iluwatar.event.sourcing.domain.Account;
import com.iluwatar.event.sourcing.event.AccountCreateEvent;
import com.iluwatar.event.sourcing.event.MoneyDepositEvent;
import com.iluwatar.event.sourcing.event.MoneyTransferEvent;
import com.iluwatar.event.sourcing.processor.DomainEventProcessor;
import com.iluwatar.event.sourcing.state.AccountAggregate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;

import static com.iluwatar.event.sourcing.app.App.ACCOUNT_OF_DAENERYS;
import static com.iluwatar.event.sourcing.app.App.ACCOUNT_OF_JON;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Intergartion Test for Event Sourcing state recovery
 *
 * Created by Serdar Hamzaogullari on 19.08.2017.
 */
public class IntegrationTest {

  /**
   * The Domain event processor.
   */
  private DomainEventProcessor eventProcessor;

  /**
   * Initialize.
   */
  @BeforeEach
  public void initialize() {
    eventProcessor = new DomainEventProcessor();
  }

  /**
   * Test state recovery.
   */
  @Test
  public void testStateRecovery() {
    eventProcessor.reset();

    eventProcessor.process(new AccountCreateEvent(
        0, new Date().getTime(), ACCOUNT_OF_DAENERYS, "Daenerys Targaryen"));

    eventProcessor.process(new AccountCreateEvent(
        1, new Date().getTime(), ACCOUNT_OF_JON, "Jon Snow"));

    eventProcessor.process(new MoneyDepositEvent(
        2, new Date().getTime(), ACCOUNT_OF_DAENERYS,  new BigDecimal("100000")));

    eventProcessor.process(new MoneyDepositEvent(
        3, new Date().getTime(), ACCOUNT_OF_JON,  new BigDecimal("100")));

    eventProcessor.process(new MoneyTransferEvent(
        4, new Date().getTime(), new BigDecimal("10000"), ACCOUNT_OF_DAENERYS,
        ACCOUNT_OF_JON));

    Account accountOfDaenerysBeforeShotDown = AccountAggregate.getAccount(ACCOUNT_OF_DAENERYS);
    Account accountOfJonBeforeShotDown = AccountAggregate.getAccount(ACCOUNT_OF_JON);

    AccountAggregate.resetState();

    eventProcessor = new DomainEventProcessor();
    eventProcessor.recover();

    Account accountOfDaenerysAfterShotDown = AccountAggregate.getAccount(ACCOUNT_OF_DAENERYS);
    Account accountOfJonAfterShotDown = AccountAggregate.getAccount(ACCOUNT_OF_JON);

    assertEquals(accountOfDaenerysBeforeShotDown.getMoney(),
        accountOfDaenerysAfterShotDown.getMoney());
    assertEquals(accountOfJonBeforeShotDown.getMoney(), accountOfJonAfterShotDown.getMoney());
  }

}