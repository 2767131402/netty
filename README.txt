Netty例子：
1. 实现心跳机制
2. 实现客户端断线重连
3. 实现服务端主动向客户端推送数据

实现思路：
demo中数据传输格式如下：
{
	"data": "业务数据",
	"type": 3
}

type为 心跳包/业务数据
*数据的封装在code包下，封装是在编码和解码过程中自动封装数据格式*

-----------------------------------------
netty-client-starter 项目: 是将客户端 制作一个starter
netty-client-starter-test：为stater的测试