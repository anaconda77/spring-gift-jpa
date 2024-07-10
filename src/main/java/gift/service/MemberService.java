package gift.service;

import gift.exception.member.NotFoundMemberException;
import gift.exception.member.DuplicateEmailException;
import gift.model.Member;
import gift.repository.MemberRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    public static final String DUPLICATE_EMAIL_MESSAGE = "중복된 이메일의 회원이 이미 존재합니다.";

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public Member join(String email, String password) {
        try {
            return memberRepository.save(new Member(email, password));
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException(DUPLICATE_EMAIL_MESSAGE);
        }

    }

    public Member login(String email, String password) {

        return memberRepository.findByEmail(email)
            .filter(member -> member.validating(email, password))
            .orElseThrow(() -> new NotFoundMemberException("아이디 또는 비밀번호가 일치하지 않습니다."));
    }

}
