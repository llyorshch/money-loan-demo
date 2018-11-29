package org.flowable.demo.codemotion18.moneyloandemo.elasticsearch;

import java.io.IOException;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.engine.delegate.event.impl.FlowableEntityWithVariablesEventImpl;

public class ElasticsearchEventListener implements FlowableEventListener {

    private final RestHighLevelClient client;

    public ElasticsearchEventListener(RestHighLevelClient client) {
        this.client = client;
    }

    @Override
    public void onEvent(FlowableEvent event) {
        if (event.getType().equals(FlowableEngineEventType.TASK_COMPLETED)
                && event instanceof FlowableEntityWithVariablesEventImpl) {
            FlowableEntityWithVariablesEventImpl entityEvent = (FlowableEntityWithVariablesEventImpl) event;
            IndexRequest indexRequest = new IndexRequest("loanreviews", "doc", entityEvent.getExecutionId())
                    .source(entityEvent.getVariables());
            try {
                client.index(indexRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                throw new RuntimeException("Exception while indexing a task", e);
            }
        }
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }

    @Override
    public boolean isFireOnTransactionLifecycleEvent() {
        return false;
    }

    @Override
    public String getOnTransaction() {
        return null;
    }

}