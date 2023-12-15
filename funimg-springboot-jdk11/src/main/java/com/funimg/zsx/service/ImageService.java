package com.funimg.zsx.service;

import com.funimg.zsx.vo.json.JsonData;
import com.funimg.zsx.entity.FunImages;
import com.funimg.zsx.util.PageData;

import java.util.Map;

/**
 * Created by highness on 2018/5/26 0026.
 */
public interface ImageService {

    PageData<FunImages> getFunImagesPageList(Map<String, Object> search, Integer pageNum, Integer pageSize);

    JsonData addFunImages(FunImages funImages);
}
