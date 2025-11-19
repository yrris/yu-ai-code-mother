# Service层JPA重构进度

##  ✅ 已完成

### 1. **UserServiceImpl** - 完全重构完成 ✅
**文件**: `src/main/java/com/yupi/yuaicodemother/service/impl/UserServiceImpl.java`

**重构内容**:
- ✅ 移除 `ServiceImpl<UserMapper, User>` 继承
- ✅ 注入 `UserRepository` 替代 `UserMapper`
- ✅ 所有方法改为使用 JPA repository
- ✅ `QueryWrapper` 改为 `Specification<User>`
- ✅ 添加 `getSort()` 方法处理排序
- ✅ 所有CRUD操作使用 `userRepository.save/findById/existsByUserAccount`

**关键变更**:
```java
// 旧代码 (MyBatis-Flex)
QueryWrapper queryWrapper = new QueryWrapper();
queryWrapper.eq("userAccount", userAccount);
User user = this.mapper.selectOneByQuery(queryWrapper);

// 新代码 (JPA)
Specification<User> spec = (root, query, cb) -> cb.and(
    cb.equal(root.get("userAccount"), userAccount),
    cb.equal(root.get("userPassword"), encryptPassword)
);
User user = userRepository.findOne(spec).orElse(null);
```

### 2. **UserService** - 接口已更新 ✅
**文件**: `src/main/java/com/yupi/yuaicodemother/service/UserService.java`

- ✅ 移除 `IService<User>` 继承
- ✅ `getQueryWrapper` 返回类型改为 `Specification<User>`

---

## 🔄 待重构

### 3. **AppServiceImpl** - 需要重构 🚧
**文件**: `src/main/java/com/yupi/yuaicodemother/service/impl/AppServiceImpl.java`

**需要修改的方法**:
1. `chatToGenCode()` - 使用 `appRepository.findById()`
2. `createApp()` - 使用 `appRepository.save()`
3. `deployApp()` - 使用 `appRepository.findById()` 和 `save()`
4. `getQueryWrapper()` - 改为 `Specification<App>`
5. 所有使用 `this.getById()`, `this.save()`, `this.updateById()` 的地方

**重构模板**:
```java
@Service
@Slf4j
public class AppServiceImpl implements AppService {

    @Resource
    private AppRepository appRepository;

    @Resource
    private UserService userService;

    // ... 其他依赖注入

    @Override
    public Flux<String> chatToGenCode(Long appId, String message, User loginUser) {
        // ... 参数校验

        // 旧代码: App app = this.getById(appId);
        // 新代码:
        App app = appRepository.findById(appId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_ERROR, "应用不存在"));

        // ... 其余业务逻辑
    }

    @Override
    public Long createApp(AppAddRequest appAddRequest, User loginUser) {
        App app = new App();
        BeanUtil.copyProperties(appAddRequest, app);
        app.setUserId(loginUser.getId());

        // 旧代码: boolean result = this.save(app);
        // 新代码:
        App savedApp = appRepository.save(app);
        return savedApp.getId();
    }

    @Override
    public Specification<App> getQueryWrapper(AppQueryRequest appQueryRequest) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (appQueryRequest.getId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), appQueryRequest.getId()));
            }
            if (StrUtil.isNotBlank(appQueryRequest.getAppName())) {
                predicates.add(criteriaBuilder.like(root.get("appName"),
                    "%" + appQueryRequest.getAppName() + "%"));
            }
            // ... 其他条件

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
```

### 4. **AppService** - 接口需要更新 🚧
**文件**: `src/main/java/com/yupi/yuaicodemother/service/AppService.java`

**需要修改**:
- 移除 `extends IService<App>`
- 更新 `getQueryWrapper()` 返回类型为 `Specification<App>`

### 5. **ChatHistoryServiceImpl** - 需要重构 🚧
**文件**: `src/main/java/com/yupi/yuaicodemother/service/impl/ChatHistoryServiceImpl.java`

