package com.ruoyi.common.utils.aliyun;

import com.aliyun.darabonba.stream.Client;
import com.aliyun.ocr_api20210707.models.RecognizeBasicResponse;
import com.aliyun.ocr_api20210707.models.RecognizeGeneralStructureRequest;
import com.aliyun.ocr_api20210707.models.RecognizeGeneralStructureResponse;
import com.aliyun.tea.*;
import com.google.gson.Gson;

import java.io.InputStream;
import java.util.Arrays;

public class OCRImage {

    private static final String ALIBABA_CLOUD_ACCESS_KEY_ID = "LTAI5tPJpcvuvZgZZcSXmAdD";
    private static final String ALIBABA_CLOUD_ACCESS_KEY_SECRET = "4sQHaxIX8QFUpXcRhng1RNe1AlccZe";

    public static com.aliyun.ocr_api20210707.Client createClient() throws Exception {
        // 工程代码建议使用更安全的无AK方式，凭据配置方式请参见：https://help.aliyun.com/document_detail/378657.html。
        com.aliyun.credentials.Client credential = new com.aliyun.credentials.Client();
//        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
//                .setCredential(credential);

        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                // 您的AccessKey ID
                .setAccessKeyId(ALIBABA_CLOUD_ACCESS_KEY_ID)
                // 您的AccessKey Secrett
                .setAccessKeySecret(ALIBABA_CLOUD_ACCESS_KEY_SECRET);

        // Endpoint 请参考 https://api.aliyun.com/product/ocr-api
        config.endpoint = "ocr-api.cn-hangzhou.aliyuncs.com";
        return new com.aliyun.ocr_api20210707.Client(config);
    }
    public static String recognizeBasic(String filePath) throws Exception {
        String result = "{}";
//        String filePath = "/Users/luohd/Documents/GitHub/mega-ems/uploadPath/upload/tenant5/2025/05/08/NEW_20250508154131A051.png";

        com.aliyun.ocr_api20210707.Client client = OCRImage.createClient();

        InputStream bodyStream = Client.readFromFilePath(filePath);
//                .setUrl("https://www.mihoot.cn/prod-api/common/download/resource/upload/tenant3/2025/05/09/NEW_20250509091944A004.png");
        RecognizeGeneralStructureRequest recognizeBasicRequest = new RecognizeGeneralStructureRequest()
                .setBody(bodyStream).setKeys(Arrays.asList(
                        "name",
                        "mobile",
                        "telephone",
                        "email",
                        "address",
                        "office",
                        "company"
                ));
        try {
            // 复制代码运行请自行打印 API 的返回值
            RecognizeGeneralStructureResponse response = client.recognizeGeneralStructureWithOptions(recognizeBasicRequest, new com.aliyun.teautil.models.RuntimeOptions());

            result = new Gson().toJson(response.body);
            System.out.println(result);

        } catch (TeaException error) {
            // 此处仅做打印展示，请谨慎对待异常处理，在工程项目中切勿直接忽略异常。
            // 错误 message
            System.out.println(error.getMessage());
            // 诊断地址
            System.out.println(error.getData().get("Recommend"));
            com.aliyun.teautil.Common.assertAsString(error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            // 此处仅做打印展示，请谨慎对待异常处理，在工程项目中切勿直接忽略异常。
            // 错误 message
            System.out.println(error.getMessage());
            // 诊断地址
            System.out.println(error.getData().get("Recommend"));
            com.aliyun.teautil.Common.assertAsString(error.message);
        }

        return result;
    }

}
