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
package com.denimgroup.threadfix.webapp.controller.rest;

import com.denimgroup.threadfix.data.entities.Role;
import com.denimgroup.threadfix.data.entities.User;
import com.denimgroup.threadfix.remote.response.RestResponse;
import com.denimgroup.threadfix.service.RoleService;
import com.denimgroup.threadfix.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.denimgroup.threadfix.remote.response.RestResponse.success;

/**
 * This is a QA only class so we can skip steps while testing
 *
 * Created by daniel on 8/6/14.
 */

@Controller
@RequestMapping("/rest/user")
public class UserRestController {

    @Autowired
    UserService userService;
    @Autowired
    RoleService roleService;

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public @ResponseBody RestResponse<User> createUser(@RequestParam String username,
                                                       @RequestParam(required = false) String globalRoleName) {

        User user = new User();
        user.setName(username);

        user.setHasGlobalGroupAccess(globalRoleName != null);
        if (globalRoleName != null) {
            user.setGlobalRole(roleService.loadRole(globalRoleName));
        }

        user.setSalt("c892c2c6-2bd9-4b6a-a826-d9a71f5db441");
        user.setPassword("3ac7de35360886d9aa7c821e4908f7c260c63eea9c229bff38ac40b28279b7a5");

        userService.storeUser(user);

        return success(user);
    }

}