**需要修改的方法**:
1. `addChatMessage()` - 使用 `chatHistoryRepository.save()`
2. `getAllMessagesOrderByCreateTimeDesc()` - 使用游标分页查询
3. `getQueryWrapper()` - 改为 `Specification<ChatHistory>`

**重构模板**:
```java
@Service
@Slf4j
public class ChatHistoryServiceImpl implements ChatHistoryService {

    @Resource
    private ChatHistoryRepository chatHistoryRepository;

    @Override
    public Long addChatMessage(Long appId, String message, String messageType, Long userId) {
        ChatHistory chatHistory = ChatHistory.builder()
                .appId(appId)
                .message(message)
                .messageType(messageType)
                .userId(userId)
                .build();

        ChatHistory saved = chatHistoryRepository.save(chatHistory);
        return saved.getId();
    }

    @Override
    public List<ChatHistory> getAllMessagesOrderByCreateTimeDesc(Long appId, LocalDateTime lastTime, int limit) {
        if (lastTime == null) {
            // 首次加载
            Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createTime"));
            return chatHistoryRepository.findByAppIdOrderByCreateTimeDesc(appId, pageable).getContent();
        } else {
            // 游标分页
            return chatHistoryRepository.findByAppIdAfterTime(appId, lastTime,
                    PageRequest.of(0, limit, Sort.by(Sort.Direction.ASC, "createTime")));
        }
    }

    @Override
    public Specification<ChatHistory> getQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (chatHistoryQueryRequest.getAppId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("appId"), chatHistoryQueryRequest.getAppId()));
            }
            if (StrUtil.isNotBlank(chatHistoryQueryRequest.getMessageType())) {
                predicates.add(criteriaBuilder.equal(root.get("messageType"), chatHistoryQueryRequest.getMessageType()));
            }
            // ... 其他条件

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
```

### 6. **ChatHistoryService** - 接口需要更新 🚧
**文件**: `src/main/java/com/yupi/yuaicodemother/service/ChatHistoryService.java`

**需要修改**:
- 移除 `extends IService<ChatHistory>`
- 更新 `getQueryWrapper()` 返回类型为 `Specification<ChatHistory>`

---

## 📋 Controller层调整

### UserController - 需要更新分页查询 🚧
**文件**: `src/main/java/com/yupi/yuaicodemother/controller/UserController.java`

**需要修改**:
```java
// 旧代码 (MyBatis-Flex)
public BaseResponse<Page<User>> listUserByPage(@RequestBody UserQueryRequest userQueryRequest) {
    long current = userQueryRequest.getCurrent();
    long size = userQueryRequest.getPageSize();
    QueryWrapper queryWrapper = userService.getQueryWrapper(userQueryRequest);
    Page<User> userPage = userService.page(new Page<>(current, size), queryWrapper);
    return ResultUtils.success(userPage);
}

// 新代码 (JPA)
public BaseResponse<Page<UserVO>> listUserByPage(@RequestBody UserQueryRequest userQueryRequest) {
    int current = userQueryRequest.getCurrent() != null ? userQueryRequest.getCurrent().intValue() - 1 : 0;
    int size = userQueryRequest.getPageSize() != null ? userQueryRequest.getPageSize().intValue() : 10;

    Specification<User> spec = userService.getQueryWrapper(userQueryRequest);
    Sort sort = ((UserServiceImpl) userService).getSort(userQueryRequest);

    Pageable pageable = PageRequest.of(current, size, sort);
    org.springframework.data.domain.Page<User> userPage = userRepository.findAll(spec, pageable);

    // 转换为 VO
    Page<UserVO> userVOPage = new Page<>();
    userVOPage.setTotal(userPage.getTotalElements());
    userVOPage.setRecords(userService.getUserVOList(userPage.getContent()));

    return ResultUtils.success(userVOPage);
}
```

### AppController - 需要更新分页查询 🚧
同样的模式适用于 `AppController`

