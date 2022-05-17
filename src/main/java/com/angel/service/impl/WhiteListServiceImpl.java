package com.angel.service.impl;

import com.angel.entity.WhiteList;
import com.angel.mapper.WhiteListMapper;
import com.angel.service.IWhiteListService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class WhiteListServiceImpl extends ServiceImpl<WhiteListMapper, WhiteList> implements IWhiteListService {
}
