package com.sky.controller.user;

import com.sky.constant.MessageConstant;
import com.sky.dto.DefaultAddressDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import com.sky.vo.OrderSubmitVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
@Slf4j
@Api(tags = "地址簿相关接口")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增地址
     *
     * @param addressBook
     * @return
     */
    @PostMapping
    @ApiOperation("新增地址")
    public Result add(@RequestBody AddressBook addressBook) {
        log.info("新增地址：{}", addressBook);
        addressBookService.add(addressBook);
        return Result.success();
    }

    /**
     * 查询当前登录用户中的所有地址信息
     *
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("查询当前登录用户中的所有地址信息")
    public Result<List<AddressBook>> list() {
        log.info("查询当前登录用户中的所有地址信息");
        List<AddressBook> addressBooks = addressBookService.list();
        return Result.success(addressBooks);
    }

    /**
     * 查询默认地址
     *
     * @return
     */
    @GetMapping("/default")
    @ApiOperation("查询默认地址")
    public Result<AddressBook> listDefault() {
        log.info("查询默认地址");
        AddressBook defaultAddress = addressBookService.listDefault();
        if (defaultAddress != null) {
            return Result.success(defaultAddress);
        } else {
            return Result.error(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
    }

    /**
     * 根据id查询地址
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询地址")
    public Result<AddressBook> selectById(@PathVariable Long id) {
        log.info("根据id查询地址：{}", id);
        AddressBook addressBook = addressBookService.selectById(id);
        return Result.success(addressBook);
    }

    /**
     * 根据id修改地址
     *
     * @param addressBook
     * @return
     */
    @PutMapping
    @ApiOperation("根据id修改地址")
    public Result update(@RequestBody AddressBook addressBook) {
        log.info("根据id修改地址：{}", addressBook);
        addressBookService.update(addressBook);
        return Result.success();
    }

    /**
     * 根据id删除地址
     * @param id
     * @return
     */
    @DeleteMapping
    @ApiOperation("根据id删除地址")
    public Result delete(Long id) {
        log.info("根据id删除地址：{}", id);
        addressBookService.delete(id);
        return Result.success();
    }

    /**
     * 设置默认地址
     * @param id
     * @return
     */
    @PutMapping("/default")
    @ApiOperation("设置默认地址")
    public Result setDefault(@RequestBody DefaultAddressDTO defaultAddressDTO) {
        log.info("设置默认地址");
        System.out.println(defaultAddressDTO);
        addressBookService.setDefault(defaultAddressDTO.getId());
        return Result.success();
    }
}

