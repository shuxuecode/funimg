package com.funimg.zsx.service;

import com.funimg.zsx.vo.json.JsonData;
import com.funimg.zsx.entity.FunPanoramaImage;
import com.funimg.zsx.util.PageData;

import java.util.Map;

public interface PanoramaService {

    PageData<FunPanoramaImage> getFunPanoramaImagePageList(Map<String, Object> search, Integer pageNum, Integer pageSize);

    JsonData addFunPanoramaImage(FunPanoramaImage funPanoramaImage);

    JsonData getPanoramaImage(Long id);
}
