package org.flowable.demo.codemotion18.moneyloandemo;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MoneyLoanService {

    private static final Logger logger = LoggerFactory.getLogger(MoneyLoanService.class);

    private final RuntimeService runtimeService;
    private final RepositoryService repositoryService;
    private final ProcessDefinitionQuery loanRequestProcessDefinitionQuery;
    private final TaskService taskService;

    public MoneyLoanService(ProcessEngine processEngine) {
        this.runtimeService = processEngine.getRuntimeService();
        this.repositoryService = processEngine.getRepositoryService();
        this.loanRequestProcessDefinitionQuery = repositoryService.createProcessDefinitionQuery().processDefinitionName("Loan request").latestVersion();
        this.taskService = processEngine.getTaskService();
    }

    public boolean loanRequestProcessExists() {
        return (this.loanRequestProcessDefinitionQuery.count() > 0);
    }

    @Transactional
    public ProcessInstance startRequestLoanProcess(String userName, Date birthDate, int amount) {
        ProcessDefinition loanRequestProcessDefinition = this.loanRequestProcessDefinitionQuery.singleResult();
        Map<String,Object> loanRequestVariables = ImmutableMap.of("name",userName,"birthDate",birthDate,"amount",amount);
        ProcessInstance loanRequest =  this.runtimeService.startProcessInstanceById(loanRequestProcessDefinition.getId(), loanRequestVariables); 
        logger.info("Started loan request '{}'.", loanRequest.getId());
        return loanRequest;
    }

    public List<Task> getUnclaimedLoanReviewerTasks(){
        return getUnclaimedTasksByCandidateGroup(MoneyLoanSetupService.LOAN_REVIEWERS_GROUP_ID);
    }

    public List<Task> getUnclaimedBankTellerTasks(){
        return getUnclaimedTasksByCandidateGroup(MoneyLoanSetupService.BANK_TELLERS_GROUP_ID);
    }

    private List<Task> getUnclaimedTasksByCandidateGroup(String candidateGroupId) {
        return this.taskService.createTaskQuery().taskCandidateGroup(candidateGroupId).active().list();
    }

    @Transactional
    public void claimTask(String taskId, String userId) {
        this.taskService.claim(taskId, userId);
    }

    @Transactional
    public void completeLoanReview(String taskId, boolean loanAccepted, String comments) {
        Map<String, Object> variables = ImmutableMap.of("comments",comments,"form_reviewLoan_outcome",(loanAccepted?"Accept":"Reject"));
        this.taskService.complete(taskId, variables);
    }
    
    @Transactional
    public void completeTask(String taskId) {
        this.taskService.complete(taskId);
    }

}