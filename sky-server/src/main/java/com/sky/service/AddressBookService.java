package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService {
    /**
     * 新增地址
     * @param addressBook
     */
    void add(AddressBook addressBook);

    /**
     * 查询当前登录用户的所有地址信息
     * @return
     */
    List<AddressBook> list();

    /**
     * 查询当前登录用户的默认地址
     * @return
     */
    AddressBook listDefault();

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    AddressBook selectById(Long id);

    /**
     * 根据id修改地址
     * @param addressBook
     */
    void update(AddressBook addressBook);

    /**
     * 根据id删除地址
     * @param id
     */
    void delete(Long id);

    /**
     * 设置默认地址
     * @param id
     */
    void setDefault(Long id);
}
