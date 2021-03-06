////////////////////////////////////////////////////////////////////////
//
//     Copyright (c) 2009-2014 Denim Group, Ltd.
//
//     The contents of this file are subject to the Mozilla Public License
//     Version 2.0 (the "License"); you may not use this file except in
//     compliance with the License. You may obtain a copy of the License at
//     http://www.mozilla.org/MPL/
//
//     Software distributed under the License is distributed on an "AS IS"
//     basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
//     License for the specific language governing rights and limitations
//     under the License.
//
//     The Original Code is ThreadFix.
//
//     The Initial Developer of the Original Code is Denim Group, Ltd.
//     Portions created by Denim Group, Ltd. are Copyright (C)
//     Denim Group, Ltd. All Rights Reserved.
//
//     Contributor(s): Denim Group, Ltd.
//
////////////////////////////////////////////////////////////////////////

package com.denimgroup.threadfix.selenium.enttests;

import com.denimgroup.threadfix.data.entities.Role;
import com.denimgroup.threadfix.EnterpriseTests;
import com.denimgroup.threadfix.selenium.pages.*;
import com.denimgroup.threadfix.selenium.tests.BaseIT;
import com.denimgroup.threadfix.selenium.utils.DatabaseUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category(EnterpriseTests.class)
public class RoleEntIT extends BaseIT {

	@Test
	public void createRoleTest() {
        String roleName = getRandomString(8);

		RolesIndexPage rolesIndexPage = loginPage.defaultLogin()
				.clickManageRolesLink()
				.clickCreateRole()
				.setRoleName(roleName)
				.clickSaveRole();

		assertTrue("Role not added.", rolesIndexPage.isNamePresent(roleName));
		assertTrue("Validation message is Present.",rolesIndexPage.isCreateValidationPresent(roleName));
	}

    @Test
    public void deleteRoleTest() {
        String roleName = getRandomString(8);

        RolesIndexPage rolesIndexPage = loginPage.defaultLogin()
                .clickManageRolesLink()
                .clickCreateRole()
                .setRoleName(roleName)
                .clickSaveRole();

        assertTrue("Role not added.", rolesIndexPage.isNamePresent(roleName));

        rolesIndexPage = rolesIndexPage.clickDeleteButton(roleName);

        assertTrue("Validation message is Present.",rolesIndexPage.isDeleteValidationPresent(roleName));
        assertFalse("Role not removed.", rolesIndexPage.isNamePresent(roleName));
    }

	@Test
	public void editRoleTest() {
		String originalRoleName = getRandomString(8);
		String editedRoleName = getRandomString(8);

		RolesIndexPage rolesIndexPage = loginPage.defaultLogin()
				.clickManageRolesLink()
				.clickCreateRole()
				.setRoleName(originalRoleName)
				.clickSaveRole();

		assertTrue("Role not added.", rolesIndexPage.isNamePresent(originalRoleName));

		rolesIndexPage = rolesIndexPage.clickEditLink(originalRoleName)
				.setRoleName(editedRoleName)
				.clickSaveRole();
		
		assertTrue("Role not Edited Correctly.", rolesIndexPage.isNamePresent(editedRoleName));
		assertTrue("Validation message not present.",rolesIndexPage.isEditValidationPresent(editedRoleName));
		
		rolesIndexPage = rolesIndexPage.clickDeleteButton(editedRoleName);

		assertTrue("Validation message not present.",rolesIndexPage.isDeleteValidationPresent(editedRoleName));
		assertFalse("Role not removed.", rolesIndexPage.isNamePresent(editedRoleName));

	}

    @Test
    public void createRoleValidation() {
        String whiteSpaceName = "     ";

        // Test whitespace
        RolesIndexPage rolesIndexPage = loginPage.defaultLogin()
                .clickManageRolesLink()
                .clickCreateRole()
                .setRoleName(whiteSpaceName)
                .clickSaveRoleInvalid();

        assertTrue("Blank field error didn't show correctly.",
                rolesIndexPage.getNameError().contains("Name is required."));
    }

    @Test
    public void editRoleValidation() {
        String roleName = getRandomString(8);
        String whiteSpaceName = "     ";
        String emptyStringName = "";

        RolesIndexPage rolesIndexPage = loginPage.defaultLogin()
                .clickManageRolesLink()
                .clickCreateRole()
                .setRoleName(roleName)
                .clickSaveRole();

        rolesIndexPage = rolesIndexPage.clickEditLink(roleName)
                .setRoleName(whiteSpaceName);

        assertTrue("White space name error name not shown.",
                rolesIndexPage.getNameError().contains("Name is required."));

        rolesIndexPage.setRoleName(emptyStringName);

        assertTrue("Empty string name error name not shown.",
                rolesIndexPage.getNameError().contains("Name is required."));
    }

