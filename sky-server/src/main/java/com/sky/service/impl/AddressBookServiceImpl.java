package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressBookServiceImpl implements AddressBookService {

    @Autowired
    private AddressBookMapper addressBookMapper;

    /**
     * 新增地址
     * @param addressBook
     */
    @Override
    public void add(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookMapper.add(addressBook);
    }

    /**
     * 查询当前登录用户的所有地址信息
     * @return
     */
    @Override
    public List<AddressBook> list() {
        Long currentId = BaseContext.getCurrentId();
        return addressBookMapper.list(currentId);
    }

    /**
     * 查询当前登录用户的默认地址
     * @return
     */
    @Override
    public AddressBook listDefault() {
        AddressBook addressBook = new AddressBook();
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(StatusConstant.ENABLE);

        AddressBook defaultAddress = addressBookMapper.listDefault(addressBook);

        return defaultAddress;
    }

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    @Override
    public AddressBook selectById(Long id) {
        return addressBookMapper.selectById(id);
    }

    /**
     * 根据id修改地址
     * @param addressBook
     */
    @Override
    public void update(AddressBook addressBook) {
        addressBookMapper.update(addressBook);
    }

    /**
     * 根据id删除地址
     * @param id
     */
    @Override
    public void delete(Long id) {
        addressBookMapper.delete(id);
    }

    /**
     * 设置默认地址
     * @param id
     */
    @Override
    public void setDefault(Long id) {
        // 想要设置默认地址，需要先找到哪个地址是默认地址，然后将改地址的is_default字段设置为0
        // 但是查找默认地址需要访问数据库，降低性能，所以不妨将当前登录用户的所有地址的is_default字段设置为0
        // 然后更新id对应的地址的is_default字段为1
        AddressBook addressBook = new AddressBook();

        // 将当前登录用户的所有地址都设置为非默认地址
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(StatusConstant.DISABLE);

        addressBookMapper.update(addressBook);

        addressBook.setId(id);
        addressBook.setIsDefault(StatusConstant.ENABLE);

        addressBookMapper.update(addressBook);
    }
}
