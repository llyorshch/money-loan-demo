package org.flowable.demo.codemotion18.moneyloandemo.config;

import com.google.common.collect.ImmutableList;

import org.elasticsearch.client.RestHighLevelClient;
import org.flowable.demo.codemotion18.moneyloandemo.elasticsearch.ElasticsearchEventListener;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.flowable.spring.boot.ProcessEngineServicesAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Configuration;

@AutoConfigureBefore({
    ProcessEngineServicesAutoConfiguration.class
})
@Configuration
public class CustomProcessEngineConfig implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {

    private final RestHighLevelClient client;

    @Autowired
    public CustomProcessEngineConfig (RestHighLevelClient client) {
        this.client = client;
    }

    @Override
    public void configure(SpringProcessEngineConfiguration engineConfiguration) {
        engineConfiguration.setEventListeners(ImmutableList.of(new ElasticsearchEventListener(this.client)));
    }

}