## 下载 protobuf 的安装包
https://github.com/protocolbuffers/protobuf/releases

## 解压后添加到环境变量 Path
protoc --version
libprotoc 3.15.8

## 安装 idea protobuf
protobuf 插件

## 导入依赖
```xml
<dependency>
    <groupId>com.google.protobuf</groupId>
    <artifactId>protobuf-java</artifactId>
    <version>3.15.8</version>
</dependency>

<dependency>
    <groupId>com.google.protobuf</groupId>
    <artifactId>protobuf-java-util</artifactId>
    <version>3.15.8</version>
</dependency>
```

## 创建一个 proto 文件
```protobuf
syntax = "proto3";

message person {
    int64 id = 1;
    string name = 2;
    int64 age = 3;
}
```

## .proto 生成 java 文件
```shell
protoc --java_out=./ person.proto
```
- `--java_out`：指定生成 java 文件路径
- 指定文件名称