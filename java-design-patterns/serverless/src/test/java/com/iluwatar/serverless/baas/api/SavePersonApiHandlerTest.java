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
package com.iluwatar.serverless.baas.api;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iluwatar.serverless.baas.api.SavePersonApiHandler;
import com.iluwatar.serverless.baas.model.Address;
import com.iluwatar.serverless.baas.model.Person;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

/**
 * Unit tests for SavePersonApiHandler
 * Created by dheeraj.mummar on 3/4/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class SavePersonApiHandlerTest {

  private SavePersonApiHandler savePersonApiHandler;

  @Mock
  private DynamoDBMapper dynamoDbMapper;

  private ObjectMapper objectMapper = new ObjectMapper();

  @Before
  public void setUp() {
    this.savePersonApiHandler = new SavePersonApiHandler();
    this.savePersonApiHandler.setDynamoDbMapper(dynamoDbMapper);
  }

  @Test
  public void handleRequestSavePersonSuccessful() throws JsonProcessingException {
    Person person = newPerson();
    APIGatewayProxyResponseEvent apiGatewayProxyResponseEvent =
        this.savePersonApiHandler
            .handleRequest(apiGatewayProxyRequestEvent(objectMapper.writeValueAsString(person)), mock(Context.class));
    verify(dynamoDbMapper, times(1)).save(person);
    Assert.assertNotNull(apiGatewayProxyResponseEvent);
    Assert.assertEquals(new Integer(201), apiGatewayProxyResponseEvent.getStatusCode());
  }

  @Test
  public void handleRequestSavePersonException() {
    APIGatewayProxyResponseEvent apiGatewayProxyResponseEvent =
        this.savePersonApiHandler
            .handleRequest(apiGatewayProxyRequestEvent("invalid sample request"), mock(Context.class));
    Assert.assertNotNull(apiGatewayProxyResponseEvent);
    Assert.assertEquals(new Integer(400), apiGatewayProxyResponseEvent.getStatusCode());
  }

  private APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent(String body) {
    APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent = new APIGatewayProxyRequestEvent();
    return apiGatewayProxyRequestEvent.withBody(body);
  }

  private Person newPerson() {
    Person person = new Person();
    person.setFirstName("Thor");
    person.setLastName("Odinson");
    Address address = new Address();
    address.setAddressLineOne("1 Odin ln");
    address.setCity("Asgard");
    address.setState("country of the Gods");
    address.setZipCode("00001");
    person.setAddress(address);

    return person;
  }
}
