package com.demo.controller.register;
import com.demo.common.ServerResponse;
import com.demo.pojo.user.UserDTO;
import com.demo.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/*
 * Author  : baiye <baiye_21@163.com>
   Time    : 2021/06/30
   Function:
*/
@RestController
@RequestMapping("/register/")
public class RegisterController {

    @Autowired
    IUserService iUserService;

    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> userRegister(@RequestBody UserDTO userDto) {

        //TODO userid 不能重复check
        return iUserService.userRegister(userDto);
    }
}
