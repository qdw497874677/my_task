# 分片上传文件-预上传

发送初始化请求，以获取上传事务 ID 和分片策略，为[上传分片](https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/reference/drive-v1/file/upload_part)做准备。平台固定以 4MB 的大小对文件进行分片。了解完整的上传文件流程，参考[上传文件概述](https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/reference/drive-v1/file/multipart-upload-file-/introduction)。

## 注意事项

上传事务 ID 和上传进度在 24 小时内有效。请及时保存和恢复上传。

## 使用限制

- 该接口不支持并发调用，且调用频率上限为 5 QPS，10000 次/天。否则会返回 1061045 错误码，可通过稍后重试解决。
- 上传文件的大小限制因飞书版本而异，详情参考[文件上传、在线预览的大小及格式要求](https://www.feishu.cn/hc/zh-CN/articles/360049067549-%E6%96%87%E4%BB%B6%E4%B8%8A%E4%BC%A0-%E5%9C%A8%E7%BA%BF%E9%A2%84%E8%A7%88%E7%9A%84%E5%A4%A7%E5%B0%8F%E5%8F%8A%E6%A0%BC%E5%BC%8F%E8%A6%81%E6%B1%82)。

## 请求

基本 |
---|---
HTTP URL | https://open.feishu.cn/open-apis/drive/v1/files/upload_prepare
HTTP Method | POST
接口频率限制 | [5 次/秒](https://open.feishu.cn/document/ukTMukTMukTM/uUzN04SN3QjL1cDN)
支持的应用类型 | Custom App、Store App
权限要求<br>**调用该 API 所需的权限。开启其中任意一项权限即可调用**<br>开启任一权限即可 | 查看、评论、编辑和管理云空间中所有文件(drive:drive)<br>上传、下载文件到云空间(drive:file)<br>上传文件(drive:file:upload)

### 请求头

名称 | 类型 | 必填 | 描述
---|---|---|---
Authorization | string | 是 | `tenant_access_token`<br>或<br>`user_access_token`<br>**值格式**："Bearer `access_token`"<br>**示例值**："Bearer u-7f1bcd13fc57d46bac21793a18e560"<br>[了解更多：如何选择与获取 access token](https://open.feishu.cn/document/uAjLw4CM/ugTN1YjL4UTN24CO1UjN/trouble-shooting/how-to-choose-which-type-of-token-to-use)
Content-Type | string | 是 | **固定值**："application/json; charset=utf-8"

更多云文档接口权限问题，参考[常见问题](https://open.feishu.cn/document/ukTMukTMukTM/uczNzUjL3czM14yN3MTN)。

### 请求体

名称 | 类型 | 必填 | 描述
---|---|---|---
file_name | string | 是 | 文件的名称<br>**示例值**："test.txt"<br>**数据校验规则**：<br>- 最大长度：`250` 字符
parent_type | string | 是 | 上传点的类型。取固定值 explorer，表示将文件上传至云空间中。<br>**示例值**："explorer"<br>**可选值有**：<br>- explorer：云空间
parent_node | string | 是 | 云空间中文件夹的 token。了解如何获取文件夹 token，参考[文件夹概述](https://open.feishu.cn/document/ukTMukTMukTM/ugTNzUjL4UzM14CO1MTN/folder-overview)。<br>**示例值**："fldbcO1UuPz8VwnpPx5a92abcef"
size | int | 是 | 文件的大小，单位为字节。<br>**示例值**：1024<br>**数据校验规则**：<br>- 最小值：`0`

### 请求体示例
```json
{
    "file_name": "test.txt",
    "parent_type": "explorer",
    "parent_node": "fldbcO1UuPz8VwnpPx5a92abcef",
    "size": 1024
}
```

## 响应

### 响应体

名称 | 类型 | 描述
---|---|---
code | int | 错误码，非 0 表示失败
msg | string | 错误描述
data | \- | \-
upload_id | string | 分片上传事务 ID
block_size | int | 分片大小策略
block_num | int | 分片的数量

### 响应体示例
```json
{
    "code": 0,
    "msg": "success",
    "data": {
        "upload_id": "7111211691345512356",
        "block_size": 4194304,
        "block_num": 1
    }
}
```

### 错误码

HTTP状态码 | 错误码 | 描述 | 排查建议
---|---|---|---
200 | 1061001 | internal error. | 服务内部错误，包括超时、错误码没处理等。<br>**注意**：<br>上传文件接口不支持直接将文件上传至知识库，请先将文件上传至文件夹后再移动至知识库。
400 | 1061002 | params error. | 请检查请求参数是否正确。
403 | 1061004 | forbidden. | 当前调用身份没有文件或文件夹的阅读或编辑等权限。请参考以下方式解决：<br>- 若上传素材，请确保当前调用身份具有目标云文档的编辑权限<br>- 若上传文件，请确保当前调用身份具有文件夹的编辑权限<br>- 若对文件或文件夹进行增删改等操作，请确保调用身份具有足够文档权限：<br>- 对于新建文件接口，调用身份需要有目标文件夹的编辑权限<br>- 对于复制文件接口，调用身份需要有文件的阅读或编辑权限、并且具有目标文件夹的编辑权限<br>- 对于移动文件接口，调用身份需要有被移动文件的可管理权限、被移动文件所在位置的编辑权限、目标位置的编辑权限<br>- 对于删除文件接口，调用身份需要具有以下两种权限之一：<br>- 该应用或用户是文件所有者并且具有该文件所在父文件夹的编辑权限<br>- 该应用或用户并非文件所有者，但是该文件所在父文件夹的所有者或者拥有该父文件夹的所有权限（full access）<br>了解开通权限步骤，参考[如何为应用开通云文档相关资源的权限](https://open.feishu.cn/document/uAjLw4CM/ugTN1YjL4UTN24CO1UjN/trouble-shooting/how-to-add-permissions-to-app)。
500 | 1061022 | file version conflict. | 文件版本号冲突。
400 | 1061043 | file size beyond limit. | 请检查文件大小以避免超出限制。详情参考飞书帮助中心[云盘文件上传大小限制](https://www.feishu.cn/hc/zh-CN/articles/360049067549)。
400 | 1061044 | parent node not exist. | `parent_node` 不存在。请确认上传点 token 是否有误：<br>- 对于上传文件接口，请参考[文件夹 token 获取方式](https://open.feishu.cn/document/ukTMukTMukTM/ugTNzUjL4UzM14CO1MTN/folder-overview#-717d325)确认是否填写了正确的文件夹 token<br>- 对于上传素材接口，请参考[上传点类型和上传点 token](https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/reference/drive-v1/media/introduction#cc82be3c) 确认 `parent_node` 是否填写正确
400 | 1061109 | file name cqc not passed. | 请确保上传的文件和文件名合规。
400 | 1061101 | file quota exceeded. | 租户容量超限，请确保租户有足够容量进行上传。
400 | 1061061 | user quota exceeded. | 个人容量超限，请确保个人有足够容量进行上传。
403 | 1061073 | no scope auth. | 没有申请接口权限。
200 | 1064230 | locked for data migration | 数据迁移中，暂时无法上传。
400 | 1062507 | parent node out of sibling num. | 云空间目录下挂载数量超过限制（单层**1500**限制 ）。



# 分片上传文件-上传分片

根据 [预上传](https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/reference/drive-v1/file/upload_prepare)接口返回的上传事务 ID 和分片策略上传对应的文件分片。上传完成后，你需调用[分片上传文件（完成上传）](https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/reference/drive-v1/file/upload_finish)触发完成上传。了解完整的上传文件流程，参考[分片上传文件概述](https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/reference/drive-v1/file/multipart-upload-file-/introduction)。

## 使用限制

该接口不支持并发调用，且调用频率上限为 5 QPS，10000 次/天。否则会返回 1061045 错误码，可通过稍后重试解决。

## 请求

基本 |
---|---
HTTP URL | https://open.feishu.cn/open-apis/drive/v1/files/upload_part
HTTP Method | POST
接口频率限制 | [特殊频控](https://open.feishu.cn/document/ukTMukTMukTM/uUzN04SN3QjL1cDN)
支持的应用类型 | Custom App、Store App
权限要求<br>**调用该 API 所需的权限。开启其中任意一项权限即可调用**<br>开启任一权限即可 | 查看、评论、编辑和管理云空间中所有文件(drive:drive)<br>上传、下载文件到云空间(drive:file)<br>上传文件(drive:file:upload)

### 请求头

名称 | 类型 | 必填 | 描述
---|---|---|---
Authorization | string | 是 | `tenant_access_token`<br>或<br>`user_access_token`<br>**值格式**："Bearer `access_token`"<br>**示例值**："Bearer u-7f1bcd13fc57d46bac21793a18e560"<br>[了解更多：如何选择与获取 access token](https://open.feishu.cn/document/uAjLw4CM/ugTN1YjL4UTN24CO1UjN/trouble-shooting/how-to-choose-which-type-of-token-to-use)
Content-Type | string | 是 | **示例值**："multipart/form-data; boundary=---7MA4YWxkTrZu0gW"

### 请求体

名称 | 类型 | 必填 | 描述
---|---|---|---
upload_id | string | 是 | 分片上传事务 ID。通过调用[分片上传文件-预上传](https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/reference/drive-v1/file/upload_prepare)接口获取。<br>**示例值**："7111211691345512356"
seq | int | 是 | 文件分片的序号，从 0 开始计数。<br>**示例值**：0
size | int | 是 | 分片的大小，单位为字节。<br>**示例值**：4194304
checksum | string | 否 | 文件分片的 Adler-32 校验和<br>**示例值**："3248270248"
file | file | 是 | 文件分片的二进制内容<br>**示例值**：file binary

### 请求体示例

```HTTP
---7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="upload_id";

7111211691345512356
---7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="seq";

0
---7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="size";

4194304
---7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="checksum";

3248270248
---7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="file";
Content-Type: application/octet-stream

file binary
---7MA4YWxkTrZu0gW
```

## 响应

### 响应体

名称 | 类型 | 描述
---|---|---
code | int | 错误码，非 0 表示失败
msg | string | 错误描述
data | \- | \-

### 响应体示例
```json
{
    "code": 0,
    "msg": "success",
    "data": {}
}
```

### 错误码

HTTP状态码 | 错误码 | 描述 | 排查建议
---|---|---|---
200 | 1061001 | internal error. | 服务内部错误，包括超时，错误码没处理。
400 | 1061002 | params error. | 请检查请求参数是否正确。
400 | 1061021 | upload id expire. | 上传事务 ID 过期，请重新调用预上传接口。
403 | 1061073 | no scope auth. | 没有申请接口权限。
400 | 1062007 | upload user not match. | 请确保当前请求身份和上传任务的身份为同一个。
400 | 1062008 | checksum param Invalid. | 请确保文件或文件分片的 checksum 正确。
400 | 1062009 | the actual size is inconsistent with the parameter declaration size. | 实际传输的文件大小和参数说明的大小不符合一致。
400 | 1062010 | block missing, please upload all blocks. | 部分文件分片缺失，请确保所有文件分片上传完成。
400 | 1062011 | block num out of bounds. | 上传过多文件分片，请确保上传的为对应文件。


# 分片上传文件-完成上传

调用[上传分片](https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/reference/drive-v1/file/upload_part)接口将分片全部上传完毕后，你需调用本接口触发完成上传。否则将上传失败。了解完整的上传文件流程，参考[上传文件概述](https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/reference/drive-v1/file/multipart-upload-file-/introduction)。

## 使用限制

该接口不支持并发调用，且调用频率上限为 5 QPS，10000 次/天。否则会返回 1061045 错误码，可通过稍后重试解决。

## 请求

基本 |
---|---
HTTP URL | https://open.feishu.cn/open-apis/drive/v1/files/upload_finish
HTTP Method | POST
接口频率限制 | [特殊频控](https://open.feishu.cn/document/ukTMukTMukTM/uUzN04SN3QjL1cDN)
支持的应用类型 | Custom App、Store App
权限要求<br>**调用该 API 所需的权限。开启其中任意一项权限即可调用**<br>开启任一权限即可 | 查看、评论、编辑和管理云空间中所有文件(drive:drive)<br>上传、下载文件到云空间(drive:file)<br>上传文件(drive:file:upload)

### 请求头

名称 | 类型 | 必填 | 描述
---|---|---|---
Authorization | string | 是 | `tenant_access_token`<br>或<br>`user_access_token`<br>**值格式**："Bearer `access_token`"<br>**示例值**："Bearer u-7f1bcd13fc57d46bac21793a18e560"<br>[了解更多：如何选择与获取 access token](https://open.feishu.cn/document/uAjLw4CM/ugTN1YjL4UTN24CO1UjN/trouble-shooting/how-to-choose-which-type-of-token-to-use)
Content-Type | string | 是 | **固定值**："application/json; charset=utf-8"

### 请求体

名称 | 类型 | 必填 | 描述
---|---|---|---
upload_id | string | 是 | 分片上传事务 ID。通过调用[分片上传文件-预上传](https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/reference/drive-v1/file/upload_prepare)接口获取。<br>**示例值**："7111211691345512356"
block_num | int | 是 | 分片的数量。通过调用[分片上传文件-预上传](https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/reference/drive-v1/file/upload_prepare)接口获取。<br>**示例值**：1

### 请求体示例
```json
{
    "upload_id": "7111211691345512356",
    "block_num": 1
}
```

## 响应

### 响应体

名称 | 类型 | 描述
---|---|---
code | int | 错误码，非 0 表示失败
msg | string | 错误描述
data | \- | \-
file_token | string | 新创建的文件token

### 响应体示例
```json
{
    "code": 0,
    "msg": "Success",
    "data": {
        "file_token": "boxcnrHpsg1QDqXAAAyachabcef"
    }
}
```

### 错误码

HTTP状态码 | 错误码 | 描述 | 排查建议
---|---|---|---
200 | 1061001 | internal error. | 服务内部错误，包括超时，错误码没处理。
400 | 1061002 | params error. | 请检查请求参数是否正确。
404 | 1061003 | not found. | 请确认对应资源是否存在。
403 | 1061004 | forbidden. | 当前调用身份没有文件或文件夹的阅读或编辑等权限。请参考以下方式解决：<br>- 若上传素材，请确保当前调用身份具有目标云文档的编辑权限<br>- 若上传文件，请确保当前调用身份具有文件夹的编辑权限<br>- 若对文件或文件夹进行增删改等操作，请确保调用身份具有足够文档权限：<br>- 对于新建文件接口，调用身份需要有目标文件夹的编辑权限<br>- 对于复制文件接口，调用身份需要有文件的阅读或编辑权限、并且具有目标文件夹的编辑权限<br>- 对于移动文件接口，调用身份需要有被移动文件的可管理权限、被移动文件所在位置的编辑权限、目标位置的编辑权限<br>- 对于删除文件接口，调用身份需要具有以下两种权限之一：<br>- 该应用或用户是文件所有者并且具有该文件所在父文件夹的编辑权限<br>- 该应用或用户并非文件所有者，但是该文件所在父文件夹的所有者或者拥有该父文件夹的所有权限（full access）<br>了解开通权限步骤，参考[如何为应用开通云文档相关资源的权限](https://open.feishu.cn/document/uAjLw4CM/ugTN1YjL4UTN24CO1UjN/trouble-shooting/how-to-add-permissions-to-app)。
400 | 1061101 | file quota exceeded. | 租户容量超限，请确保租户有足够容量进行上传。
400 | 1061021 | upload id expire. | 上传事务过期，请重头开始上传。
500 | 1061022 | file version conflict. | 文件版本号冲突。
400 | 1061041 | parent node has been deleted. | 请确认上传点未被删除。
400 | 1061042 | parent node out of limit. | 在当前上传点上传过多素材，请更换上传点。
400 | 1061043 | file size beyond limit. | 请检查文件大小以避免超出限制。详情参考飞书帮助中心[云盘文件上传大小限制](https://www.feishu.cn/hc/zh-CN/articles/360049067549)。
400 | 1061044 | parent node not exist. | `parent_node` 不存在。请确认上传点 token 是否有误：<br>- 对于上传文件接口，请参考[文件夹 token 获取方式](https://open.feishu.cn/document/ukTMukTMukTM/ugTNzUjL4UzM14CO1MTN/folder-overview#-717d325)确认是否填写了正确的文件夹 token<br>- 对于上传素材接口，请参考[上传点类型和上传点 token](https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/reference/drive-v1/media/introduction#cc82be3c) 确认 `parent_node` 是否填写正确
200 | 1061045 | can retry. | 内部可重试错误，请稍后重试。
400 | 1061061 | user quota exceeded. | 个人容量超限，请确保个人有足够容量进行上传。
403 | 1061073 | no scope auth. | 没有申请接口权限。
403 | 1061500 | mount node point kill | 挂载点不存在。
400 | 1062007 | upload user not match. | 请确保当前请求身份和上传任务的身份为同一个。
400 | 1062010 | block missing, please upload all blocks. | 部分文件分片缺失，请确保所有文件分片上传完成。
400 | 1062505 | parent node out of size. | 云空间中所有层级的节点总和超限。上限为 40 万个，请检查节点数量。了解更多，参考[云空间概述](https://open.feishu.cn/document/ukTMukTMukTM/uUDN04SN0QjL1QDN/files/guide/introduction)。
400 | 1062506 | parent node out of depth. | 云空间目录深度超限制（15限制）。
400 | 1062507 | parent node out of sibling num. | 云空间中根目录或文件夹的单层节点超限。上限为 1500 个，你可通过将文件新建到不同文件夹中解决。
