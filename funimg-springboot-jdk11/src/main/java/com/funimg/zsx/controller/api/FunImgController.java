package com.funimg.zsx.controller.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.funimg.zsx.entity.FunPanoramaImage;
import com.funimg.zsx.service.FunAlbumService;
import com.funimg.zsx.service.PanoramaService;
import com.funimg.zsx.service.VideoService;
import com.funimg.zsx.util.HttpUtil;
import com.funimg.zsx.util.PageData;
import com.funimg.zsx.vo.FunPanorama;
import com.funimg.zsx.vo.app.*;
import com.funimg.zsx.vo.json.JsonData;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * Created by highness on 2018/5/10 0010.
 */
@RequestMapping(path = "/api")
@RestController
public class FunImgController {

    /**
     * 配置文件中的配置
     */
    @Value("${miniapp.appId}")
    private String appId;
    @Value("${miniapp.appsecret}")
    private String appsecret;

    @Autowired
    private FunAlbumService funAlbumService;
    @Autowired
    private VideoService videoService;
    @Autowired
    private PanoramaService panoramaService;

    @GetMapping("/lists")
    public PageData getUserList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        HashMap<String, Object> search = Maps.newHashMap();
        search.put("del", 0);
        search.put("status", 0);
        PageData<AlbumList> pageData = funAlbumService.getFunAlbumPageList(search, pageNum, pageSize);
        return pageData;
    }


    @GetMapping("/albumData")
    public AlbumDetail getAlbumData(
            @RequestParam(defaultValue = "0") Long albumId
    ) {
        AlbumDetail albumDetail = funAlbumService.getAlbumData(albumId);
        return albumDetail;
    }


    @GetMapping("/hotImgs")
    public PageData getHotImageList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        HashMap<String, Object> search = Maps.newHashMap();
        search.put("del", 0);
        PageData<AlbumData> pageData = funAlbumService.getFunHotImagePageList(search, pageNum, pageSize);
        return pageData;
    }

    @GetMapping("/imgComment")
    public ImageComment getCommentData(
            @RequestParam(defaultValue = "0") Long id
    ) {
        ImageComment comment = funAlbumService.getImageComment(id);
        return comment;
    }


    @GetMapping("/getOpenId")
    public JsonData getOpenId(
//            @ApiParam(value = "code")
            @RequestParam(value = "code") String code
    ) {
        try {
            String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" +
                    appId + "&secret=" +
                    appsecret + "&js_code=" +
                    code + "&grant_type=authorization_code";
            String str = HttpUtil.httpGet(url);
            JSONObject object = JSONObject.parseObject(str);
            String openid = object.getString("openid");
            return JsonData.returnObject(openid);
        } catch (Exception e) {
            return JsonData.fail("");
        }
    }

    @PostMapping("addComment")
    public JsonData addFavorite(
            @RequestParam(value = "openid") String openid,
            @RequestParam(value = "toOpenid", defaultValue = "") String toOpenid,
            @RequestParam(value = "nickName", defaultValue = "") String nickName,
            @RequestParam(value = "head", defaultValue = "") String head,
            @RequestParam(value = "text", defaultValue = "") String text,
            @RequestParam(value = "id", defaultValue = "0") Long id
    ) {
        if (StringUtils.isBlank(openid)) {
            return JsonData.fail("");
        }
        if (StringUtils.isBlank(text)) {
            return JsonData.fail("");
        }
        Comment comment = new Comment();

        comment.setFromUser(openid);
        comment.setToUser(toOpenid);
        comment.setNickName(nickName);
        comment.setHeadImg(head);
        comment.setAlbumDetailId(id);
        comment.setText(text);

        return funAlbumService.addComment(comment);
    }


    @GetMapping("/videos")
    public PageData getVideoList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        HashMap<String, Object> search = Maps.newHashMap();
        search.put("del", 0);
        PageData<VideoData> pageData = videoService.getFunVideoDataPageList(search, pageNum, pageSize);
        return pageData;
    }

    @GetMapping("/videoDetail")
    public VideoData getVideoDetail(@RequestParam(defaultValue = "0") Long id) {
        return videoService.getVideoData(id);
    }


    @GetMapping("/pano/list")
    public PageData getPanoList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageData<FunPanoramaImage> pageData = panoramaService.getFunPanoramaImagePageList(new HashMap<String, Object>(1), pageNum, pageSize);
        PageData<FunPanorama> resultData = new PageData<>(pageData.getTotal(), pageData.getPages(), null);

        List<FunPanoramaImage> list = pageData.getList();
        if (CollectionUtils.isNotEmpty(list)) {
//            List<FunPanoramaImage> images = Lists.newArrayList();

//            for (FunPanoramaImage panoramaImage : list) {
//                FunPanoramaImage image = new FunPanoramaImage();
//
//                image.setId(panoramaImage.getId());
//                image.setTitle(panoramaImage.getTitle());
////                使用腾讯云图片处理缩放功能  todo 图片处理功能为收费项
////                imageMogr2/thumbnail/!10p
//                image.setThumbnail(panoramaImage.getThumbnail());
//
//                images.add(image);
//            }

            List<FunPanorama> funPanoramas = JSONArray.parseArray(JSON.toJSONString(list), FunPanorama.class);

            resultData.setList(funPanoramas);
        }

        /*
        JsonTable jsonTable = null;
        HashMap<String, Object> search = Maps.newHashMap();
        if (StringUtils.isNotBlank(del)) {
            search.put("del", Integer.parseInt(del));
        }
        PageData<FunPanoramaImage> pageData = panoramaService.getFunPanoramaImagePageList(search, pageNum, pageSize);
        if (pageData.getList() != null) {
            List<FunPanoramaImage> list = pageData.getList();

            List<FunPanorama> resultList = JSONArray.parseArray(JSON.toJSONString(list), FunPanorama.class);

            jsonTable = JsonTable.toTable(pageData.getTotal(), resultList);
        }
        return jsonTable;
         */

        return resultData;
    }

    @GetMapping("/panoDetail")
    public JsonData getPanoDetail(@RequestParam(defaultValue = "0") Long id) {
        return panoramaService.getPanoramaImage(id);
    }
}
