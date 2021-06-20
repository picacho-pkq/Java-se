package com.pikacho;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    // 测试HttpClientUtils类
    @Test
    public void testHttpClientUtils(){
        String url = "https://pic.sogou.com/napi/pc/searchList?mode=1&start=0&xml_len=50&query=美女";
        String res = HttpClientUtils.get(url);
        System.out.println(res);
    }

    // 测试downloadImg方法
    @Test
    public void testDownloadImg(){
        List<String> data = new ArrayList<>();
        data.add("https://t1.hxzdhn.com/uploads/tu/202003/9999/b80d03c3f4.jpg");
        SougouImgPipeline sip = new SougouImgPipeline();
        sip.process(data, "美女");
    }


}
