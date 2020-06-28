package com.example.listen;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**********************************************************
 * 源URL https://fanyi-api.baidu.com/api/trans/vip/translate
 * 参数如下
 * String q  英文单词/中文
 * String from  原始语种 zh中文/eh英文
 * String to 目标语种   zh中文/eh英文
 * String from zh中文/eh英文
 * String appid 你的appid
 * String salt 随机数（整形转字符串）
 * String sign 签名 32位字母小写MD5编码的 appid+q+salt+密钥
 **********************************************************/

public interface BaiduTranslateService {

    /********************************
     * 翻译接口
     * 表示提交表单数据，@Field注解键名
     * 适用于数据量少的情况
     ********************************/

    @POST("translate")
    @FormUrlEncoded
    Call<RespondBean> translate(@Field("q") String q, @Field("from") String from, @Field("to") String to, @Field("appid") String appid, @Field("salt") String salt,
                                @Field("sign") String sign);

}
