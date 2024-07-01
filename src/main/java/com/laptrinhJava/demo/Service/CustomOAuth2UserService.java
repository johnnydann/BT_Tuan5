package com.laptrinhJava.demo.Service;

import com.laptrinhJava.demo.Model.Role;
import com.laptrinhJava.demo.Model.User;
import com.laptrinhJava.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");

        if (email != null) {
            Optional<User> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isEmpty()) {
                User user = new User();
                user.setEmail(email);
                user.setUsername(email.split("@")[0]);
                user.setPassword(""); // Không cần mật khẩu khi đăng nhập bằng OAuth2
                //user.setEnabled(true); // Mặc định enabled khi đăng nhập

                // Các giá trị mặc định cho các trường không bắt buộc
                user.setPhone("0000000000"); // Giả lập số điện thoại
                user.setAddress("Unknown"); // Giả lập địa chỉ
                user.setBirthdate(LocalDate.of(2000, 1, 1)); // Giả lập ngày sinh

                userRepository.save(user);
            }
        }

        return oAuth2User;
    }
}

