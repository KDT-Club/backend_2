package com.ac.su.member;

import com.ac.su.clubmember.ClubMemberRepository;
import com.ac.su.clubmember.MemberStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClubMemberRepository clubMemberRepository;

    public MemberService(PasswordEncoder passwordEncoder, ClubMemberRepository clubMemberRepository) {
        this.passwordEncoder = passwordEncoder;
        this.clubMemberRepository = clubMemberRepository;//
    }

    //멤버 정보 불러오기
    public Optional<Member> getMemberById(Long memberId) {

        return memberRepository.findById(memberId);
    }
    //멤버 정보 수정
    public Member updateMember(Long memberId, MemberDTO memberDTO) {
        Optional<Member> memberOptional = memberRepository.findById(memberId);
        //멤버 존재 여부 확인 및 수정
        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            member.setName(memberDTO.getName());
            member.setDepartment(memberDTO.getDepartment());
            member.setStudentId(memberDTO.getStudentId());
            member.setMemberImageURL(memberDTO.getMemberImageURL());
            member.setPhone(memberDTO.getPhone());

            // 비밀번호 암호화
            if (memberDTO.getPassword() != null && !memberDTO.getPassword().isEmpty()) {
                String encryptedPassword = passwordEncoder.encode(memberDTO.getPassword());
                member.setPassword(encryptedPassword);
            }

            //DB에 저장
            return memberRepository.save(member);
        }
        return null;
    }
    //동아리 회장인지 확인
    private boolean isMemberAlreadyPresident(Long memberId) {
        return clubMemberRepository.existsByMemberIdAndStatus(memberId, MemberStatus.CLUB_PRESIDENT);

    }
    // 멤버 삭제
    public void deleteMember(Long memberId) {
        if (isMemberAlreadyPresident(memberId)) {
            throw new RuntimeException("동아리 회장은 탈퇴할 수 없습니다.");//
        }
        memberRepository.deleteById(memberId);
    }
}