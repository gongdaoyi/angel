package com.angel.service.impl;

import com.angel.entity.Roles;
import com.angel.mapper.RolesMapper;
import com.angel.service.IRolesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class RolesServiceImpl extends ServiceImpl<RolesMapper, Roles> implements IRolesService {

}
