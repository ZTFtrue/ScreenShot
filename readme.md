#

截图+百度识别

这个类`BaiduOCR` 修改百度的`Key`

不能跨平台, `linux` 上不能用.

## Json 示例

```json
{
  "clientId": "",
  "tokenExpireTime": 0,
  "clientSecret": "",
  "accurateBasic": "https://aip.baidubce.com/rest/2.0/ocr/v1/accurate_basic",
  "accessToken": ""
}
```

## package

```shell
mvn clean compile assembly:single
```