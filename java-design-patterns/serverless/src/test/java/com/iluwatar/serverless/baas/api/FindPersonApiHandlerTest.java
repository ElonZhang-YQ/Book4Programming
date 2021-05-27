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
import com.iluwatar.serverless.baas.api.FindPersonApiHandler;
import com.iluwatar.serverless.baas.api.SavePersonApiHandler;
import com.iluwatar.serverless.baas.model.Person;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for FindPersonApiHandler
 * Created by dheeraj.mummar on 3/5/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class FindPersonApiHandlerTest {

  private FindPersonApiHandler findPersonApiHandler;

  @Mock
  private DynamoDBMapper dynamoDbMapper;

  @Before
  public void setUp() {
    this.findPersonApiHandler = new FindPersonApiHandler();
    this.findPersonApiHandler.setDynamoDbMapper(dynamoDbMapper);
  }

  @Test
  public void handleRequest() {
    findPersonApiHandler.handleRequest(apiGatewayProxyRequestEvent(), mock(Context.class));
    verify(dynamoDbMapper, times(1)).load(Person.class, "37e7a1fe-3544-473d-b764-18128f02d72d");
  }

  private APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent() {
    return new APIGatewayProxyRequestEvent()
        .withPathParamters(Collections
            .singletonMap("id", "37e7a1fe-3544-473d-b764-18128f02d72d"));
  }
}
