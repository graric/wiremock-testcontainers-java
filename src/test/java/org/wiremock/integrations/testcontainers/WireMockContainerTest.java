/*
 * Copyright (C) 2023 WireMock Inc, Oleg Nenashev and all project contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wiremock.integrations.testcontainers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.integrations.testcontainers.util.SimpleHttpResponse;
import org.wiremock.integrations.testcontainers.util.SimpleHttpClient;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers(parallel = true)
class WireMockContainerTest {

    @Container
    WireMockContainer wiremockServer = new WireMockContainer(WireMockContainer.WIREMOCK_2_LATEST)
            .withMapping("hello", WireMockContainerTest.class, "hello-world.json")
            .withMapping("hello-resource", WireMockContainerTest.class, "hello-world-resource.json")
            .withFileFromResource("hello-world-resource-response.xml", WireMockContainerTest.class,
                    "hello-world-resource-response.xml");


    @ParameterizedTest
    @ValueSource(strings = {
            "hello",
            "/hello"
    })
    void helloWorld(String path) throws Exception {
        // given
        String url = wiremockServer.getUrl(path);

        // when
        SimpleHttpResponse response = new SimpleHttpClient().get(url);

        // then
        assertThat(response.getBody())
                .as("Wrong response body")
                .contains("Hello, world!");
    }

    @Test
    void helloWorldFromFile() throws Exception {
        // given
        String url = wiremockServer.getUrl("/hello-from-file");

        // when
        SimpleHttpResponse response = new SimpleHttpClient().get(url);

        // then
        assertThat(response.getBody())
                .as("Wrong response body")
                .contains("Hello, world!");
    }
}
