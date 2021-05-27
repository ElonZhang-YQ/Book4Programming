/**
 * The MIT License
 * Copyright (c) 2014-2016 Ilkka Seppälä
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
package com.iluwatar.dao;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

/**
 * Tests {@link DbCustomerDao}.
 */
public class DbCustomerDaoTest {

  private static final String DB_URL = "jdbc:h2:~/dao";
  private DbCustomerDao dao;
  private Customer existingCustomer = new Customer(1, "Freddy", "Krueger");

  /**
   * Creates customers schema.
   * @throws SQLException if there is any error while creating schema.
   */
  @BeforeEach
  public void createSchema() throws SQLException {
    try (Connection connection = DriverManager.getConnection(DB_URL);
        Statement statement = connection.createStatement()) {
      statement.execute(CustomerSchemaSql.CREATE_SCHEMA_SQL);
    }
  }

  /**
   * Represents the scenario where DB connectivity is present.
   */
  @Nested
  public class ConnectionSuccess {

    /**
     * Setup for connection success scenario.
     * @throws Exception if any error occurs.
     */
    @BeforeEach
    public void setUp() throws Exception {
      JdbcDataSource dataSource = new JdbcDataSource();
      dataSource.setURL(DB_URL);
      dao = new DbCustomerDao(dataSource);
      boolean result = dao.add(existingCustomer);
      assertTrue(result);
    }

    /**
     * Represents the scenario when DAO operations are being performed on a non existing customer.
     */
    @Nested
    public class NonExistingCustomer {

      @Test
      public void addingShouldResultInSuccess() throws Exception {
        try (Stream<Customer> allCustomers = dao.getAll()) {
          assumeTrue(allCustomers.count() == 1);
        }

        final Customer nonExistingCustomer = new Customer(2, "Robert", "Englund");
        boolean result = dao.add(nonExistingCustomer);
        assertTrue(result);

        assertCustomerCountIs(2);
        assertEquals(nonExistingCustomer, dao.getById(nonExistingCustomer.getId()).get());
      }

      @Test
      public void deletionShouldBeFailureAndNotAffectExistingCustomers() throws Exception {
        final Customer nonExistingCustomer = new Customer(2, "Robert", "Englund");
        boolean result = dao.delete(nonExistingCustomer);

        assertFalse(result);
        assertCustomerCountIs(1);
      }

      @Test
      public void updationShouldBeFailureAndNotAffectExistingCustomers() throws Exception {
        final int nonExistingId = getNonExistingCustomerId();
        final String newFirstname = "Douglas";
        final String newLastname = "MacArthur";
        final Customer customer = new Customer(nonExistingId, newFirstname, newLastname);
        boolean result = dao.update(customer);

        assertFalse(result);
        assertFalse(dao.getById(nonExistingId).isPresent());
      }

      @Test
      public void retrieveShouldReturnNoCustomer() throws Exception {
        assertFalse(dao.getById(getNonExistingCustomerId()).isPresent());
      }
    }

    /**
     * Represents a scenario where DAO operations are being performed on an already existing
     * customer.
     *
     */
    @Nested
    public class ExistingCustomer {

      @Test
      public void addingShouldResultInFailureAndNotAffectExistingCustomers() throws Exception {
        Customer existingCustomer = new Customer(1, "Freddy", "Krueger");

        boolean result = dao.add(existingCustomer);

        assertFalse(result);
        assertCustomerCountIs(1);
        assertEquals(existingCustomer, dao.getById(existingCustomer.getId()).get());
      }

      @Test
      public void deletionShouldBeSuccessAndCustomerShouldBeNonAccessible() throws Exception {
        boolean result = dao.delete(existingCustomer);

        assertTrue(result);
        assertCustomerCountIs(0);
        assertFalse(dao.getById(existingCustomer.getId()).isPresent());
      }

      @Test
      public void updationShouldBeSuccessAndAccessingTheSameCustomerShouldReturnUpdatedInformation() throws Exception {
        final String newFirstname = "Bernard";
        final String newLastname = "Montgomery";
        final Customer customer = new Customer(existingCustomer.getId(), newFirstname, newLastname);
        boolean result = dao.update(customer);

        assertTrue(result);

        final Customer cust = dao.getById(existingCustomer.getId()).get();
        assertEquals(newFirstname, cust.getFirstName());
        assertEquals(newLastname, cust.getLastName());
      }
    }
  }

  /**
   * Represents a scenario where DB connectivity is not present due to network issue, or
   * DB service unavailable.
   * 
   */
  @Nested
  public class ConnectivityIssue {
    
    private static final String EXCEPTION_CAUSE = "Connection not available";

    /**
     * setup a connection failure scenario.
     * @throws SQLException if any error occurs.
     */
    @BeforeEach
    public void setUp() throws SQLException {
      dao = new DbCustomerDao(mockedDatasource());
    }
    
    private DataSource mockedDatasource() throws SQLException {
      DataSource mockedDataSource = mock(DataSource.class);
      Connection mockedConnection = mock(Connection.class);
      SQLException exception = new SQLException(EXCEPTION_CAUSE);
      doThrow(exception).when(mockedConnection).prepareStatement(Mockito.anyString());
      doReturn(mockedConnection).when(mockedDataSource).getConnection();
      return mockedDataSource;
    }

    @Test
    public void addingACustomerFailsWithExceptionAsFeedbackToClient() {
      assertThrows(Exception.class, () -> {
        dao.add(new Customer(2, "Bernard", "Montgomery"));
      });
    }
    
    @Test
    public void deletingACustomerFailsWithExceptionAsFeedbackToTheClient() {
      assertThrows(Exception.class, () -> {
        dao.delete(existingCustomer);
      });
    }
    
    @Test
    public void updatingACustomerFailsWithFeedbackToTheClient() {
      final String newFirstname = "Bernard";
      final String newLastname = "Montgomery";
      assertThrows(Exception.class, () -> {
        dao.update(new Customer(existingCustomer.getId(), newFirstname, newLastname));
      });
    }
    
    @Test
    public void retrievingACustomerByIdFailsWithExceptionAsFeedbackToClient() {
      assertThrows(Exception.class, () -> {
        dao.getById(existingCustomer.getId());
      });
    }
    
    @Test
    public void retrievingAllCustomersFailsWithExceptionAsFeedbackToClient() {
      assertThrows(Exception.class, () -> {
        dao.getAll();
      });
    }

  }

  /**
   * Delete customer schema for fresh setup per test.
   * @throws SQLException if any error occurs.
   */
  @AfterEach
  public void deleteSchema() throws SQLException {
    try (Connection connection = DriverManager.getConnection(DB_URL);
        Statement statement = connection.createStatement()) {
      statement.execute(CustomerSchemaSql.DELETE_SCHEMA_SQL);
    }
  }

  private void assertCustomerCountIs(int count) throws Exception {
    try (Stream<Customer> allCustomers = dao.getAll()) {
      assertTrue(allCustomers.count() == count);
    }
  }


  /**
   * An arbitrary number which does not correspond to an active Customer id.
   * 
   * @return an int of a customer id which doesn't exist
   */
  private int getNonExistingCustomerId() {
    return 999;
  }
}
