package cn.smxy.application.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.smxy.application.bean.Course;
import cn.smxy.application.core.AbstractService;
import cn.smxy.application.mapper.CourseMapper;
import cn.smxy.application.service.CourseService;

import javax.annotation.Resource;

/**
 * @author ling_cx 
 * @version 1.0
 * @Description 
 * @date 2018/03/17.
 * @Copyright: 2018 www.lingcx.cn Inc. All rights reserved.
 */
@Service
@Transactional
public class CourseServiceImpl extends AbstractService<Course> implements CourseService {
    @Resource
    private CourseMapper oaCoursesMapper;

	@Override
	public List<Course> selectCourseByCondition(Map<String, Object> params) {
		return oaCoursesMapper.selectCourseByCondition(params);
	}
}
