# 飞书云空间分片上传功能说明

## 功能概述

本模块实现了飞书云空间的分片上传功能，支持大文件的分片上传、断点续传等特性。

## 核心类说明

### 1. IFeishuDriveService 接口
定义了分片上传的三个核心方法：
- `uploadPrepare`: 预上传，获取上传事务ID和分片策略
- `uploadPart`: 上传分片
- `uploadFinish`: 完成上传

### 2. IFeishuDriveServiceImpl 实现类
实现了IFeishuDriveService接口，提供了分片上传的具体实现。

### 3. FeishuDriveUploadUtil 工具类
封装了完整的分片上传流程，简化了使用方式。

### 4. FeishuDriveExampleService 示例类
提供了使用分片上传功能的示例代码。

## 使用方法

### 1. 基础使用
```java
@Autowired
private IFeishuDriveService feishuDriveService;

// 预上传
UploadPrepareFileResp prepareResp = feishuDriveService.uploadPrepare("test.txt", "fldbcO1UuPz8VwnpPx5a92abcef", 1024);

// 上传分片
UploadPartFileResp partResp = feishuDriveService.uploadPart(uploadId, 0, 1024, partFile);

// 完成上传
UploadFinishFileResp finishResp = feishuDriveService.uploadFinish(uploadId, 1);
```

### 2. 简化使用
```java
@Autowired
private FeishuDriveUploadUtil feishuDriveUploadUtil;

String fileToken = feishuDriveUploadUtil.uploadFile(file, "test.txt", "fldbcO1UuPz8VwnpPx5a92abcef");
```

## 测试

提供了单元测试类 `IFeishuDriveServiceImplTest` 来验证功能：
- `testUploadPrepare`: 测试预上传功能
- `testUploadFile`: 测试完整上传流程

注意：测试需要有效的飞书应用凭证和实际文件。

## 注意事项

1. 分片大小固定为4MB
2. 上传事务ID和上传进度在24小时内有效
3. 该接口不支持并发调用，调用频率上限为5 QPS
4. 需要确保应用具有相应的飞书云空间权限

## 错误处理

在使用过程中可能会遇到以下错误：
- 1061002: 参数错误，请检查请求参数是否正确
- 1061004: 权限不足，请确保应用具有相应权限
- 1061021: 上传事务ID过期，请重新调用预上传接口
- 1061043: 文件大小超出限制，请检查文件大小