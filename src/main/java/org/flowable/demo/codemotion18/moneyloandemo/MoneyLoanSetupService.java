package org.flowable.demo.codemotion18.moneyloandemo;

import java.util.List;
import com.google.common.collect.ImmutableList;
import org.flowable.idm.api.IdmIdentityService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.Privilege;
import org.flowable.idm.api.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MoneyLoanSetupService {

    public static final String BANK_TELLERS_GROUP_ID = "bankTellers";
    public static final String LOAN_REVIEWERS_GROUP_ID = "loanReviewers";

    private static final Logger logger = LoggerFactory.getLogger(MoneyLoanSetupService.class);

    private final ProcessEngine processEngine;
    private final RepositoryService repositoryService;
    private final IdmIdentityService identityService;
    private final MoneyLoanService moneyLoanService;

    @Autowired
    public MoneyLoanSetupService(ProcessEngine processEngine, IdmIdentityService idmIdentityService,
            MoneyLoanService moneyLoanService) {
        this.processEngine = processEngine;
        this.repositoryService = processEngine.getRepositoryService();
        this.identityService = idmIdentityService;// processEngine.getIdentityService();
        this.moneyLoanService = moneyLoanService;
    }

    public void setupMoneyLoanApplication() {
        createApplicationUsersAndGroups();
        createSystemUsersAndGroups();
    }

    private void createApplicationUsersAndGroups() {
        String bankersGroupId = "bankers";
        checkCreateGroup(bankersGroupId, "Bankers");
        checkCreateGroup(LOAN_REVIEWERS_GROUP_ID, "Loan reviewers");
        checkCreateGroup(BANK_TELLERS_GROUP_ID, "Bank Tellers");
        checkCreateUser("rafa", "Rafa", "test", ImmutableList.of(bankersGroupId, BANK_TELLERS_GROUP_ID));
        checkCreateUser("nuria", "Nuria", "test", ImmutableList.of(bankersGroupId, LOAN_REVIEWERS_GROUP_ID));
    }

    private void createSystemUsersAndGroups() {
        String adminGroupId = "admin";
        if (identityService.createGroupQuery().groupId(adminGroupId).count() < 1) {
            Group adminGroup = identityService.newGroup(adminGroupId);
            adminGroup.setName(adminGroupId);
            adminGroup.setType("security-role");
            identityService.saveGroup(adminGroup);
        }

        String restAdminUserId = "rest-admin";
        if (identityService.createUserQuery().userId(restAdminUserId).count() < 1) {
            Privilege adminPrivilege = identityService.createPrivilege("admin-privilege");
            User restAdminUser = identityService.newUser(restAdminUserId);
            restAdminUser.setFirstName("Rest Administrator");
            restAdminUser.setPassword("test");
            identityService.saveUser(restAdminUser);
            identityService.addUserPrivilegeMapping(adminPrivilege.getId(), restAdminUser.getId());
            identityService.createMembership(restAdminUserId, adminGroupId);
        }
    }

    private void checkCreateGroup(String groupId, String groupName) {
        if (identityService.createGroupQuery().groupId(groupId).count() < 1) {
            logger.info("Creating group '{}'.", groupName);
            Group bankersGroup = identityService.newGroup(groupId);
            bankersGroup.setName(groupName);
            identityService.saveGroup(bankersGroup);
        } else {
            logger.info("Group '{}' found.", groupName);
        }
    }

    private void checkCreateUser(String userId, String displayName, String password, List<String> groupIds) {
        if (identityService.createUserQuery().userId(userId).count() < 1) {
            logger.info("Creating user '{}'.", displayName);
            User user = identityService.newUser(userId);
            user.setDisplayName(displayName);
            user.setPassword(password);
            identityService.saveUser(user);
            groupIds.forEach(groupId -> identityService.createMembership(userId, groupId));
        } else {
            logger.info("User '{}' found.", displayName);
        }
    }
    
}