package com.funimg.zsx.service;

import com.funimg.zsx.vo.app.VideoData;
import com.funimg.zsx.vo.json.JsonData;
import com.funimg.zsx.entity.FunVideo;
import com.funimg.zsx.util.PageData;

import java.util.Map;

/**
 * Created by highness on 2018/5/26 0026.
 */
public interface VideoService {

    PageData<VideoData> getFunVideoDataPageList(Map<String, Object> search, Integer pageNum, Integer pageSize);


//    *******************************************************************
//    web页面接口
//    *******************************************************************


    PageData<FunVideo> getFunVideoPageList(Map<String, Object> search, Integer pageNum, Integer pageSize);

    JsonData addFunVideo(FunVideo funVideo);

    VideoData getVideoData(Long id);
}
