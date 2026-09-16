package com.team.orderapp.auth;

import com.team.orderapp.common.BusinessException;

import java.util.Optional;

/**
 * 인증 및 권한 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
public class AuthService {

    private final AppUserDao appUserDao;

    public AuthService() {
        this.appUserDao = new AppUserDao();
    }

    public AuthService(AppUserDao InAppUserDao) {
        this.appUserDao = InAppUserDao;
    }

    /**
     * 사용자 로그인을 수행하고 세션에 등록합니다.
     *
     * @param InUsername 사용자 아이디
     * @param InRawPassword 평문 비밀번호
     * @return 로그인 성공한 AppUser 객체
     */
    public AppUser Login(String InUsername, String InRawPassword) {
        AppUser user = ValidateCredentials(InUsername, InRawPassword);
        LoginSession.SetCurrentUser(user);
        return user;
    }

    /**
     * 신규 사용자를 회원가입 처리합니다.
     *
     * @param InNewUser 등록할 사용자 객체
     * @param InRawPassword 평문 비밀번호
     * @return 등록 완료된 AppUser 객체
     */
    public AppUser RegisterUser(AppUser InNewUser, String InRawPassword) {
        ValidateUserRegistrationInput(InNewUser, InRawPassword);
        CheckUsernameDuplicate(InNewUser.GetUsername());

        String hashedPassword = PasswordHasher.HashPassword(InRawPassword);
        InNewUser.SetPasswordHash(hashedPassword);

        boolean success = appUserDao.Save(InNewUser);
        if (!success) {
            throw new BusinessException("사용자 등록에 실패하였습니다.");
        }
        return InNewUser;
    }

    /**
     * 사용자 아이디 및 비밀번호 유효성을 검증하는 헬퍼 메서드입니다.
     *
     * @param InUsername 사용자 아이디
     * @param InRawPassword 평문 비밀번호
     * @return 검증된 AppUser 객체
     */
    public AppUser ValidateCredentials(String InUsername, String InRawPassword) {
        Optional<AppUser> userOptional = appUserDao.FindByUsername(InUsername);
        if (userOptional.isEmpty()) {
            throw new BusinessException("존재하지 않는 사용자입니다.");
        }

        AppUser user = userOptional.get();
        if (!PasswordHasher.VerifyPassword(InRawPassword, user.GetPasswordHash())) {
            throw new BusinessException("비밀번호가 일치하지 않습니다.");
        }
        return user;
    }

    /**
     * 사용자 등록 입력값 유효성을 검사하는 헬퍼 메서드입니다.
     *
     * @param InUser 사용자 정보 객체
     * @param InRawPassword 평문 비밀번호
     */
    private void ValidateUserRegistrationInput(AppUser InUser, String InRawPassword) {
        if (InUser == null || InUser.GetUsername() == null || InUser.GetUsername().trim().isEmpty()) {
            throw new BusinessException("사용자 아이디는 필수 입력 항목입니다.");
        }
        if (InRawPassword == null || InRawPassword.length() < 4) {
            throw new BusinessException("비밀번호는 최소 4자리 이상이어야 합니다.");
        }
    }

    /**
     * 아이디 중복 여부를 검사하는 헬퍼 메서드입니다.
     *
     * @param InUsername 검사할 사용자 아이디
     */
    private void CheckUsernameDuplicate(String InUsername) {
        if (appUserDao.FindByUsername(InUsername).isPresent()) {
            throw new BusinessException("이미 사용 중인 사용자 아이디입니다: " + InUsername);
        }
    }
}
