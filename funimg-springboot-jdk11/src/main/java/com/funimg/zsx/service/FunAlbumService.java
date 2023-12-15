package com.funimg.zsx.service;

import com.funimg.zsx.entity.FunAlbum;
import com.funimg.zsx.entity.FunAlbumDetail;
import com.funimg.zsx.util.PageData;
import com.funimg.zsx.vo.FunAlbumVO;
import com.funimg.zsx.vo.app.*;
import com.funimg.zsx.vo.json.JsonData;
import com.funimg.zsx.vo.json.JsonTable;

import java.util.List;
import java.util.Map;

/**
 * Created by highness on 2018/5/10 0010.
 */
public interface FunAlbumService {

    /**
     * 给小程序提供的分页查询专辑列表接口
     *
     * @param search
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageData<AlbumList> getFunAlbumPageList(Map<String, Object> search, Integer pageNum, Integer pageSize);


    PageData<FunAlbumVO> queryFunAlbumPageList(Map<String, Object> search, Integer pageNum, Integer pageSize);


    AlbumDetail getAlbumData(Long albumId);

    PageData<AlbumData> getFunHotImagePageList(Map<String, Object> search, Integer pageNum, Integer pageSize);

    ImageComment getImageComment(Long id);

    List<Comment> getImageCommentList(Long albumDetailId);

    JsonData addComment(Comment comment);


//    *******************************************************************
//    web页面接口
//    *******************************************************************

    JsonData addAlbum(FunAlbum funAlbum);

    JsonData updAlbum(FunAlbum funAlbum);

    JsonTable getAlbumDetailList(Long albumId);

    JsonTable getAlbumDetailPageList(Map<String, Object> search, Integer pageNum, Integer pageSize);

    JsonTable getHotImagePageList(Map<String, Object> search, Integer pageNum, Integer pageSize);

    JsonData saveFunAlbumDetail(FunAlbumDetail funAlbumDetail);

    JsonData updateAlbumDetailSort(List<FunAlbumDetail> albumDetailList);

    JsonData setHotImages(Long id);

    void flushFunAlbum();

    JsonData delAlbumDetail(Long id);

}
