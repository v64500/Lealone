package org.lealone.test.service.generated;

import org.lealone.client.ClientServiceProxy;
import org.lealone.orm.json.JsonArray;

/**
 * Service interface for 'hello_world_service'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
public interface HelloWorldService {

    static HelloWorldService create(String url) {
        if (new org.lealone.db.ConnectionInfo(url).isEmbedded())
            return new org.lealone.test.service.impl.HelloWorldServiceImpl();
        else;
            return new Proxy(url);
    }

    void sayHello();

    String sayGoodbyeTo(String name);

    static class Proxy implements HelloWorldService {

        private final String url;

        private Proxy(String url) {
            this.url = url;
        }

        @Override
        public void sayHello() {
            JsonArray ja = new JsonArray();
            ClientServiceProxy.executeNoReturnValue(url, "HELLO_WORLD_SERVICE.SAY_HELLO", ja.encode());
        }

        @Override
        public String sayGoodbyeTo(String name) {
            JsonArray ja = new JsonArray();
            ja.add(name);
            String result = ClientServiceProxy.executeWithReturnValue(url, "HELLO_WORLD_SERVICE.SAY_GOODBYE_TO", ja.encode());
            if (result != null) {
                return result;
            }
            return null;
        }
    }
}
