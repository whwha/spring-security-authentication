package nextstep.app.ui;

import nextstep.app.domain.Member;
import nextstep.app.domain.MemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@RestController
public class MemberController {

    public static final String SPRING_SECURITY_CONTEXT_KEY = "SPRING_SECURITY_CONTEXT";
    private final MemberRepository memberRepository;

    public MemberController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @GetMapping("/members")
    public ResponseEntity<List<Member>> list(
            @RequestHeader("Authorization") String authorization,
            HttpSession session
    ) {
        String username;
        String password;

        try {
            String encodedToken = authorization.split(" ")[1];
            byte[] decode = Base64.getDecoder().decode(encodedToken);
            String token = new String(decode, StandardCharsets.UTF_8);
            username = token.split(":")[0];
            password = token.split(":")[1];
        } catch (Exception e) {
            throw new AuthenticationException();
        }

        Member member = memberRepository
                .findByEmail(username)
                .orElseThrow(AuthenticationException::new);

        if (!member.getPassword().equals(password)) {
            throw new AuthenticationException();
        }

        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, member);

        List<Member> members = memberRepository.findAll();
        return ResponseEntity.ok(members);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Void> handleAuthenticationException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
