package com.pikacho;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SougouImgProcessor {
    private String url;
    private SougouImgPipeline pipeline;
    private List<JSONObject> dataList;
    private List<String> urlList;
    private String word;

    public SougouImgProcessor(String url, String word) {
        this.url = url;
        this.word = word;
        this.pipeline = new SougouImgPipeline();
        this.dataList = new ArrayList<>();
        this.urlList = new ArrayList<>();
    }

    public void process(int idx, int size) {
        // 格式化访问地址，添加请求参数
        // https://pic.sogou.com/napi/pc/searchList?mode=1&start=%s&xml_len=%s&query=%s 用idx size参数只来替换访问地址中的%s
        String res = HttpClientUtils.get(String.format(this.url, idx, size, this.word));
        // 将返回的json数据转换成object
        JSONObject object = JSONObject.parseObject(res);
        List<JSONObject> items = (List<JSONObject>) ((JSONObject) object.get("data")).get("items");
        // 提取item里保存的图片地址，放入urlList列表中
        for (JSONObject item : items) {
            this.urlList.add(item.getString("picUrl"));
        }
        this.dataList.addAll(items);
    }

    // 下载操作
    public void pipelineData() {
        // 多线程下载
        pipeline.processSync(this.urlList, this.word);
    }

    public static void main(String[] args) {
        String url = "https://pic.sogou.com/napi/pc/searchList?mode=1&start=%s&xml_len=%s&query=%s";
        SougouImgProcessor processor = new SougouImgProcessor(url, "美女");

        int start = 0, size = 50, limit = 1000; // 定义爬取开始索引、每次爬取数量、总共爬取数量

        for (int i = start; i < start + limit; i += size)
            processor.process(i, size);

        processor.pipelineData();

    }
}
