package com.attachakki.security.auth;

import com.attachakki.components.personalization.Personalization;
import com.attachakki.entity.User;
import com.attachakki.entity.UserDetails;
import com.attachakki.entity.type.SystemRole;
import com.attachakki.exception.businessLogic.UserAlreadyExistException;
import com.attachakki.exception.entityNotFound.UserDetailNotFoundException;
import com.attachakki.repository.PersonalizationRepository;
import com.attachakki.repository.UserDetailsRepository;
import com.attachakki.repository.UserRepository;
import com.attachakki.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final UserRepository userRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final PersonalizationRepository personalizationRepository;

    public AuthServiceImpl(
            UserRepository userRepository,
            UserDetailsRepository userDetailsRepository,
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            PersonalizationRepository personalizationRepository) {
        this.userRepository = userRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.personalizationRepository = personalizationRepository;
    }

    @Override
    @Transactional
    public RegisterResponseDto register(@Valid RegisterRequestDto requestDto) {

        User user = resolveUserByUsername(requestDto.getUsername());
        if (user != null && user.getUsername().equals(requestDto.getUsername())) {
            log.debug("User already exist with {}", requestDto.getUsername());
            throw new UserAlreadyExistException(requestDto.getUsername());
        }

        user = new User(
                requestDto.getUsername(),
                passwordEncoder.encode(requestDto.getPassword()),
                SystemRole.CLIENT,
                requestDto.getProvider()
        );
        User saveUser = userRepository.save(user);

        UserDetails userDetails = new UserDetails(
                saveUser,
                requestDto.getName(),
                requestDto.getPhoneNo(),
                null
        );
        UserDetails sud = userDetailsRepository.save(userDetails);

        Personalization personalization = new Personalization(sud);
        personalizationRepository.save(personalization);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateNewRefreshToken(user);

        return new RegisterResponseDto(
                sud.getId(),
                sud.getName(),
                sud.getUser().getUsername(),
                sud.getPhoneNo(),
                sud.getProfileUrl(),
                sud.getUser().getProvider(),
                accessToken,
                refreshToken
        );
    }

    @Override
    public RegisterResponseDto login(@Valid LoginRequestDto requestDto) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDto.getUsername(),
                        requestDto.getPassword()
                )
        );

        User user = (User) authentication.getPrincipal();

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        UserDetails sud = userDetailsRepository.findByUser(user)
                .orElseThrow(() -> new UserDetailNotFoundException("UserDetail not found", null));

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.updateRefreshToken(user);

        return new RegisterResponseDto(
                sud.getId(),
                sud.getName(),
                sud.getUser().getUsername(),
                sud.getPhoneNo(),
                sud.getProfileUrl(),
                sud.getUser().getProvider(),
                accessToken,
                refreshToken
        );
    }

    @Override
    public AccessTokenDto refreshToken(HttpServletRequest request) {
        log.info("refresh-token executing");
        String refreshToken = resolveToken(request);
        User user = jwtService.isValidRefreshToken(refreshToken);
        String accessToken = jwtService.generateAccessToken(user);
        return new AccessTokenDto(accessToken);
    }

    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    private User resolveUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElse(null);
    }
}
