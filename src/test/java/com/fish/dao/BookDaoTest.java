package com.fish.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fish.domain.Book;
import org.apache.ibatis.annotations.Param;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BookDaoTest {

    @Autowired
    private BookDao bookDao;
    @Test
    void getByIdTest(){
        System.out.println(bookDao.selectById(1));
    }
    @Test
    void saveTest(){
        Book book= new Book();
        book.setType("测试数据123");
        book.setName("测试数据123");
        book.setDescription("测试数据123");
        bookDao.insert(book);
    }
    @Test
    void updateTest(){
        Book book= new Book();
        book.setId(13);
        book.setType("测试数据bbbb");
        book.setName("测试数据123");
        book.setDescription("测试数据123");
        bookDao.updateById(book);
    }
    @Test
    void deleteTest(){
        bookDao.deleteById(13);
    }
    @Test
    void getAllTest(){
      bookDao.selectList(null);
    }
    @Test
    void getPageTest(){
        IPage page = new Page(1, 5);
        bookDao.selectPage(page,null);
    }
    @Test
    void getByTest(){
        String name="1";
        LambdaQueryWrapper<Book> bookLambdaQueryWrapper = new LambdaQueryWrapper<>();
        bookLambdaQueryWrapper.like(name!=null,Book::getName,name);
        bookDao.selectList(bookLambdaQueryWrapper);
    }
}