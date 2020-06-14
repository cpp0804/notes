# GET和POST

1. url是获取资源，post是提交数据
2. get请求可以被浏览器缓存，而post不可以。因为如果提交一个页面，这个页面被缓存了，那么服务器并没有收到post，其实提交就没有成功。
3. get参数包含在URL, post通过request body传参
4. post并没有比get安全，因为他们用的都是HTTP就不安全，安全的话要用HTTPS

GET|POST
---|--- 
获取资源|提交数据
参数包含在URL|通过request body传参
浏览器回退时无害|浏览器回退时会再次提交请求
产生的URL地址可以被Bookmark|产生的URL地址不可以被Bookmark
GET请求会被浏览器主动cache|POST请求不会被浏览器主动cache
GET请求只能进行url编码|POST支持多种编码方式
GET请求参数会被完整保留在浏览器历史记录里|POST中的参数不会被保留
对参数的数据类型，GET只接受ASCII字符|没有限制
