package com.funimg.zsx.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.funimg.zsx.entity.*;
import com.funimg.zsx.ext.*;
import com.funimg.zsx.service.FunAlbumService;
import com.funimg.zsx.util.PageData;
import com.funimg.zsx.vo.FunAlbumDetailVO;
import com.funimg.zsx.vo.FunAlbumVO;
import com.funimg.zsx.vo.app.*;
import com.funimg.zsx.vo.json.JsonData;
import com.funimg.zsx.vo.json.JsonTable;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by highness on 2018/5/10 0010.
 */
@Service
public class FunAlbumServiceImpl implements FunAlbumService {

    @Autowired
    private FunAlbumDao funAlbumDao;
    @Autowired
    private FunAlbumDetailDao funAlbumDetailDao;
    @Autowired
    private FunImagesDao funImagesDao;
    @Autowired
    private FunHotImagesDao funHotImagesDao;
    @Autowired
    private FunWxUserDao funWxUserDao;
    @Autowired
    private FunCommentDao funCommentDao;


    @Override
    public PageData<AlbumList> getFunAlbumPageList(Map<String, Object> search, Integer pageNum, Integer pageSize) {

        SimpleDateFormat sdf = new SimpleDateFormat("MMdd");

        PageHelper.startPage(pageNum, pageSize, "publish_date desc");
        List<FunAlbum> funAlbums = funAlbumDao.selectByParams(search);
        PageInfo pageInfo = new PageInfo(funAlbums);

        List<AlbumList> albumLists = Lists.newArrayList();
        AlbumList albumList = null;
        for (FunAlbum funAlbum : funAlbums) {
            albumList = new AlbumList();

            albumList.setAlbumId(funAlbum.getId());
            String title = sdf.format(funAlbum.getPublishDate()) + "：" + funAlbum.getTitle();
            albumList.setTitle(title);
            albumList.setImgUrl(funAlbum.getImgUrl());

            albumLists.add(albumList);
        }

        PageData pageData = new PageData(pageInfo.getTotal(), pageInfo.getPages(), albumLists);
        pageData.setPageNum(pageInfo.getPageNum());
        pageData.setPageSize(pageInfo.getPageSize());
        return pageData;
    }

    @Override
    public PageData<FunAlbumVO> queryFunAlbumPageList(Map<String, Object> search, Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize, "publish_date desc");
        List<FunAlbum> funAlbums = funAlbumDao.selectByParams(search);
        PageInfo pageInfo = new PageInfo(funAlbums);

