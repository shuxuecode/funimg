package com.funimg.zsx.controller;

import com.funimg.zsx.vo.FunAlbumVO;
import com.funimg.zsx.vo.json.JsonData;
import com.funimg.zsx.vo.json.JsonTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.funimg.zsx.entity.FunAlbum;
import com.funimg.zsx.entity.FunAlbumDetail;
import com.funimg.zsx.service.FunAlbumService;
import com.funimg.zsx.util.PageData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by highness on 2018/5/25 0025.
 */
@RequestMapping(path = "/album")
@Controller
public class AlbumController {

    @Autowired
    private FunAlbumService funAlbumService;

    @PostMapping("/lists")
    @ResponseBody
    public JsonTable getList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        HashMap<String, Object> search = Maps.newHashMap();
        search.put("del", 0);
        PageData<FunAlbumVO> pageData = funAlbumService.queryFunAlbumPageList(search, pageNum, pageSize);
        JsonTable jsonTable = JsonTable.toTable(pageData.getTotal(), pageData.getList());
        return jsonTable;
    }


    /**
     * 添加专辑
     *
     * @param title
     * @param imgUrl
     * @param imgUuid
     * @param publish_date
     * @return
     */
    @PostMapping("add")
    @ResponseBody
    public JsonData addAlbum(
            @RequestParam(value = "title") String title,
            @RequestParam(value = "imgUrl") String imgUrl,
            @RequestParam(value = "imgUuid") String imgUuid,
            @RequestParam(value = "publish_date") String publish_date
    ) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime publishDate = DateTime.parse(publish_date, formatter);

        FunAlbum funAlbum = new FunAlbum();
        funAlbum.setPublishDate(publishDate.toDate());
        funAlbum.setTitle(title);
        funAlbum.setImgUuid(imgUuid);
        funAlbum.setImgUrl(imgUrl);
        funAlbum.setStatus(1);
        funAlbum.setDel(0);
        funAlbum.setCreateTime(new Date());
        funAlbum.setUpdateTime(new Date());
        funAlbum.setCreatorId("");
        funAlbum.setCreatorName("");
        funAlbum.setUpdaterId("");
        funAlbum.setUpdaterName("");
        return funAlbumService.addAlbum(funAlbum);
    }

    /**
     * 修改专辑的状态，发布/不发布
     *
     * @param albumId
     * @param status
     * @return
     */
    @PostMapping("updateAlbumStatus")
    @ResponseBody
    public JsonData updateAlbumStatus(
            @RequestParam(value = "albumId") Long albumId,
            @RequestParam(value = "status") Integer status
    ) {
        FunAlbum funAlbum = new FunAlbum();
        funAlbum.setId(albumId);
        funAlbum.setStatus(status);
        funAlbum.setUpdateTime(new Date());
//        funAlbum.setUpdaterId("");
//        funAlbum.setUpdaterName("");
        return funAlbumService.updAlbum(funAlbum);
    }


    /**
     * 获取专辑的详细列表
     *
     * @param albumId
     * @return
     */
    @PostMapping("albumDetailList")
    @ResponseBody
    public JsonTable getAlbumDetailList(
            @RequestParam(value = "albumId") Long albumId
    ) {
        return funAlbumService.getAlbumDetailList(albumId);
    }


    /**
     * 添加或修改 专辑详情
     *
     * @param id
     * @param albumId
     * @param title
     * @param imgUuids
     * @param imgSource
     * @return
     */
    @PostMapping("saveAlbumDetail")
    @ResponseBody
    public JsonData saveAlbumDetail(
            @RequestParam(value = "id", defaultValue = "0") Long id,
            @RequestParam(value = "albumId") Long albumId,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "imgUuids") String imgUuids,
            @RequestParam(value = "imgSource", defaultValue = "") String imgSource
    ) {
        FunAlbumDetail funAlbumDetail = new FunAlbumDetail();
        funAlbumDetail.setId(id);
        funAlbumDetail.setAlbumId(albumId);
        funAlbumDetail.setTitle(title);
        funAlbumDetail.setImgUuids(imgUuids);
        funAlbumDetail.setImgSource(imgSource);

        return funAlbumService.saveFunAlbumDetail(funAlbumDetail);
    }


    /**
     * 对图片排序
     *
     * @param ids
     * @return
     */
    @PostMapping("sortAlbumDetail")
    @ResponseBody
    public JsonData sortAlbumDetail(
            @RequestParam(value = "ids") String ids
    ) {
        List<FunAlbumDetail> albumDetailList = Lists.newArrayList();
        if (StringUtils.isNotBlank(ids)) {
            FunAlbumDetail funAlbumDetail;
            String[] split = ids.split(",");
            for (int i = 0; i < split.length; i++) {
                funAlbumDetail = new FunAlbumDetail();
                funAlbumDetail.setId(Long.valueOf(split[i]));
                funAlbumDetail.setSort(i + 1);
                funAlbumDetail.setUpdateTime(new Date());

                albumDetailList.add(funAlbumDetail);
            }
        }
        if (CollectionUtils.isNotEmpty(albumDetailList)) {
            return funAlbumService.updateAlbumDetailSort(albumDetailList);
        }
        return JsonData.fail("保存失败");
    }

    @PostMapping("delAlbumDetail")
    @ResponseBody
    public JsonData delAlbumDetail(
            @RequestParam(value = "id") String id
    ) {
        return funAlbumService.delAlbumDetail(Long.parseLong(id));
    }

    @PostMapping("albumDetailPageList")
    @ResponseBody
    public JsonTable getAlbumDetailPageList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        HashMap<String, Object> search = Maps.newHashMap();
        search.put("del", 0);
        return funAlbumService.getAlbumDetailPageList(search, pageNum, pageSize);
    }

    @PostMapping("hotImgList")
    @ResponseBody
    public JsonTable getHotImagePageList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        HashMap<String, Object> search = Maps.newHashMap();
        search.put("del", 0);
        return funAlbumService.getHotImagePageList(search, pageNum, pageSize);
    }

    @PostMapping("setHotImg")
    @ResponseBody
    public JsonData setHotImg(
            @RequestParam(value = "id") Long id
    ) {
        return funAlbumService.setHotImages(id);
    }
}
