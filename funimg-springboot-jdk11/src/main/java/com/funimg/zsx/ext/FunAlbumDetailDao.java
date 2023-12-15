package com.funimg.zsx.ext;

import com.funimg.zsx.dao.FunAlbumDetailMapper;
import com.funimg.zsx.entity.FunAlbumDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by highness on 2018/4/28 0028.
 */
@Mapper
public interface FunAlbumDetailDao extends FunAlbumDetailMapper {


    List<FunAlbumDetail> selectByAlbumId(Long albumId);

}
