package com.angel.service.impl;

import com.angel.entity.Roles;
import com.angel.mapper.RolesMapper;
import com.angel.service.IRolesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RolesServiceImpl extends ServiceImpl<RolesMapper, Roles> implements IRolesService {

    private static final Logger log = LoggerFactory.getLogger(RolesServiceImpl.class);

}
