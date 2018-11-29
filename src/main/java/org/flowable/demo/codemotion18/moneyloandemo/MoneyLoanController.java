package org.flowable.demo.codemotion18.moneyloandemo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;

import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loanapp")
public class MoneyLoanController {

    private final MoneyLoanService moneyLoanService;

    @Autowired
    public MoneyLoanController(MoneyLoanService moneyLoanService) {
        this.moneyLoanService = moneyLoanService;
    }

    @PostMapping("/loans")
    @ResponseBody
    public Map<String, Object> startLoanRequestProcessInstance(@RequestBody LoanRequest loanRequest)
            throws ParseException {
        Date birthDate = new SimpleDateFormat("yyyy-MM-dd").parse(loanRequest.getBirthDate());
        ProcessInstance loanRequestProcessInstance = this.moneyLoanService
                .startRequestLoanProcess(loanRequest.getName(), birthDate, loanRequest.getAmount());
        return ImmutableMap.of("id", loanRequestProcessInstance.getId());
    }

    @GetMapping("/loanreviews")
    @ResponseBody
    public List<String> getLoanReviews() {
        return this.moneyLoanService.getUnclaimedLoanReviewerTasks().stream().map(task -> task.getId())
                .collect(Collectors.toList());
    }

    @GetMapping("/informativetasks")
    @ResponseBody
    public List<String> getInformativeTasks() {
        return this.moneyLoanService.getUnclaimedBankTellerTasks().stream().map(task -> task.getId())
                .collect(Collectors.toList());
    }

    @PutMapping("/{resourceName}/{taskId}")
    public void claimTask(@PathVariable String taskId, @PathVariable String resourceName,
            @RequestParam(required = false) String userId, @RequestParam String action,
            @RequestBody(required = false) LoanReview loanReview) {
        if ("claim".equals(action)) {
            this.moneyLoanService.claimTask(taskId, userId);
        } else if ("complete".equals(action)) {
            if ("loanreviews".equals(resourceName)) {
                this.moneyLoanService.completeLoanReview(taskId, loanReview.isLoanAccepted(), loanReview.comment);
            } else {
                this.moneyLoanService.completeTask(taskId);
            }
        } else {
            throw new RuntimeException("Action not recognized. Options are 'claim' or 'complete'.");
        }

    }

    private static class LoanRequest {
        private String name;
        private String birthDate;
        private int amount;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBirthDate() {
            return birthDate;
        }

        public void setBirthDate(String birthDate) {
            this.birthDate = birthDate;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }
    }

    public static class LoanReview {
        private String comment;
        private boolean loanAccepted;

        public String getComment() {
            return comment;
        }

        public boolean isLoanAccepted() {
            return loanAccepted;
        }

        public void setLoanAccepted(boolean loanAccepted) {
            this.loanAccepted = loanAccepted;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }

}