### ChatHistoryController - 需要更新分页查询 🚧
同样的模式适用于 `ChatHistoryController`

---

## 🔧 辅助工具类

### Page转换工具
**建议创建**: `src/main/java/com/yupi/yuaicodemother/utils/PageUtils.java`

```java
package com.yupi.yuaicodemother.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageUtils {

    /**
     * 创建分页对象 (JPA)
     * @param current 当前页 (从1开始)
     * @param size 每页大小
     * @param sort 排序对象
     * @return Pageable
     */
    public static Pageable createPageable(Long current, Long size, Sort sort) {
        int page = current != null && current > 0 ? current.intValue() - 1 : 0;
        int pageSize = size != null && size > 0 ? size.intValue() : 10;
        return PageRequest.of(page, pageSize, sort != null ? sort : Sort.unsorted());
    }

    /**
     * 转换 Spring Data JPA Page 为自定义 Page
     */
    public static <T> com.mybatisflex.core.paginate.Page<T> convertPage(
            org.springframework.data.domain.Page<T> jpaPage) {
        com.mybatisflex.core.paginate.Page<T> page = new com.mybatisflex.core.paginate.Page<>();
        page.setTotal(jpaPage.getTotalElements());
        page.setRecords(jpaPage.getContent());
        page.setSize(jpaPage.getSize());
        page.setPageNumber(jpaPage.getNumber() + 1); // JPA从0开始，业务从1开始
        return page;
    }
}
```

---

## 📝 重构检查清单

### 每个Service实现需要:
- [ ] 移除 `extends ServiceImpl<XxxMapper, Xxx>`
- [ ] 注入对应的 `XxxRepository`
- [ ] 替换所有 `this.save()` 为 `repository.save()`
- [ ] 替换所有 `this.getById()` 为 `repository.findById().orElse(null)` 或 `orElseThrow()`
- [ ] 替换所有 `this.updateById()` 为 `repository.save()`
- [ ] 替换所有 `this.removeById()` 为 `repository.deleteById()` (考虑软删除)
- [ ] 替换所有 `this.page()` 为 `repository.findAll(spec, pageable)`
- [ ] 将 `QueryWrapper` 查询改为 `Specification`
- [ ] 添加日志记录 `@Slf4j`

### 每个Service接口需要:
- [ ] 移除 `extends IService<Xxx>`
- [ ] 更新 `getQueryWrapper()` 返回类型为 `Specification<Xxx>`

### 每个Controller需要:
- [ ] 更新分页查询逻辑
- [ ] 使用 `repository.findAll(spec, pageable)`
- [ ] 转换 Spring Data Page 为业务 Page 对象

---

## 🚀 下一步操作

1. **立即执行**:
   - 重构 `AppServiceImpl` 和 `AppService`
   - 重构 `ChatHistoryServiceImpl` 和 `ChatHistoryService`

2. **后续优化**:
   - 更新所有 Controller 的分页查询
   - 创建 `PageUtils` 工具类
   - 添加单元测试
   - 性能测试和优化

3. **验证**:
   - 编译通过: `./mvnw clean compile`
   - 运行测试: `./mvnw test`
   - 启动应用: `./mvnw spring-boot:run`

---

## ⚠️ 注意事项

1. **软删除处理**: JPA实体已配置 `@SQLDelete` 和 `@SQLRestriction`，自动处理软删除
2. **分页起始索引**: MyBatis-Flex 从1开始，JPA从0开始，需要转换
3. **查询null值**: JPA Specification需要明确处理null条件
4. **事务管理**: JPA自动管理事务，`@Transactional` 注解可继续使用
5. **懒加载**: 注意 `@ManyToOne(fetch = FetchType.LAZY)` 的使用

---

**重构进度**: UserService ✅ | AppService 🚧 | ChatHistoryService 🚧
**预计完成时间**: 继续重构约需30-45分钟
