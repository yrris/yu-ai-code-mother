package com.yupi.yuaicodemother.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.yupi.yuaicodemother.exception.BusinessException;
import com.yupi.yuaicodemother.exception.ErrorCode;
import com.yupi.yuaicodemother.model.dto.user.UserQueryRequest;
import com.yupi.yuaicodemother.model.entity.User;
import com.yupi.yuaicodemother.model.enums.UserRoleEnum;
import com.yupi.yuaicodemother.model.vo.LoginUserVO;
import com.yupi.yuaicodemother.model.vo.UserVO;
import com.yupi.yuaicodemother.repository.UserRepository;
import com.yupi.yuaicodemother.service.UserService;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.yupi.yuaicodemother.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户 服务层实现。
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @author Refactored for JPA/Hibernate
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private UserRepository userRepository;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验参数
        if (StrUtil.hasBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度过短");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        // 2. 查询用户是否已存在 (使用JPA repository)
        if (userRepository.existsByUserAccount(userAccount)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }
        // 3. 加密密码
        String encryptPassword = getEncryptPassword(userPassword);
        // 4. 创建用户，插入数据库 (使用JPA repository)
        User user = User.builder()
                .userAccount(userAccount)
                .userPassword(encryptPassword)
                .userName("无名")
                .userRole(UserRoleEnum.USER.getValue())
                .build();

        try {
            User savedUser = userRepository.save(user);
            log.info("用户注册成功，ID: {}, 账号: {}", savedUser.getId(), savedUser.getUserAccount());
            return savedUser.getId();
        } catch (Exception e) {
            log.error("用户注册失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "注册失败，数据库错误");
        }
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtil.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验参数
        if (StrUtil.hasBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度过短");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度过短");
        }
        // 2. 加密
        String encryptPassword = getEncryptPassword(userPassword);
        // 3. 查询用户是否存在 (使用JPA Specification)
        Specification<User> spec = (root, query, cb) -> cb.and(
                cb.equal(root.get("userAccount"), userAccount),
                cb.equal(root.get("userPassword"), encryptPassword)
        );

        User user = userRepository.findOne(spec).orElse(null);
        if (user == null) {
            log.warn("登录失败，账号: {}", userAccount);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 4. 如果用户存在，记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        log.info("用户登录成功，ID: {}, 账号: {}", user.getId(), user.getUserAccount());
        // 5. 返回脱敏的用户信息
        return this.getLoginUserVO(user);
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断用户是否登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询当前用户信息 (使用JPA repository)
        long userId = currentUser.getId();
        currentUser = userRepository.findById(userId).orElse(null);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream()
                .map(this::getUserVO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        // 先判断用户是否登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        log.info("用户登出成功");
        return true;
    }

    /**
     * 获取查询条件 (JPA Specification)
     *
     * @param userQueryRequest 查询请求
     * @return JPA Specification
     */
    @Override
    public Specification<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // ID 精确匹配
            if (id != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), id));
            }
            // 用户角色精确匹配
            if (StrUtil.isNotBlank(userRole)) {
                predicates.add(criteriaBuilder.equal(root.get("userRole"), userRole));
            }
            // 账号模糊查询
            if (StrUtil.isNotBlank(userAccount)) {
                predicates.add(criteriaBuilder.like(root.get("userAccount"), "%" + userAccount + "%"));
            }
            // 用户名模糊查询
            if (StrUtil.isNotBlank(userName)) {
                predicates.add(criteriaBuilder.like(root.get("userName"), "%" + userName + "%"));
            }
            // 用户简介模糊查询
            if (StrUtil.isNotBlank(userProfile)) {
                predicates.add(criteriaBuilder.like(root.get("userProfile"), "%" + userProfile + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * 获取排序对象
     *
     * @param userQueryRequest 查询请求
     * @return Sort 对象
     */
    public Sort getSort(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            return Sort.by(Sort.Direction.DESC, "createTime");
        }

        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();

        if (StrUtil.isBlank(sortField)) {
            sortField = "createTime";
        }

        Sort.Direction direction = "ascend".equals(sortOrder)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        return Sort.by(direction, sortField);
    }

    @Override
    public String getEncryptPassword(String userPassword) {
        // 盐值，混淆密码
        final String SALT = "yupi";
        return DigestUtils.md5DigestAsHex((userPassword + SALT).getBytes(StandardCharsets.UTF_8));
    }
}
