# 实体类

1. 勾选SpringMVC与Mysql坐标

2. 修改配置文件为yml格式

3. 设置端口为80方便访问

   

# mybatis-plus开日志

```yaml
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```



# 分页

分页操作需要设定分页对象IPage

```JAVA
@Test
void getPageTest(){
    IPage page = new Page(1, 5);
    bookDao.selectPage(page,null);
}
```

另外还要配置MybatisPlus拦截器（作用动态地拼limit语句）

```java
@Configuration
public class MPConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor mybatisPlusInterceptor1 = new MybatisPlusInterceptor();
        mybatisPlusInterceptor1.addInnerInterceptor(new PaginationInnerInterceptor());
        return mybatisPlusInterceptor1;
    }
}
```

# 条件查询

1. 使用QueryWrapper对象封装查询条件

2. 推荐使用LambdaQueryWrapper 对象

3. 所有查询操作封装成方法调用

4. 查询条件支持动态条件拼装

   ```java
   @Test
   void getByTest(){
       String name="1";
       LambdaQueryWrapper<Book> bookLambdaQueryWrapper = new LambdaQueryWrapper<>();
       bookLambdaQueryWrapper.like(name!=null,Book::getName,name);
       bookDao.selectList(bookLambdaQueryWrapper);
   }
   ```



# 业务层开发

Service层接口定义与数据层接口定义具有较大区别，不要混用：

+ selectByUserNameAndPassword(String username,String password);

+ login(String username,String password);

**注意测试业务层 ！！！**

## 快速开发方案

使用MybatisPlus提供有业务层通用接口（Iserivce）



# 基于Restful制作表现层接口

+ 新增 ：POST

+ 删除： DELETE

+ 修改： PUT

+ 查询： GET

  ## 接受参数

  + 实体数据：@RequestBody
  + 路径变量： @PathVariable

# 表现层消息一致性处理

+ 设计表现层返回结果的模型类，用于后端与前端进行数据格式统一，也称前后端数据协议

  ```java
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public class R {
      private Boolean flag;
      private Object data;
  
      public R(Boolean flag) {
          this.flag = flag;
      }
  
  }
  ```

+ 表现层接口统一返回值类型结果

  ```java
  @RestController
  @RequestMapping("/books")
  public class BookController {
      @Autowired
      public IBookService bookService;
  
      @GetMapping
      public R getAll(){
          return new R(true,bookService.list());
      }
      @PostMapping
      public R save(@RequestBody Book book){
          return new R(bookService.save(book));
      }
      @PutMapping
      public R update(@RequestBody Book book){
          return new R(bookService.updateById(book)) ;
      }
  
      @DeleteMapping("{id}")
      public R delete(@PathVariable Integer id){
          return new R(true,bookService.removeById(id)) ;
      }
  
      @GetMapping("/{id}")
      public R getById(@PathVariable Integer id){
          return new R(true,bookService.getById(id)) ;
      }
  
      @GetMapping("/{currentPage}/{pageSize}")
      public R getPage(@PathVariable("currentPage") int currentPage, @PathVariable("pageSize") int  pageSize) {
          return new R(true,bookService.getPage(currentPage,pageSize)) ;
      }
  }
  ```

  

将查询数据返回到页面，利用前端数据双向绑定进行数据展示

# 对异常进行统一处理，出现异常后，返回指定消息

```java
//作为SpringMVC的异常处理器
//@ControllerAdvice
@RestControllerAdvice
public class ProjectExceptionAdvice {
//   拦截所有的异常信息
    @ExceptionHandler
    public R doException(Exception e){
//        记录日志
//        通知运维
//        通知开发
        e.printStackTrace();
        return new R("服务器故障，请稍后再试！");
    }
}
```

修改表现层返回结果的模型类，封装出现异常后对应的信息

flag：false

Data：null

消息（msg）：要显示信息

```java
package com.fish.service.controller.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class R {
    private Boolean flag;
    private Object data;
    private String msg;

    public R() {
    }

    public R(Boolean flag) {
        this.flag = flag;
    }

    public R(Boolean flag, Object data) {
        this.flag = flag;
        this.data = data;
    }

    public R(Boolean flag, String msg) {
        this.flag = flag;
        this.msg = msg;
    }

    public R(String msg) {
        this.flag = false;
        this.msg = msg;
    }

}
```

可以在表现层Controller中进行消息统一处理

