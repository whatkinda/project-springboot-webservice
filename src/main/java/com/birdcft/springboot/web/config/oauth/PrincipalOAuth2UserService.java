package com.birdcft.springboot.web.config.oauth;

import com.birdcft.springboot.web.auth.PrincipalDetails;
import com.birdcft.springboot.web.config.oauth.provider.OAuth2UserInfo;
import com.birdcft.springboot.web.config.oauth.provider.impl.GoogleUserInfo;
import com.birdcft.springboot.web.config.oauth.provider.impl.KakaoUserInfo;
import com.birdcft.springboot.web.config.oauth.provider.impl.NaverUserInfo;
import com.birdcft.springboot.web.domain.user.Role;
import com.birdcft.springboot.web.domain.user.User;
import com.birdcft.springboot.web.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class PrincipalOAuth2UserService extends DefaultOAuth2UserService {

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        OAuth2UserInfo oAuth2UserInfo = null;

        oAuth2UserInfo = switch (userRequest.getClientRegistration().getRegistrationId()) {
            case "google" -> new GoogleUserInfo(oAuth2User.getAttributes());
            case "kakao" -> new KakaoUserInfo(oAuth2User.getAttributes());
            case "naver" -> new NaverUserInfo((Map<String, Object>) oAuth2User.getAttributes().get("response"));
            default -> throw new IllegalStateException(">>> Unexpected Value :: " + userRequest.getClientRegistration().getRegistrationId());
        };

        String provider = oAuth2UserInfo.getProvider();
        String providerId = oAuth2UserInfo.getProviderId();
        String username = provider + "_" + providerId; // ex) `google_105897093875265670868`
        String password = passwordEncoder.encode("jonghwa");
        String email = oAuth2UserInfo.getEmail();
        Role role = Role.USER;

        Optional<User> user = userRepository.findByUsername(username);
        User userEntity = null;

        if (user.isEmpty()) {
            userEntity = User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();

            User savedUser = userRepository.save(userEntity);
        } else {
            log.debug("== already registered user: " + user.get().getUsername() + " ==");
        }

        return new PrincipalDetails(user.isEmpty() ? userEntity : user.get(), oAuth2User.getAttributes());
    }
}
