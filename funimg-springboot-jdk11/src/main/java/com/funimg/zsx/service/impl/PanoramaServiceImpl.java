package com.funimg.zsx.service.impl;

import com.funimg.zsx.ext.FunPanoramaImageDao;
import com.funimg.zsx.vo.json.JsonData;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.funimg.zsx.entity.FunPanoramaImage;
import com.funimg.zsx.service.PanoramaService;
import com.funimg.zsx.util.PageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PanoramaServiceImpl implements PanoramaService {

    @Autowired
    private FunPanoramaImageDao funPanoramaImageDao;

    @Override
    public PageData<FunPanoramaImage> getFunPanoramaImagePageList(Map<String, Object> search, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize, "id desc");
        List<FunPanoramaImage> list = funPanoramaImageDao.selectByParams(search);
        PageInfo pageInfo = new PageInfo(list);

        PageData pageData = new PageData(pageInfo.getTotal(), pageInfo.getPages(), list);
        pageData.setPageNum(pageInfo.getPageNum());
        pageData.setPageSize(pageInfo.getPageSize());
        pageData.setPages(pageInfo.getPages());
        return pageData;
    }

    @Override
    public JsonData addFunPanoramaImage(FunPanoramaImage funPanoramaImage) {
        int i = funPanoramaImageDao.insertSelective(funPanoramaImage);
        if (i == 1) {
            return JsonData.returnObject(funPanoramaImage);
        }
        return JsonData.fail("保存失败");
    }

    @Override
    public JsonData getPanoramaImage(Long id) {
        FunPanoramaImage funPanoramaImage = funPanoramaImageDao.selectByPrimaryKey(id);
        HashMap<String, String> map = Maps.newHashMap();
        if (funPanoramaImage != null) {
            map.put("title", funPanoramaImage.getTitle());
            map.put("imgUrl", funPanoramaImage.getImgUrl());
            return JsonData.returnObject(map);
        }
        return JsonData.fail("null");
    }
}
