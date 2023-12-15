package com.funimg.zsx.service.impl;

import com.funimg.zsx.vo.json.JsonData;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.funimg.zsx.entity.FunImages;
import com.funimg.zsx.ext.FunImagesDao;
import com.funimg.zsx.service.ImageService;
import com.funimg.zsx.util.PageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by highness on 2018/5/26 0026.
 */
@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private FunImagesDao funImagesDao;


    @Override
    public PageData<FunImages> getFunImagesPageList(Map<String, Object> search, Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize, "id desc");
        List<FunImages> funImages = funImagesDao.selectByParams(search);
        PageInfo pageInfo = new PageInfo(funImages);

        PageData pageData = new PageData(pageInfo.getTotal(), pageInfo.getPages(), funImages);
        pageData.setPageNum(pageInfo.getPageNum());
        pageData.setPageSize(pageInfo.getPageSize());
        return pageData;
    }

    @Override
    public JsonData addFunImages(FunImages funImages) {
        int i = funImagesDao.insertSelective(funImages);
        if (i == 1) {
            return JsonData.returnObject(funImages);
        }
        return JsonData.fail("保存失败");
    }
}
