vmall模拟了（垂直）电商的后端处理逻辑, 使用SpringBoot开发, 借助Redis和MySQL存储数据, 实现了简单的订单提交系统API, 
不能用于生产环境.   

vmall实现的订单提交业务逻辑类似于苏宁、国美等电商的业务逻辑, 下面以A公司为例, 分析这类电商的实际业务.   

**库存系统：**   
A公司主要销售特定领域的产品, 比如家电, 家电产品大致有冰箱（单开门、双开门）、冰柜、洗衣机（波轮洗衣机、滚筒洗衣机）、空调、电视、微波炉、油烟机, 相比于其他日常用品, 这些产品的共同点是质量大、体积大, 如果进行频繁的远程运输, 会导致物流成本极高, 再加上中途多方转运, 破损率较高. 因此, 为了节约成本, 这类电商都有自己的库房, 专门用来存储上述产品. 

以中国为例, A公司会在34个省市自治区的省会租赁库房, 一旦库房租赁完毕, 经销商将会运来商品. 比如在X市, Y冰箱的经销商Z将运输自己代理销售的Y冰箱到A公司在X市的库房. A公司本身不生产商品, 其在各地库房的产品均来自于当地的经销商（如果当地有经销商的话）, A公司负责销售, 经销商负责补货. 

除了销售上面的自营（库房中存储的商品）大件商品, 公司有可能销售自营小件商品, 比如卫生纸、白酒、图书、手机等, 与大件产品的储存方式不同, 有些地区的库房不存储小件商品, 需要时, 从临近的库房或中心库房配送. 

另外, A公司还开放购物平台, 第三方商家可以注册销售产品. 

因此, A公司购物平台的商品可以分为自营商品和第三方销售商品, 而第三方销售的商品由第三方存储、配送. 

**线上购买：**  
举例来说, 顾客S购买了冰箱（自营大件）、白酒（自营小件）、红木家具（由第三方销售的产品）, 订单系统
会查询库存.   
>·对于自营大件, 查询离顾客最近的库房是否有存货, 如果有存货则可以生成订单, 如果没有存货, 则返回缺货, 
因为大件商品不从别处调配, 如果缺货, 由当地的经销商补货, 再更新库存.   
>·对于自营小件, 查询离顾客最近的库房是否有存货, 如果有存货则可以生成订单, 如果没有存货, 则（可能）会请求临近的
库房调拨.   
>·对于第三方商品, 直接查询数据库, 缺货与否取决于第三方商家  

**物流配送：**  
顾客下单支付完成后, 公司库房的管理人员与外包的物流团队将货物拉出库房, 等待外包的物流配送人员
运送到目的地. 

**实现订单提交系统API:**  
```
@RestController  
public class SubmitOrderController {  
    //接受前端的订单数据  
    @PostMapping("/submitorder")  
    public String submitOrder(@RequestBody Order order) {  
        //分拆订单  
        //处理自营商品  
        //处理第三方  
    }  
}  
```  
  
**说明**  
vmall项目实现了订单提交系统API, $mvn package 命令默认生成war格式文件;  
data-init用来初始化vmall所需的初始数据, 执行时, 先创建vmall数据库中的表, 
然后插入数据, 最后从vmall数据库读取数据到Redis. 每次执行时删除之前创建  
的所有数据, $mvn package 命令默认生成jar格式文件.   
mock-client以json格式发送post请求到vmall, $mvn package 命令默认生成jar格式文件.  
  
**运行:**  
1)在MySQL中手动创建一个名为vmall的database, 再在data-init和vmall的application.properties中配置可访问vmall数据库的用户名和密码    
2)git clone https://github.com/fdsxaar/vmall.git ,以maven project导入  
3)先运行data-init初始化数据, 等数据初始化结束, 再运行vmall, 最后运行mock-client发送数据  
  
**NOTE**  
1) 如果要将vmall以vmall.war文件部署到tomcat, 则要注意mock-client项目ClientMultiThreadedExecution.java类的postOrder()方法的urisToPOST数组中设定的路径为"http://localhost:8080/vmall/submitorder/", 而vmall中com.ecommerce.vmall.webcontroller.SubmitOrderController.submitOrder方法的映射路径依然为
"http://localhost:8080/submitorder/". 如果以IDE执行程序, 则这两个路径都应修改为"http://localhost:8080/submitorder/"  

2) 如果要部署到docker, 要先在宿主机创建一个vmall-info的卷 在启动时通过 选项 --mount source=vmall-info,target=/vmall-info 加载, docker容器输出GC日志和Heapdump到这个卷;   
  
由于vmall程序默认访问localhost地址的MySQL和Redis,因此,如果在Linux系统中, 在以docker run 启动容器时可以提供    --network="host" 选项, 使得容器可以访问localhost地址,比如:   
```  
sudo docker run --name vmall -ti --network="host" --mount source=vmall-info,target=/vmall-info fdsxaar/vmall  
```   

**版本信息**  
Java: 1.8  
JVM: Parallel Scavenge + Parallel Old   
Spring Boot: 2.3.3    
Maven: 3.6.3  
Redis: 5.0.7  
MySQL: 8.0  
Docker: 19.03  
Tomcat: 8   
OS: Ubuntu 20.04   