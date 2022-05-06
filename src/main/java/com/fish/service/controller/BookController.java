package com.fish.service.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fish.domain.Book;
import com.fish.service.IBookService;
import com.fish.service.controller.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {
    @Autowired
    public IBookService bookService;

    @GetMapping
    public R getAll() {
        return new R(true, bookService.list());
    }

    @PostMapping
    public R save(@RequestBody Book book) throws IOException {

//        if(true) throw new IOException();
        if (book.getName().equals("123")) throw new IOException();
        boolean flag = bookService.save(book);
        return new R(flag, flag ? "添加成功^_^" : "添加失败-_-!");
    }

    @PutMapping
    public R update(@RequestBody Book book) {
        return new R(bookService.updateById(book));
    }

    @DeleteMapping("{id}")
    public R delete(@PathVariable Integer id) {
        return new R(true, bookService.removeById(id));
    }

    @GetMapping("/{id}")
    public R getById(@PathVariable Integer id) {
        return new R(true, bookService.getById(id));
    }

//    @GetMapping("/{currentPage}/{pageSize}")
//    public R getPage(@PathVariable("currentPage") int currentPage, @PathVariable("pageSize") int pageSize) {
//        IPage<Book> page = bookService.getPage(currentPage, pageSize);
////        如果当前页码值大于了总页码值，那么重新执行查询操作，使用最大页码值作为当前页码值
//        if(currentPage >page.getPages()){
//            page=bookService.getPage((int) page.getPages(), pageSize);
//        }
//        return new R(true,page);
//    }

    @GetMapping("/{currentPage}/{pageSize}")
    public R getPage(@PathVariable("currentPage") int currentPage, @PathVariable("pageSize") int pageSize,Book book) {
        IPage<Book> page = bookService.getPage(currentPage, pageSize,book);
//        如果当前页码值大于了总页码值，那么重新执行查询操作，使用最大页码值作为当前页码值
        if(currentPage >page.getPages()){
            page=bookService.getPage((int) page.getPages(), pageSize,book);
        }
        return new R(true,page);
    }
}
