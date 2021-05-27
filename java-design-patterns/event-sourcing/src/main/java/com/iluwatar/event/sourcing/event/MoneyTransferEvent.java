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
package com.iluwatar.event.sourcing.event;

import com.iluwatar.event.sourcing.domain.Account;
import com.iluwatar.event.sourcing.state.AccountAggregate;
import java.math.BigDecimal;

/**
 * This is the class that implements money transfer event.
 * Holds the necessary info for a money transfer event.
 * Implements the process function that finds the event related
 * domain objects and calls the related domain object's handle event functions
 *
 * Created by Serdar Hamzaogullari on 06.08.2017.
 */
public class MoneyTransferEvent extends DomainEvent {

  private final BigDecimal money;
  private final int accountNoFrom;
  private final int accountNoTo;

  /**
   * Instantiates a new Money transfer event.
   *
   * @param sequenceId the sequence id
   * @param createdTime the created time
   * @param money the money
   * @param accountNoFrom the account no from
   * @param accountNoTo the account no to
   */
  public MoneyTransferEvent(long sequenceId, long createdTime, BigDecimal money, int accountNoFrom,
      int accountNoTo) {
    super(sequenceId, createdTime, "MoneyTransferEvent");
    this.money = money;
    this.accountNoFrom = accountNoFrom;
    this.accountNoTo = accountNoTo;
  }

  /**
   * Gets money.
   *
   * @return the money
   */
  public BigDecimal getMoney() {
    return money;
  }

  /**
   * Gets account no which the money comes from.
   *
   * @return the account no from
   */
  public int getAccountNoFrom() {
    return accountNoFrom;
  }

  /**
   * Gets account no which the money goes to.
   *
   * @return the account no to
   */
  public int getAccountNoTo() {
    return accountNoTo;
  }

  @Override
  public void process() {
    Account accountFrom = AccountAggregate.getAccount(accountNoFrom);
    if (accountFrom == null) {
      throw new RuntimeException("Account not found " + accountNoFrom);
    }
    Account accountTo = AccountAggregate.getAccount(accountNoTo);
    if (accountTo == null) {
      throw new RuntimeException("Account not found" + accountTo);
    }

    accountFrom.handleTransferFromEvent(this);
    accountTo.handleTransferToEvent(this);
  }
}
