package com.angel.service.impl;

import com.angel.entity.WhiteList;
import com.angel.mapper.WhiteListMapper;
import com.angel.service.IWhiteListService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WhiteListServiceImpl extends ServiceImpl<WhiteListMapper, WhiteList> implements IWhiteListService {

    @Autowired
    WhiteListMapper whiteListMapper;

    @Autowired
    SqlSessionFactory sqlSessionFactory;

    /**
     * 正确的批量插入姿势
     */
    public void batchInsert() {
        List<WhiteList> list = new ArrayList<>();
        list.add(new WhiteList());

        SqlSession session = sqlSessionFactory.openSession(ExecutorType.BATCH);
        WhiteListMapper mapper = session.getMapper(WhiteListMapper.class);

        for (int index = 0; index < list.size(); index++) {
            mapper.insert(list.get(index));

            if (index != 0 && index % 1000 == 0) {
                session.commit();
            }
        }

        session.commit();
    }
}
