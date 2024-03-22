package com.example.springsocial.config.oauth;

import com.example.springsocial.config.oauth.dto.OAuthAttributes;
import com.example.springsocial.domain.Users;
import com.example.springsocial.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // Oauth2 서비스 id (구글, 카카오, 네이버)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        // Oauth2 로그인 진행 시 키가 되는 필드 값(PK)
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        // OauthUserService
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        Users user = saveOrUpdate(attributes);

        return buildOAuth2User(user);
    }

    // OAuth2User 객체 생성
    private OAuth2User buildOAuth2User(Users user) {
        Set<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey()));
        Map<String, Object> attributes = Map.of(
                "email", user.getEmail(),
                "name", user.getName(),
                "picture", user.getPicture()
        );

        return new DefaultOAuth2User(authorities, attributes, "email");
    }

    // 유저 생성 및 수정 서비스 로직
    private Users saveOrUpdate(OAuthAttributes attributes) {
        Users user = userRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getName(), attributes.getPicture(), attributes.getProvider())) // email을 검색해 사용자가 존재하면 update
                .orElse(attributes.toEntity()); // 비어있으면 새로운 데이터 insert

        return userRepository.save(user); //최종적으로 저장 or 업데이트 한다.
    }

    public List<Users> getAllMembers() {
        return userRepository.findAll();
    }
}