```java
    @PostMapping
    public R save(@RequestBody Book book) throws IOException {

//        if(true) throw new IOException();
        if (book.getName().equals("123")) throw new IOException();
        boolean flag = bookService.save(book);
        return new R(flag, flag ? "添加成功^_^" : "添加失败-_-!");
    }
```

目的：国际化

另外改过后的页面消息处理：

```javascript
//添加
handleAdd() {
    axios.post("/books", this.formData).then((res) => {
        //判断当前操作是否成功
        if (res.data.flag) {
            //    1.关闭弹层
            this.dialogFormVisible = false;
            this.$message.success(res.data.msg);
        } else {
            this.$message.error(res.data.msg);
        }
    }).finally(() => {
        //    2.重新加载数据
        this.getAll();
    });
},
```

# 分页功能

+ 页面使用el分页组件添加分页功能

```html
<!--分页组件-->
<div class="pagination-container">

    <el-pagination
            class="pagiantion"

            @current-change="handleCurrentChange"

            :current-page="pagination.currentPage"

            :page-size="pagination.pageSize"

            layout="total, prev, pager, next, jumper"

            :total="pagination.total">

    </el-pagination>

</div>
```

+ 定义分页组件需要使用的数据并将数据绑定到分页组件

```javascript
data: {
    pagination: {//分页相关模型数据
        currentPage: 1,//当前页码
        pageSize: 10,//每页显示的记录数
        total: 0//总记录数
    }
},
```

+ 替换查询全部功能为分页功能

```javascript
//分页查询
getAll() {
    axios.get("/books/"+this.pagination.currentPage+"/"+this.pagination.pageSize).then((res) => {
        this.pagination.pagesize=res.data.data.size;
        this.pagination.currentPage=res.data.data.current;
        this.pagination.total=res.data.data.total;
        // console.log(res.data);
        this.dataList = res.data.data.records;
    });
},
//切换页码
handleCurrentChange(currentPage) {
    this.pagination.currentPage=currentPage;
    this.getAll();
},
```



#   条件查询

查询条件数据封装

+ 单独封装

+ 与分页操作混合封装

  ```javascript
  pagination: {//分页相关模型数据
      currentPage: 1,//当前页码
      pageSize: 10,//每页显示的记录数
      total: 0,//总记录数
      type: "",
      name: "",
      description: ""
  }
  ```

+  页面数据模型绑定

  ```html
  <div class="filter-container">
      <el-input placeholder="图书类别" v-model="pagination.type" style="width: 200px;"
                class="filter-item"></el-input>
      <el-input placeholder="图书名称" v-model="pagination.name" style="width: 200px;"
                class="filter-item"></el-input>
      <el-input placeholder="图书描述" v-model="pagination.description" style="width: 200px;"
                class="filter-item"></el-input>
      <el-button @click="getAll()" class="dalfBut">查询</el-button>
      <el-button type="primary" class="butT" @click="handleCreate()">新建</el-button>
  </div>
  ```

+ 组织数据成为get请求发送的数据

  ```javascript
  getAll() {
      //组织参数，拼接url请求地址
      // console.log(this.pagination.type);
      param = "?type=" + this.pagination.type;
      param += "&name=" + this.pagination.name;
      param += "&description=" + this.pagination.description;
  
      axios.get("/books/" + this.pagination.currentPage + "/" + this.pagination.pageSize + param).then((res) => {
          this.pagination.pagesize = res.data.data.size;
          this.pagination.currentPage = res.data.data.current;
          this.pagination.total = res.data.data.total;
          // console.log(res.data);
          this.dataList = res.data.data.records;
      });
  },
  ```

+ Controller接受参数

  ```java
     @GetMapping("/{currentPage}/{pageSize}")
      public R getPage(@PathVariable("currentPage") int currentPage, @PathVariable("pageSize") int pageSize,Book book) {
          IPage<Book> page = bookService.getPage(currentPage, pageSize,book);
  //        如果当前页码值大于了总页码值，那么重新执行查询操作，使用最大页码值作为当前页码值
          if(currentPage >page.getPages()){
              page=bookService.getPage((int) page.getPages(), pageSize,book);
          }
          return new R(true,page);
      }
  ```

  

总结：

1. pom.xml

   配置起步依赖

2. application.yml

   设置数据源、端口、框架技术相关配置等

3. dao

   继承BaseMapper、设置@Mapper

4. dao测试类

5. service
   调用数据层接口或Mybatis-Plus提供的接口快速开发

6. service测试类

7. controller
   基于Restful开发，使用Postman测试跑通功能

8. 页面
   放置在resources目录下的static目录中