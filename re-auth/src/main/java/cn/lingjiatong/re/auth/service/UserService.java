package cn.lingjiatong.re.auth.service;

import cn.lingjiatong.re.auth.vo.UserLoginVO;
import cn.lingjiatong.re.common.constant.CommonConstant;
import cn.lingjiatong.re.common.constant.RedisCacheKeyEnum;
import cn.lingjiatong.re.common.entity.Permission;
import cn.lingjiatong.re.common.entity.Role;
import cn.lingjiatong.re.common.entity.User;
import cn.lingjiatong.re.common.entity.cache.LoginVerifyCodeCache;
import cn.lingjiatong.re.auth.mapper.PermissionMapper;
import cn.lingjiatong.re.auth.mapper.RoleMapper;
import cn.lingjiatong.re.auth.mapper.UserMapper;
import cn.lingjiatong.re.common.util.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.*;

/**
 * 用户模块service层
 *
 * @author Ling, Jiatong
 * Date: 2022/10/22 18:56
 */
@Slf4j
@Service
public class UserService implements UserDetailsService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private PermissionMapper permissionMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private DefaultKaptcha defaultKaptcha;
    @Autowired
    private TokenEndpoint tokenEndpoint;

    // ********************************新增类接口********************************
    // ********************************删除类接口********************************
    // ********************************修改类接口********************************
    // ********************************查询类接口********************************



    /**
     * 用户登录
     *
     * @param principal principal
     * @param parameters 参数列表
     * @return 用户登录VO对象
     */
    public UserLoginVO login(Principal principal, @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        UserLoginVO result = new UserLoginVO();
        UserLoginVO.UserInfo userInfo = new UserLoginVO.UserInfo();
        UserLoginVO.TokenInfo tokenInfo = new UserLoginVO.TokenInfo();
        List<UserLoginVO.MenuInfo> menus = Lists.newArrayList();

        // 获取token信息
        ResponseEntity<OAuth2AccessToken> tokenResponseEntity = tokenEndpoint.postAccessToken(principal, parameters);
        // 获取用户信息
        // 获取菜单信息

        result.setUserInfo(userInfo);
        result.setMenus(menus);
        result.setTokenInfo(tokenInfo);
        return result;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 根据用户名查询用户信息，包括权限信息
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .select(User::getId, User::getUsername, User::getPassword, User::getEmail, User::getPhone)
                .eq(User::getUsername, username)
                .eq(User::getDeleted, CommonConstant.ENTITY_NORMAL));
        Optional.ofNullable(user)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在"));
        // 查询角色和权限列表
        List<Role> roles = roleMapper.findRoleByUserId(user.getId());
        // 根据权限id去重
        Set<Permission> finalPermissions = Sets.newTreeSet(Comparator.comparing(Permission::getId));

        roles.forEach(role -> {
            List<Permission> permissionList = permissionMapper.findPermissionByRoleId(role.getId());
            finalPermissions.addAll(permissionList);
        });

        user.setRoles(roles);
        user.setPermissions(finalPermissions);
        return user;
    }

    /**
     * 刷新登录验证码
     *
     * @param verifyCodeKey 前端传递过来的验证码随机值
     */
    public void refreshVerifyCode(String verifyCodeKey, HttpServletResponse httpServletResponse) throws IOException {
        byte[] captchaChallengeAsJpeg;
        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
        // 生产验证码字符串并保存到redis中
        String createText = defaultKaptcha.createText();
        LoginVerifyCodeCache cache = new LoginVerifyCodeCache();
        cache.setValue(createText);
        redisUtil.setCacheObject(RedisCacheKeyEnum.LOGIN_VERIFY_CODE.getValue() + verifyCodeKey, cache);
        // 使用生成的验证码字符串返回一个BufferedImage对象并转为byte写入到byte数组中
        BufferedImage challenge = defaultKaptcha.createImage(createText);
        ImageIO.write(challenge, "jpg", jpegOutputStream);
        // 定义response输出类型为image/jpeg类型，使用response输出流输出图片的byte数组
        captchaChallengeAsJpeg = jpegOutputStream.toByteArray();
        httpServletResponse.setHeader("Cache-Control", "no-store");
        httpServletResponse.setHeader("Pragma", "no-cache");
        httpServletResponse.setDateHeader("Expires", 0);
        httpServletResponse.setContentType("image/jpeg");
        ServletOutputStream responseOutputStream = httpServletResponse.getOutputStream();
        responseOutputStream.write(captchaChallengeAsJpeg);
        responseOutputStream.flush();
        responseOutputStream.close();
    }

    // ********************************私有函数********************************

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户信息对象
     */
    private UserLoginVO.UserInfo getUserInfoByUsernameAndPassword(String username) {

//        userMapper.selectOne(new LambdaQueryWrapper<User>()
//                .select(User::getUsername, User::get)
//                .eq());
        return null;
    }

    // ********************************公用函数********************************
}