    @Test
	public void createRoleDuplicateValidation() {
		String roleName1 = "testNameDuplication";
        String roleName2 = "testNameDuplication";

		// Test duplicates
		RolesIndexPage rolesIndexPage = loginPage.defaultLogin().clickOrganizationHeaderLink()
                .clickManageRolesLink()
				.clickCreateRole()
				.setRoleName(roleName1)
				.clickSaveRole()
				.clickManageRolesLink()
				.clickCreateRole()
				.setRoleName(roleName2)
				.clickSaveRoleInvalid();

		assertTrue("Duplicate name error did not show correctly.",
				rolesIndexPage.getDupNameError().contains("That name is already taken."));
	}

	@Test
	public void addApplicationOnlyRoleTest(){
		String roleName = getName();
		String userName = getName();
		String password = getRandomString(15);
		String teamName = createTeam();
		String appName = getName();

		TeamIndexPage teamIndexPage = loginPage.defaultLogin()
                 .clickOrganizationHeaderLink()
                 .clickManageRolesLink()
                 .clickCreateRole()
                 .setRoleName(roleName)
                 .setPermissionValue("canManageApplications", true)
                 .clickSaveRole()
                 .clickOrganizationHeaderLink();

        teamIndexPage.clickManageUsersLink()
                 .clickAddUserLink()
                 .setName(userName)
                 .setPassword(password)
                 .setConfirmPassword(password)
                 .toggleGlobalAccess()
                 .chooseRoleForGlobalAccess(roleName)
                 .clickAddNewUserBtn();

		ApplicationDetailPage applicationDetailPage = teamIndexPage.logout()
                .login(userName, password)
                .clickOrganizationHeaderLink()
                .expandTeamRowByName(teamName)
                .addNewApplication(teamName, appName, "", "Low")
                .saveApplication()
                .clickOrganizationHeaderLink()
                .expandTeamRowByName(teamName)
                .clickViewAppLink(appName, teamName);

		assertTrue("New role user was not able to add an application",
                applicationDetailPage.getNameText().contains(appName));
	}

	@Test
	public void setPermissionsTest() {
		String roleName = getRandomString(10);
		
		RolesIndexPage rolesIndexPage = loginPage.defaultLogin()
				.clickManageRolesLink()
				.clickCreateRole()
				.setRoleName(roleName);
		
		for (String permission : Role.ALL_PERMISSIONS) {
            if (!permission.equals("enterprise")) {
                assertFalse("Permission was set to yes when it should have been set to no.",
                        rolesIndexPage.getPermissionValue(permission));
            }
		}

		rolesIndexPage = rolesIndexPage.toggleAllPermissions(true)
                .clickSaveRole(roleName)
                .clickManageRolesLink()
                .clickEditLink(roleName);

		for (String permission : Role.ALL_PERMISSIONS) {
            if (!permission.equals("enterprise")) {
                assertTrue("Permission was set to no when it should have been set to yes."
                        , rolesIndexPage.getPermissionValue(permission));
            }
		}
		
		rolesIndexPage = rolesIndexPage.toggleAllPermissions(false)
                .clickSaveRole(roleName)
                .clickManageRolesLink()
                .clickEditLink(roleName);
		
		for (String permission : Role.ALL_PERMISSIONS) {
            if (!permission.equals("enterprise")) {
                assertFalse("Permission was set to yes when it should have been set to no.",
                        rolesIndexPage.getPermissionValue(permission));
            }
		}

		rolesIndexPage = rolesIndexPage.clickSaveRole(roleName)
                .clickManageRolesLink()
				.clickDeleteButton(roleName);

		assertTrue("Validation message is Present.",rolesIndexPage.isDeleteValidationPresent(roleName));
		assertFalse("Role not removed.", rolesIndexPage.isNamePresent(roleName));
	}

	@Test
	public void deleteRoleWithUserAttachedTest(){
		String roleName = getRandomString(10);
        String tempUser = getRandomString(8);

		RolesIndexPage rolesIndexPage = loginPage.defaultLogin()
                .clickManageRolesLink();
		
		rolesIndexPage = rolesIndexPage.clickCreateRole()
				.setRoleName(roleName);
		
		for (String permission : Role.ALL_PERMISSIONS) {
            if (!permission.equals("enterprise")) {
                rolesIndexPage = rolesIndexPage.setPermissionValue(permission, true);
            }
		}
		
		rolesIndexPage = rolesIndexPage.clickSaveRole()
                .clickManageUsersLink()
                .clickAddUserLink()
                .setName(tempUser)
                .setPassword("TestPassword")
                .setConfirmPassword("TestPassword")
                .toggleGlobalAccess()
                .chooseRoleForGlobalAccess(roleName)
                .clickModalSubmit()
                .clickManageRolesLink()
                .clickDeleteButton(roleName)
                .clickManageRolesLink();

		assertFalse("Role was not removed.", rolesIndexPage.isNamePresent(roleName));
	}
}
