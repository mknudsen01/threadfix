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
package com.denimgroup.threadfix.selenium.tests;

import com.denimgroup.threadfix.CommunityTests;
import com.denimgroup.threadfix.selenium.pages.AnalyticsPage;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category(CommunityTests.class)
public class AnalyticsSnapshotTest extends BaseDataTest{

    @Test
    public void pieD3WedgeNavigation() {
        initializeTeamAndAppWithWebInspectScan();
        String[] levels = {"Info","Low","Medium","High","Critical"};

        AnalyticsPage analyticsPage = loginPage.defaultLogin()
                .clickAnalyticsLink();

        for(int i = 0; i < 5; i++) {
            analyticsPage.clickSnapshotTab();

            sleep(1500);

            analyticsPage.clickSVGElement("pointInTime" + levels[i] + "Arc")
                    .clickModalSubmit();

            assertTrue("Navigation @ level " + levels[i] + " failed", analyticsPage.checkCorrectFilterLevel(levels[i]));
        }
    }

    @Test
    public void snapshotD3TeamFilterTest() {
        initializeTeamAndAppWithIBMScan();
        String teamName2 = createTeam();

        AnalyticsPage analyticsPage = loginPage.defaultLogin()
                .clickAnalyticsLink()
                .clickSnapshotTab();

        analyticsPage.expandTeamApplicationFilterReport("snapshotFilterDiv")
                .addTeamFilterReport(teamName, "snapshotFilterDiv");

        assertTrue("Only 10 critical vulnerabilities should be shown.",
                analyticsPage.isVulnerabilityCountCorrect("Critical", "10"));
        assertTrue("Only 9 medium vulnerabilities should be shown.",
                analyticsPage.isVulnerabilityCountCorrect("Medium", "9"));
        assertTrue("Only 21 low vulnerabilities should be shown.",
                analyticsPage.isVulnerabilityCountCorrect("Low", "21"));
        assertTrue("Only 5 info vulnerabilities should be shown.",
                analyticsPage.isVulnerabilityCountCorrect("Info", "5"));

        analyticsPage.clearFilterReport("snapshotFilterDiv")
                .addTeamFilterReport(teamName2, "snapshotFilterDiv");

        assertTrue("There should be no results shown.",
                analyticsPage.areAllVulnerabilitiesHidden());
    }

    @Test
    public void snapshotD3ApplicationFilterTest() {
        initializeTeamAndAppWithIBMScan();
        String teamName2 = createTeam();
        String appName2 = createApplication(teamName2);

        AnalyticsPage analyticsPage = loginPage.defaultLogin()
                .clickAnalyticsLink()
                .clickSnapshotTab();

        analyticsPage.expandTeamApplicationFilterReport("snapshotFilterDiv")
                .addApplicationFilterReport(appName,"snapshotFilterDiv");

        assertTrue("Only 10 critical vulnerabilities should be shown.",
                analyticsPage.isVulnerabilityCountCorrect("Critical", "10"));
        assertTrue("Only 9 medium vulnerabilities should be shown.",
                analyticsPage.isVulnerabilityCountCorrect("Medium", "9"));
        assertTrue("Only 21 low vulnerabilities should be shown.",
                analyticsPage.isVulnerabilityCountCorrect("Low", "21"));
        assertTrue("Only 5 info vulnerabilities should be shown.",
                analyticsPage.isVulnerabilityCountCorrect("Info", "5"));

        analyticsPage.clearFilterReport("snapshotFilterDiv")
                .addApplicationFilterReport(appName2,"snapshotFilterDiv");

        assertTrue("There should be no results shown.",
                analyticsPage.areAllVulnerabilitiesHidden());
    }
    @Test
    public void expandCollapseTest() {
        int filtersExpandedSize;
        int filtersCollapsedSize;

        AnalyticsPage analyticsPage = loginPage.defaultLogin()
                .clickAnalyticsLink()
                .clickSnapshotTab();

        filtersCollapsedSize = analyticsPage.getFilterDivHeight("snapshotFilterDiv");
        analyticsPage.toggleAllFilterReport("snapshotFilterDiv");

        filtersExpandedSize = analyticsPage.getFilterDivHeight("snapshotFilterDiv");
        assertFalse("Filters were not expanded.", filtersCollapsedSize == filtersExpandedSize);

        analyticsPage = analyticsPage.toggleAllFilterReport("snapshotFilterDiv");
        assertFalse("Filters were not collapsed.",
                filtersExpandedSize == analyticsPage.getFilterDivHeight("snapshotFilterDiv"));
    }
}