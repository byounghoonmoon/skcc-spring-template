package skcc.arch.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import skcc.arch.app.exception.CustomException;
import skcc.arch.app.exception.ErrorCode;
import skcc.arch.mock.FakePasswordEncoder;
import skcc.arch.mock.FakeUserRepository;
import skcc.arch.user.domain.User;
import skcc.arch.user.domain.UserCreateRequest;
import skcc.arch.user.domain.UserStatus;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserServiceTest {

    private UserServiceImpl userService;
    private final int TOTAL_USER_COUNT = 45;

    @BeforeEach
    void setUp() {
        FakeUserRepository fakeUserRepository = new FakeUserRepository();
        FakePasswordEncoder fakePasswordEncoder = new FakePasswordEncoder();
        this.userService = new UserServiceImpl(fakeUserRepository, fakePasswordEncoder);

        for (int i = 1; i <= TOTAL_USER_COUNT; i++) {
            fakeUserRepository.save(User.builder()
                    .id((long) (i + 1))
                    .email("test"+i+"@sk.com")
                    .password(fakePasswordEncoder.encode("password"))
                    .username("홍길동"+i)
                    .status(UserStatus.ACTIVE)
                    .build());
        }
    }

    @Test
    void userCreate_를_이용해_생성한다() throws Exception {
        //given
        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .username("홍길동")
                .email("email@sk.com")
                .password("password")
                .build();

        // when
        User result = userService.create(userCreateRequest);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(result.getPassword()).isEqualTo("ENC_password");

    }

    @Test
    void 정상적인_비밀번호_로그인_시도() throws Exception {
        //given
        String email = "test1@sk.com";
        String rawPassword = "password";

        //when
        User loginUser = userService.login(email, rawPassword);

        //then
        assertThat(loginUser.getEmail()).isEqualTo(email);
        assertThat(loginUser.getStatus()).isEqualTo(UserStatus.ACTIVE);

    }

    @Test
    void 잘못된_비밀번호_로그인_시도() throws Exception {
        //given
        String email = "test1@sk.com";
        String rawPassword = "passwordXXX";

        //when
        CustomException exception = assertThrows(CustomException.class, () -> userService.login(email, rawPassword));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_MATCHED_PASSWORD);

    }

    @Test
    void 존재하지_않은_이메일로_로그인_시도() throws Exception {
        //given
        String email = "empty@sk.com";
        String rawPassword = "password";

        //when
        CustomException exception =
                assertThrows(CustomException.class, () -> userService.login(email, rawPassword));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND_ELEMENT);
    }

    @Test
    void 전체_사용자_조회() throws Exception {
        //given

        //when
        List<User> allUsers = userService.findAllUsers();

        //then
        assertThat(allUsers.size()).isEqualTo(TOTAL_USER_COUNT);

    }

    @Test
    void 페이지정보를_이용한_사용자_조회() throws Exception {
        //given
        int pageSize = 5;
        PageRequest pageRequest = PageRequest.of(0, pageSize);

        //when
        Page<User> users = userService.findAll(pageRequest);

        //then
        assertThat(users.getTotalElements()).isEqualTo(TOTAL_USER_COUNT);
        assertThat(users.getContent().size()).isEqualTo(pageSize);
        assertThat(users.getTotalPages()).isEqualTo(TOTAL_USER_COUNT % pageSize == 0 ? TOTAL_USER_COUNT / pageSize : TOTAL_USER_COUNT / pageSize + 1);

    }

}