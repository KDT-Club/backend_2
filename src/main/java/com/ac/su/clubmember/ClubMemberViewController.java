//package com.ac.su.clubmember;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@Controller
//@RequiredArgsConstructor
//@RequestMapping("/clubs")
//public class ClubMemberViewController {
//    private final ClubMemberService clubMemberService;
//
//    // 동아리 id에 따른 동아리 회원 리스트 출력
//    @GetMapping("/{clubId}/clubMember")
//    public String clubMemberList(@PathVariable("clubId") Long clubId,
//                                 Model model) {
//        List<ClubMemberDTO> clubMemberDTOList = clubMemberService.findAllByClubId(clubId);
//        model.addAttribute("clubMemberList", clubMemberDTOList);
//        return "club_member_list";
//    }
//
//    // 회원 상세 정보
//    @GetMapping("/{clubId}/clubMember/{memberId}")
//    public String memberDetail(@PathVariable("memberId") Long memberId,
//                               @PathVariable("clubId") Long clubId,
//                               Model model) {
//        ClubMemberDTO clubMemberDTO = clubMemberService.findByMemberId(memberId, clubId);
//        model.addAttribute("clubMember", clubMemberDTO);
//        return "club_member_detail";
//    }


//
//    // 회원 상태 수정
//    @PostMapping("/{clubId}/clubMember/{memberId}/changeStatus")
//    public String changeStatus(@PathVariable("memberId") Long memberId,
//                               @PathVariable("clubId") Long clubId,
//                               @RequestParam("status") MemberStatus status) {
//        clubMemberService.changeStatus(memberId, clubId, status);
//        // 회원 상태 수정 후 회원 리스트로 리다이렉트
//        return "redirect:/clubs/" + clubId + "/clubMember";
//    }
//
//    // 회원 삭제
//    @PostMapping("/{clubId}/clubMember/{memberId}/deleteMember")
//    public String deleteMember(@PathVariable("memberId") Long memberId,
//                               @PathVariable("clubId") Long clubId) {
//        clubMemberService.deleteMember(memberId, clubId);
//        // 회원 삭제 후 회원 리스트로 리다이렉트
//        return "redirect:/clubs/" + clubId + "/clubMember";
//    }
//}