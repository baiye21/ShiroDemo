package com.demo.controller.role;

import com.demo.common.Const;
import com.demo.common.ServerResponse;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.*;


/*
 * Author  : baiye <baiye_21@163.com>
   Time    : 2021/06/30
   Function:
*/
@RestController
@RequestMapping("/role/")
public class TestRoleController {

    /**
     * 只有管理员用户才能访问
     *
     * @return
     * @throws Exception
     */
    @RequiresRoles(Const.ADMIN_USER)
    @RequestMapping(value = "/OneRole.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> OneRole() throws Exception {

        return ServerResponse.createBySuccessMessage("One Role");
    }

    /**
     * 管理员用户和Leader用户能访问
     *
     * @return
     * @throws Exception
     */
    @RequiresRoles(value = { Const.ADMIN_USER, Const.LEADER_USER }, logical = Logical.OR)
    @RequestMapping(value = "/TwoRole.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> TwoRole() throws Exception {

        return ServerResponse.createBySuccessMessage("Two Role");
    }

    /**
     * Permission 001 能访问
     *
     * @return
     * @throws Exception
     */
    @RequiresPermissions(Const.LEVEL_004)
    @RequestMapping(value = "/OnePermission.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> OnePermission() throws Exception {

        return ServerResponse.createBySuccessMessage("One Permission");
    }

    /**
     * Permission 004 005 能访问
     *
     * @return
     * @throws Exception
     */
    @RequiresPermissions(value = {Const.LEVEL_004,Const.LEVEL_005}, logical = Logical.AND)
    @RequestMapping(value = "/TwoPermission.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> TwoPermission() throws Exception {

        return ServerResponse.createBySuccessMessage("Two Permission");
    }
}