        List<FunAlbumVO> funAlbumVOS = JSONArray.parseArray(JSON.toJSONString(funAlbums), FunAlbumVO.class);
        PageData pageData = new PageData(pageInfo.getTotal(), pageInfo.getPages(), funAlbumVOS);
        pageData.setPageNum(pageInfo.getPageNum());
        pageData.setPageSize(pageInfo.getPageSize());
        return pageData;
    }

    /**
     * 给小程序提供的接口
     *
     * @param albumId
     * @return
     */
    @Override
    public AlbumDetail getAlbumData(Long albumId) {

        FunAlbum funAlbum = funAlbumDao.selectByPrimaryKey(albumId);
        if (funAlbum == null) {
            return null;
        }
        List<AlbumData> resultData = Lists.newArrayList();

        List<FunAlbumDetail> funAlbumDetails = funAlbumDetailDao.selectByAlbumId(albumId);
        if (CollectionUtils.isEmpty(funAlbumDetails)) {
            return null;
        }

        AlbumData albumData;
        for (FunAlbumDetail funAlbumDetail : funAlbumDetails) {
            albumData = new AlbumData();

            albumData.setId(funAlbumDetail.getId());
            albumData.setTitle(funAlbumDetail.getTitle());

            albumData = this.setAlbumDataImageList(funAlbumDetail, albumData);

            resultData.add(albumData);
        }

        AlbumDetail albumDetail = new AlbumDetail(albumId, funAlbum.getTitle(), resultData);

        Long lastAlbumId = funAlbumDao.getLastAlbumId(funAlbum.getPublishDate());
        Long nextAlbumId = funAlbumDao.getNextAlbumId(funAlbum.getPublishDate());

        albumDetail.setLastAlbumId(lastAlbumId);
        albumDetail.setNextAlbumId(nextAlbumId);
        return albumDetail;
    }

    /**
     * 转换图片的链接url信息
     *
     * @param funAlbumDetail
     * @param albumData
     * @return
     */
    public AlbumData setAlbumDataImageList(FunAlbumDetail funAlbumDetail, AlbumData albumData) {
        String imgUuids = funAlbumDetail.getImgUuids();
        String[] imgSources = null;
        String imgSource = funAlbumDetail.getImgSource();
        if (StringUtils.isNotBlank(imgSource)) {
            imgSources = imgSource.split(",");
        }
        String[] imageIds = imgUuids.split(",");
        for (int i = 0; i < imageIds.length; i++) {
            String imageId = imageIds[i];
            FunImages funImages = funImagesDao.selectByPrimaryKey(Long.valueOf(imageId));
            if (funImages == null) {
                continue;
            }

            String imgUrl = funImages.getImgUrl();
            if (imgSources != null) {
                String imgSource1 = imgSources[i];
                if ("2".equals(imgSource1)) {
                    imgUrl = funImages.getSinaimgUrl();
                } else if ("3".equals(imgSource1)) {
                    imgUrl = funImages.getQiniuImgUrl();
                }
            }

            AlbumData.ImageList imageList = AlbumData.imgListBuilder()
                    .imgUrl(imgUrl)
                    .type(funImages.getImgType())
                    .width(funImages.getWidth())
                    .height(funImages.getHeight());

            albumData.addImgList(imageList);
        }
        return albumData;
    }


    @Override
    public PageData<AlbumData> getFunHotImagePageList(Map<String, Object> search, Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize, "update_time desc");
        List<FunHotImages> funHotImages = funHotImagesDao.selectByParams(search);

        List<Long> linkIds = Lists.transform(funHotImages, new Function<FunHotImages, Long>() {
            @Override
            public Long apply(FunHotImages funHotImages) {
                return funHotImages.getLinkId();
            }
        });
        HashMap<String, Object> params = Maps.newHashMap();
        params.put("del", 0);
        params.put("ids", linkIds);
        List<FunAlbumDetail> funAlbumDetails = funAlbumDetailDao.selectByParams(params);

        ImmutableMap<Long, FunAlbumDetail> funAlbumDetailMap = Maps.uniqueIndex(funAlbumDetails, new Function<FunAlbumDetail, Long>() {
            @Override
            public Long apply(FunAlbumDetail funAlbumDetail) {
                return funAlbumDetail.getId();
            }
        });

        ArrayList<AlbumData> albumDatas = Lists.newArrayList();
        AlbumData albumData;
        for (FunHotImages funHotImage : funHotImages) {
            albumData = new AlbumData();

            FunAlbumDetail funAlbumDetail = funAlbumDetailMap.get(funHotImage.getLinkId());
            if (funAlbumDetail == null) {
                continue;
            }

            albumData.setId(funAlbumDetail.getId());
            albumData.setTitle(funAlbumDetail.getTitle());

            albumData = this.setAlbumDataImageList(funAlbumDetail, albumData);

            albumDatas.add(albumData);
        }

        PageInfo pageInfo = new PageInfo(funHotImages);

        PageData pageData = new PageData(pageInfo.getTotal(), pageInfo.getPages(), albumDatas);
        pageData.setPageNum(pageInfo.getPageNum());
        pageData.setPageSize(pageInfo.getPageSize());
        return pageData;
    }


    @Override
    public ImageComment getImageComment(Long id) {
        ImageComment comment = new ImageComment();

        FunAlbumDetail funAlbumDetail = funAlbumDetailDao.selectByPrimaryKey(id);

        AlbumData albumData = new AlbumData();

        albumData.setId(funAlbumDetail.getId());
        albumData.setTitle(funAlbumDetail.getTitle());

        albumData = this.setAlbumDataImageList(funAlbumDetail, albumData);

        comment.setImgData(albumData);
        List<Comment> commentList = this.getImageCommentList(id);
        if (CollectionUtils.isNotEmpty(commentList)) {
            comment.setComments(commentList);
        }
        return comment;
    }


    @Override
    public List<Comment> getImageCommentList(Long albumDetailId) {
        List<Comment> commentResult = Lists.newArrayList();
        HashMap<String, Object> params = Maps.newHashMap();
        params.put("del", 0);
        params.put("albumDetailId", albumDetailId);
//        PageHelper.startPage(1, 50, "create_time desc");
        PageHelper.startPage(1, 50);
        List<FunComment> funComments = funCommentDao.selectByParams(params);

        if (CollectionUtils.isEmpty(funComments)) {
            return commentResult;
        }

        Set<String> openIdSet = Sets.newHashSet();
        for (FunComment funComment : funComments) {
            openIdSet.add(funComment.getOpenid());
            if (StringUtils.isNotBlank(funComment.getToOpenid())) {
                openIdSet.add(funComment.getToOpenid());
            }
        }

        params.clear();
        params.put("openids", Lists.newArrayList(openIdSet));
        List<FunWxUser> funWxUsers = funWxUserDao.selectByParams(params);

        HashMap<String, FunWxUser> wxUserMap = Maps.newHashMap();

        for (FunWxUser funWxUser : funWxUsers) {
            wxUserMap.put(funWxUser.getOpenid(), funWxUser);
        }

        Comment comment;
        for (FunComment funComment : funComments) {
            comment = new Comment();

            comment.setAlbumDetailId(albumDetailId);
            comment.setCommentId(funComment.getId());
            comment.setFromUser(funComment.getOpenid());
            comment.setToUser(funComment.getToOpenid());
            comment.setText(funComment.getText());
//            comment.setCreateTime(String.valueOf(funComment.getCreateTime()));

            FunWxUser funWxUser = wxUserMap.get(funComment.getOpenid());
            if (funWxUser != null) {
                comment.setNickName(funWxUser.getNickName());
                comment.setHeadImg(funWxUser.getAvatarUrl());

                if (StringUtils.isNotBlank(funComment.getToOpenid())) {
                    funWxUser = wxUserMap.get(funComment.getToOpenid());
                    if (funWxUser != null) {
                        comment.setToNickName(funWxUser.getNickName());
                    }
                }

                commentResult.add(comment);
            }
        }
        return commentResult;
    }

    @Override
    public JsonData addComment(Comment comment) {
        HashMap<String, Object> params = Maps.newHashMap();
        params.put("openid", comment.getFromUser());
        List<FunWxUser> funWxUsers = funWxUserDao.selectByParams(params);
        FunWxUser funWxUser;
        if (CollectionUtils.isEmpty(funWxUsers)) {
            funWxUser = new FunWxUser();

            funWxUser.setOpenid(comment.getFromUser());
            funWxUser.setNickName(comment.getNickName());
            funWxUser.setAvatarUrl(comment.getHeadImg());
            funWxUser.setCountry("");
            funWxUser.setProvince("");
            funWxUser.setCity("");
            funWxUser.setLanguage("");
            funWxUser.setGender(0);
            funWxUser.setDel(0);
            funWxUser.setCreateTime(new Date());
            funWxUser.setUpdateTime(new Date());
            funWxUser.setCreatorId("");
            funWxUser.setCreatorName("");
            funWxUser.setUpdaterId("");
            funWxUser.setUpdaterName("");


            funWxUserDao.insertSelective(funWxUser);
        } else {
            funWxUser = funWxUsers.get(0);
        }

        FunComment funComment = new FunComment();

        funComment.setAlbumDetailId(comment.getAlbumDetailId());
        funComment.setOpenid(comment.getFromUser());
        funComment.setToOpenid(comment.getToUser());
        funComment.setText(comment.getText());
        funComment.setDel(0);
        funComment.setCreateTime(new Date());
        funComment.setUpdateTime(new Date());
        funComment.setCreatorId("");
        funComment.setCreatorName("");
        funComment.setUpdaterId("");
        funComment.setUpdaterName("");

        funCommentDao.insertSelective(funComment);

        return JsonData.returnObject(comment);
    }


    @Override
    public JsonData addAlbum(FunAlbum funAlbum) {
        funAlbumDao.insertSelective(funAlbum);
        return JsonData.success("保存成功");
    }

    @Override
    public JsonData updAlbum(FunAlbum funAlbum) {
        funAlbumDao.updateByPrimaryKeySelective(funAlbum);
        return JsonData.success("更新成功");
    }

    @Override
    public JsonTable getAlbumDetailList(Long albumId) {
        List<FunAlbumDetail> funAlbumDetails = funAlbumDetailDao.selectByAlbumId(albumId);
        if (CollectionUtils.isEmpty(funAlbumDetails)) {
            return JsonTable.toTable(0L, Lists.newArrayList());
        }
        List<FunAlbumDetailVO> albumDetailVOList = this.transferAlbumDetailList(funAlbumDetails);
        return JsonTable.toTable(Long.valueOf(String.valueOf(albumDetailVOList.size())), albumDetailVOList);
    }

    @Override
    public JsonTable getAlbumDetailPageList(Map<String, Object> search, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize, "update_time desc");
        List<FunAlbumDetail> albumDetailList = funAlbumDetailDao.selectByParams(search);
        if (CollectionUtils.isEmpty(albumDetailList)) {
            return JsonTable.toTable(0L, Lists.newArrayList());
        }
        PageInfo pageInfo = new PageInfo(albumDetailList);

        List<FunAlbumDetailVO> albumDetailVOList = this.transferAlbumDetailList(albumDetailList);

        return JsonTable.toTable(Long.valueOf(String.valueOf(pageInfo.getTotal())), albumDetailVOList);
    }

    /**
     * 把专辑详情转换为VO对象
     *
     * @param albumDetailList
     * @return
     */
    public List<FunAlbumDetailVO> transferAlbumDetailList(List<FunAlbumDetail> albumDetailList) {
        List<Long> funImageIds = Lists.newArrayList();
        for (FunAlbumDetail funAlbumDetail : albumDetailList) {
            String imgUuids = funAlbumDetail.getImgUuids();
            String[] imageIds = imgUuids.split(",");
            for (String imageId : imageIds) {
                funImageIds.add(Long.valueOf(imageId));
            }
        }

        Map<String, Object> params = Maps.newHashMap();
        params.put("ids", funImageIds);
        List<FunImages> funImagesList = funImagesDao.selectByParams(params);
        Map<Long, FunImages> funImagesMap = Maps.uniqueIndex(funImagesList, new Function<FunImages, Long>() {
            @Override
            public Long apply(FunImages funImages) {
                return funImages.getId();
            }
        });

        List<FunAlbumDetailVO> albumDetailVOList = Lists.newArrayList();
        FunAlbumDetailVO funAlbumDetailVO;
        for (FunAlbumDetail funAlbumDetail : albumDetailList) {
            funAlbumDetailVO = JSONObject.parseObject(JSON.toJSONString(funAlbumDetail), FunAlbumDetailVO.class);

            List<FunImages> images = Lists.newArrayList();
            String imgUuids = funAlbumDetailVO.getImgUuids();
            if (StringUtils.isNotBlank(imgUuids)) {
                String[] imageIds = imgUuids.split(",");
                for (String imageId : imageIds) {
                    FunImages funImages = funImagesMap.get(Long.valueOf(imageId));
                    if (funImages == null) {
                        continue;
                    }
                    images.add(funImages);
                }
            }
            funAlbumDetailVO.setImages(images);

            albumDetailVOList.add(funAlbumDetailVO);
        }
        return albumDetailVOList;
    }

    @Override
    public JsonTable getHotImagePageList(Map<String, Object> search, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize, "update_time desc");
        List<FunHotImages> funHotImages = funHotImagesDao.selectByParams(search);
        if (CollectionUtils.isEmpty(funHotImages)) {
            return JsonTable.toTable(0L, Lists.newArrayList());
        }
        PageInfo pageInfo = new PageInfo(funHotImages);
        return JsonTable.toTable(Long.valueOf(String.valueOf(pageInfo.getTotal())), funHotImages);
    }

    @Override
    public JsonData saveFunAlbumDetail(FunAlbumDetail funAlbumDetail) {
        Long id = funAlbumDetail.getId();

        funAlbumDetail.setUpdateTime(new Date());
        funAlbumDetail.setUpdaterId("");
        funAlbumDetail.setUpdaterName("");

        if (id != null && id != 0L) {
            funAlbumDetailDao.updateByPrimaryKeySelective(funAlbumDetail);
        } else {
            funAlbumDetail.setSort(0);
            funAlbumDetail.setDel(0);
            funAlbumDetail.setCreateTime(new Date());
            funAlbumDetail.setCreatorId("");
            funAlbumDetail.setCreatorName("");

            funAlbumDetailDao.insertSelective(funAlbumDetail);

            this.updateAlbumDetailImageListDelStatus(funAlbumDetail, 0);

        }
        return JsonData.returnObject(funAlbumDetail);
    }

    public void updateAlbumDetailImageListDelStatus(FunAlbumDetail funAlbumDetail, Integer del){
        String imgUuids = funAlbumDetail.getImgUuids();
        String[] imageIds = imgUuids.split(",");
        List<Long> funImageIds = Lists.newArrayList();
        for (String imageId : imageIds) {
            funImageIds.add(Long.valueOf(imageId));
        }

        Map<String, Object> params = Maps.newHashMap();
        params.put("ids", funImageIds);
        List<FunImages> funImagesList = funImagesDao.selectByParams(params);
        for (FunImages funImages : funImagesList) {
            funImages.setDel(del);
            funImagesDao.updateByPrimaryKey(funImages);
        }
    }

    @Override
    public JsonData updateAlbumDetailSort(List<FunAlbumDetail> albumDetailList) {
        for (FunAlbumDetail funAlbumDetail : albumDetailList) {
            funAlbumDetailDao.updateByPrimaryKeySelective(funAlbumDetail);
        }
        return JsonData.success("保存成功");
    }

    @Override
    public JsonData setHotImages(Long id) {
        HashMap<String, Object> search = Maps.newHashMap();
        search.put("del", 0);
        search.put("linkId", id);
        List<FunHotImages> funHotImagesList = funHotImagesDao.selectByParams(search);
        if (CollectionUtils.isNotEmpty(funHotImagesList)) {
            FunHotImages funHotImages = funHotImagesList.get(0);
            funHotImages.setUpdateTime(new Date());

            funHotImagesDao.updateByPrimaryKeySelective(funHotImages);

            return JsonData.success("已是热门，更新了时间");
        }

        FunHotImages funHotImages = new FunHotImages();
        funHotImages.setLinkId(id);
        funHotImages.setDel(0);
        funHotImages.setCreateTime(new Date());
        funHotImages.setUpdateTime(new Date());
        funHotImages.setCreatorId("");
        funHotImages.setCreatorName("");
        funHotImages.setUpdaterId("");
        funHotImages.setUpdaterName("");

        funHotImagesDao.insertSelective(funHotImages);

        return JsonData.success("成功设置为热门图片");
    }

    @Override
    public void flushFunAlbum() {
        HashMap<String, Object> search = Maps.newHashMap();
        search.put("del", 0);
        search.put("status", 1);
        search.put("publishDate", new LocalDate().toDate());
        List<FunAlbum> funAlbums = funAlbumDao.selectByParams(search);
        if (CollectionUtils.isNotEmpty(funAlbums)) {
            FunAlbum funAlbum = funAlbums.get(0);
            funAlbum.setStatus(0);
            funAlbumDao.updateByPrimaryKeySelective(funAlbum);
        }
    }

    @Override
    public JsonData delAlbumDetail(Long id) {
        FunAlbumDetail funAlbumDetail = funAlbumDetailDao.selectByPrimaryKey(id);
        if (funAlbumDetail == null){
            return JsonData.fail("删除失败，数据不存在");
        }

        int i = funAlbumDetailDao.deleteByPrimaryKey(id);
        if (i == 1) {
            this.updateAlbumDetailImageListDelStatus(funAlbumDetail, 2);
            return JsonData.success("删除成功");
        }
        return JsonData.fail("删除失败");
    }
}
